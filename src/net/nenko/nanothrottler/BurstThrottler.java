package net.nenko.nanothrottler;

public class BurstThrottler implements NanoThrottler {

	/**
	 * BurstThrottler() creates new throttler, which allows 'actionsInPeriod' actions during latest 'periodMs'
	 * @param periodMs - time in milliseconds
	 * @param actionsInPeriod - counter of actions
	 *
	 * For example, BurstThrottler(1000*60, 300) creates throttler, which allows 300 actions during 1 minute
	 * (300 requests per minute to glassy legacy service).
	 *
	 * Note, that actions may be distributed UNevenly over time.
	 */
	public BurstThrottler(int periodMs, int actionsInPeriod) {
		if(actionsInPeriod < 2 || actionsInPeriod > 10000) {
			throw new IllegalArgumentException("actionsInPeriod should be in [2...10000] range");
		}
		if(periodMs < actionsInPeriod) {
			throw new IllegalArgumentException("periodMs should not be less that number of actions milliseconds due to resolution of timer");
		}
		if(periodMs < 0) {
			throw new IllegalArgumentException("Negative periodMs ?");
		}
		this.periodMs = periodMs;
		this.actionsInPeriod = actionsInPeriod;
		timestamps = new long[actionsInPeriod];
		head = tail = 0;
	}

	@Override
	public void allow() {
		// TODO Auto-generated method stub
		return;
	}

	private int periodMs;
	private int actionsInPeriod;

	// Cyclic buffer with timestamps of all recent actions
	private long[] timestamps;

	// Indices in timestamp
	// Invariant: head in [0 .. actionsInPeriod [
	// Invariant: tail in [0 .. actionsInPeriod [
	// head == tail for empty buffer
	private int head, tail;
}
