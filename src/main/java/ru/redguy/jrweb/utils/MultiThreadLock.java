package ru.redguy.jrweb.utils;

public class MultiThreadLock {
    private boolean isLocked = false;

    public synchronized void lock() {
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        isLocked = true;
    }

    public synchronized void waitLock() {
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }

    public synchronized void unlock() {
        if (!isLocked)
            throw new IllegalStateException();
        isLocked = false;
        notify();
    }
}
