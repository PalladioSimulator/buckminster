/*******************************************************************************
 * Copyright (c) 2004, 2005
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.cmdline.parser;

import org.eclipse.buckminster.cmdline.Messages;

public class NoOptionNameException extends AbstractOptionParsingException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6869141594127060224L;

	public NoOptionNameException() {
		super(Messages.NoOptionNameException_Missing_option_name);
	}
}
