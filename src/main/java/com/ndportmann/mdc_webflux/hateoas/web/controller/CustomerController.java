/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import java.util.List;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
//import static org.springframework.hateoas.server.mvc.WebFluxLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.WebFluxLink;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndportmann.mdc_webflux.hateoas.services.CustomerService;
import com.ndportmann.mdc_webflux.hateoas.services.OrderService;
import com.ndportmann.mdc_webflux.service.model.Order;
import com.ndportmann.mdc_webflux.service.model.vo.CustomerVO;

import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;

	/**
	 * 
	 * @param customerId
	 * @return
	 */
	@GetMapping("/{customerId}")
	public CustomerVO getCustomerById(@PathVariable final String customerId) {
		return customerService.getCustomerDetail(customerId);
	}

	/**
	 * 
	 * @param customerId
	 * @param orderId
	 * @return
	 */
	@GetMapping("/{customerId}/{orderId}")
	public Order getOrderById(@PathVariable final String customerId, @PathVariable final String orderId) {
		return orderService.getOrderByIdForCustomer(customerId, orderId);
	}

	/**
	 * 
	 * @param customerId
	 * @return
	 */
	@GetMapping(value = "/{customerId}/orders", produces = { "application/hal+json" })
	public ResponseEntity<List<Order>> getOrdersForCustomer(@PathVariable final String customerId) {

		final List<Order> orders = orderService.getAllOrdersForCustomer(customerId);

		return ResponseEntity.ok(orders);
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping(produces = { "application/hal+json" })
	public ResponseEntity<List<CustomerVO>> getAllCustomers() {
		final List<CustomerVO> allCustomers = customerService.allCustomers();
  
		return ResponseEntity.ok(allCustomers);
	}

}
