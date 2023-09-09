/**
 * 
 */
package com.ndportmann.mdc_webflux.error.handler;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.ndportmann.mdc_webflux.exceptions.ApplicationDefinedRuntimeException;
import com.ndportmann.mdc_webflux.exceptions.BadRequestException;
import com.ndportmann.mdc_webflux.exceptions.ResourceAlreadyExistException;
import com.ndportmann.mdc_webflux.exceptions.ResourceNotFoundException;
import com.ndportmann.mdc_webflux.exceptions.ServiceException;
import com.ndportmann.mdc_webflux.exceptions.ServiceUnavailableException;

/*
import com.cometbid.oauth2.demo.exceptions.ApplicationDefinedRuntimeException;
import com.cometbid.oauth2.demo.exceptions.AuthenticationError;
import com.cometbid.oauth2.demo.exceptions.BadRequestException;
import com.cometbid.oauth2.demo.exceptions.BlockedIPAttemptsLoginWarning;
import com.cometbid.oauth2.demo.exceptions.InvalidInputException;
import com.cometbid.oauth2.demo.exceptions.InvalidJwtTokenException;
import com.cometbid.oauth2.demo.exceptions.NewLocationTokenValidationException;
import com.cometbid.oauth2.demo.exceptions.PasswordNotAcceptableException;
import com.cometbid.oauth2.demo.exceptions.ResetPasswordTokenValidationException;
import com.cometbid.oauth2.demo.exceptions.ResourceNotFoundException;
import com.cometbid.oauth2.demo.exceptions.ServiceException;
import com.cometbid.oauth2.demo.exceptions.SessionExpiredException;
import com.cometbid.oauth2.demo.exceptions.ServiceUnavailableException;
import com.cometbid.oauth2.demo.exceptions.UnauthenticatedUserException;
import com.cometbid.oauth2.demo.exceptions.UserAlreadyExistException;
import com.cometbid.oauth2.demo.exceptions.UserProfileDisabledException;
import com.cometbid.oauth2.demo.exceptions.UserProfileExpiredException;
import com.cometbid.oauth2.demo.exceptions.UserProfileLockedException;
import com.cometbid.oauth2.demo.exceptions.UserProfileUnverifiedException;
*/
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public class ErrorPublisher {

	private ErrorPublisher() {

	}

	/*
	public static <T> Mono<T> raiseBadCredentials(String messageKey, Object[] args) {
		return Mono.error(new AuthenticationError(messageKey, args));
	}

	public static <T> Mono<T> raiseUserAlreadyExist() {
		return Mono.error(new UserAlreadyExistException(new Object[] {}));
	}

	public static <T> Mono<T> raiseBadRequestError(String messageKey, Object[] args) {
		return Mono.error(new BadRequestException(messageKey, args));
	}

	public static <T> Mono<T> raiseLoginSessionExpiredError(String messageKey, Object[] args) {
		return Mono.error(new SessionExpiredException(messageKey, args));
	}

	public static <T> Mono<T> raiseNewLocationTokenInvalidError(String messageKey, Object[] args) {
		return Mono.error(new NewLocationTokenValidationException(messageKey, args));
	}

	public static <T> Mono<T> raiseResetPasswordSessionExpiredError(String messageKey, Object[] args) {
		return Mono.error(new SessionExpiredException(messageKey, args));
	}

	public static <T> Mono<T> raiseUnauthenticatedUserError(String messagekey, Object[] args) {
		return Mono.error(new UnauthenticatedUserException(messagekey, args));
	}
	*/

	public static <T> Mono<T> raiseRuntimeError(final String message, final Throwable cause) {
		return Mono.error(new ApplicationDefinedRuntimeException(message, cause));
	}

	public static <T> Mono<T> raiseServiceUnavailableError(final String messageKey, Object[] args) {
		return Mono.error(new ServiceUnavailableException(messageKey, args));
	}

	public static <T> Mono<T> raiseResourceNotFoundError(final String messageKey, Object[] args) {
		return Mono.error(new ResourceNotFoundException(messageKey, args));
	}

	public static <T> Mono<T> raiseResourceAlreadyExistError(final String messageKey, Object[] args) {
		return Mono.error(new ResourceAlreadyExistException(messageKey, args));
	}

	/*
	public static <T> Mono<T> raiseResetPasswordTokenError(String messageKey, Object[] args) {
		return Mono.error(new ResetPasswordTokenValidationException(messageKey, args));
	}
	*/

	public static <T> Mono<T> raiseInvalidInputRequestError(final String message) {
		Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();

		return Mono.error(new ConstraintViolationException(message, constraintViolations));
	}

	public static <T> Mono<T> raiseServiceExceptionError(final String body, Integer statusCode) {
		return Mono.error(new ServiceException(body, statusCode));
	}

	public static void raiseRuntimeException(String message, Throwable ex) {
		throw new ApplicationDefinedRuntimeException(message, ex);
	}

	public static ServiceUnavailableException raiseServiceUnavailableException(String messageKey, Object[] args) {
		throw new ServiceUnavailableException(messageKey, args);
	}
	
	public static void raiseBadRequestException(String messageKey, Object[] args) {
		throw new BadRequestException(messageKey, args);
	}

	
	/*

	public static void raiseUserProfileLockedException(Object[] args) {
		throw new UserProfileLockedException(args);
	}

	public static void raiseUserProfileExpiredException(Object[] args) {
		throw new UserProfileExpiredException(args);
	}

	public static void raiseUserProfileUnverifiedException(Object[] args) {
		throw new UserProfileUnverifiedException(args);
	}

	public static void raiseUserProfileDisabledException(Object[] args) {
		throw new UserProfileDisabledException(args);
	}

	public static void raiseResourceNotFoundException(Object[] args) {
		throw new ResourceNotFoundException(args);
	}

	public static void raisePasswordUnacceptableException(Object[] args) {
		throw new PasswordNotAcceptableException(args);
	}

	public static void raisePasswordUnacceptableException(String messageKey, Object[] args) {
		throw new PasswordNotAcceptableException(messageKey, args);
	}

	public static void raiseBadCredentialsException(String messageKey, Object[] args) {
		throw new AuthenticationError(messageKey, args);
	}

	public static void raiseUserAlreadyExistException(Object[] args) {
		throw new UserAlreadyExistException(args);
	}

	public static void raiseNewLocationTokenInvalidException(Object[] args) {
		throw new NewLocationTokenValidationException(args);
	}

	public static void raiseUnauthenticatedUserException(Object[] args) {
		throw new UnauthenticatedUserException(args);
	}

	public static void raiseBlockedIPAttemptLoginAlert(Object[] args) {
		throw new BlockedIPAttemptsLoginWarning(args);
	}

	public static void raiseLoginSessionExpiredException(String messageKey, Object[] args) {
		throw new SessionExpiredException(messageKey, args);
	}


	public static InvalidInputException raiseInvalidInputException(String message) {
		Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();

		throw new ConstraintViolationException(message, constraintViolations);
	}

	public static <T> Mono<T> raiseInvalidJwtToken(String messageKey, Object[] args) {

		return Mono.error(new InvalidJwtTokenException(messageKey, args));
	}

	public static void raiseInvalidJwtTokenException(String messageKey, Object[] args) {

		throw new InvalidJwtTokenException(messageKey, args);
	}
	*/

}
