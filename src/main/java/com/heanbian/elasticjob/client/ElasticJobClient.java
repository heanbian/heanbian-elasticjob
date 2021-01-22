package com.heanbian.elasticjob.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Heanbian
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticJobClient {

	/**
	 * cron表达式，用于控制作业触发时间
	 * 
	 * @return
	 */
	String cron();

	/**
	 * 作业名称
	 * 
	 * @return
	 */
	String jobName() default "";

	/**
	 * 作业分片总数
	 * 
	 * @return
	 */
	int shardingTotalCount() default 1;

	/**
	 * 分片序列号和参数用等号分隔，多个键值对用逗号分隔 分片序列号从0开始，不可大于或等于作业分片总数 如： 0=a,1=b,2=c
	 * 
	 * @return
	 */
	String shardingItemParameters() default "";

	/**
	 * 作业自定义参数， 作业自定义参数，可通过传递该参数为作业调度的业务方法传参，用于实现带参数的作业， 例：每次获取的数据量、作业实例从数据库读取的主键等
	 * 
	 * @return
	 */
	String jobParameter() default "";

	/**
	 * 数据源 beanName
	 * 
	 * @return
	 */
	String dataSource() default "";

	/**
	 * 是否覆盖 zookeeper上的配置
	 * 
	 * @return
	 */
	boolean overwrite() default true;
}
