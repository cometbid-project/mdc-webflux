/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model.vo;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ndportmann.mdc_webflux.service.model.Order;

import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonPropertyOrder({"customerId", "firstName", "lastName"})
@JsonInclude(Include.NON_NULL)
public class CustomerVO extends RepresentationModel<CustomerVO> {

	@Schema(required = false, description = "The ID for the customer. Should be a Unique ID with max 36 Characters. If not provided, the system will assign one.")
	@Size(max = 36)
	private String customerId;

	@Schema(required = true, description = "The first name. The length cannot exceed 50.")
	@NotBlank
	@Size(max = 50)
	private String firstName;
	
	@Schema(required = true, description = "The last name. The length cannot exceed 50.")
    @NotBlank @Size(max = 50)
    private String lastName;
	
	private AddressVO address;
	
	private String companyName;
	private Map<String, Order> orders;

	/*
	public CustomerVO() {
		super();
	}

	public CustomerVO(final String customerId, final String customerName, final String companyName) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.companyName = companyName;
	}
	*/


}