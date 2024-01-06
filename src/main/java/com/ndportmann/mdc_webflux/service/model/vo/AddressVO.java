/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model.vo;

import java.util.Map;


import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ndportmann.mdc_webflux.service.model.Order;
import com.ndportmann.mdc_webflux.service.model.Point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gbenga
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressVO {

	@Schema(required = true, description = "Street address. The length cannot exceed 100.")
    @NotBlank @Size(max = 100)
	@JsonProperty(value = "street")
	private @Indexed String street;

	@Schema(required = true, description = "City address. The length cannot exceed 100.")
    @NotBlank @Size(max = 100)
	@JsonProperty(value = "city")
	private @Indexed String city;

	@Schema(required = true, description = "Country Address. The length cannot exceed 50.")
    @NotBlank @Size(max = 50)
	@JsonProperty(value = "country")
	private String country;

	private Point location;
}
