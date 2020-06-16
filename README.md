# heanbian-elasticjob

## 前提条件

JDK11+

## pom.xml

具体版本，可以上Maven中央仓库查询

```
<dependency>
	<groupId>com.heanbian</groupId>
	<artifactId>heanbian-elastic-job-client</artifactId>
	<version>11.1.0</version>
</dependency>
```

## 使用示例

配置 `application.yml`

```
heanbian:
  elasticjob:
    zookeeper-nodes: 192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181
    zookeeper-namespace: elasticjob_fgFDGdfg45435SDFsfb #自定义
```

Spring Boot 启动类添加注解：

`@EnableElasticJob`

Java代码片段：

```
@ElasticJobClient(cron = "*/10 * * * * ?")
@Component
public class TestTask implements SimpleJob {

	@Override
	public void execute(ShardingContext shardingContext) {
		// your task

	}

}
```


说明：适用于 Spring Boot 2.3.x 项目。