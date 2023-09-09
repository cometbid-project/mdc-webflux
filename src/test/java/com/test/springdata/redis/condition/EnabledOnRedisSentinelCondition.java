/**
 * 
 */
package com.test.springdata.redis.condition;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.*;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;

/**
 * {@link ExecutionCondition} for
 * {@link EnabledOnRedisSentinelCondition @EnabledOnRedisSentinelAvailable}.
 *
 * @author Mark Paluch
 * @author Christoph Strobl
 * @see EnabledOnRedisSentinelCondition
 */
class EnabledOnRedisSentinelCondition implements ExecutionCondition {

	private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled(
			"@EnabledOnSentinelAvailable is not present");

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

		var optional = AnnotationUtils.findAnnotation(context.getElement(), EnabledOnRedisSentinelAvailable.class);

		if (!optional.isPresent()) {
			return ENABLED_BY_DEFAULT;
		}

		var annotation = optional.get();

		if (RedisDetector.canConnectToPort(annotation.host(), annotation.value())) {

			return enabled(String.format("Connection successful to Redis Sentinel at %s:%d", annotation.host(),
					annotation.value()));
		}

		return disabled(
				String.format("Cannot connect to Redis Sentinel at %s:%d", annotation.host(), annotation.value()));

	}
}
