/**
 * 
 */
package com.test.springdata.redis.operations;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author Gbenga
 *
 */
@JsonTypeInfo(use = Id.CLASS, property = "_type")
public record EmailAddress(String address) {}
