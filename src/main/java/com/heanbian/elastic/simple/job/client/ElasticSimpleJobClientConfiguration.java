package com.heanbian.elastic.simple.job.client;

import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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
public class ElasticSimpleJobClientConfiguration {

	@Value("${dgg.elastic.simple.job.client.zookeeper-node:}")
	private String zookeeperNode;

	@Value("${dgg.elastic.simple.job.client.namespace:}")
	private String namespace;

	@Value("${dgg.elastic.simple.job.client.zookeeper-token:}")
	private String zookeeperToken;

	@Value("${dgg.elastic.simple.job.client.session-timeout-milliseconds:60000}")
	private int sessionTimeoutMilliseconds;

	@Value("${dgg.elastic.simple.job.client.connection-timeout-milliseconds:15000}")
	private int connectionTimeoutMilliseconds;

	@Value("${dgg.elastic.simple.job.client.base-sleep-time-milliseconds:3000}")
	private int baseSleepTimeMilliseconds;

	@Value("${dgg.elastic.simple.job.client.max-sleep-time-milliseconds:3000}")
	private int maxSleepTimeMilliseconds;

	@Value("${dgg.elastic.simple.job.client.max-retry:10}")
	private int maxRetry;

	@Value("${dgg.elastic.simple.job.client.started-timeout-milliseconds:15000}")
	private long startedTimeoutMilliseconds;

	@Value("${dgg.elastic.simple.job.client.completed-timeout-milliseconds:15000}")
	private long completedTimeoutMilliseconds;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void init() {
		Objects.requireNonNull(zookeeperNode, "'dgg.elastic.simple.job.client.zookeeper-node' must not be null");
		Objects.requireNonNull(namespace, "'dgg.elastic.simple.job.client.namespace' must not be null");

		ZookeeperConfiguration config = new ZookeeperConfiguration(zookeeperNode, namespace);
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
			ElasticSimpleJobClient annotation = simpleJob.getClass().getAnnotation(ElasticSimpleJobClient.class);

			String cron = StringUtils.defaultIfBlank(annotation.cron(), annotation.value());
			SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(
					JobCoreConfiguration
							.newBuilder(simpleJob.getClass().getName(), cron, annotation.shardingTotalCount())
							.shardingItemParameters(annotation.shardingItemParameters()).build(),
					simpleJob.getClass().getCanonicalName());

			LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
					.overwrite(true).build();

			String dataSource = annotation.dataSource();
			if (StringUtils.isNotBlank(dataSource)) {

				if (!context.containsBean(dataSource)) {
					throw new RuntimeException("beanName '" + dataSource + "' does not exist");
				}

				DataSource ds = (DataSource) context.getBean(dataSource);
				JobEventRdbConfiguration jobEventRdbConfiguration = new JobEventRdbConfiguration(ds);
				SpringJobScheduler jobScheduler = new SpringJobScheduler(simpleJob, registry, liteJobConfiguration,
						jobEventRdbConfiguration);
				jobScheduler.init();
			} else {
				SpringJobScheduler jobScheduler = new SpringJobScheduler(simpleJob, registry, liteJobConfiguration);
				jobScheduler.init();
			}
		}
	}

	@Bean
	public ElasticJobListener getElasticSimpleJobListener() {
		return new ElasticSimpleJobClientListener(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	public String getZookeeperNode() {
		return zookeeperNode;
	}

	public void setZookeeperNode(String zookeeperNode) {
		this.zookeeperNode = zookeeperNode;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getZookeeperToken() {
		return zookeeperToken;
	}

	public void setZookeeperToken(String zookeeperToken) {
		this.zookeeperToken = zookeeperToken;
	}

	public int getSessionTimeoutMilliseconds() {
		return sessionTimeoutMilliseconds;
	}

	public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
		this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
	}

	public int getConnectionTimeoutMilliseconds() {
		return connectionTimeoutMilliseconds;
	}

	public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
		this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
	}

	public int getBaseSleepTimeMilliseconds() {
		return baseSleepTimeMilliseconds;
	}

	public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
		this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
	}

	public int getMaxSleepTimeMilliseconds() {
		return maxSleepTimeMilliseconds;
	}

	public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
		this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public long getStartedTimeoutMilliseconds() {
		return startedTimeoutMilliseconds;
	}

	public void setStartedTimeoutMilliseconds(long startedTimeoutMilliseconds) {
		this.startedTimeoutMilliseconds = startedTimeoutMilliseconds;
	}

	public long getCompletedTimeoutMilliseconds() {
		return completedTimeoutMilliseconds;
	}

	public void setCompletedTimeoutMilliseconds(long completedTimeoutMilliseconds) {
		this.completedTimeoutMilliseconds = completedTimeoutMilliseconds;
	}

}
