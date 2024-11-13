package edu.yu.parallel;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

public class CompletionServiceProvider {

    private static final Executor exec = Executors.newCachedThreadPool();
    private static final CompletionService completionService = new ExecutorCompletionService(exec);

    public static Executor getExec() {
        return exec;
    }

    public static CompletionService getCompletionservice() {
        return completionService;
    }

}