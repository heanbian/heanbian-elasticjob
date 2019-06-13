package com.heanbian.elastic.simple.job.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;

public class ElasticSimpleJobClientListener extends AbstractDistributeOnceElasticJobListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSimpleJobClientListener.class);

	public ElasticSimpleJobClientListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
		LOGGER.info("taskId={} ,jobName={}", shardingContexts.getTaskId(), shardingContexts.getJobName());
	}

}