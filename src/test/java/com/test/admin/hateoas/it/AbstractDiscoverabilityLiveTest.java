/**
 * 
 */
package com.test.admin.hateoas.it;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Serializable;
import java.util.Arrays;

import org.hamcrest.core.AnyOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.ndportmann.mdc_webflux.service.model.Foo;
import com.test.admin.web.util.HTTPLinkHeaderUtil;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author Gbenga
 *
 */
public abstract class AbstractDiscoverabilityLiveTest<T extends Serializable> extends AbstractLiveTest<T> {

	public AbstractDiscoverabilityLiveTest(final Class<T> clazzToSet) {
		super(clazzToSet);
	}

	// tests

	// discoverability

	@Test
	public void whenInvalidPOSTIsSentToValidURIOfResource_thenAllowHeaderListsTheAllowedActions() {
		// Given
		final String uriOfExistingResource = createAsUri();

		// When
		final Response res = RestAssured.post(uriOfExistingResource);

		// Then
		final String allowHeader = res.getHeader(HttpHeaders.ALLOW);

		Assertions.assertTrue(Arrays.asList("GET", "PUT", "DELETE").stream().anyMatch(allowHeader::contains));
	}

	@Test
	public void whenResourceIsCreated_thenUriOfTheNewlyCreatedResourceIsDiscoverable() {
		// When
		final Foo newResource = new Foo(randomAlphabetic(6));
		final Response createResp = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(newResource)
				.post(getURL());
		final String uriOfNewResource = createResp.getHeader(HttpHeaders.LOCATION);

		// Then
		final Response response = RestAssured.given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.get(uriOfNewResource);

		final Foo resourceFromServer = response.body().as(Foo.class);
		assertEquals(newResource, resourceFromServer);
	}

	@Test
	public void whenResourceIsRetrieved_thenUriToGetAllResourcesIsDiscoverable() {
		// Given
		final String uriOfExistingResource = createAsUri();

		// When
		final Response getResponse = RestAssured.get(uriOfExistingResource);

		// Then
		final String uriToAllResources = HTTPLinkHeaderUtil.extractURIByRel(getResponse.getHeader("Link"),
				"collection");

		final Response getAllResponse = RestAssured.get(uriToAllResources);
		assertEquals(getAllResponse.getStatusCode(), 200);
	}

	// template method

}
