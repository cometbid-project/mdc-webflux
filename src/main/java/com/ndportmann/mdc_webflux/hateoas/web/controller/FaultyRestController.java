/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gbenga
 *
 */
@RestController
public class FaultyRestController {
    
    @GetMapping("/exception")
    public ResponseEntity<Void> requestWithException() {
        throw new RuntimeException("Error in the faulty controller!");
    }

}
