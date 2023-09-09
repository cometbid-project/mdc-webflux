/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

/**
 * @author Gbenga
 *
 */
@Data
@Builder
public class OutputMessage {

	private Instant time;
	private String content;
}