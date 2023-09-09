/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.List;

import com.ndportmann.mdc_webflux.service.model.vo.CustomerVO;

/**
 * @author Gbenga
 *
 */
public interface CustomerService {

    List<CustomerVO> allCustomers();

    CustomerVO getCustomerDetail(final String id);

}
