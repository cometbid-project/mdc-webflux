/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

/**
 * @author Gbenga
 *
 */
@RedisHash
@Data
@EqualsAndHashCode(callSuper = true)
public class Book extends Entity {

	private String title;

	private int page;

	private String isbn;

	private String description;

	private double price;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate publicationDate;

	private String language;

	/**
	 * @param title
	 * @param page
	 * @param isbn
	 * @param description
	 * @param price
	 * @param publicationDate
	 * @param language
	 */
	@JsonCreator
	@Builder
	public Book(String id, String title, int page, String isbn, String description, double price,
			LocalDate publicationDate, String language) {

		super(id);
		this.title = title;
		this.page = page;
		this.isbn = isbn;
		this.description = description;
		this.price = price;
		this.publicationDate = publicationDate;
		this.language = language;
	}

	/**
	 * @param id
	 */
	private Book(@NotNull String id) {
		super(id);
	}

}
