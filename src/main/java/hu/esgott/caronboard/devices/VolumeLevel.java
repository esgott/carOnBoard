package hu.esgott.caronboard.devices;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VolumeLevel {

    private static final int MAX_LEVEL = 10;
    private static final int LOW_VOLUME = 1;

    private final Lock lock = new ReentrantLock();
    private int volume = MAX_LEVEL;
    private int oldVolume = MAX_LEVEL;

    public boolean increase() {
        try {
            lock.lock();
            if (volume < MAX_LEVEL) {
                volume++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean decrease() {
        try {
            lock.lock();
            if (volume > 0) {
                volume--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void storeVolume() {
        try {
            lock.lock();
            oldVolume = volume;
            volume = LOW_VOLUME;
        } finally {
            lock.unlock();
        }
    }

    public void restoreVolume() {
        try {
            lock.lock();
            volume = oldVolume;
        } finally {
            lock.unlock();
        }
    }

    public double getVolume() {
        double currentVolume;
        try {
            lock.lock();
            currentVolume = volume / (double) MAX_LEVEL;
        } finally {
            lock.unlock();
        }
        return currentVolume;
    }

    @Override
    public String toString() {
        return Double.toString(getVolume());
    }
}
