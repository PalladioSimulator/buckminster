package org.eclipse.buckminster.model.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.p2.metadata.VersionFormat;
import org.eclipse.equinox.p2.metadata.IVersionFormat;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionFormatException;
import org.eclipse.equinox.p2.metadata.VersionRange;

@SuppressWarnings("restriction")
public class VersionHelper {
	private static final Version sampleOSGiVersion;

	static final String VERSION_TYPES_POINT = Buckminster.NAMESPACE + ".core" + ".versionTypes"; //$NON-NLS-1$

	private static final HashMap<String, VersionType> knownTypes = new HashMap<String, VersionType>();

	private static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd'.'HHmmss"); //$NON-NLS-1$

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //$NON-NLS-1$

	static {
		try {
			sampleOSGiVersion = Version.parseVersion("1.0.0"); //$NON-NLS-1$
			IVersionFormat osgiFormat = sampleOSGiVersion.getFormat();
			VersionType osgiType = new VersionType(osgiFormat, VersionType.OSGI);

			String osgiFmtString = osgiFormat.toString();
			osgiFmtString = osgiFmtString.substring(7, osgiFmtString.length() - 1);
			knownTypes.put(osgiFmtString, osgiType);

			if (!osgiFmtString.equals(VersionFormat.OSGI_FORMAT_STRING))
				knownTypes.put(VersionFormat.OSGI_FORMAT_STRING, osgiType);

			IExtensionRegistry exReg = Platform.getExtensionRegistry();
			IConfigurationElement[] elems = exReg.getConfigurationElementsFor(VERSION_TYPES_POINT);
			int idx = elems.length;
			elements: while (--idx >= 0) {
				IConfigurationElement elem = elems[idx];
				String format = elem.getAttribute("format"); //$NON-NLS-1$
				String id = elem.getAttribute("id"); //$NON-NLS-1$
				VersionType vt = VersionType.OSGI.equals(id) ? osgiType : knownTypes.get(format);
				if (vt != null) {
					String[] labels = vt.getLabels();
					int top = labels.length;
					for (int ldx = 0; ldx < top; ++ldx)
						if (labels[ldx].equals(id))
							continue elements;

					String[] newLabels = new String[top + 1];
					System.arraycopy(labels, 0, newLabels, 0, top);
					newLabels[top] = id;
					vt = new VersionType(vt.getFormat(), newLabels);
				} else {
					vt = new VersionType(Version.compile(format), id);
				}
				knownTypes.put(format, vt);
			}

		} catch (VersionFormatException e) {
			throw new ExceptionInInitializerError(e);
		}

		TimeZone utc = TimeZone.getTimeZone("UTC"); //$NON-NLS-1$
		timestampFormat.setTimeZone(utc);
		dateFormat.setTimeZone(utc);
	}

	public static VersionRange createRange(IVersionFormat versionFormat, String rangeString) {
		if (rangeString == null)
			return null;

		if (versionFormat == null || versionFormat.equals(getOSGiFormat()))
			return new VersionRange(rangeString);

		StringBuffer bld = new StringBuffer();
		versionFormat.toString(bld);
		bld.append(':');
		bld.append(rangeString);
		return new VersionRange(bld.toString());
	}

	public static VersionRange createRange(String versionTypeLabel, String rangeString) throws MissingVersionTypeException {
		return createRange(getVersionType(versionTypeLabel), rangeString);
	}

	public static VersionRange createRange(VersionType versionType, String rangeString) {
		return createRange(versionType == null ? (IVersionFormat) null : versionType.getFormat(), rangeString);
	}

	public static Version createVersion(IVersionFormat versionFormat, String versionString) {
		if (versionString == null)
			return null;

		versionString = Trivial.trim(versionString);
		if (versionString == null)
			return null;

		if (versionFormat == null)
			versionFormat = getOSGiFormat();

		return versionFormat.parse(versionString);
	}

	public static Version createVersion(String versionTypeLabel, String versionString) throws MissingVersionTypeException {
		return createVersion(getVersionType(versionTypeLabel), versionString);
	}

	public static Version createVersion(VersionType versionType, String versionString) {
		return createVersion(versionType == null ? null : versionType.getFormat(), versionString);
	}

	/**
	 * Returns <code>true</code> if <code>a</code> is equal to <code>b</code> in
	 * all but the version qualifier.
	 * 
	 * @param version
	 * @return true when the versions are equal irrespective of qualifiers
	 */
	public static boolean equalsUnqualified(Version a, Version b) {
		if (a == null)
			return b == null;

		if (b == null)
			return false;

		if (a.equals(b))
			return true;

		String aq = getQualifier(a);
		String bq = getQualifier(b);
		if (aq == null && bq == null)
			return false;

		String astr = getOriginal(a);
		if (aq != null)
			astr = astr.substring(0, astr.length() - (aq.length() + 1));

		String bstr = getOriginal(b);
		if (bq != null)
			bstr = bstr.substring(0, bstr.length() - (bq.length() + 1));

		IVersionFormat af = a.getFormat();
		if (af == null)
			af = getVersionType((IVersionFormat) null).getFormat();

		IVersionFormat bf = a.getFormat();
		if (bf == null)
			bf = getVersionType((IVersionFormat) null).getFormat();

		return af.parse(astr).equals(bf.parse(bstr));
	}

	public static VersionRange exactRange(org.osgi.framework.Version v) {
		return v == null ? null : exactRange(Version.parseVersion(v.toString()));
	}

	public static VersionRange exactRange(Version v) {
		return v == null ? null : new VersionRange(v, true, v, true);
	}

	public static String getHumanReadable(Version version) {
		if (version == null)
			return null;

		StringBuffer buf = new StringBuffer();
		getOriginal(version, buf);
		if (!getOSGiFormat().equals(version.getFormat())) {
			buf.append('#');
			buf.append(getVersionType(version).getId());
		}
		return buf.toString();
	}

	public static String getHumanReadable(VersionRange range) {
		if (range == null)
			return null;

		StringBuffer buf = new StringBuffer();
		if (getOSGiFormat().equals(range.getFormat()))
			range.toString(buf);
		else {
			if (range.getMaximum().equals(Version.MAX_VERSION) && range.getIncludeMaximum())
				//
				// Return a string that corresponds to the range >= minimum
				// version
				//
				getOriginal(range.getMinimum(), buf);
			else {
				buf.append(range.getIncludeMinimum() ? '[' : '(');
				getOriginal(range.getMinimum(), buf);
				buf.append(',');
				getOriginal(range.getMaximum(), buf);
				buf.append(range.getIncludeMaximum() ? ']' : ')');
			}
			buf.append('#');
			buf.append(getVersionType(range).getId());
		}
		return buf.toString();
	}

	public static List<VersionType> getKnownTypes() {
		ArrayList<VersionType> knownTypeList = new ArrayList<VersionType>(knownTypes.values());
		Collections.sort(knownTypeList, new Comparator<VersionType>() {

			@Override
			public int compare(VersionType o1, VersionType o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		return knownTypeList;
	}

	public static String getOriginal(Version version) {
		if (version == null)
			return null;

		StringBuffer sb = new StringBuffer();
		getOriginal(version, sb);
		return sb.toString();
	}

	public static void getOriginal(Version version, StringBuffer sb) {
		if (version == null)
			return;

		String orig = version.getOriginal();
		if (orig != null)
			sb.append(orig);
		else
			version.toString(sb);
	}

	public static String getOriginal(VersionRange range) {
		Version min = range.getMinimum();
		Version max = range.getMaximum();
		String minOrig = min.getOriginal();
		if (minOrig == null) {
			if (!min.equals(Version.emptyVersion))
				return null;
			minOrig = "0.0.0";
		}

		if (max.equals(Version.MAX_VERSION) && range.getIncludeMaximum())
			// Open ended
			return minOrig;

		String maxOrig = max.getOriginal();
		if (maxOrig == null)
			return null;

		StringBuilder bld = new StringBuilder();
		bld.append(range.getIncludeMinimum() ? '[' : '(');
		bld.append(minOrig);
		bld.append(',');
		bld.append(maxOrig);
		bld.append(range.getIncludeMaximum() ? ']' : ')');
		return bld.toString();
	}

	public static IVersionFormat getOSGiFormat() {
		return sampleOSGiVersion.getFormat();
	}

	public static String getQualifier(Version version) {
		if (version == null || version.getSegmentCount() == 0)
			return null;

		Object last = version.getSegment(version.getSegmentCount() - 1);
		if (!(last instanceof String))
			return null;

		String qual = (String) last;
		return qual.length() > 0 ? qual : null;
	}

	public static VersionType getVersionType(IVersionFormat format) {
		if (format == null)
			try {
				return getVersionType(VersionType.RAW);
			} catch (MissingVersionTypeException e) {
				throw new RuntimeException("Unable to find version type extension for type: " + VersionType.RAW); //$NON-NLS-1$
			}

		String fmtString = format.toString();
		fmtString = fmtString.substring(7, fmtString.length() - 1);
		synchronized (knownTypes) {
			VersionType vt = knownTypes.get(fmtString);
			if (vt == null) {
				// Create a default type that uses the actual format string
				// as its label
				//
				vt = new VersionType(format, fmtString);
				knownTypes.put(fmtString, vt);
			}
			return vt;
		}
	}

	public static VersionType getVersionType(String id) throws MissingVersionTypeException {
		if (id == null)
			id = VersionType.OSGI;

		for (VersionType vt : knownTypes.values())
			for (String label : vt.getLabels())
				if (label.equals(id))
					return vt;

		throw new MissingVersionTypeException(id);
	}

	public static VersionType getVersionType(Version version) {
		return getVersionType(version.getFormat());
	}

	public static VersionType getVersionType(VersionRange range) {
		return getVersionType(range.getFormat());
	}

	public static VersionRange greaterOrEqualRange(Version version) {
		return version == null ? null : new VersionRange(version.toString());
	}

	public static Version parseVersion(String versionStr) {
		Version version = Version.parseVersion(versionStr);
		if (version.equals(Version.emptyVersion))
			version = null;
		return version;
	}

	public static Version replaceQualifier(Version v, String qualifier) {
		if (v == null)
			return null;

		String q = getQualifier(v);
		if (q == null)
			return v;

		IVersionFormat vf = v.getFormat();
		String vs = v.getOriginal();
		if (vf == null || vs == null || !vs.endsWith(q))
			return v;

		int lenWOQ = vs.length() - (q.length() + 1);
		char sep = vs.charAt(lenWOQ);
		vs = vs.substring(0, lenWOQ);
		if (qualifier != null && qualifier.length() > 0)
			vs = vs + sep + qualifier;
		return vf.parse(vs);
	}

	public static Object toTimestampString(Date timestamp) {
		return timestampFormat.format(timestamp);
	}
}
