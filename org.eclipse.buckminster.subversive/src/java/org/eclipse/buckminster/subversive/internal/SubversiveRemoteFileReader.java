/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.subversive.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.helpers.FileUtils;
import org.eclipse.buckminster.core.reader.AbstractRemoteReader;
import org.eclipse.buckminster.core.reader.IReaderType;
import org.eclipse.buckminster.core.version.ProviderMatch;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.connector.ISVNProgressMonitor;
import org.eclipse.team.svn.core.connector.SVNConnectorException;
import org.eclipse.team.svn.core.connector.SVNEntry;
import org.eclipse.team.svn.core.connector.SVNEntryRevisionReference;
import org.eclipse.team.svn.core.connector.SVNRevision;
import org.eclipse.team.svn.core.operation.file.GetFileContentOperation;

/**
 * The SVN repository reader assumes that any repository contains the three recommended directories <code>trunk</code>,
 * <code>tags</code>, and <code>branches</code>. A missing <code>tags</code> directory is interpreted as no
 * <code>tags</code>. A missing <code>branches</code> directory is interpreted as no branches. The URL used as the
 * repository identifier must contain the path element trunk. Anything that follows the <code>trunk</code> element in
 * the path will be considered a <code>module</code> path. The repository URL may also contain a query part that in
 * turn may have four different flags:
 * <dl>
 * <dt>moduleBeforeTag</dt>
 * <dd>When resolving a tag, put the module name between the <code>tags</code> directory and the actual tag</dd>
 * <dt>moduleAfterTag</dt>
 * <dd>When resolving a tag, append the module name after the actual tag</dd>
 * <dt>moduleBeforeBranch</dt>
 * <dd>When resolving a branch, put the module name between the <code>branches</code> directory and the actual branch</dd>
 * <dt>moduleAfterBranch</dt>
 * <dd>When resolving a branch, append the module name after the actual branch</dd>
 * </dl>
 * A fragment in the repository URL will be treated as a sub-module. It will be appended at the end of the resolved URL.
 * 
 * @author Thomas Hallgren
 */
public class SubversiveRemoteFileReader extends AbstractRemoteReader
{
	private final SubversiveSession m_session;
	private final SVNEntry[] m_topEntries;
	/**
	 * @param readerType
	 * @param rInfo
	 * @param withResolvedBranch
	 * @throws CoreException
	 */
	public SubversiveRemoteFileReader(IReaderType readerType, ProviderMatch rInfo, IProgressMonitor monitor) throws CoreException
	{
		super(readerType, rInfo);
		VersionMatch vm = rInfo.getVersionMatch();
		VersionSelector branchOrTag = vm.getBranchOrTag();
		m_session = new SubversiveSession(rInfo.getRepositoryURI(), branchOrTag, vm.getRevision(), vm.getTimestamp(), rInfo.getNodeQuery().getContext());
		m_topEntries = m_session.listFolder(m_session.getSVNUrl(null), monitor);
	}

	@Override
	public void close()
	{
		m_session.close();
	}

	public void innerMaterialize(IPath destination, IProgressMonitor monitor) throws CoreException
	{
		boolean success = false;
		File destDir = destination.toFile();

		ISVNProgressMonitor svnMon = SimpleMonitorWrapper.beginTask(monitor, 12);
		try
		{
			m_session.getSVNProxy().checkout(new SVNEntryRevisionReference(m_session.getSVNUrl(null).toString(), null, m_session.getRevision()), destDir.toString(), ISVNConnector.Depth.INFINITY, true, false, svnMon);
			success = true;
		}
		catch(SVNConnectorException e)
		{
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			if(!success)
			{
				try
				{
					FileUtils.deleteRecursive(destDir, new NullProgressMonitor());
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
			}
			monitor.done();
		}
	}

	@Override
	public String toString()
	{
		return m_session.toString();
	}

	@Override
	protected File innerGetContents(String fileName, boolean[] isTemporary, IProgressMonitor monitor) throws CoreException,
			IOException
	{
		Logger logger = CorePlugin.getLogger();
		IPath path = Path.fromPortableString(fileName);
		String topEntry = path.segment(0);

		boolean found = false;
		for(SVNEntry dirEntry : m_topEntries)
		{
			if(topEntry.equals(dirEntry.path))
			{
				found = true;
				break;
			}
		}

		URI url = m_session.getSVNUrl(fileName);
		SVNRevision revision = m_session.getRevision();
		String key = SubversiveSession.cacheKey(url, revision);
		if(!found)
			throw new FileNotFoundException(key);

		File destFile = null;
		OutputStream output = null;
		InputStream input = null;

		int ticksLeft = 3;
		isTemporary[0] = false;
		ISVNProgressMonitor svnMon = SimpleMonitorWrapper.beginTask(monitor, ticksLeft);

		try
		{
			if(logger.isDebugEnabled())
				logger.debug(String.format("Reading remote file %s", key));

			ISVNConnector proxy = m_session.getSVNProxy();
			destFile = this.createTempFile();
			output = new FileOutputStream(destFile);
			proxy.streamFileContent(new SVNEntryRevisionReference(url.toString(), null, revision), GetFileContentOperation.DEFAULT_BUFFER_SIZE, output, svnMon);
			IOUtils.close(output);
			
			if(destFile.length() == 0)
			{
				// Suspect file not found
				//
				if(m_session.getDirEntry(url, revision, null) == null)
				{
					if(logger.isDebugEnabled())
						logger.debug(String.format("Remote file not found %s", key));
					throw new FileNotFoundException(url.toString());
				}
			}
			
			isTemporary[0] = true;

			return destFile;
		}
		catch(SVNConnectorException e)
		{
			// Unwind until we get a message and create an IOException.
			//
			Throwable p = e;
			Throwable t;
			String msg = e.getMessage();
			while(msg == null && (t = p.getCause()) != null)
				p = t;
			if(msg == null)
				msg = e.toString();
			else
			{
				String lcMsg = msg.toLowerCase();
				if(lcMsg.contains("file not found") || lcMsg.contains("path not found"))
				{
					if(logger.isDebugEnabled())
						logger.debug(String.format("Remote file not found %s", key));
					throw new FileNotFoundException(key);
				}
			}
			IOException ioe = new IOException(msg);
			ioe.initCause(p);
			throw ioe;
		}
		finally
		{
			IOUtils.close(input);
			IOUtils.close(output);
			if(!isTemporary[0] && destFile != null)
				destFile.delete();
			if(ticksLeft > 0)
    			MonitorUtils.worked(monitor, ticksLeft);
			monitor.done();
		}
	}
}
