package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class MockElapsedTimerTest {
    @Test
    public void advanceTimer() {
        // GIVEN I have a timer
        // AND its current time is zero
        var timer = new MockElapsedTimer();
        // WHEN I advance the time
        timer.advanceTimer();
        // THEN the timer should have a time of 30
        var actual = timer.getTime();
        var expected = "30";
        assertEquals(expected, actual);
    }

    @Test
    public void getTime() {
        // GIVEN I have a timer
        var timer = new MockElapsedTimer();
        // AND its current time is zero
        // WHEN I get the time
        var actual = timer.getTime();
        // THEN the string should be "0"
        assertEquals("0", actual);
    }

    @Test
    public void startTimer() {
        // GIVEN I have a timer
        // AND it has not been started yet
        var timer = new MockElapsedTimer();
        // WHEN I start the timer
        timer.startTimer();
        // THEN the timer should start
        assertTrue(timer.isRunning());
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should not read "0"
        assertFalse(timer.isRunning());
    }

    @Test
    public void startTimerAfterPaused() {
        // GIVEN I have a paused timer
        // AND it has been started before
        var timer = new MockElapsedTimer();
        timer.startTimer();
        assertTrue(timer.isRunning());
        timer.stopTimer();

        // WHEN I start the timer
        timer.startTimer();
        assertTrue(timer.isRunning());

        // THEN the timer should start
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should stop again
        assertFalse(timer.isRunning());
    }

    @Test
    public void startTimerWhileRunning() {
        // GIVEN I have a timer
        var timer = new MockElapsedTimer();
        // AND it is running
        timer.startTimer();
        // WHEN I start the timer
        timer.startTimer();
        // THEN the timer should continue running
        var isRunning = timer.isRunning();
        assertTrue(isRunning);
    }

    @Test
    public void stopTimer() {
        // GIVEN I have a running timer
        var timer = new MockElapsedTimer();
        timer.startTimer();
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should stop
        assertFalse(timer.isRunning());
    }

    @Test
    public void stopTimerTwice() {
        // GIVEN I have a paused timer
        var timer = new MockElapsedTimer();
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should remain paused
        assertFalse(timer.isRunning());
    }
}
