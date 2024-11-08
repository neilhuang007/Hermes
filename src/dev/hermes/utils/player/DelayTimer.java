package dev.hermes.utils.player;

public class DelayTimer {
    private long lastTime;

    public DelayTimer() {
        reset();
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - lastTime;
    }

    public void setTimeElapsed(long time) {
        this.lastTime = System.currentTimeMillis() - time;
    }

    public boolean hasTimePassed(long time) {
        return getTimeElapsed() >= time;
    }

    public void reset() {
        lastTime = System.currentTimeMillis();
    }
}
