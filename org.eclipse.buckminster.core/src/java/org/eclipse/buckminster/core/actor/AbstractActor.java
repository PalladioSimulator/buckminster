/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.KeyConstants;
import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.cspec.PathGroup;
import org.eclipse.buckminster.core.cspec.model.Action;
import org.eclipse.buckminster.core.cspec.model.ActionArtifact;
import org.eclipse.buckminster.core.cspec.model.Attribute;
import org.eclipse.buckminster.core.internal.actor.ActorFactory;
import org.eclipse.buckminster.core.metadata.model.IModelCache;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;

/**
 * @author kolwing
 * 
 */
public abstract class AbstractActor implements IActor, IExecutableExtension {
	private static InheritableThreadLocal<Stack<IActionContext>> actionContext = new InheritableThreadLocal<Stack<IActionContext>>();

	public static IActionContext getActiveContext() {
		Stack<IActionContext> ctxStack = actionContext.get();
		if (ctxStack == null || ctxStack.isEmpty())
			throw new IllegalStateException(Messages.No_active_IActionContext);
		return ctxStack.peek();
	}

	public static boolean getBooleanProperty(Map<String, String> properties, String key, boolean dflt) {
		String propVal = properties.get(key);
		if (propVal == null)
			return dflt;
		if ("true".equalsIgnoreCase(propVal)) //$NON-NLS-1$
			return true;
		if ("false".equalsIgnoreCase(propVal)) //$NON-NLS-1$
			return false;
		throw new IllegalArgumentException(NLS.bind(Messages._0_not_valid_value_of_boolean_property, propVal));
	}

	public static List<IPath> getPathList(IActionContext ctx, Attribute attr, boolean atBase) throws CoreException {
		PathGroup[] groups = attr.getPathGroups(ctx, null);
		if (groups.length == 0)
			return Collections.emptyList();

		ArrayList<IPath> pathList = new ArrayList<IPath>();
		for (PathGroup pathGroup : groups) {
			IPath base = pathGroup.getBase();
			if (atBase) {
				pathList.add(base);
				continue;
			}

			for (IPath path : pathGroup.getPaths())
				pathList.add(base.append(path));
		}
		return pathList;
	}

	public static IPath getProductPath(IActionContext ctx, String productAlias, boolean atBase) throws CoreException {
		Action action = ctx.getAction();
		for (ActionArtifact product : action.getProductArtifacts())
			if (productAlias.equals(product.getAlias()))
				return getSingleAttributePath(ctx, product, atBase);
		throw BuckminsterException.fromMessage(NLS.bind(Messages.unable_to_find_product_0_for_action_1, productAlias, action.getQualifiedName()));
	}

	public static IPath getSingleAttributePath(IActionContext ctx, Attribute attr, boolean atBase) throws CoreException {
		IPath productPath = null;
		for (PathGroup pathGroup : attr.getPathGroups(ctx, null)) {
			IPath pp = null;
			if (atBase)
				pp = pathGroup.getBase();
			else {
				IPath[] paths = pathGroup.getPaths();
				if (paths.length == 1)
					pp = pathGroup.getBase().append(paths[0]);
				else if (paths.length == 0)
					pp = pathGroup.getBase();
			}
			if (pp == null) {
				productPath = null;
				break;
			}

			if (productPath == null)
				productPath = pp;
			else if (!productPath.equals(pp)) {
				productPath = null;
				break;
			}
		}

		if (productPath == null)
			throw BuckminsterException.fromMessage(NLS.bind(Messages.product_for_action_0_must_be_single_path, attr.getQualifiedName()));
		return productPath;
	}

	private String name;

	private String id;

	private final Logger logger;

	private Action action;

	public AbstractActor() {
		logger = CorePlugin.getLogger();
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void init(Action act) throws CoreException {
		action = act;
		if (logger.isDebugEnabled()) {
			StringBuilder bld = new StringBuilder();
			bld.append("init actor: "); //$NON-NLS-1$
			bld.append(this.getId());
			bld.append('[');
			action.toString(bld);
			bld.append(']');
			loggableProps(bld, action.getActorProperties());
			logger.debug(bld.toString());
		}
		this.internalInit();
	}

	@Override
	public boolean isUpToDate(Action act, IModelCache ctx, long prerequisiteAge, long oldestTarget) throws CoreException {
		// Return true if the oldest target is younger or the same age as the
		// youngest prerequisite.
		return oldestTarget >= prerequisiteAge;
	}

	@Override
	public final synchronized IStatus perform(IActionContext ctx, IProgressMonitor monitor) throws CoreException {
		// the stored context is per perform only; if referenced otherwise we
		// ensure that
		// null is received, triggering a NPE
		//
		Map<String, ? extends Object> props = ctx.getProperties();
		Stack<IActionContext> ctxStack = actionContext.get();
		if (ctxStack == null) {
			ctxStack = new Stack<IActionContext>();
			actionContext.set(ctxStack);
		}
		ctxStack.push(ctx);
		try {
			Action act = ctx.getAction();
			boolean quiet = ctx.isQuiet();
			boolean isDebug = logger.isDebugEnabled();
			if (isDebug || !quiet) {
				StringBuilder bld = new StringBuilder();
				bld.append("[start "); //$NON-NLS-1$
				act.toString(bld);
				bld.append(']');
				if (isDebug) {
					loggableActionInfo(bld);
					loggableProps(bld, props);
				}
				logger.info(bld.toString());
			}

			ctx.getGlobalContext().scheduleRemoval(new Path(props.get(KeyConstants.ACTION_TEMP).toString()));
			IStatus status = internalPerform(ctx, monitor);
			if (isDebug || !quiet) {
				StringBuilder bld = new StringBuilder();
				bld.append("[end "); //$NON-NLS-1$
				act.toString(bld);
				bld.append(']');
				logger.info(bld.toString());
			}
			return status;
		} catch (Throwable t) {
			throw BuckminsterException.wrap(t);
		} finally {
			ctxStack.pop();
			if (ctxStack.isEmpty())
				actionContext.set(null);
		}
	}

	@Override
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		name = config.getAttribute(ActorFactory.ACTOR_NAME_ATTR);
		id = config.getAttribute(ActorFactory.ACTOR_ID_ATTR);
	}

	protected final String getActorProperty(String key) {
		return action.getActorProperties().get(key);
	}

	protected final Logger getLogger() {
		return logger;
	}

	protected void internalInit() throws CoreException {
		// noop
	}

	abstract protected IStatus internalPerform(IActionContext ctx, IProgressMonitor monitor) throws CoreException;

	private void loggableActionInfo(StringBuilder sb) {
		if (action.getPrerequisitesAlias() != null) {
			sb.append("\n  "); //$NON-NLS-1$
			sb.append(Messages.Prerequisite_alias);
			sb.append(action.getPrerequisitesAlias());
		}
		if (action.getPrerequisiteRebase() != null) {
			sb.append("\n  "); //$NON-NLS-1$
			sb.append(Messages.Prerequisite_rebase);
			sb.append(action.getPrerequisiteRebase().toOSString());
		}
		if (action.getProductAlias() != null) {
			sb.append("\n  "); //$NON-NLS-1$
			sb.append(Messages.Product_alias);
			sb.append(action.getProductAlias());
		}
		if (action.getProductBase() != null) {
			sb.append("\n  "); //$NON-NLS-1$
			sb.append(Messages.Product_base);
			sb.append(action.getProductBase().toOSString());
		}
	}

	private void loggableProps(StringBuilder sb, Map<String, ? extends Object> props) {
		Properties sysProps = System.getProperties();
		for (Map.Entry<String, ? extends Object> entry : props.entrySet()) {
			if (sysProps.getProperty(entry.getKey()) == null) {
				sb.append("\n  "); //$NON-NLS-1$
				sb.append(entry.getKey()).append('=').append(entry.getValue());
			}
		}
	}
}
