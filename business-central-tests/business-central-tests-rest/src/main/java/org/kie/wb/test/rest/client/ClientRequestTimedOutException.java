package org.kie.wb.test.rest.client;

import org.guvnor.rest.client.JobResult;

/**
 * Special case of {@link NotSuccessException} to be used in cases when failure was caused by client request timing out.
 */
public class ClientRequestTimedOutException extends NotSuccessException {

    private final int timeout;

    public ClientRequestTimedOutException(JobResult jobResult, int timeout) {
        super(jobResult);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }
}
