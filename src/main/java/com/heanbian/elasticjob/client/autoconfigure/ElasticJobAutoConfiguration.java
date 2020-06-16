package com.heanbian.elasticjob.client.autoconfigure;

import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.heanbian.elasticjob.client.ElasticJobClient;

/**
 * 
 * @author 马洪
 *
 */
public class ElasticJobAutoConfiguration {

	public ElasticJobAutoConfiguration(@Autowired ApplicationContext context, @Autowired ElasticJobProperties p) {
		Objects.requireNonNull(p.getZookeeperNodes(), "heanbian.elasticjob.zookeeper-nodes must not be null");
		Objects.requireNonNull(p.getZookeeperNamespace(), "heanbian.elasticjob.zookeeper-namespace must not be null");

		ZookeeperConfiguration config = new ZookeeperConfiguration(p.getZookeeperNodes(), p.getZookeeperNamespace());
		config.setMaxRetries(p.getMaxRetry());
		config.setDigest(p.getZookeeperToken());
		config.setBaseSleepTimeMilliseconds(p.getBaseSleepTimeMilliseconds());
		config.setMaxSleepTimeMilliseconds(p.getMaxSleepTimeMilliseconds());
		config.setSessionTimeoutMilliseconds(p.getSessionTimeoutMilliseconds());
		config.setConnectionTimeoutMilliseconds(p.getConnectionTimeoutMilliseconds());

		ZookeeperRegistryCenter registry = new ZookeeperRegistryCenter(config);
		registry.init();

		Map<String, SimpleJob> map = context.getBeansOfType(SimpleJob.class);
		for (Map.Entry<String, SimpleJob> entry : map.entrySet()) {
			SimpleJob simpleJob = entry.getValue();
			ElasticJobClient annotation = simpleJob.getClass().getAnnotation(ElasticJobClient.class);
			if (annotation == null) {
				continue;
			}

			if (StringUtils.isBlank(annotation.cron())) {
				continue;
			}

			String jobName = StringUtils.defaultIfBlank(annotation.jobName(), simpleJob.getClass().getName());

			SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(
					JobCoreConfiguration.newBuilder(jobName, annotation.cron(), annotation.shardingTotalCount())
							.shardingItemParameters(annotation.shardingItemParameters()).build(),
					simpleJob.getClass().getCanonicalName());

			LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
					.overwrite(annotation.overwrite()).build();

			String dataSource = annotation.dataSource();
			if (StringUtils.isNotBlank(dataSource)) {
				if (!context.containsBean(dataSource)) {
					throw new RuntimeException("beanName " + dataSource + " not found");
				}

				DataSource ds = (DataSource) context.getBean(dataSource);
				JobEventRdbConfiguration jobEventRdbConfiguration = new JobEventRdbConfiguration(ds);
				SpringJobScheduler jobScheduler = new SpringJobScheduler(simpleJob, registry, liteJobConfiguration,
						jobEventRdbConfiguration, elasticJobListener(p));
				jobScheduler.init();
			} else {
				SpringJobScheduler jobScheduler = new SpringJobScheduler(simpleJob, registry, liteJobConfiguration,
						elasticJobListener(p));
				jobScheduler.init();
			}
		}
	}

	@Bean
	public ElasticJobListener elasticJobListener(ElasticJobProperties p) {
		return new ElasticJobClientListener(p.getStartedTimeoutMilliseconds(), p.getCompletedTimeoutMilliseconds());
	}

}
