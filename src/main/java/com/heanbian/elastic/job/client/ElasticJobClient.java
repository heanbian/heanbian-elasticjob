package com.heanbian.elastic.job.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticJobClient {

	@AliasFor("cron")
	String value() default "";

	@AliasFor("value")
	String cron() default "";

	String jobName() default "";

	int shardingTotalCount() default 1;

	String shardingItemParameters() default "";

	String jobParameter() default "";

	String dataSource() default "";

	boolean overwrite() default true;
}
