package exception;

import job.base.Job;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WrongJobPriorityOrderException extends Exception {
    public WrongJobPriorityOrderException() {
        super(String.format("Set JobPriority From Available Options [%s]", Arrays.stream(Job.JobPriorityOrder.values()).map(Job.JobPriorityOrder::getName).collect(Collectors.joining())));
    }
}
