package com.heanbian.elasticjob.client.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author 马洪
 *
 */
@ConfigurationProperties(prefix = "heanbian.elasticjob")
public class ElasticJobProperties {

	private String zookeeperNodes;

	private String zookeeperNamespace;

	private String zookeeperToken;

	private int sessionTimeoutMilliseconds = 60000;

	private int connectionTimeoutMilliseconds = 15000;

	private int baseSleepTimeMilliseconds = 3000;

	private int maxSleepTimeMilliseconds = 3000;

	private int maxRetry = 10;

	private long startedTimeoutMilliseconds = 15000;

	private long completedTimeoutMilliseconds = 15000;

	public String getZookeeperNodes() {
		return zookeeperNodes;
	}

	public void setZookeeperNodes(String zookeeperNodes) {
		this.zookeeperNodes = zookeeperNodes;
	}

	public String getZookeeperNamespace() {
		return zookeeperNamespace;
	}

	public void setZookeeperNamespace(String zookeeperNamespace) {
		this.zookeeperNamespace = zookeeperNamespace;
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
