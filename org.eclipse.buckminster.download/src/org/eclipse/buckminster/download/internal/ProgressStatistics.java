/*******************************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text or
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.download.internal;

import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.buckminster.download.Messages;
import org.eclipse.osgi.util.NLS;

public class ProgressStatistics
{
	private static final int DEFAULT_REPORT_INTERVAL = 1000;

	private static final int SPEED_INTERVAL = 5000;

	private static final int SPEED_RESOLUTION = 1000;

	private static String convert(long amount)
	{
		if(amount < 1024)
			return String.format(Locale.US, "%dB", Long.valueOf(amount)); //$NON-NLS-1$

		if(amount < 1024 * 1024)
			return String.format(Locale.US, "%.2fkB", Double.valueOf(((double)amount) / 1024)); //$NON-NLS-1$

		return String.format(Locale.US, "%.2fMB", Double.valueOf(((double)amount) / (1024 * 1024))); //$NON-NLS-1$
	}

	private final String m_fileName;

	private final long m_total;

	private final long m_startTime;

	private long m_current;

	private long m_lastReportTime;

	private int m_reportInterval;

	private SortedMap<Long, Long> m_recentSpeedMap;

	private long m_recentSpeedMapKey;

	public ProgressStatistics(String fileName, long total)
	{
		m_startTime = System.currentTimeMillis();
		m_fileName = fileName;

		m_total = total;

		m_current = 0;
		m_lastReportTime = 0;
		m_reportInterval = DEFAULT_REPORT_INTERVAL;
		m_recentSpeedMap = new TreeMap<Long, Long>();
		m_recentSpeedMapKey = 0L;
	}

	public long getAverageSpeed()
	{
		long dur = getDuration();

		if(dur >= 1000)
			return m_current / (dur / 1000);

		return 0L;
	}

	public long getDuration()
	{
		return System.currentTimeMillis() - m_startTime;
	}

	public double getPercentage()
	{
		if(m_total > 0)
			return ((double)m_current) / ((double)m_total);

		return 0.0;
	}

	synchronized public long getRecentSpeed()
	{
		removeObsoleteRecentSpeedData(getDuration() / SPEED_RESOLUTION);
		long dur = 0L;
		long amount = 0L;
		SortedMap<Long, Long> relevantData = m_recentSpeedMap.headMap(Long.valueOf(m_recentSpeedMapKey));

		for(Entry<Long, Long> entry : relevantData.entrySet())
		{
			dur += SPEED_RESOLUTION;
			amount += entry.getValue().longValue();
		}

		if(dur >= 1000)
			return amount / (dur / 1000);

		return 0L;
	}

	public int getReportInterval()
	{
		return m_reportInterval;
	}

	public long getTotal()
	{
		return m_total;
	}

	public void increase(long inc)
	{
		registerRecentSpeed(getDuration() / SPEED_RESOLUTION, inc);
		m_current += inc;
	}

	public synchronized String report()
	{
		return m_total != -1
				? NLS.bind(Messages.fetching_0_1_of_2_at_3, new String[] { m_fileName, convert(m_current),
						convert(m_total), convert(getRecentSpeed()) })
				: NLS.bind(Messages.fetching_0_1_at_2, new String[] { m_fileName, convert(m_current),
						convert(getRecentSpeed()) });
	}

	public void setReportInterval(int reportInterval)
	{
		m_reportInterval = reportInterval;
	}

	public boolean shouldReport()
	{
		long currentTime = System.currentTimeMillis();
		if(m_lastReportTime == 0 || currentTime - m_lastReportTime >= m_reportInterval)
		{
			m_lastReportTime = currentTime;
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return report();
	}

	synchronized private void registerRecentSpeed(long key, long inc)
	{
		Long keyL = Long.valueOf(key);
		Long currentValueL = m_recentSpeedMap.get(keyL);
		long currentValue = 0L;
		if(currentValueL != null)
			currentValue = currentValueL.longValue();

		m_recentSpeedMap.put(keyL, Long.valueOf(inc + currentValue));

		if(m_recentSpeedMapKey != key)
		{
			m_recentSpeedMapKey = key;
			removeObsoleteRecentSpeedData(key);
		}
	}

	synchronized private void removeObsoleteRecentSpeedData(long lastKey)
	{
		long threshold = lastKey - SPEED_INTERVAL / SPEED_RESOLUTION;
		m_recentSpeedMap.headMap(Long.valueOf(threshold)).clear();
	}
}
