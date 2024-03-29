/**
 * 
 */
package com.ndportmann.mdc_webflux.error.model;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Gbenga
 *
 */
@ToString
@Getter
@Setter
public class AppResponse {

	@JsonProperty("metadata")
	private final AppResponseMetadata metadata;
	
	@JsonProperty("response")
	private final ApiResponse apiResponse;

	public AppResponse(final String apiVersion, String errorCode, final int code, final String message,
			final String domain, final String reason, final String traceId, final String errorReportUri,
			final String moreInfoUrl) {

		String status = HttpStatus.valueOf(code).name();
		
		this.metadata = AppResponseMetadata.builder().apiVersion(apiVersion).status(status)
				.moreInfo(moreInfoUrl + status).sendReport(errorReportUri + "?id=" + traceId).shortMessage(message)
				.build();

		this.apiResponse = new ApiError(domain, errorCode, reason, code, message, null);
	}

	public static AppResponse fromDefaultAttributeMap(final String apiVersion, String errorCode,
			final Map<String, Object> defaultErrorAttributes, final String sendReportBaseUri,
			final String moreInfoUrl) {

		// original attribute values are documented in
		// org.springframework.boot.web.servlet.error.DefaultErrorAttributes
		return new AppResponse(apiVersion, errorCode, ((Integer) defaultErrorAttributes.get("status")),
				(String) defaultErrorAttributes.getOrDefault("message", "no message available"),
				(String) defaultErrorAttributes.getOrDefault("path", "no domain available"),
				(String) defaultErrorAttributes.getOrDefault("error", "no reason available"),
				(String) defaultErrorAttributes.get("trace_id"), sendReportBaseUri, moreInfoUrl);
	}

	// utility method to return a map of serialized root attributes,
	// see the last part of the guide for more details
	public Map<String, Object> toAttributeMap() {
		return Map.of("meta", metadata, "response", apiResponse);
	}

	public AppResponse(String currentApiVersion, String errorCode, HttpStatus httpStatus, String message, String path,
			final String traceId, String sendReportUri, String moreInfoUrl, Exception ex) {
		// TODO Auto-generated constructor stub
		super();
		this.apiResponse = new ApiError(path, errorCode, httpStatus, message, ex.getMessage());
		this.metadata = AppResponseMetadata.builder().apiVersion(currentApiVersion).status(httpStatus.name())
				.moreInfo(moreInfoUrl + httpStatus.name()).sendReport(sendReportUri + "?id=" + traceId)
				.shortMessage(message).build();
	}

	/**
	 * @param apiVersion
	 * @param apiError
	 * @param sendReport
	 */
	public AppResponse(String apiVersion, String message, ApiError apiError, final String traceId, String sendReportUri,
			String status, String moreInfoUrl) {
		super();
		this.apiResponse = apiError;
		this.metadata = AppResponseMetadata.builder().apiVersion(apiVersion).status(status)
				.moreInfo(moreInfoUrl + status).sendReport(sendReportUri + "?id=" + traceId).shortMessage(message)
				.build();
	}

	/**
	 * @param apiVersion
	 * @param apiError
	 * @param sendReport
	 */
	public AppResponse(String apiVersion, String message, ApiMessage apiMessage, String status, String moreInfoUrl) {
		super();
		this.apiResponse = apiMessage;
		this.metadata = AppResponseMetadata.builder().apiVersion(apiVersion).status(status)
				.moreInfo(moreInfoUrl + status).shortMessage(message).build();
	}

	public ApiResponse getApiResponse() {
		// TODO Auto-generated method stub
		return this.apiResponse;
	}

}
