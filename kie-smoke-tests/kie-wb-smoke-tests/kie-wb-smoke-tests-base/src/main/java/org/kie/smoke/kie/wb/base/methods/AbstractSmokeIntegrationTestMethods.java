package org.kie.smoke.kie.wb.base.methods;

import static org.junit.Assert.fail;

import java.util.List;

import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractSmokeIntegrationTestMethods {

    protected static Logger logger = LoggerFactory.getLogger(AbstractSmokeIntegrationTestMethods.class);
    
    protected TaskSummary findTaskSummary(Long procInstId, List<TaskSummary> taskSumList) { 
        for( TaskSummary task : taskSumList ) { 
            if( procInstId.equals(task.getProcessInstanceId()) ) {
                return task;
            }
        }
        fail( "Unable to find task summary for process instance " + procInstId); 
        return null;
    }

}
