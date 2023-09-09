/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Gbenga
 *
 */
@Value
@Builder
public class Address {

	// @JsonProperty(value = "street")
	private @Indexed String street;

	// @JsonProperty(value = "city")
	private @Indexed String city;

	// @JsonProperty(value = "country")
	private String country;

	private @GeoIndexed Point location;

	/**
	 * @param city
	 * @param country
	 * @param location
	 */
	// @JsonCreator
	public Address(String street, String city, String country, Point location) {
		this.street = street;
		this.city = city;
		this.country = country;
		this.location = location;
	}

	/**
	 * @param city
	 * @param country
	 */
	public Address(String street, String city, String country) {
		this(street, city, country, null);
	}

	/**
	 * 
	 */
	public Address() {
		this(null, null, null);
	}

}