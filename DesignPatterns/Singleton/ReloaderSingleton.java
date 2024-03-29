package org.acm.seguin.uml.loader;

public class ReloaderSingleton {
	private static boolean isFirstTime = true;
	private static Reloader singleton;

	private ReloaderSingleton() {
	}

	public static void reload() {

		if (singleton != null) {
			singleton.reload();
		}
	}

	public static void register(Reloader init) {
		singleton = init;
	}
}
