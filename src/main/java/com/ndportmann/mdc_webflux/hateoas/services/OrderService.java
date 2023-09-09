/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.List;

import com.ndportmann.mdc_webflux.service.model.Order;

/**
 * @author Gbenga
 *
 */
public interface OrderService {

    List<Order> getAllOrdersForCustomer(String customerId);

    Order getOrderByIdForCustomer(String customerId, String orderId);

}
