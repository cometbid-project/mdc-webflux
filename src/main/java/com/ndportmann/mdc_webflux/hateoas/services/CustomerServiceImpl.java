/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ndportmann.mdc_webflux.service.model.vo.CustomerVO;

/**
 * @author Gbenga
 *
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	private HashMap<String, CustomerVO> customerMap;

	public CustomerServiceImpl() {

		customerMap = new HashMap<>();

		final CustomerVO customerOne = new CustomerVO("10A", "Jane", "Abe", null, "ABC Company", null);
		final CustomerVO customerTwo = new CustomerVO("20B", "Bob", "Markifi", null, "XYZ Company", null);
		final CustomerVO customerThree = new CustomerVO("30C", "Tim", "Antonio", null, "CKV Company", null);

		customerMap.put("10A", customerOne);
		customerMap.put("20B", customerTwo);
		customerMap.put("30C", customerThree);

	}

	@Override
	public List<CustomerVO> allCustomers() {
		return new ArrayList<>(customerMap.values());
	}

	@Override
	public CustomerVO getCustomerDetail(final String customerId) {
		return customerMap.get(customerId);
	}

	public Page<CustomerVO> getPagedAllCustomers(Pageable pageable) {
		List<CustomerVO> customerList = new ArrayList<>(customerMap.values()); 
		
		Sort sort = pageable.getSort();
		Order order = sort.get().findFirst().orElseThrow();
		
		String field = order.getProperty(); 
		Direction dir = order.getDirection();
		
		customerList.sort((p1, p2) -> {
			if (p1.getCompanyName().equalsIgnoreCase(p2.getCompanyName())) {
				return p1.getCompanyName().compareTo(p2.getCompanyName());
			} else {
				return p1.getCompanyName().compareTo(p2.getCompanyName());
			}
		});

		int start = (int) pageable.getOffset();
		int end = (int) ((start + pageable.getPageSize()) > customerList.size() ? customerList.size()
				: (start + pageable.getPageSize()));

		return new PageImpl<CustomerVO>(customerList.subList(start, end), pageable, customerList.size());
	}
}
