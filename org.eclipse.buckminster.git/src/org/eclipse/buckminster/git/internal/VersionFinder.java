package org.eclipse.buckminster.git.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.version.AbstractSCCSVersionFinder;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.treewalk.TreeWalk;

public class VersionFinder extends AbstractSCCSVersionFinder {
	private RepositoryAccess repoAccess;

	public VersionFinder(Provider provider, IComponentType ctype, NodeQuery query) throws CoreException {
		super(provider, ctype, query);
		@SuppressWarnings("unchecked")
		Map<String,String> props = (Map<String,String>)provider.getProperties(query.getProperties());
		repoAccess = new RepositoryAccess(getProvider().getURI(props), props);
	}

	@Override
	public synchronized void close() {
		if (repoAccess != null) {
			repoAccess.close();
			repoAccess = null;
		}
	}

	@Override
	protected boolean checkComponentExistence(VersionMatch versionMatch, IProgressMonitor monitor) throws CoreException {
		return repoAccess.getComponentTree(versionMatch, monitor) != null;
	}

	@Override
	protected List<RevisionEntry> getBranchesOrTags(boolean branches, IProgressMonitor monitor) throws CoreException {
		try {
			ArrayList<RevisionEntry> branchesOrTags = new ArrayList<RevisionEntry>();
			Repository repo = repoAccess.getRepository(monitor);
			String component = repoAccess.getComponent();
			System.out.println(repo.getBranch());
			for (Ref ref : repo.getAllRefs().values()) {

				String name = ref.getName();
				int lastSlash = name.lastIndexOf('/');
				if (lastSlash < 0)
					continue;

				RevObject obj = repoAccess.getRevWalk().parseAny(ref.getObjectId());
				if (branches) {
					if(!(obj instanceof RevCommit))
						continue;

					// Last part of name is the branch
					String branch = name.substring(lastSlash + 1);
					if (Constants.MASTER.equals(branch))
						continue;

					RevCommit c = (RevCommit) obj;
					if (!(component == null || TreeWalk.forPath(repo, component, c.getTree()) != null))
						continue;

					// repoAccess.inspectRef(ref);

					// TODO: RevisionEntry should hold abbreviated object id
					// instead of long revision
					branchesOrTags.add(new RevisionEntry(branch, c.getAuthorIdent().getWhen(), 0L));
				} else {
					if (!(obj instanceof RevTag))
						continue;

					RevTag tag = (RevTag) obj;
					if (component != null) {
						// Check that the component exists in the associated
						// Commit
						do {
							obj = ((RevTag) obj).getObject();
						} while (obj instanceof RevTag);

						if (!(obj instanceof RevCommit && TreeWalk.forPath(repo, component, ((RevCommit)obj).getTree()) != null))
							continue;
					}
					branchesOrTags.add(new RevisionEntry(tag.getTagName(), tag.getTaggerIdent().getWhen(), 0L));
				}
			}
			return branchesOrTags;
		} catch (Exception e) {
			throw BuckminsterException.wrap(e);
		}
	}

	@Override
	protected RevisionEntry getTrunk(IProgressMonitor monitor) throws CoreException {
		try {
			Repository repo = repoAccess.getRepository(monitor);
			String component = repoAccess.getComponent();
			System.out.println(repo.getBranch());
			for (Ref ref : repo.getAllRefs().values()) {

				String name = ref.getName();
				int lastSlash = name.lastIndexOf('/');
				if (lastSlash < 0)
					continue;

				RevObject obj = repoAccess.getRevWalk().parseAny(ref.getObjectId());
				if (!(obj instanceof RevCommit))
					continue;

				// Last part of name is the branch
				String branch = name.substring(lastSlash + 1);
				if (!Constants.MASTER.equals(branch))
					continue;

				RevCommit c = (RevCommit) obj;
				if (!(component == null || TreeWalk.forPath(repo, component, c.getTree()) != null))
					continue;

				// repoAccess.inspectRef(ref);

				// TODO: RevisionEntry should hold abbreviated object id instead
				// of long revision
				return new RevisionEntry(component, c.getAuthorIdent().getWhen(), 0L);
			}
			return null;
		} catch (Exception e) {
			throw BuckminsterException.wrap(e);
		}
	}

	String getGitBranch(String branchName) {
		if (branchName == null)
			return Constants.R_HEADS + Constants.MASTER;
		return Constants.R_HEADS + branchName;
	}
}
