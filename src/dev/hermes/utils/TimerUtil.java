package dev.hermes.utils;

import lombok.Getter;

import java.util.Timer;

public class TimerUtil {

    @Getter
    private static Timer timer = new Timer(); // Create a timer
    private static long startTime = 0; // Track start time

    public static void startTimer() {
        startTime = System.currentTimeMillis(); // Save the start time
        timer = new Timer(); // Reinitialize the timer to start fresh
    }

    public static void stopTimer() {
        timer.cancel(); // Stop the timer
        timer.purge(); // Remove all cancelled tasks
    }

    public static boolean delay(double delayInSeconds) {
        // Convert delay from seconds to milliseconds
        long delayInMillis = (long) (delayInSeconds * 1000);
        long delayStartTime = System.currentTimeMillis(); // Record the start time

        while (System.currentTimeMillis() - delayStartTime < delayInMillis) {
            // Busy wait until the delay period has passed
        }

        return true; // Return true to indicate the delay period has passed
    }


    public static long getCurrentTime() {
        // Return the elapsed time since the timer started, or 0 if not started
        return startTime > 0 ? System.currentTimeMillis() - startTime : 0;
    }

    public boolean finished(final long delay) {
        return System.currentTimeMillis() - delay >= startTime;
    }

    public static void resetTimer() {
        // Stop and restart the timer to reset
        stopTimer();
        startTimer();
    }
}
