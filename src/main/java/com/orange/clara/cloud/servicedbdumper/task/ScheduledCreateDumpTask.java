package com.orange.clara.cloud.servicedbdumper.task;

import com.orange.clara.cloud.servicedbdumper.model.Job;
import com.orange.clara.cloud.servicedbdumper.model.JobEvent;
import com.orange.clara.cloud.servicedbdumper.model.JobType;
import com.orange.clara.cloud.servicedbdumper.repo.JobRepo;
import com.orange.clara.cloud.servicedbdumper.task.asynctask.CreateDumpTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Copyright (C) 2015 Orange
 * <p/>
 * This software is distributed under the terms and conditions of the 'MIT'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'http://opensource.org/licenses/MIT'.
 * <p/>
 * Author: Arthur Halet
 * Date: 25/11/2015
 */
@Component
public class ScheduledCreateDumpTask {
    private Logger logger = LoggerFactory.getLogger(ScheduledCreateDumpTask.class);

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    @Qualifier(value = "createDumpTask")
    private CreateDumpTask createDumpTask;

    @Scheduled(fixedDelay = 5000)
    public void createDump() {
        logger.info("Running: create dump scheduled task ...");
        for (Job job : jobRepo.findByJobTypeAndJobEvent(JobType.CREATE_DUMP, JobEvent.START)) {
            job.setJobEvent(JobEvent.RUNNING);
            jobRepo.save(job);
            this.createDumpTask.runCreateDump(job.getId());
        }
        logger.info("Finished: create dump scheduled task .");
    }
}