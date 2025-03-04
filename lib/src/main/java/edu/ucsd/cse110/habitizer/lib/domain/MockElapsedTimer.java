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
        // If the timer is running, update the duration before stopping.
        if (isRunning && start != null) {
            duration = duration.plus(Duration.between((LocalTime) start, LocalTime.now()));
        }
        isRunning = false;
        start = null;       // Clear start to ensure getTime() returns the final duration.
        // Do NOT reset duration to zero here.
    }

    @Override
    public void pauseTimer() {
        if (!isRunning) return;

        // Capture the elapsed time since the timer started/resumed
        duration = duration.plus(Duration.between((LocalTime) start, LocalTime.now()));
        isRunning = false;
        start = null;       // Clear start to prevent double-adding in getTime()
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
        // Advance timer by 15 seconds regardless of whether it's paused
        if (isRunning && start != null) {
            // Incorporate the time elapsed since the last start before advancing
            duration = duration.plus(Duration.between((LocalTime)start, LocalTime.now()));
            start = LocalTime.now();
        }
        duration = duration.plusSeconds(15);
    }

    @Override
    public String getTime() {
        // If the timer is running, add the time since the last start to duration.
        Duration currentDuration = duration;
        if (isRunning && start != null) {
            currentDuration = currentDuration.plus(Duration.between((LocalTime) start, LocalTime.now()));
        }

        long totalSeconds = currentDuration.getSeconds();
        int minutes = (int)((totalSeconds % 3600) / 60);
        int seconds = (int)(totalSeconds % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public String getRoundedUpTime() {
        Duration currentDuration = duration;
        if (isRunning && start != null) {
            currentDuration = currentDuration.plus(Duration.between((LocalTime) start, LocalTime.now()));
        }

        long totalSeconds = currentDuration.getSeconds();
        int minutes = (int)((totalSeconds % 3600) / 60);
        return String.format(Locale.getDefault(), "%01d", minutes + 1);
    }

    @Override
    public int getSeconds() {
        return 0;
    }

    @Override
    public void setSeconds(int seconds) {}

    @Override
    public String getRoundedDownTime() {
        Duration currentDuration = duration;
        if (isRunning && start != null) {
            currentDuration = currentDuration.plus(Duration.between((LocalTime) start, LocalTime.now()));
        }

        long totalSeconds = currentDuration.getSeconds();
        int minutes = (int)((totalSeconds % 3600) / 60);

        if (minutes == 0) { return "-"; }

        return String.format(Locale.getDefault(), "%01d", minutes);
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
