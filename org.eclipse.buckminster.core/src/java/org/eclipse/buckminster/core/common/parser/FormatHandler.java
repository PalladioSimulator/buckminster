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

import org.eclipse.buckminster.core.common.model.Format;
import org.eclipse.buckminster.sax.AbstractHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FormatHandler extends ValueFilterHandler {
	public FormatHandler(AbstractHandler parent) {
		super(parent);
	}

	@Override
	public void handleAttributes(Attributes attrs) throws SAXException {
		this.setValueHolder(new Format(this.getStringValue(attrs, "format"))); //$NON-NLS-1$
	}
}
