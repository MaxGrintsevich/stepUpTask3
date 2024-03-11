package ru.stepup.task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class CacheCleaner implements Runnable{
    private static CacheCleaner cleaner;
    private static Thread job;
    private int timeout;
    private CopyOnWriteArraySet<Cleanable> cleanList = new CopyOnWriteArraySet<>();
    private boolean goWork = true;
    private int test;

    private CacheCleaner(int timeout){
        this.timeout = timeout;
    }
    public void setGoWork(boolean goWork) {
        this.goWork = goWork;
    }

    @Override
    public void run() {
        while (goWork){
            for (Cleanable cache: cleanList) {
                if (cache.clean()){
                    cleanList.remove(cache);
                    System.out.println("CacheCleaner: cache удален для - " + cache);
                };
            }
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createJob(int timeout){
        cleaner = new CacheCleaner(timeout);
        job =  new Thread(cleaner, "CacheCleaner");
        job.setDaemon(true);
    }
    public static void startJob(){
        job.start();
    }

    public static void stopJob(){
        cleaner.setGoWork(false);
    }

    public static void submitCleaning(Cleanable clean){
        if (cleaner==null){
            createJob(200);
            startJob();
        }
        cleaner.cleanList.add(clean);
    }


}
