/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import org.springframework.data.redis.core.RedisHash;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

/**
 * @author Gbenga
 *
 */
@Data
@RedisHash
//@Document
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserData extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5046049463771539635L;

	@NotEmpty(message = "{registration.validation.firstName}")
	private String firstName;

	@NotEmpty(message = "{registration.validation.lastName}")
	private String lastName;

	//@NotEmpty(message = "{registration.validation.username}")
	private String username; // We will make this to be unique

	@NotEmpty(message = "Email can not be empty")
	@Email(message = "{registration.validation.email}")
	private String email; // We will make this to be unique

	@NotEmpty(message = "{registration.validation.password}")
	private String password;
	
	private String token;
	private boolean loginDisabled;
    private boolean accountVerified;
	private boolean mfaEnabled;
    private String secret;

	
	/**
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param username
	 * @param email
	 * @param password
	 * @param version
	 */
	public UserData(String id, @NotEmpty(message = "{registration.validation.firstName}") String firstName,
			@NotEmpty(message = "{registration.validation.lastName}") String lastName,
			@NotEmpty(message = "{registration.validation.username}") String username,
			@Email(message = "{registration.validation.email}") String email,
			@NotEmpty(message = "{registration.validation.password}") String password) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		
		this.mfaEnabled = false;
		this.accountVerified = false;
		this.loginDisabled = false;
		this.secret = null;
		this.token = null;
	}

	/**
	 * @param firstName
	 * @param lastName
	 * @param username
	 * @param email
	 * @param password
	 * @param version
	 */
	public UserData(@NotEmpty(message = "{registration.validation.firstName}") String firstName,
			@NotEmpty(message = "{registration.validation.lastName}") String lastName,
			@NotEmpty(message = "{registration.validation.username}") String username,
			@Email(message = "{registration.validation.email}") String email,
			@NotEmpty(message = "{registration.validation.password}") String password) {
		super(null);
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		
		this.mfaEnabled = false;
		this.loginDisabled = false;
		this.accountVerified = false;
		this.secret = null;
		this.token = null;
	}
	
	

	/**
	 * @param id
	 */
	public UserData() {
		this(null, null, null, null, null);
	}

	/**
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param username
	 * @param email
	 * @param password
	 * @param mfaEnabled
	 * @param secret
	 */
	@Builder
	public UserData(@NotNull String id, @NotEmpty(message = "{registration.validation.firstName}") String firstName,
			@NotEmpty(message = "{registration.validation.lastName}") String lastName,
			@NotEmpty(message = "{registration.validation.username}") String username,
			@Email(message = "{registration.validation.email}") String email,
			@NotEmpty(message = "{registration.validation.password}") String password, boolean mfaEnabled,
			String secret) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		
		this.mfaEnabled = false;
		this.loginDisabled = false;
		this.accountVerified = false;
		this.secret = null;
		this.token = null;
	}	

}
