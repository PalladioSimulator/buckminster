/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.maven.internal;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.resolver.ResolverDecisionType;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.version.IVersion;
import org.eclipse.buckminster.core.version.IVersionDesignator;
import org.eclipse.buckminster.core.version.VersionFactory;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.core.version.VersionSyntaxException;
import org.eclipse.buckminster.download.DownloadManager;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.core.security.IConnectContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Thomas Hallgren
 */
public class Maven2VersionFinder extends MavenVersionFinder
{
	public Maven2VersionFinder(MavenReaderType readerType, Provider provider, IComponentType ctype, NodeQuery query)
	throws CoreException
	{
		super(readerType, provider, ctype, query);
	}

	/**
	 * Returns an array of versions known to this repository.
	 * 
	 * @return known versions or <code>null</code> if not applicable.
	 * @throws CoreException
	 */
	@Override
	List<VersionMatch> getComponentVersions(IProgressMonitor monitor) throws CoreException
	{
		NodeQuery query = getQuery();
		IVersionDesignator designator = query.getVersionDesignator();
		if(designator == null)
			designator = VersionFactory.createDesignator(VersionFactory.TripletType, "0.0.0");
		else
		{
			if(designator.getVersion().getType().equals(VersionFactory.OSGiType))
				//
				// Convert the OSGi version to a Triplet version instead.
				//
				designator = VersionFactory.createDesignator(VersionFactory.TripletType, designator.toString());
		}

		List<VersionMatch> versions = new ArrayList<VersionMatch>();
		Maven2ReaderType readerType = (Maven2ReaderType)getReaderType();
		URI uri = getURI();
		StringBuilder pbld = new StringBuilder();
		readerType.appendFolder(pbld, uri.getPath());
		readerType.appendEntryFolder(pbld, getMapEntry());
		String rootPath = pbld.toString();
		IConnectContext cctx = getConnectContext();

		LocalCache lc = getReaderType().getLocalCache();
		IVersionDesignator versionDesignator = query.getVersionDesignator();
		monitor.beginTask(null, 2000);
		try
		{
			DocumentBuilder docBld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = getMetadataDocument(docBld, MavenReaderType.createURL(uri, rootPath + "maven-metadata.xml"), lc, cctx, monitor);
			for(String versionStr : getVersions(doc))
			{
				String v = versionStr;
				if(v.endsWith("SNAPSHOT"))
				{
					try
					{
						doc = getMetadataDocument(docBld, MavenReaderType.createURL(uri, rootPath + v + "/" + "maven-metadata.xml"), lc, cctx, new NullProgressMonitor());
						v = getSnapshotVersion(doc, v);
					}
					catch(CoreException e)
					{
						logDecision(ResolverDecisionType.VERSION_REJECTED, v, e.getMessage());
						continue;
					}
				}

				IVersion version;
				if(versionDesignator == null)
					version = MavenComponentType.createVersion(v);
				else
				{
					try
					{
						version = versionDesignator.getVersion().getType().fromString(v);
						if(!versionDesignator.designates(version))
							continue;
					}
					catch(VersionSyntaxException e)
					{
						continue;
					}
				}

				pbld.setLength(0);
				pbld.append(versionStr);
				pbld.append('/');
				pbld.append(getMapEntry().getArtifactId());
				pbld.append('-');
				pbld.append(version);
				pbld.append(".jar");
				versions.add(new VersionMatch(version, null, -1, null, pbld.toString()));
			}
			return versions;
		}
		catch(ParserConfigurationException e)
		{
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			monitor.done();
		}
	}

	public static List<String> getVersions(Document doc)
	{
		List<String> versionList = null;

		Element versioningElement = getElement(doc, "versioning");
		if(versioningElement != null)
		{
			Element versionsElement = getElement(versioningElement, "versions");
			if(versionsElement != null)
			{
				NodeList versions = versionsElement.getElementsByTagName("version");
				int top = versions.getLength();
				for (int i = 0; i < top; i++)
				{
					if(versionList == null)
						versionList = new ArrayList<String>();
					versionList.add(versions.item(i).getTextContent());
				}
			}
		}
		return versionList == null ? Collections.<String>emptyList() : versionList;
	}

	public static IPath getDefaultLocalRepoPath()
	{
		return new Path(System.getProperty("user.home")).append(".m2").append("repository");
	}

	public static String getSnapshotVersion(Document doc, String version) throws CoreException
	{
		Element versioningElement = getElement(doc, "versioning");
		if(versioningElement != null)
		{
			Element snapshotElement = getElement(versioningElement, "snapshot");
			if(snapshotElement != null)
			{
				Element ts = getElement(snapshotElement, "timestamp");
				Element buildNum = getElement(snapshotElement, "buildNumber");
				if(ts != null && buildNum != null)
					return version.substring(0, version.length() - 8) + ts.getTextContent() + '-' + buildNum.getTextContent();
			}
		}
		throw BuckminsterException.fromMessage("Unable to read snapshot metadata");
	}

	private static Element getElement(Document doc, String elementName)
	{
		return getElement(doc.getElementsByTagName(elementName));
	}

	private static Element getElement(Element elem, String elementName)
	{
		return elem == null ? null : getElement(elem.getElementsByTagName(elementName));
	}

	private static Element getElement(NodeList nodeList)
	{
		return (nodeList != null && nodeList.getLength() > 0)
			? (Element)nodeList.item(0)
			: null;
	}

	public static Document getMetadataDocument(DocumentBuilder docBld, URL url, LocalCache cache, IConnectContext cctx, IProgressMonitor monitor) throws CoreException
	{
		InputStream input = null;
		try
		{
			input = DownloadManager.read(url, cctx);
			return docBld.parse(input);
		}
		catch(CoreException e)
		{
			docBld.reset();
			throw e;
		}
		catch(Exception e)
		{
			docBld.reset();
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			IOUtils.close(input);
		}
	}
}
