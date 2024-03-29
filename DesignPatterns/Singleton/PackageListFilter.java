package org.acm.seguin.ide.common;

import org.acm.seguin.summary.PackageSummary;

public abstract class PackageListFilter {
	private static PackageListFilter singleton;

	public abstract boolean isIncluded(PackageSummary summary);

	public static void setSingleton(PackageListFilter value)
	{
		singleton = value;
	}

	public static PackageListFilter get()
	{
		if (singleton == null) {
			singleton = new DefaultPackageListFilter();
		}
		return singleton;
	}
}
