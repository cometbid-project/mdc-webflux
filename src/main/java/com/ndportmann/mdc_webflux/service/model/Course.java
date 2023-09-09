/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gbenga
 *
 */
@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Course {

	@Id
	private String id;

	private String name;

	private String category;

	private int rating;

	private String description;

	private String courseId;
	
	@Builder.Default
	private long created = Instant.now().getEpochSecond();
	
	private String courseName;

	public Course(String courseName) {
		this.courseName = courseName;
		
		if (StringUtils.isBlank(courseId)) {
			courseId = UUID.randomUUID().toString();
		}
	}

}
