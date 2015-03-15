package hu.esgott.caronboard.devices;

import hu.esgott.caronboard.devices.AudioFeedback.A;

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

    public A getClip() {
        switch (volume) {
        case 10:
            return A.VOLUME;
        case 9:
            return A.VOLUME_5;
        case 8:
            return A.VOLUME_10;
        case 7:
            return A.VOLUME_15;
        case 6:
            return A.VOLUME_20;
        case 5:
            return A.VOLUME_25;
        case 4:
            return A.VOLUME_30;
        case 3:
            return A.VOLUME_35;
        case 2:
            return A.VOLUME_40;
        case 1:
            return A.VOLUME_45;
        case 0:
            return A.VOLUME_45;
        default:
            return A.VOLUME;
        }
    }

    @Override
    public String toString() {
        return Double.toString(getVolume());
    }
}
