package com.dreamgyf.gmqyttf.client.structure;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingObject<T> {

    private ArrayBlockingQueue<T> mBlockingQueue = new ArrayBlockingQueue<>(1);

    public T poll() {
        return mBlockingQueue.poll();
    }

    public T take() throws InterruptedException {
        return mBlockingQueue.take();
    }

    public boolean offer(T t) {
        return mBlockingQueue.offer(t);
    }

    public void put(T t) throws InterruptedException {
        mBlockingQueue.put(t);
    }

}
