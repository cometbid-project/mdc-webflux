/**
 * 
 */
package com.ndportmann.mdc_webflux.orm.jpa;

//import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndportmann.mdc_webflux.service.model.Role;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Gbenga
 *
 */
//@Entity
//@Table(name = "_user")
public class User extends AbstractEntity<UserId> implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7942567671253409530L;

	private static final String ROLE_PREFIX = "ROLE_";
	
	private String email;
	private String password;
	
	private boolean accountLocked; //
	private boolean disabled;
	private boolean expired;
	private boolean emailVerified;

	// @ElementCollection(fetch = FetchType.EAGER)
	// @Enumerated(EnumType.STRING)
	@NotNull
	private Set<Role> roles;

	protected User() {
		super(null);
		this.email = null;
		this.password = null;
		this.roles = null;
	}

	public User(UserId id, String email, String password, Set<Role> roles) {
		super(id);
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public Set<Role> getRoles() {
		return roles;
	}
	
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream().map(authority -> new SimpleGrantedAuthority(ROLE_PREFIX + authority.toString())).collect(Collectors.toSet());
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !this.expired;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !this.accountLocked;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !this.emailVerified;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return !this.disabled;
	}
}