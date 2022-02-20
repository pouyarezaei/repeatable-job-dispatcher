package job.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface Job<T> extends Comparable<Job> {

    T exec();

    JobPriority getPriority();

    Boolean isEnable();

    Boolean isNotRunning();

    @RequiredArgsConstructor
    enum JobPriority {
        HIGH(3),
        MEDIUM(2),
        LOW(1);

        @Getter
        private final Integer value;
    }

    @RequiredArgsConstructor
    enum JobPriorityOrder {
        ASC("ASC"),
        DESC("DESC");

        @Getter
        private final String name;
    }

    @Override
    default int compareTo(Job job) {
        return this.getPriority().getValue().compareTo(job.getPriority().getValue());

    }
}
