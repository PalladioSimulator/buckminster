/*******************************************************************************
 * Copyright (c) 2004, 2005
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.common.parser;

import org.eclipse.buckminster.core.common.model.ValueHolder;
import org.eclipse.buckminster.core.parser.ExtensionAwareHandler;
import org.eclipse.buckminster.sax.AbstractHandler;

public abstract class ValueHandler extends ExtensionAwareHandler {
	private ValueHolder<String> valueHolder;

	public ValueHandler(AbstractHandler parent) {
		super(parent);
	}

	public final ValueHolder<String> getValueHolder() {
		return valueHolder;
	}

	protected final void setValueHolder(ValueHolder<String> valueHolder) {
		this.valueHolder = valueHolder;
	}
}
