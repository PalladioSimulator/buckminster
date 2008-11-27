/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.metadata;

import java.util.UUID;

/**
 * @author Thomas Hallgren
 */
public class TimestampedKey
{
	private final UUID m_uuid;

	private final long m_creationTime;

	public TimestampedKey(UUID uuid, long creationTime)
	{
		m_uuid = uuid;
		m_creationTime = creationTime;
	}

	public long getCreationTime()
	{
		return m_creationTime;
	}

	public UUID getKey()
	{
		return m_uuid;
	}
}
