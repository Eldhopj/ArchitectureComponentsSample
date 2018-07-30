package com.eldhopj.architecturecomponentssample.DataBase;
/**This class is for making Database handling in background
 * Executor: it is normally used instead of explicitly creating threads*/
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    // For Singleton instantiation, So we will use the same instance of the class
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) { // Actually we only need diskIO executor
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(
                        Executors.newSingleThreadExecutor(), //Disk I/O is a single thread executor ,This ensure our db transaction are done in order so there wont be any Race condition occours
                        Executors.newFixedThreadPool(3), // Network I/O executor is a pool of 3 threads
                        new MainThreadExecutor()); //
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    //When we in an activity we don't need this MainThreadExecutor because we can use runOnUiThread method
    //But when we are in a different class and we don't have runOnUiThread method we can use MainThreadExecutor
    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
