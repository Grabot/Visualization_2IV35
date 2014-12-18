/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 *
 * @author Luuk
 */
public class PixelThreadPoolExecutor extends ThreadPoolExecutor {

    private ArrayList<PixelThreadPoolChangeListener> listeners = new ArrayList<PixelThreadPoolChangeListener>();
    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public PixelThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
    }

    public void addListener(PixelThreadPoolChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void ProgressUpdated() {
        for (PixelThreadPoolChangeListener listener : listeners) {
            listener.OnProgressUpdated();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if ((getQueue().size() % 1000) == 0)
        {
            ProgressUpdated();
        }
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        ProgressUpdated();
    }

}
