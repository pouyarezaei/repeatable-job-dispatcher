package job.base;

import job.time.TimingStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
abstract public class AbstractJob<T> implements Runnable, Job<T> {
    protected final String jobId;
    protected final String jobName;
    protected final Long createdAt;
    protected final Integer time;
    protected final TimingStrategy timingStrategy;
    protected Long executionCount = 0L;
    @Setter
    protected Long nextExecutionTime;
    @Setter
    protected Long lastExecutionTime;
    @Setter
    protected Boolean enable = true;
    private final AtomicBoolean running = new AtomicBoolean();
    protected Consumer<T> execResultCallback;

    protected AbstractJob(Integer time, TimingStrategy timingStrategy) {
        this.time = time;
        this.timingStrategy = timingStrategy;
        this.jobId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.jobName = null;
    }

    protected AbstractJob(Integer time, TimingStrategy timingStrategy, String jobName) {
        this.time = time;
        this.timingStrategy = timingStrategy;
        this.jobId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.jobName = jobName;
    }

    protected AbstractJob(Integer time, TimingStrategy timingStrategy, String jobName, Consumer<T> execResultCallback) {
        this.time = time;
        this.timingStrategy = timingStrategy;
        this.jobId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.jobName = jobName;
        this.execResultCallback = execResultCallback;
    }


    public void incrementExecutionCount() {
        executionCount++;
    }

    @Override
    public void run() {
        running.set(true);
        if (enable && (this.getNextExecutionTime() == null || this.getNextExecutionTime() <= System.currentTimeMillis())) {
            if (this instanceof BeforeExecJob) {
                ((BeforeExecJob) this).beforeExec();
            }
            T execResult = this.exec();
            if (execResultCallback != null) {
                execResultCallback.accept(execResult);
            }
            if (this instanceof AfterExecJob) {
                ((AfterExecJob) this).afterExec();
            }
            this.timingStrategy.updateJobState(this);
            incrementExecutionCount();
        }
        running.set(false);
    }

    @Override
    public Boolean isEnable() {
        return enable;
    }

    @Override
    public Boolean isNotRunning() {
        return !running.get();
    }


}
