package dispatcher;

import job.base.AbstractJob;

public interface Dispatcher {
    void start() throws Exception;

    void dispatch(AbstractJob job) throws Exception;

    void stop() throws Exception;
}
