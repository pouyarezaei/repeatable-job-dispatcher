package job.time;

import job.base.AbstractJob;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class HourTimingStrategy implements TimingStrategy {

    @Override
    public void updateJobState(AbstractJob job) {
        if (job.getLastExecutionTime() == null) {
            job.setLastExecutionTime(System.currentTimeMillis());
            Date nextTime = DateUtils.addHours(new Date(job.getLastExecutionTime()), job.getTime());
            job.setNextExecutionTime(nextTime.toInstant().toEpochMilli());
        } else {
            job.setLastExecutionTime(job.getNextExecutionTime());
            Date nextTime = DateUtils.addHours(new Date(job.getLastExecutionTime()), job.getTime());
            job.setNextExecutionTime(nextTime.toInstant().toEpochMilli());
        }
    }
}
