/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ndportmann.mdc_webflux.service.model.Order;
import com.ndportmann.mdc_webflux.service.model.vo.CustomerVO;

/**
 * @author Gbenga
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	private HashMap<String, CustomerVO> customerMap;
	private HashMap<String, Order> customerOneOrderMap;
	private HashMap<String, Order> customerTwoOrderMap;
	private HashMap<String, Order> customerThreeOrderMap;

	public OrderServiceImpl() {

		customerMap = new HashMap<>();
		customerOneOrderMap = new HashMap<>();
		customerTwoOrderMap = new HashMap<>();
		customerThreeOrderMap = new HashMap<>();

		customerOneOrderMap.put("001A", new Order("001A", 150.00, 25));
		customerOneOrderMap.put("002A", new Order("002A", 250.00, 15));

		customerTwoOrderMap.put("002B", new Order("002B", 550.00, 325));
		customerTwoOrderMap.put("002B", new Order("002B", 450.00, 525));
		
		final CustomerVO customerOne = new CustomerVO("10A", "Jane", "Abe", null, "ABC Company", null);
		final CustomerVO customerTwo = new CustomerVO("20B", "Bob", "Markifi", null, "XYZ Company", null);
		final CustomerVO customerThree = new CustomerVO("30C", "Tim", "Antonio", null, "CKV Company", null);	

		customerOne.setOrders(customerOneOrderMap);
		customerTwo.setOrders(customerTwoOrderMap);
		customerThree.setOrders(customerThreeOrderMap);
		customerMap.put("10A", customerOne);
		customerMap.put("20B", customerTwo);
		customerMap.put("30C", customerThree);
	}

	@Override
	public List<Order> getAllOrdersForCustomer(final String customerId) {
		return new ArrayList<>(customerMap.get(customerId).getOrders().values());
	}

	@Override
	public Order getOrderByIdForCustomer(final String customerId, final String orderId) {
		final Map<String, Order> orders = customerMap.get(customerId).getOrders();
		Order selectedOrder = orders.get(orderId);
		return selectedOrder;
	}

}
