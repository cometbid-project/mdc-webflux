/**
 * 
 */
package com.test.admin.hateoas.it;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Gbenga
 *
 */
@Configuration
@ComponentScan("com.test")
public class ConfigIntegrationTest implements WebFluxConfigurer {

    public ConfigIntegrationTest() {
        super();
    }

    // API

}
