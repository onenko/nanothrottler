package net.nenko.nanothrottler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GammaThrottlerTest {

	private static int ONE_SECOND_FRAME = 1000;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllFitInTimeFrame() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 5);
		long start = System.currentTimeMillis();
		System.out.println("testAllFitInTimeFrame() start:" + start);
		for(int i = 0; i < 5; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testAllFitInTimeFrame() duration:" + (end-start));
		assertTrue(end - start <= ONE_SECOND_FRAME);
	}

	@Test
	public void testNotFitInTimeFrame() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 5);
		long start = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame() start:" + start);
		for(int i = 0; i < 6; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame() duration:" + (end-start));
		assertTrue(end - start >= ONE_SECOND_FRAME);
	}

	@Test
	public void testAllFitInTimeFrame3Times() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 15);
		long start = System.currentTimeMillis();
		System.out.println("testAllFitInTimeFrame3Times() start:" + start);
		for(int i = 0; i < 45; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testAllFitInTimeFrame3Times() duration:" + (end-start));
		assertTrue(end - start <= 3 * ONE_SECOND_FRAME);
	}

	@Test
	public void testNotFitInTimeFrame3Times() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 15);
		long start = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame3Times() start:" + start);
		for(int i = 0; i < 46; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame3Times() duration:" + (end-start));
		assertTrue(end - start >= 3 * ONE_SECOND_FRAME);
	}

	@Test
	public void testFitInTimeFrame5Times() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 10);
		long start = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() start:" + start);
		for(int i = 0; i < 50; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() duration:" + (end-start));
		assertTrue(end - start <= 5 * ONE_SECOND_FRAME);
	}

	@Test
	public void testFitInTimeFrame5TimesGamma2() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 10, 2.0D);
		long start = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() start:" + start);
		for(int i = 0; i < 50; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() duration:" + (end-start));
		assertTrue(end - start <= 5 * ONE_SECOND_FRAME);
	}

	@Test
	public void testNotFitInTimeFrame5TimesGamma2() {
		NanoThrottler th = new GammaThrottler(ONE_SECOND_FRAME, 10, 2.0D);
		long start = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() start:" + start);
		for(int i = 0; i < 51; i++) {
			th.allow();
		}
		long end = System.currentTimeMillis();
		System.out.println("testNotFitInTimeFrame5Times() duration:" + (end-start));
		assertTrue(end - start >= 5 * ONE_SECOND_FRAME);
	}


}
