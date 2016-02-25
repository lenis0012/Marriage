package com.lenis0012.bukkit.marriage2.misc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LockedReference<T> {
    private final Lock lock = new ReentrantLock();
    private final Supplier<T> supplier;
    private final Consumer<T> invalidator;
    private final long expiryTime;

    private T object;
    private long timeClaimed;

    public LockedReference(Supplier<T> supplier, long expiryTime, TimeUnit unit, Consumer<T> invalidator) {
        this.supplier = supplier;
        this.invalidator = invalidator;
        this.expiryTime = unit.toMillis(expiryTime);
    }

    public T access() {
        lock.lock();

        // Check if object not defined or expired.
        if(timeClaimed + expiryTime < System.currentTimeMillis()) {
            // Invalidate old object
            if(object != null && invalidator != null) {
                invalidator.accept(object);
            }

            // Obtain new object
            this.object = supplier.get();
            this.timeClaimed = System.currentTimeMillis();
        }

        return object;
    }

    public void finish() {
        lock.unlock();
    }

    public void invalidateNow() {
        invalidator.accept(object);
        this.object = null;
        this.timeClaimed = 0L;
    }
}
