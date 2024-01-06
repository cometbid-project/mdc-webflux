/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Transient;

import jakarta.validation.constraints.NotNull;

/**
 * @author Gbenga
 *
 */
//@Entity
//@Table(name = "secureTokens")
public class SecureToken extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3915656574772975319L;

	// @Column(unique = true)
	private String token;

	// @CreationTimestamp
	// @Column(updatable = false)
	private Timestamp timeStamp;

	// @Column(updatable = false)
	// @Basic(optional = false)
	private LocalDateTime expireAt;

	// @ManyToOne
	// @JoinColumn(name = "customer_id", referencedColumnName = "id")
	private UserData user;

	@Transient
	private boolean isExpired;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(LocalDateTime expireAt) {
		this.expireAt = expireAt;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public boolean isExpired() {
		// this is generic implementation, you can always make it
		// timezone specific
		return getExpireAt().isBefore(LocalDateTime.now());
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	/**
	 * @param id
	 * @param token
	 * @param timeStamp
	 * @param expireAt
	 * @param user
	 * @param isExpired
	 */
	public SecureToken(@NotNull String id, String token, Timestamp timeStamp, LocalDateTime expireAt, UserData user,
			boolean isExpired) {
		super(id);
		this.token = token;
		this.timeStamp = timeStamp;
		this.expireAt = expireAt;
		this.user = user;
		this.isExpired = isExpired;
	}

	/**
	 * @param id
	 */
	public SecureToken(@NotNull String id) {
		super(id);
	}

	/**
	 * @param id
	 */
	public SecureToken() {
		this(null);
	}
	
}
