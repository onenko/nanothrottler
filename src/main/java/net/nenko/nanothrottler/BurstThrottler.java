package net.nenko.nanothrottler;

public class BurstThrottler implements NanoThrottler {

	/**
	 * BurstThrottler() creates new throttler, which allows 'actionsInPeriod' actions during latest 'periodMs'
	 * @param periodMs - time in milliseconds
	 * @param actionsInPeriod - counter of actions
	 *
	 * For example, BurstThrottler(1000*60, 300) creates throttler, which allows 300 actions during 1 minute
	 * (i. e. 300 requests per minute to glassy legacy service).
	 *
	 * NOTE1: actions are represented by timestamp when they occur
	 *        "register action" == save timestamp into throttler buffer
     *
	 * NOTE2: this throttler distributes actions UNEVENLY over time. Usually it allows maximum number of
	 *        actions to happen, then forces delay until time frame for these actions finishes 
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
		this.bufferSize = actionsInPeriod + 1;	// one cell is always empty
		timestamps = new long[this.bufferSize];
		head = tail = 0;
	}

	@Override
	public void allow() {
		// Remove outdated actions
		long outdatedMs = System.currentTimeMillis() - periodMs;
		while(tail != head && timestamps[tail] < outdatedMs) {
			tail = (tail + 1) % bufferSize;
		}

		// Check if buffer is full
		if((head + 1) % bufferSize == tail) {
			// wait until at least 1 action becomes old and delete it
			long relaxMs = timestamps[tail] + periodMs - System.currentTimeMillis();
			try {
				Thread.sleep(relaxMs + 1);		// avoid 1 extra action to fit exactly in 'periodMs'
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tail = (tail + 1) % bufferSize;
		}

		// Register new action
		timestamps[head] = System.currentTimeMillis();	// record new action
		head = (head + 1) % bufferSize;
	} 


	private int periodMs;
	private int bufferSize;

	// Cyclic (round) buffer with time stamps of all recent actions
	private long[] timestamps;

	// Indices in buffer
	private int head;	// available empty cell
	private int tail;	// last used cell
	// Invariant: head in [0 .. bufferSize [
	// Invariant: tail in [0 .. bufferSize [
	// head == tail for empty buffer

}
