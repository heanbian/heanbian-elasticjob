package com.heanbian.elastic.job.client;

import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

@Configuration
public class ElasticJobConfiguration implements InitializingBean {

	@Value("${elastic.job.client.zookeeper-nodes:}")
	private String zookeeperNodes;

	@Value("${elastic.job.client.zookeeper-namespace:}")
	private String namespace;

	@Value("${elastic.job.client.zookeeper-token:}")
	private String zookeeperToken;

	@Value("${elastic.job.client.session-timeout-milliseconds:60000}")
	private int sessionTimeoutMilliseconds;

	@Value("${elastic.job.client.connection-timeout-milliseconds:15000}")
	private int connectionTimeoutMilliseconds;

	@Value("${elastic.job.client.base-sleep-time-milliseconds:3000}")
	private int baseSleepTimeMilliseconds;

	@Value("${elastic.job.client.max-sleep-time-milliseconds:3000}")
	private int maxSleepTimeMilliseconds;

	@Value("${elastic.job.client.max-retry:10}")
	private int maxRetry;

	@Value("${elastic.job.client.started-timeout-milliseconds:15000}")
	private long startedTimeoutMilliseconds;

	@Value("${elastic.job.client.completed-timeout-milliseconds:15000}")
	private long completedTimeoutMilliseconds;

	@Autowired
	private ApplicationContext context;

	public void init() {
		Objects.requireNonNull(zookeeperNodes, "elastic.job.client.zookeeper-nodes must be set");
		Objects.requireNonNull(namespace, "elastic.job.client.zookeeper-namespace must be set");

		ZookeeperConfiguration config = new ZookeeperConfiguration(zookeeperNodes, namespace);
		config.setMaxRetries(maxRetry);
		config.setDigest(zookeeperToken);
		config.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
		config.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
		config.setSessionTimeoutMilliseconds(sessionTimeoutMilliseconds);
		config.setConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);

		ZookeeperRegistryCenter registry = new ZookeeperRegistryCenter(config);
		registry.init();

		Map<String, SimpleJob> map = context.getBeansOfType(SimpleJob.class);
		for (Map.Entry<String, SimpleJob> entry : map.entrySet()) {
			SimpleJob simpleJob = entry.getValue();
			ElasticJobClient annotation = simpleJob.getClass().getAnnotation(ElasticJobClient.class);
			if (annotation == null) {
				continue;
			}

			String cron = StringUtils.defaultIfBlank(annotation.cron(), annotation.value());
			if (StringUtils.isBlank(cron)) {
				continue;
			}

			String jobName = StringUtils.defaultIfBlank(annotation.jobName(), simpleJob.getClass().getName());

			SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(
					JobCoreConfiguration.newBuilder(jobName, cron, annotation.shardingTotalCount())
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
						jobEventRdbConfiguration, getElasticSimpleJobListener());
				jobScheduler.init();
			} else {
				SpringJobScheduler jobScheduler = new SpringJobScheduler(simpleJob, registry, liteJobConfiguration,
						getElasticSimpleJobListener());
				jobScheduler.init();
			}
		}
	}

	@Bean
	public ElasticJobListener getElasticSimpleJobListener() {
		return new ElasticJobClientListener(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

}
