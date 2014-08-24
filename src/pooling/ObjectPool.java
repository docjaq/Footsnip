package pooling;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ObjectPool<T>
{
    protected ConcurrentLinkedQueue<T> pool;

    private ScheduledExecutorService executorService;

    public ObjectPool(final int minIdle) {
        // initialize pool
        initialize(minIdle);
    }

    public ObjectPool(final int minIdle, final int maxIdle, final long validationInterval) {
        // initialize pool
        initialize(minIdle);

        // check pool conditions in a separate thread
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run() {
                int size = pool.size();
                if (size < minIdle) {
                    int sizeToBeAdded = minIdle - size;
                    for (int i = 0; i < sizeToBeAdded; i++) {
                        pool.add(initaliseObject());
                    }
                } else if (size > maxIdle) {
                    int sizeToBeRemoved = size - maxIdle;
                    for (int i = 0; i < sizeToBeRemoved; i++) {
                        pool.poll();
                    }
                }
            }
        }, validationInterval, validationInterval, TimeUnit.SECONDS);
    }

    protected T borrowObject() {
        T object;
        if ((object = pool.poll()) == null) {
            object = initaliseObject();
        }

        return object;
    }

    protected abstract T initaliseObject();

    public void returnObject(T object) {
        if (object == null) {
            return;
        }

        this.pool.offer(object);
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void initialize(final int minIdle) {
        pool = new ConcurrentLinkedQueue<T>();

        for (int i = 0; i < minIdle; i++) {
            pool.add(initaliseObject());
        }
    }
}