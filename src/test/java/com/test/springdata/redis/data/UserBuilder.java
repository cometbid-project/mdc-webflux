/**
 * 
 */
package com.test.springdata.redis.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import com.github.javafaker.Faker;
import com.ndportmann.mdc_webflux.service.model.Role;
import com.ndportmann.mdc_webflux.service.model.UserData;

import lombok.Data;

/**
 * @author Gbenga
 *
 */
//@Data
public class UserBuilder {

	private Faker faker;

	private String id;

	private String username;

	private String email = "john.doe@example.com";

	private String password = "secret";

	private String firstName = "John";

	private String lastName = "Doe";

	private String displayName = "John";

	private List<Role> roles = new ArrayList<>();

	private UserBuilder() {
		faker = new Faker();

		this.id = UUID.randomUUID().toString();
		this.firstName = faker.name().firstName();
		this.lastName = faker.name().lastName();
		this.email = faker.internet().emailAddress();

		this.username = this.email;
		this.password = faker.internet().password();

		this.roles.add(Role.ROLE_ADMIN);
	}

	public static UserBuilder user() {
		return new UserBuilder();
	}

	public UserBuilder withId(UUID id) {
		this.id = id == null ? null : id.toString();
		return this;
	}

	public UserBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	public UserBuilder withEmail(String email) {
		this.email = email;
		return this;
	}

	public UserBuilder withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public UserBuilder withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public UserBuilder withRoles(List<Role> roles) {
		this.roles = roles;
		return this;
	}

	public UserBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	public UserBuilder withDisplayName() {
		this.displayName = displayName(firstName, lastName);
		return this;
	}

	public UserData build() {

		return UserData.builder().firstName(this.firstName).username(this.username).lastName(this.lastName)
				.email(this.email).password(this.password).build();
	}

	private String displayName(String firstName, String lastName) {
		String formattedFirstName = StringUtils.isBlank(firstName) ? "" : firstName;
		String formattedLastName = StringUtils.isBlank(lastName) ? "" : lastName;

		return WordUtils.capitalizeFully(formattedFirstName + " " + formattedLastName);
	}

}
