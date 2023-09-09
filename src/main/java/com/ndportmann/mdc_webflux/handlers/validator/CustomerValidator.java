/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers.validator;

import com.ndportmann.mdc_webflux.exceptions.ValidationException;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public class CustomerValidator implements BaseValidator<CustomerModel> {

	private final Validator<CustomerModel> validator;

	public CustomerValidator() {
		validator = ValidatorBuilder.of(CustomerModel.class)
				.constraint(CustomerModel::getCompanyEmail, "companyEmail", c -> c.notNull().email())
				.constraint(CustomerModel::getCompanyName, "companyName", c -> c.notNull())
				.constraint(CustomerModel::getTaxId, "taxId", c -> c.pattern("")).build();
	}

	@Override
	public Mono<CustomerModel> validate(CustomerModel model) {

		ConstraintViolations violations = validator.validate(model);
		if (violations.isValid()) {
			return Mono.just(model);
		} else {
 
			return Mono.error(new ValidationException(violations.violations()));
		}
	}
}
