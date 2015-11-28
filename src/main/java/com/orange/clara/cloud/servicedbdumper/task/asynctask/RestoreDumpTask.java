package com.orange.clara.cloud.servicedbdumper.task.asynctask;

import com.orange.clara.cloud.servicedbdumper.dbdumper.running.Restorer;
import com.orange.clara.cloud.servicedbdumper.exception.RestoreException;
import com.orange.clara.cloud.servicedbdumper.model.Job;
import com.orange.clara.cloud.servicedbdumper.model.JobEvent;
import com.orange.clara.cloud.servicedbdumper.repo.JobRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

/**
 * Copyright (C) 2015 Orange
 * <p/>
 * This software is distributed under the terms and conditions of the 'MIT'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'http://opensource.org/licenses/MIT'.
 * <p/>
 * Author: Arthur Halet
 * Date: 26/11/2015
 */
public class RestoreDumpTask {
    private Logger logger = LoggerFactory.getLogger(RestoreDumpTask.class);
    @Autowired
    @Qualifier("restorer")
    private Restorer restorer;

    @Autowired
    private JobRepo jobRepo;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Boolean> runRestoreDump(Integer jobId) {
        Job job = this.jobRepo.findOne(jobId);
        try {
            this.restorer.restore(job.getDatabaseRefSrc(), job.getDatabaseRefTarget(), job.getUpdatedAt());
        } catch (RestoreException e) {
            logger.error(String.format("Cannot restore dump for '%s' in '%s': %s", job.getDatabaseRefSrc().getDatabaseName(), job.getDatabaseRefTarget().getDatabaseName(), e.getMessage()));
            job.setJobEvent(JobEvent.ERRORED);
            jobRepo.save(job);
            return new AsyncResult<Boolean>(false);
        }
        job.setJobEvent(JobEvent.FINISHED);
        jobRepo.save(job);
        return new AsyncResult<Boolean>(true);
    }
}
