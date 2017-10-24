package net.nenko.nanothrottler;

public class SmallThrottler implements NanoThrottler {

	/**
	 * SmallThrottler() creates new throttler, which allows 'actionsInPeriod' actions during latest 'periodMs'
	 *
	 * SmallThrottler repeats the BurstThrottler behavior, except:
	 *
	 *   - 50% less memory requirements
	 *   - may eventually fail on following conditions: uninterrupted work during 200 days with never emptied buffer 
	 *
	 * Above said, SmallThrottler is good choice when several throttled action channels exist on terse memory requirements
	 * and the application is buggy enough to endure 200 days without restart :).
	 *
	 * NOTE1: once again, 200 days with NEVER empty buffer; otherwise it works seamless as long as needed
	 */
	public SmallThrottler(int periodMs, int actionsInPeriod) {
		if(actionsInPeriod < 2 || actionsInPeriod > 1000000) {
			throw new IllegalArgumentException("actionsInPeriod should be in [2...1000000] range");
		}
		if(periodMs < actionsInPeriod) {
			throw new IllegalArgumentException("periodMs should not be less that number of actions milliseconds due to resolution of timer");
		}
		if(periodMs < 0) {
			throw new IllegalArgumentException("Negative periodMs ?");
		}
		this.periodMs = periodMs;
		this.bufferSize = actionsInPeriod + 1;	// one cell is always empty
		timestamps = new int[this.bufferSize];
		baseMs = System.currentTimeMillis();
		head = tail = 0;
	}

	@Override
	public void allow() {
		// Remove outdated actions
		int outdatedMs = (int)(System.currentTimeMillis() - periodMs - baseMs);
		while(tail != head && timestamps[tail] < outdatedMs) {
			tail = (tail + 1) % bufferSize;
		}

		// Check if buffer is full
		if((head + 1) % bufferSize == tail) {
			// wait until at least 1 action becomes old and delete it
			long relaxMs = timestamps[tail] + baseMs + periodMs - System.currentTimeMillis();
			try {
				Thread.sleep(relaxMs + 1);		// avoid 1 extra action to fit exactly in 'periodMs'
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tail = (tail + 1) % bufferSize;
		}

		// On empty buffer reinit baseMs for extra life
		if(head == tail) {
			baseMs = System.currentTimeMillis();
		}

		// Register new action
		timestamps[head] = (int)(System.currentTimeMillis() - baseMs);	// record new action
		head = (head + 1) % bufferSize;
	} 


	private int periodMs;
	private int bufferSize;

	// Cyclic (round) buffer with time stamps of all recent actions
	private int[] timestamps;
	private long baseMs;	// base time stamp, buffer contains increments

	// Indices in buffer
	private int head;	// available empty cell
	private int tail;	// last used cell
	// Invariant: head in [0 .. bufferSize [
	// Invariant: tail in [0 .. bufferSize [
	// head == tail for empty buffer

}
