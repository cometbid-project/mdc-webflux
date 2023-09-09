/**
 * 
 */
package com.ndportmann.mdc_webflux.error.handler;

import static com.ndportmann.mdc_webflux.error.handler.ErrorPublisher.raiseRuntimeError;
import static com.ndportmann.mdc_webflux.error.handler.ErrorPublisher.raiseServiceExceptionError;
import java.io.IOException;
import java.util.function.Function;
//import javax.ws.rs.ClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
/*
import com.cometbid.oauth2.demo.exception.handler.ApiError;
import com.cometbid.oauth2.demo.exception.handler.AppResponse;
import com.cometbid.oauth2.demo.exceptions.AuthenticationError;
import com.cometbid.oauth2.demo.exceptions.InvalidRequestException;
import com.cometbid.oauth2.demo.exceptions.ResourceNotFoundException;
import com.cometbid.oauth2.demo.exceptions.ServiceException;
import com.cometbid.oauth2.demo.exceptions.ServiceUnavailableException;
import com.cometbid.oauth2.demo.exceptions.SessionExpiredException;
import com.cometbid.oauth2.demo.exceptions.UnauthenticatedUserException;
import com.cometbid.oauth2.demo.exceptions.UnusualLocationException;
import com.cometbid.oauth2.demo.exceptions.UserAlreadyExistException;
import com.cometbid.oauth2.demo.exceptions.UserProfileDisabledException;
import com.cometbid.oauth2.demo.exceptions.UserProfileExpiredException;
import com.cometbid.oauth2.demo.exceptions.UserProfileLockedException;
import com.cometbid.oauth2.demo.exceptions.UserProfileUnverifiedException;
*/
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndportmann.mdc_webflux.error.model.ApiError;
import com.ndportmann.mdc_webflux.error.model.AppResponse;
import com.ndportmann.mdc_webflux.exceptions.AuthenticationError;
import com.ndportmann.mdc_webflux.exceptions.InvalidRequestException;
import com.ndportmann.mdc_webflux.exceptions.ResourceNotFoundException;
import com.ndportmann.mdc_webflux.exceptions.ServiceException;
import com.ndportmann.mdc_webflux.exceptions.ServiceUnavailableException;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
public class ExceptionHandler {

	private static Integer UNKNOWN_ERRORCODE = 501;

	/*
	public static <T> Function<? super Throwable, ? extends Mono<? extends T>> handleWebFluxError(String genericMsg) {

		return error -> {
			if (error instanceof ClientErrorException || error instanceof AuthenticationError
					|| error instanceof UserAlreadyExistException || error instanceof UserProfileDisabledException
					|| error instanceof UserProfileExpiredException || error instanceof UserProfileLockedException
					|| error instanceof UserProfileUnverifiedException || error instanceof UnusualLocationException
					|| error instanceof UnauthenticatedUserException || error instanceof SessionExpiredException
					|| error instanceof ResourceNotFoundException) {

				return Mono.error(error);
			}

			return raiseRuntimeError(genericMsg, error);
		};
	}
	*/
	
	public static <T, R> Function<T, Mono<R>> handleCheckedExceptionFunction(
			LambdaCheckedExceptionFunction<T, R> handlerFunction) {

		return obj -> {
			R r = null;
			try {
				r = handlerFunction.apply(obj);
			} catch (Exception ex) {
				log.error("Exception occured: ", ex);
				// ReflectionUtils.rethrowRuntimeException(ex);

				return Mono.error(ex);
			}
			return Mono.just(r);
		};
	}

	@FunctionalInterface
	public interface LambdaCheckedExceptionFunction<T, R> {

		public R apply(T t) throws Exception;
	}

	public static <R> Mono<R> processResponse(ClientResponse clientResponse, Class<? extends R> clazzResponse) {
		HttpStatus status = clientResponse.statusCode();

		Mono<R> respObj = Mono.empty();

		if (status.is2xxSuccessful()) {
			respObj = clientResponse.bodyToMono(clazzResponse);

		} else if (status.isError()) {
			if (status.is4xxClientError()) {
				log.error("Client Error occurred while processing request");
				// String errorMsgKey = "client.error";

				return clientResponse.createException().flatMap(ex -> Mono.error(handle4xxException(ex)));

			} else if (status.is5xxServerError()) {
				log.error("Server Error occurred while processing request");
				// String errorMsgKey = "server.error";
				return clientResponse.createException().flatMap(ex -> Mono.error(handle5xxException(ex)));
			} else {

				return clientResponse.createException().flatMap(ex -> {

					return raiseServiceExceptionError(clientResponse.statusCode().getReasonPhrase(),
							clientResponse.rawStatusCode());
				});
			}
		}

		return respObj;
	}

	public static Throwable handle5xxException(Throwable ex) {

		if (!(ex instanceof WebClientResponseException)) {
			log.warn("Got a unexpected error: {}, will rethrow it", ex.toString());

			return new ServiceException(ex.getMessage(), UNKNOWN_ERRORCODE);
		}

		WebClientResponseException wcre = (WebClientResponseException) ex;

		switch (wcre.getStatusCode()) {

		case SERVICE_UNAVAILABLE:

			return new ServiceUnavailableException(new Object[] { getErrorMessage(wcre) });
		case BANDWIDTH_LIMIT_EXCEEDED:

			return new InvalidRequestException(getErrorMessage(wcre));
		case INTERNAL_SERVER_ERROR:

			return new ServiceUnavailableException(new Object[] { getErrorMessage(wcre) });
		default:
			log.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
			log.warn("Error body: {}", wcre.getResponseBodyAsString());

			return new ServiceException(wcre.getMessage(), wcre.getRawStatusCode());
		}
	}

	public static Throwable handle4xxException(Throwable ex) {

		if (!(ex instanceof WebClientResponseException)) {
			log.warn("Got a unexpected error: {}, will rethrow it", ex.toString());

			return new ServiceException(ex.getMessage(), UNKNOWN_ERRORCODE);
		}

		WebClientResponseException wcre = (WebClientResponseException) ex;

		switch (wcre.getStatusCode()) {

		case NOT_FOUND:

			return new ResourceNotFoundException(new Object[] { getErrorMessage(wcre) });
		case UNPROCESSABLE_ENTITY:

			return new InvalidRequestException(getErrorMessage(wcre));
		case UNAUTHORIZED:
		case FORBIDDEN:

			return new AuthenticationError(getErrorMessage(wcre));
		default:
			log.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
			log.warn("Error body: {}", wcre.getResponseBodyAsString());

			return new ServiceException(wcre.getMessage(), wcre.getRawStatusCode());
		}
	}

	static String getErrorMessage(WebClientResponseException ex) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			AppResponse appResponse = mapper.readValue(ex.getResponseBodyAsString(), AppResponse.class);
			
			ApiError apiError = (ApiError)appResponse.getApiResponse();
			
			return apiError.getMessage();
			
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}
}
