package job.base;

import job.time.TimingStrategy;

public abstract class AbstractTimingStrategy implements TimingStrategy {
    protected final AbstractJob job;

    protected AbstractTimingStrategy(AbstractJob job) {
        this.job = job;
    }
}
