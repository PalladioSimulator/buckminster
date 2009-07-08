package org.eclipse.buckminster.aggregator.engine;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.buckminster.aggregator.Aggregator;
import org.eclipse.buckminster.aggregator.Configuration;
import org.eclipse.buckminster.aggregator.Contribution;
import org.eclipse.buckminster.aggregator.MappedRepository;
import org.eclipse.buckminster.aggregator.MappedUnit;
import org.eclipse.buckminster.aggregator.p2.InstallableUnit;
import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.director.Explanation;
import org.eclipse.equinox.internal.p2.director.Explanation.HardRequirement;
import org.eclipse.equinox.internal.p2.director.Explanation.MissingIU;
import org.eclipse.equinox.internal.p2.director.Explanation.Singleton;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.core.VersionRange;
import org.eclipse.equinox.internal.provisional.p2.director.IPlanner;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.director.RequestStatus;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.engine.InstallableUnitOperand;
import org.eclipse.equinox.internal.provisional.p2.engine.Operand;
import org.eclipse.equinox.internal.provisional.p2.engine.ProvisioningContext;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.internal.provisional.p2.metadata.IRequiredCapability;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.LatestIUVersionQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.CompositeQuery;
import org.eclipse.equinox.internal.provisional.p2.query.IQueryable;
import org.eclipse.equinox.internal.provisional.p2.query.MatchQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Query;

public class RepositoryVerifier extends BuilderPhase
{
	private static ProvisioningContext createContext(URI site)
	{
		URI[] repoLocations = new URI[] { site };
		ProvisioningContext context = new ProvisioningContext(repoLocations);
		context.setArtifactRepositories(repoLocations);
		return context;
	}

	@SuppressWarnings("unchecked")
	private static Set<Explanation> getExplanations(RequestStatus requestStatus)
	{
		return requestStatus.getExplanations();
	}

	private static IInstallableUnit[] getRootIUs(URI site, IProfile profile, String iuName, Version version,
			IProgressMonitor monitor) throws CoreException
	{
		Query query = new InstallableUnitQuery(iuName, new VersionRange(version, true, version, true));
		Collector roots = ProvisioningHelper.getInstallableUnits(site, new CompositeQuery(new Query[] { query,
				new LatestIUVersionQuery() }), new Collector(), monitor);

		if(roots.size() <= 0)
			roots = profile.query(query, roots, new NullProgressMonitor());

		if(roots.size() <= 0)
			throw BuckminsterException.fromMessage("Feature %s not found", iuName); //$NON-NLS-1$

		return (IInstallableUnit[])roots.toArray(IInstallableUnit.class);
	}

	public RepositoryVerifier(Builder builder)
	{
		super(builder);
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException
	{
		Logger log = Buckminster.getLogger();
		log.info("Starting planner verification"); //$NON-NLS-1$
		long now = System.currentTimeMillis();

		String profilePrefix = Builder.PROFILE_ID + '_';
		final HashSet<IInstallableUnit> unitsToAggregate = new HashSet<IInstallableUnit>();
		final HashSet<IInstallableUnit> trustedUnits = new HashSet<IInstallableUnit>();

		Builder builder = getBuilder();
		Aggregator aggregator = builder.getAggregator();
		List<Configuration> configs = aggregator.getConfigurations();
		MonitorUtils.begin(monitor, configs.size() * 100);
		Buckminster bucky = Buckminster.getDefault();
		IProfileRegistry profileRegistry = bucky.getService(IProfileRegistry.class);
		IPlanner planner = bucky.getService(IPlanner.class);
		boolean update = builder.isUpdate();
		URI repoLocation = builder.getGlobalRepoURI();
		try
		{
			for(Configuration config : configs)
			{
				String configName = config.getName();
				String profileId = profilePrefix + configName;
				log.info("Verifying config: %s", configName); //$NON-NLS-1$
				Map<String, String> props = new HashMap<String, String>();
				props.put(IProfile.PROP_FLAVOR, "tooling"); //$NON-NLS-1$
				props.put(IProfile.PROP_ENVIRONMENTS, config.getOSGiEnvironmentString());
				props.put(IProfile.PROP_INSTALL_FEATURES, "true");

				IProfile profile = null;
				if(update)
					profile = profileRegistry.getProfile(profileId);

				if(profile == null)
					profile = profileRegistry.addProfile(profileId, props);

				IInstallableUnit[] rootArr = getRootIUs(repoLocation, profile, Builder.ALL_CONTRIBUTED_CONTENT_FEATURE,
						Builder.ALL_CONTRIBUTED_CONTENT_VERSION, MonitorUtils.subMonitor(monitor, 10));

				// Add as root IU's to a request
				ProfileChangeRequest request = new ProfileChangeRequest(profile);
				for(IInstallableUnit rootIU : rootArr)
					request.setInstallableUnitProfileProperty(rootIU, IInstallableUnit.PROP_PROFILE_ROOT_IU,
							Boolean.TRUE.toString());
				request.addInstallableUnits(rootArr);
				ProvisioningPlan plan = planner.getProvisioningPlan(request, createContext(repoLocation),
						MonitorUtils.subMonitor(monitor, 90));

				IStatus status = plan.getStatus();
				if(status.getSeverity() == IStatus.ERROR)
				{
					sendEmails(plan.getRequestStatus());
					log.info("Done. Took %d ms", Long.valueOf(System.currentTimeMillis() - now)); //$NON-NLS-1$
					throw new CoreException(status);
				}

				Operand[] ops = plan.getOperands();
				for(Operand op : ops)
				{
					if(!(op instanceof InstallableUnitOperand))
						continue;

					InstallableUnitOperand iuOp = (InstallableUnitOperand)op;
					IInstallableUnit iu = iuOp.second();
					if(iu != null && !Builder.ALL_CONTRIBUTED_CONTENT_FEATURE.equals(iu.getId()))
						unitsToAggregate.add(iu);
				}
			}
		}
		catch(RuntimeException e)
		{
			throw BuckminsterException.wrap(e);
		}
		finally
		{
			MonitorUtils.done(monitor);
			bucky.ungetService(profileRegistry);
			bucky.ungetService(planner);
		}
		List<Contribution> contribs = aggregator.getContributions();
		for(Contribution contrib : contribs)
		{
			for(MappedRepository repo : contrib.getRepositories())
			{
				if(!repo.isMapEverything())
					continue;

				boolean mirrorArtifacts = repo.isMirrorArtifacts();
				for(InstallableUnit iu : repo.getMetadataRepository().getInstallableUnits())
				{
					if(builder.isTopLevelCategory(iu))
						//
						// Categories have special treatment so we don't add them here.
						//
						continue;

					if(mirrorArtifacts)
						unitsToAggregate.add(iu);
					else
						trustedUnits.add(iu);
				}
			}
		}

		log.info("Done. Took %d ms", Long.valueOf(System.currentTimeMillis() - now)); //$NON-NLS-1$
		log.info("Found %d units to mirror", Integer.valueOf(unitsToAggregate.size())); //$NON-NLS-1$

		boolean pruningLogged = false;

		for(Contribution contrib : contribs)
		{
			for(MappedRepository repo : contrib.getRepositories())
			{
				if(repo.isMirrorArtifacts())
					continue;

				if(!pruningLogged)
				{
					log.info(
							"Pruning mirror list from units contributed by referenced repositories", Integer.valueOf(unitsToAggregate.size())); //$NON-NLS-1$
					pruningLogged = true;
				}

				IQueryable trustedRepo = repo.getMetadataRepository();
				trustedRepo.query(new MatchQuery()
				{
					@Override
					public boolean isMatch(Object candidate)
					{
						if(unitsToAggregate.remove(candidate))
							trustedUnits.add((IInstallableUnit)candidate);
						return false;
					}
				}, new Collector(), null);
			}

		}
		if(pruningLogged)
			log.info("%d units remain after pruning", Integer.valueOf(unitsToAggregate.size())); //$NON-NLS-1$

		builder.setUnitsToAggregate(unitsToAggregate);
		builder.setTrustedUnits(trustedUnits);
	}

	private boolean addLeafmostContributions(Set<Explanation> explanations, Map<String, Contribution> contributions,
			IRequiredCapability prq)
	{
		boolean contribsFound = false;
		for(Explanation explanation : explanations)
		{
			if(explanation instanceof Singleton)
			{
				if(contribsFound)
					// All explicit contributions for Singletons are added at
					// top level. We just
					// want to find out if this Singleton is the leaf problem
					// here, not add anything
					continue;

				for(IInstallableUnit iu : ((Singleton)explanation).ius)
				{
					for(IProvidedCapability pc : iu.getProvidedCapabilities())
						if(pc.satisfies(prq))
						{
							// A singleton is always a leaf problem. Add
							// contributions
							// if we can find any
							Contribution contrib = findContribution(iu.getId());
							if(contrib == null)
								continue;
							contribsFound = true;
						}
				}
				continue;
			}

			IInstallableUnit iu;
			IRequiredCapability crq;
			if(explanation instanceof HardRequirement)
			{
				HardRequirement hrq = (HardRequirement)explanation;
				iu = hrq.iu;
				crq = hrq.req;
			}
			else if(explanation instanceof MissingIU)
			{
				MissingIU miu = (MissingIU)explanation;
				iu = miu.iu;
				crq = miu.req;
			}
			else
				continue;

			for(IProvidedCapability pc : iu.getProvidedCapabilities())
				if(pc.satisfies(prq))
				{
					// This IU would have fulfilled the failing request but it
					// has
					// apparent problems of its own.
					if(addLeafmostContributions(explanations, contributions, crq))
					{
						contribsFound = true;
						continue;
					}

					Contribution contrib = findContribution(iu, crq);
					if(contrib == null)
						continue;
					contributions.put(contrib.getLabel(), contrib);
					continue;
				}
		}
		return contribsFound;
	}

	private Contribution findContribution(IInstallableUnit iu, IRequiredCapability rq)
	{
		Contribution contrib = null;
		if(Builder.NAMESPACE_OSGI_BUNDLE.equals(rq.getNamespace())
				|| IInstallableUnit.NAMESPACE_IU_ID.equals(rq.getNamespace()))
			contrib = findContribution(rq.getName());

		if(contrib == null)
			// Not found, try the owner of the requirement
			contrib = findContribution(iu.getId());
		return contrib;
	}

	private Contribution findContribution(String componentId)
	{
		for(Contribution contrib : getBuilder().getAggregator().getContributions())
			for(MappedRepository repository : contrib.getRepositories())
				for(MappedUnit mu : repository.getUnits(true))
					if(componentId.equals(mu.getInstallableUnit().getId()))
						return contrib;
		return null;
	}

	private void sendEmails(RequestStatus requestStatus)
	{
		Builder builder = getBuilder();
		if(!builder.getAggregator().isSendmail())
			return;

		ArrayList<String> errors = new ArrayList<String>();
		Set<Explanation> explanations = getExplanations(requestStatus);
		Map<String, Contribution> contribs = new HashMap<String, Contribution>();
		for(Explanation explanation : explanations)
		{
			errors.add(explanation.toString());
			if(explanation instanceof Singleton)
			{
				// A singleton is always a leaf problem. Add contributions
				// if we can find any. They are all culprits
				for(IInstallableUnit iu : ((Singleton)explanation).ius)
				{
					Contribution contrib = findContribution(iu.getId());
					if(contrib == null)
						continue;
					contribs.put(contrib.getLabel(), contrib);
				}
				continue;
			}

			IInstallableUnit iu;
			IRequiredCapability crq;
			if(explanation instanceof HardRequirement)
			{
				HardRequirement hrq = (HardRequirement)explanation;
				iu = hrq.iu;
				crq = hrq.req;
			}
			else if(explanation instanceof MissingIU)
			{
				MissingIU miu = (MissingIU)explanation;
				iu = miu.iu;
				crq = miu.req;
			}
			else
				continue;

			// Find the leafmost contributions for the problem. We don't want to
			// blame
			// consuming contributors
			if(!addLeafmostContributions(explanations, contribs, crq))
			{
				Contribution contrib = findContribution(iu, crq);
				if(contrib == null)
					continue;
				contribs.put(contrib.getLabel(), contrib);
			}
		}
		if(contribs.isEmpty())
			builder.sendEmail(null, errors);
		else
		{
			for(Contribution contrib : contribs.values())
				builder.sendEmail(contrib, errors);
		}
	}
}