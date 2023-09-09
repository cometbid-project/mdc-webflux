/**
 * 
 */
package com.test.admin.hateoas.it;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.is;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import static org.junit.jupiter.api.Assertions.*;
import com.ndportmann.mdc_webflux.service.model.Foo;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * @author Gbenga
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigIntegrationTest.class }, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class FooPageableLiveTest extends AbstractBasicLiveTest<Foo> {

    public FooPageableLiveTest() {
        super(Foo.class);
    }

    // API

    @Override
    public final void create() {
        super.create(new Foo(randomAlphabetic(6)));
    }

    @Override
    public final String createAsUri() {
        return createAsUri(new Foo(randomAlphabetic(6)));
    }

    @Override
    @Test
    public void whenResourcesAreRetrievedPaged_then200IsReceived() {
        this.create();

        final Response response = RestAssured.get(getPageableURL() + "?page=0&size=10");

        assertEquals(response.getStatusCode(), 200);
    }

    @Override
    @Test
    public void whenPageOfResourcesAreRetrievedOutOfBounds_then404IsReceived() {
        final String url = getPageableURL() + "?page=" + randomNumeric(5) + "&size=10";
        final Response response = RestAssured.get(url);

        assertEquals(response.getStatusCode(), 404);
    }

    @Override
    @Test
    public void givenResourcesExist_whenFirstPageIsRetrieved_thenPageContainsResources() {
        create();

        final Response response = RestAssured.given()
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .get(getPageableURL() + "?page=0&size=10");

        assertFalse(response.body().as(List.class).isEmpty());
    }

    protected String getPageableURL() {
        return getURL() + "/pageable";
    }

}