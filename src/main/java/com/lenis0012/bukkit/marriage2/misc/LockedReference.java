package com.lenis0012.bukkit.marriage2.misc;

import com.lenis0012.bukkit.marriage2.internal.data.DataManager.ConnectionInvalidator;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager.ConnectionSupplier;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedReference {
    private final Lock lock = new ReentrantLock();
    private final ConnectionSupplier supplier;
    private final ConnectionInvalidator invalidator;
    private final long expiryTime;

    private Connection object;
    private long timeClaimed;

    public LockedReference(ConnectionSupplier supplier, long expiryTime, TimeUnit unit, ConnectionInvalidator invalidator) {
        this.supplier = supplier;
        this.invalidator = invalidator;
        this.expiryTime = unit.toMillis(expiryTime);
    }

    public Connection access() {
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
