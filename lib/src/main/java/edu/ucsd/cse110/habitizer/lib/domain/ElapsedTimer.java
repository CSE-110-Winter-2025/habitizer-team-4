package edu.ucsd.cse110.habitizer.lib.domain;

public interface ElapsedTimer {

    // Starts a count-up timer
    public void startTimer();

    // Pauses a count-up timer
    public void stopTimer();

    // Gets the current time of the timer
    public String getTime();

    // Always returns the rounded up time
    public String getRoundedUpTime();

    // Always returns the rounded down time
    public String getRoundedDownTime();

    public int getSeconds();
    public void setSeconds(int seconds);

    // Allows pausing the timer
    void pauseTimer();

    // Allows resuming the timer after pausing
    void resumeTimer();

    // Advances the timer by 30 seconds (For testing in US3c)
    void advanceTimer();

    // Reset the timer to 00:00
    void resetTimer();

}
