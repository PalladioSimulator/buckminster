/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.internal.actor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.actor.IActor;
import org.eclipse.buckminster.core.actor.NoSuchActorException;
import org.eclipse.buckminster.core.cspec.IAction;
import org.eclipse.buckminster.core.cspec.WellknownActions;
import org.eclipse.buckminster.core.cspec.model.Action;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * @author kolwing
 * 
 */
public class ActorFactory {
	public static final String ACTOR_ID_ATTR = "ID"; //$NON-NLS-1$

	public static final String ACTOR_CLASS_ATTR = "class"; //$NON-NLS-1$

	public static final String ACTOR_NAME_ATTR = "name"; //$NON-NLS-1$

	public static final String INTERNAL_ACTION_ATTR = "action"; //$NON-NLS-1$

	public static final String INTERNAL_ACTOR_ATTR = "actor"; //$NON-NLS-1$

	private static ActorFactory instance = null;

	public synchronized static ActorFactory getInstance() {
		if (instance == null)
			instance = new ActorFactory();
		return instance;
	}

	private final Map<String, String> fixedActionActorMappings;

	private final Map<IAction, IActor> liveActors = new HashMap<IAction, IActor>();

	// only the one instance created inside the class
	//
	private ActorFactory() {
		// fill the fixed mapping and as a nice touch, make it unmodifiable
		//
		Map<String, String> mappings = new HashMap<String, String>();
		mappings.put(WellknownActions.ECLIPSE.CLEAN.toString(), EclipseCleanActor.ID);
		mappings.put(WellknownActions.ECLIPSE.BUILD.toString(), EclipseBuildActor.ID);
		fixedActionActorMappings = Collections.unmodifiableMap(mappings);
	}

	public String findInternalActionActorName(String actionName) throws CoreException {
		// first try the fixed mappings
		//

		String aname = fixedActionActorMappings.get(actionName);
		if (aname != null)
			return aname;

		// not a fixed wellknown - check if someone has provided an extension
		// for it
		//
		for (IConfigurationElement elem : Platform.getExtensionRegistry().getConfigurationElementsFor(CorePlugin.INTERNAL_ACTORS_POINT))
			if (elem.getAttribute(INTERNAL_ACTION_ATTR).equals(actionName))
				return elem.getAttribute(INTERNAL_ACTOR_ATTR);

		// sorry, it's neither here nor there...
		//
		return null;
	}

	public synchronized IActor getActor(IAction action) throws CoreException {
		// try to find an existing actor for the component/action...
		//
		IActor actor = liveActors.get(action);
		if (actor != null)
			return actor;

		// if we're here it means that there was no actor (or maybe not
		// even a listing for the component in question)
		// either way, the map we need to insert a newly created actor is
		// present
		//

		String actorName = action.getActorName();
		actor = this.internalCreateActor(actorName);
		if (actor == null)
			throw new NoSuchActorException(actorName, action.toString());

		// ensure the new actor is done right after creation
		//
		actor.init((Action) action);

		// now we can stuff the actor into the map(s)
		//
		liveActors.put(action, actor);
		return actor;
	}

	private IActor internalCreateActor(String actorName) throws CoreException {
		for (IConfigurationElement elem : Platform.getExtensionRegistry().getConfigurationElementsFor(CorePlugin.ACTORS_POINT))
			if (elem.getAttribute(ACTOR_ID_ATTR).equals(actorName))
				return (IActor) elem.createExecutableExtension(ACTOR_CLASS_ATTR);
		return null;
	}
}
