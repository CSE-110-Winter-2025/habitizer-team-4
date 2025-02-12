package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RegularTimerTest {

    @Test
    public void getTime() {
        // GIVEN I have a timer
        var timer = new RegularTimer();
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
        var timer = new RegularTimer();
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
        var timer = new RegularTimer();
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
        var timer = new RegularTimer();
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
        var timer = new RegularTimer();
        timer.startTimer();
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should stop
        assertFalse(timer.isRunning());
    }

    @Test
    public void stopTimerTwice() {
        // GIVEN I have a paused timer
        var timer = new RegularTimer();
        // WHEN I stop the timer
        timer.stopTimer();
        // THEN the timer should remain paused
        assertFalse(timer.isRunning());
    }
}
