package com.didichuxing.datachannel.arius.admin.task.template.srv;

import com.didichuxing.datachannel.arius.admin.common.exception.AdminOperateException;
import com.didichuxing.datachannel.arius.admin.task.BaseConcurrentTemplateTask;
import com.didiglobal.knowframework.job.annotation.Task;
import com.didiglobal.knowframework.job.common.TaskResult;
import com.didiglobal.knowframework.job.core.job.Job;
import com.didiglobal.knowframework.job.core.job.JobContext;
import com.didiglobal.knowframework.log.ILog;
import com.didiglobal.knowframework.log.LogFactory;

@Task(name = "DcdrInfoCollectorRandomTask", description = "采集dcdr相关数据", cron = "0 0/5 * * * ? *", autoRegister = true)
public class DcdrInfoCollectorRandomTask extends BaseConcurrentTemplateTask implements Job {

    private static final ILog LOGGER = LogFactory.getLog(DcdrInfoCollectorRandomTask.class);

    @Override
    public TaskResult execute(JobContext jobContext) throws Exception {
        LOGGER.info("class=DcdrInfoCollectorRandomTask||method=execute||msg=DcdrInfoCollectorRandomTask start.");
        if (execute()) {
            return TaskResult.buildSuccess();
        }
        return TaskResult.buildFail();
    }

    @Override
    public String getTaskName() {
        return "DcdrInfoCollectorRandomTask";
    }

    @Override
    public int poolSize() {
        return 10;
    }

    @Override
    public int current() {
        return 5;
    }

    @Override
    protected boolean executeByLogicTemplate(Integer logicId) throws AdminOperateException {
        return templateLogicManager.updateDCDRInfo(logicId);
    }
}