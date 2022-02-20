package dispatcher;

import exception.MultiStartException;
import exception.StartAgainException;
import exception.WrongJobPriorityOrderException;
import job.base.AbstractJob;
import job.base.Job;

import java.util.Comparator;
import java.util.concurrent.*;

public class InMemoryStoreJobDispatcher implements Dispatcher {
    private final BlockingQueue<AbstractJob> jobs;
    private final ExecutorService executorService;
    private final ExecutorService handlerThread;
    private final ThreadGroup threadGroup;
    private final ThreadFactory threadFactory;
    private final JobHandler jobHandler;
    private Boolean running = false;

    public InMemoryStoreJobDispatcher(PriorityBlockingQueue<AbstractJob> priorityBlockingQueue, Integer threadCount) {
        this.jobs = priorityBlockingQueue;
        this.threadGroup = new ThreadGroup(InMemoryStoreJobDispatcher.class.getSimpleName());
        this.threadFactory = runnable -> new Thread(threadGroup, runnable, String.format("%s-%d", InMemoryStoreJobDispatcher.class.getSimpleName(), threadGroup.activeCount()));
        this.handlerThread = Executors.newSingleThreadExecutor(threadFactory);
        this.executorService = Executors.newScheduledThreadPool(threadCount, threadFactory);
        this.jobHandler = new JobHandler(TimeUnit.SECONDS, 1L, jobs, executorService);
    }

    public static InMemoryStoreJobDispatcher createJobDispatcher() {
        return new InMemoryStoreJobDispatcher(new PriorityBlockingQueue<>(), Runtime.getRuntime().availableProcessors() / 2);
    }

    public static InMemoryStoreJobDispatcher createJobDispatcher(Integer threadCount) {
        return new InMemoryStoreJobDispatcher(new PriorityBlockingQueue<>(), threadCount);
    }

    public static InMemoryStoreJobDispatcher createJobDispatcher(Integer threadCount, Job.JobPriorityOrder jobPriorityOrder) throws WrongJobPriorityOrderException {

        if (jobPriorityOrder == Job.JobPriorityOrder.ASC) {
            return new InMemoryStoreJobDispatcher(new PriorityBlockingQueue<>(Runtime.getRuntime().availableProcessors(), Comparator.comparing(job -> job.getPriority().getValue())), threadCount);
        } else if (jobPriorityOrder == Job.JobPriorityOrder.DESC) {
            return new InMemoryStoreJobDispatcher(new PriorityBlockingQueue<>(Runtime.getRuntime().availableProcessors(), (job, t1) -> t1.getPriority().getValue().compareTo(job.getPriority().getValue())), threadCount);
        } else {
            throw new WrongJobPriorityOrderException();
        }

    }

    @Override
    public synchronized void start() throws Exception {
        if (!running) {
            try {
                running = true;
                jobHandler.start();
                handlerThread.execute(jobHandler);
            } catch (RejectedExecutionException e) {
                throw new StartAgainException();
            }
        } else {
            throw new MultiStartException();
        }
    }

    @Override
    public synchronized void dispatch(AbstractJob job) throws Exception {
        jobs.add(job);
    }

    @Override
    public synchronized void stop() throws Exception {
        jobs.forEach(job -> job.setEnable(false));
        jobHandler.stop();
        handlerThread.shutdown();
        executorService.shutdown();
        running = false;
    }

}
