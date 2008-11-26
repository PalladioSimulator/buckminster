/*****************************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/

package org.eclipse.buckminster.remote;

import org.eclipse.buckminster.core.helpers.LocalizedException;

/**
 * @author Filip Hrbek
 */
public class NoSuchProviderException extends LocalizedException
{
	private static final long serialVersionUID = 7686800201456353564L;

	public NoSuchProviderException(String message)
	{
		super(message);
	}
}
