package util;

import org.lwjgl.Sys;

public final class Utils {
	/**
	 * Gets the current time in milliseconds, using LWJGLs high resolution
	 * timer.
	 * 
	 * @return The current time in milliseconds.
	 */
	public static long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
