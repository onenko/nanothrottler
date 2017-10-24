package net.nenko.nanothrottler;

public interface NanoThrottler {
	/**
	 * allow() - returns when action is possible according to throttled/shaped speed
	 */
	void allow();
}
