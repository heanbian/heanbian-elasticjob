package com.heanbian.elasticjob.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.heanbian.elasticjob.client.autoconfigure.ElasticJobAutoConfiguration;
import com.heanbian.elasticjob.client.autoconfigure.ElasticJobProperties;

/**
 * 启云动类或配置类使用的注解
 * 
 * @author 马洪
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ElasticJobProperties.class, ElasticJobAutoConfiguration.class })
public @interface EnableElasticJob {
}