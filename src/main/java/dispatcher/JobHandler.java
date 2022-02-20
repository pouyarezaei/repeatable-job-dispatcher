package dispatcher;

import job.base.AbstractJob;
import lombok.SneakyThrows;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class JobHandler implements Runnable {
    Boolean enable = false;
    private final TimeUnit intervalTimeUnit;
    private final Long intervalTime;
    private final BlockingQueue<AbstractJob> jobs;
    private final ExecutorService executorService;

    public JobHandler(TimeUnit intervalTimeUnit, Long intervalTime, BlockingQueue<AbstractJob> jobs, ExecutorService executorService) {
        this.intervalTimeUnit = intervalTimeUnit;
        this.intervalTime = intervalTime;
        this.jobs = jobs;
        this.executorService = executorService;
    }


    @SneakyThrows
    @Override
    public void run() {
        while (enable) {
            jobs.stream()
                    .filter(AbstractJob::getEnable)
                    .filter(AbstractJob::isNotRunning)
                    .forEach(executorService::execute);
            intervalTimeUnit.sleep(intervalTime);
        }
    }

    public void stop() {
        this.enable = false;
    }

    public void start() {
        this.enable = true;
    }
}
