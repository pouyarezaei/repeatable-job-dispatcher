package job.time;

import job.base.AbstractJob;

public interface TimingStrategy {
    void updateJobState(AbstractJob job);
}
