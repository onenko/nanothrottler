package net.nenko.nanothrottler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SmallThrottlerTest {

	private static int ONE_SECOND_FRAME = 1000;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllFitInTimeFrame() {
		NanoThrottler bt = new SmallThrottler(ONE_SECOND_FRAME, 5);
		long start = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			bt.allow();
		}
		long end = System.currentTimeMillis();
		assertTrue(end - start < ONE_SECOND_FRAME);
	}

	@Test
	public void testNotFitInTimeFrame() {
		NanoThrottler bt = new SmallThrottler(ONE_SECOND_FRAME, 5);
		long start = System.currentTimeMillis();
		for(int i = 0; i < 6; i++) {
			bt.allow();
		}
		long end = System.currentTimeMillis();
		assertTrue(end - start > ONE_SECOND_FRAME);
	}


}
