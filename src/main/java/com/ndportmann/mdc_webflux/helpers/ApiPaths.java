/**
 * 
 */
package com.ndportmann.mdc_webflux.helpers;

/**
 * @author Gbenga
 *
 */
public interface ApiPaths {

	String EMPLOYEE_PATH = "/employees";
	String PRODUCT_PATH = "/products";

	static String getEmployeePath(String host) {
		return host + EMPLOYEE_PATH;
	}

	static String getProductPath(String host) {
		return host + PRODUCT_PATH;
	}
}
