package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RegularTimer implements ElapsedTimer {
    private Timer timer;
    private TimerTask timerTask;
    private int secondsElapsed; // Needed to keep track of seconds
    private boolean isRunning; // Needed to track status of timer

    RegularTimer() {
        this.secondsElapsed = -1; // Needs to be -1 since the timer begins IMMEDIATELY (i.e. resolves to 0 when started)
        this.isRunning = false;
        this.timer = null; // Not instantiated until we startTimer()
        this.timerTask = null; // Also not instantiated until we startTimer()
    }

    @Override
    public void startTimer() {
        // Should handle starting the timer again even if the timer has already been started
        if (this.isRunning == true) return;

        this.timer = new Timer();

        this.timerTask = new TimerTask() {
            @Override
            public void run() { // Adding a second for every time run() is called (every 1000ms)
                secondsElapsed++;
            }
        };

        // Starts immediately; every 1000ms (i.e. one second) secondsElapsed is added 1 second
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        this.isRunning = true;
    }

    @Override
    public void stopTimer() {
        // Should handle stopping the timer even if the timer hasn't been started
        if (Objects.isNull(this.timerTask)) return;

        this.timerTask.cancel();
        this.timer.cancel();
        this.isRunning = false;
    }

    @Override
    public String getTime() {
        // Casting with (int) should handle, but will check later
        int hours = (int)(secondsElapsed / 3600); // Will be an integer because of casting, should not have to worry about double casting
        int minutes = (int)((secondsElapsed % 3600) / 60); // Same idea here
        int seconds = (int)(secondsElapsed % 60);

        // Redundant, but nice to reformatting just in case in my opinion
        String message = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return message;
    }

    // For testing purposes
    public boolean isRunning() {
        return isRunning;
    }
}
