package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Objects;

public class MockElapsedTimer implements ElapsedTimer {
    private Temporal start; // Not necessarily a "timer" per se, more of a timestamp in Java LocalTime (local machine time)
    private Duration duration;
    private boolean isRunning;

    MockElapsedTimer() {
        this.duration = Duration.ZERO;
        this.isRunning = false;
        this.start = null;
    }

    @Override
    public void startTimer() {
        // Handles if the timer is already started
        if (this.isRunning) return;

        start = LocalTime.now();
        isRunning = true;
    }

    @Override
    public void stopTimer() {
        // Handles if the timer isn't running yet
        if (!this.isRunning || Objects.isNull(start)) return;

        // Calculating the time between when the "timer" started and now
        duration = duration.plus(Duration.between((LocalTime) start, LocalTime.now()));

        this.isRunning = false;
        start = null;
    }

    @Override
    public String getTime() {
        Duration currDuration = duration;

        // Calculating the time between when the "timer" started and now
        if (this.isRunning && Objects.nonNull(start)) {
            currDuration = duration.plus(Duration.between((LocalTime) start, LocalTime.now()));
            start = LocalTime.now();
        }

        // Duration class returns long integers, have to convert
        var longSeconds = currDuration.getSeconds();

        // int casting should handle conversion but will check more later
        //int hours = (int)(longSeconds / 3600);
        int minutes = (int)((longSeconds % 3600) / 60);
        //int seconds = (int)(longSeconds % 60);

        // Redundant, but helps debug imo
        //String message = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return String.format("%01d", minutes);
    }

    // Advances the timer by 30 seconds
    public void advanceTimer() {
        this.duration = this.duration.plusSeconds(30);
    }

    // For testing purposes
    public boolean isRunning() {
        return isRunning;
    }
}
