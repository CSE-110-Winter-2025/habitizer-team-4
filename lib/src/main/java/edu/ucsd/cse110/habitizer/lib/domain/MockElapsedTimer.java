package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Locale;

public class MockElapsedTimer implements ElapsedTimer {
    private Temporal start;
    private Duration duration;   // Needed to keep track of elapsed time
    private boolean isRunning;   // Needed to track status of timer

    public MockElapsedTimer() {
        this.duration = Duration.ZERO; // Start with zero elapsed time
        this.isRunning = false;
        this.start = null;             // Not instantiated until we startTimer()
    }

    public static MockElapsedTimer immediateTimer() {
        MockElapsedTimer immediateTimer = new MockElapsedTimer();
        immediateTimer.startTimer();
        return immediateTimer;
    }

    @Override
    public void startTimer() {
        // Should handle starting the timer again even if the timer has already been started
        if (isRunning == true) return;

        // For a fresh start, reset the duration and set start to now
        this.duration = Duration.ZERO;
        this.start = LocalTime.now();
        isRunning = true;
    }

    @Override
    public void stopTimer() {
        // Should handle stopping the timer even if the timer hasn't been started
        if (isRunning == false && start == null) return;

        isRunning = false;
        start = null;
        duration = Duration.ZERO; // Reset elapsed time when stopping
    }

    @Override
    public void pauseTimer() {
        // Should handle pausing the timer
        if (isRunning == false) return;

        // Capture the elapsed time since the timer started/resumed
        duration = duration.plus(Duration.between((LocalTime)start, LocalTime.now()));
        isRunning = false;
        start = null;
    }

    @Override
    public void resumeTimer() {
        // Only resume if the timer was paused.
        if (isRunning == true) return;

        // Resume the timer by setting start to now without resetting duration.
        start = LocalTime.now();
        isRunning = true;
    }

    @Override
    public void advanceTimer() {
        // Advance timer by 30 seconds regardless of whether it's paused
        if (isRunning && start != null) {
            // Incorporate the time elapsed since the last start before advancing
            duration = duration.plus(Duration.between((LocalTime)start, LocalTime.now()));
            start = LocalTime.now();
        }
        duration = duration.plusSeconds(30);
    }

    @Override
    public String getTime() {
        // If the timer hasn't started or has been stopped, return "00:00"
        Duration currentDuration = duration;
        if (isRunning && start != null) {
            currentDuration = currentDuration.plus(Duration.between((LocalTime)start, LocalTime.now()));
        }
        if (!isRunning && start == null && duration.equals(Duration.ZERO)) {
            return "00:00";
        }

        long totalSeconds = currentDuration.getSeconds();
        int minutes = (int) ((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60);

        // Using Locale to test whether or not this is better than just a formatted string
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void resetTimer() {
        this.duration = Duration.ZERO; // Start with zero elapsed time
        this.isRunning = false;
        this.start = null;
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
