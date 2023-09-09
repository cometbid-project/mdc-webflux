/**
 * 
 */
package com.test.springdata.redis.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * {@code @EnabledOnRedisSentinelAvailable} is used to signal that the annotated test class or test method is only
 * <em>enabled</em> if Redis Sentinel is running.
 * <p/>
 * When applied at the class level, all test methods within that class will be enabled.
 *
 * @author Mark Paluch
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ExtendWith(EnabledOnRedisSentinelCondition.class)
public @interface EnabledOnRedisSentinelAvailable {

	String host() default "localhost";

	/**
	 * Sentinel port number.
	 */
	int value() default 26379;
}
