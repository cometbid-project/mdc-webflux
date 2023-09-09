/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Gbenga
 *
 */
@Data
@Accessors(chain = true)
public class EmployeeModel {

	private Long id;
	private String name;
	private String department;

}
