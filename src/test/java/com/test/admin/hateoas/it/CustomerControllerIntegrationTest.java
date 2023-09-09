/**
 * 
 */
package com.test.admin.hateoas.it;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.ndportmann.mdc_webflux.hateoas.services.CustomerService;
import com.ndportmann.mdc_webflux.hateoas.services.OrderService;
import com.ndportmann.mdc_webflux.hateoas.web.controller.CustomerController;
import com.ndportmann.mdc_webflux.service.model.Order;
import com.ndportmann.mdc_webflux.service.model.vo.CustomerVO;

/**
 * @author Gbenga
 *
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CustomerController.class})
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private OrderService orderService;

    private static final String DEFAULT_CUSTOMER_ID = "customer1";
    private static final String DEFAULT_ORDER_ID = "order1";

    @Test
    public void givenExistingCustomer_whenCustomerRequested_thenResourceRetrieved() throws Exception {
        given(this.customerService.getCustomerDetail(DEFAULT_CUSTOMER_ID))
            .willReturn(new CustomerVO(DEFAULT_CUSTOMER_ID, "customerJohn", "companyOne"));

        this.mvc.perform(get("/customers/" + DEFAULT_CUSTOMER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links").doesNotExist())
            .andExpect(jsonPath("$.customerId", is(DEFAULT_CUSTOMER_ID)));
    }

    @Test
    public void givenExistingOrder_whenOrderRequested_thenResourceRetrieved() throws Exception {
        given(this.orderService.getOrderByIdForCustomer(DEFAULT_CUSTOMER_ID, DEFAULT_ORDER_ID))
            .willReturn(new Order(DEFAULT_ORDER_ID, 1., 1));

        this.mvc.perform(get("/customers/" + DEFAULT_CUSTOMER_ID + "/" + DEFAULT_ORDER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links").doesNotExist())
            .andExpect(jsonPath("$.orderId", is(DEFAULT_ORDER_ID)));
    }

    @Test
    public void givenExistingCustomerWithOrders_whenOrdersRequested_thenHalResourceRetrieved() throws Exception {
        Order order1 = new Order(DEFAULT_ORDER_ID, 1., 1);
        List<Order> orders = Collections.singletonList(order1);
        given(this.orderService.getAllOrdersForCustomer(DEFAULT_CUSTOMER_ID)).willReturn(orders);

        this.mvc.perform(get("/customers/" + DEFAULT_CUSTOMER_ID + "/orders").accept(MediaTypes.HAL_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.orders[0]._links.self.href",
                is("http://localhost/customers/customer1/order1")))
            .andExpect(jsonPath("$._links.self.href", is("http://localhost/customers/customer1/orders")));
    }

    @Test
    public void givenExistingCustomer_whenAllCustomersRequested_thenHalResourceRetrieved() throws Exception {
        // customers
        CustomerVO retrievedCustomer = new CustomerVO(DEFAULT_CUSTOMER_ID, "customerJohn", "companyOne");
        List<CustomerVO> customers = Collections.singletonList(retrievedCustomer);
        given(this.customerService.allCustomers()).willReturn(customers);
        // orders
        Order order1 = new Order(DEFAULT_ORDER_ID, 1., 1);
        List<Order> orders = Collections.singletonList(order1);
        given(this.orderService.getAllOrdersForCustomer(DEFAULT_CUSTOMER_ID)).willReturn(orders);

        this.mvc.perform(get("/customers/").accept(MediaTypes.HAL_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$._embedded.customers[0]._links.self.href", is("http://localhost/customers/customer1")))
            .andExpect(jsonPath("$._embedded.customers[0]._links.allOrders.href",
                is("http://localhost/customers/customer1/orders")))
            .andExpect(jsonPath("$._links.self.href", is("http://localhost/customers")));
    }

}
