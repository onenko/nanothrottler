package net.nenko.nanothrottler;

/**
 * GammaThrottler
 *
 * GammaThrottler tries more or less evenly distribute incoming actions over time frames
 */
public class GammaThrottler implements NanoThrottler {

	/**
	 * GammaThrottler() creates new throttler, which allows 'actionsInPeriod' actions during latest 'periodMs'
	 *
	 * @param periodMs - time in milliseconds
	 * @param actionsInPeriod - counter of actions
	 * @param gamma - parameter to control actions distribution curve (default 1.0)
	 */
	public GammaThrottler(int periodMs, int actionsInPeriod, double gamma) {
		if(actionsInPeriod < 2 || actionsInPeriod > 10000) {
			throw new IllegalArgumentException("actionsInPeriod should be in [2...10000] range");
		}
		if(periodMs < actionsInPeriod) {
			throw new IllegalArgumentException("periodMs should not be less that number of actions milliseconds due to resolution of timer");
		}
		if(periodMs < 0) {
			throw new IllegalArgumentException("Negative periodMs ?");
		}
		this.gamma = gamma;
		this.periodMs = periodMs;
		this.bufferSize = actionsInPeriod + 1;	// one cell is always empty
		timestamps = new long[this.bufferSize];
		head = tail = 0;
	}

	public GammaThrottler(int periodMs, int actionsInPeriod) {
		this(periodMs, actionsInPeriod, 1.0D);
	}

	@Override
	public void allow() {
		removeOutdatedActions();
		registerNewAction();
		return;
	}

	private void removeOutdatedActions() {
		long currentMs = System.currentTimeMillis();
		long outdatedMs = currentMs - periodMs;
		while(tail != head && timestamps[tail] < outdatedMs) {
			tail = (tail + 1) % bufferSize;
		}
	}

	private void registerNewAction() {

		System.out.println("registerNewAction:" + head + " " + tail);

		if((head + 1) % bufferSize == tail) {
			// round buffer is full
			System.out.println("registerNewAction: relax " + (timestamps[tail] + periodMs - System.currentTimeMillis() + 1));

			relax(timestamps[tail] + periodMs - System.currentTimeMillis());
			tail = (tail + 1) % bufferSize;
		} else {
			// round buffer is not empty
			if(tail != head) {
				int actionsCount = head - tail;
				if(actionsCount < 0) {
					actionsCount += bufferSize;
				}
				relax(gamma(actionsCount));
			}
		}
		timestamps[head] = System.currentTimeMillis();
		head = (head + 1) % bufferSize;
	} 

	private void relax(long ms) {
		if(ms > 0L) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Use gamma parameter to calculate delay depending on current number of active actions
	 * @param actionsCount actions registered in cyclic buffer
	 * @return milliseconds to delay: [0.0 .. periodMs]
	 */
	private long gamma(int actionsCount) {
		long timeReserveMs = timestamps[tail] + periodMs - System.currentTimeMillis();
		int actionsReserve = bufferSize - 1 - actionsCount;
		long result;
		if(actionsReserve < 2) {
			result = timeReserveMs - 1;
		} else {
			result = /* (double) gamma calculation here */ timeReserveMs / actionsReserve;
		}
		System.out.println("gamma:" + timestamps[tail] + ' ' + timeReserveMs + ' ' + actionsCount + ' ' + result);
		return result;  
	}

	private double gamma;
	private int periodMs;
	private int bufferSize;

	// Cyclic buffer with time stamps of all recent actions
	private long[] timestamps;

	// Indices in timestamp
	private int head;	// available empty cell
	private int tail;	// last used cell
	// Invariant: head in [0 .. bufferSize [
	// Invariant: tail in [0 .. bufferSize [
	// head == tail for empty buffer

}
