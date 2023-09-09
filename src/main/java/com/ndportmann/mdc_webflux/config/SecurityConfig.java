/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * @author Gbenga 
 *
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

	private static final String NO_RESTRICT_WELCOME_URI = "/welcome";
	private static final String DEFAULT_ADMIN_SECRET = "admSecret";
	private static final String DEFAULT_ADMIN_ID = "admin";
	private static final String DEFAULT_USER_ROLE = "USER";
	private static final String DEFAULT_ADMIN_ROLE = "ADMIN";
	private static final String DEFAULT_USER_SECRET = "usrSecret";
	private static final String DEFAULT_USER_ID = "user";

	private static final String[] USER_PATH = {"/investors/invr*/**"};
	private static final String[] ADMIN_PATH = {"/investors/admin"};
	private static final String[] AUTH_WHITELIST = {
	        "/login", "/*/auth/register", "/run-job", "/schedule-job",
	        "/*/auth/refresh", "/logout", 
	        "/resources/**", "/webjars/**"};

	@Autowired
	private ServerAccessDeniedHandler accessDeniedHandler;
	
	/*
	@Bean
	protected void configure(AuthenticationManagerBuilder authMgrBldr) throws Exception {
		authMgrBldr.inMemoryAuthentication()
				.passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance())
				.withUser(DEFAULT_USER_ID).password(DEFAULT_USER_SECRET).authorities(DEFAULT_USER_ROLE).and()
				.withUser(DEFAULT_ADMIN_ID).password(DEFAULT_ADMIN_SECRET)
				.authorities(DEFAULT_USER_ROLE, DEFAULT_ADMIN_ROLE);
	}
	*/
	
	@Bean
	public ServerLogoutSuccessHandler logoutSuccessHandler(){
	      RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
	      handler.setLogoutSuccessUrl(URI.create("/"));
	      return handler;
	}
	
	// roles admin allow to access /admin/**
    // roles user allow to access /user/**
    // custom 403 access denied handler
    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {

        http.httpBasic().disable()
                //.formLogin().disable()
                .csrf().disable();
        		//.logout().disable();

        http.authorizeExchange()
                .pathMatchers("/admin/**").hasAnyRole("ADMIN")
                .pathMatchers("/user/**").hasAnyRole("USER")
                .pathMatchers("/", "/home", "/about", "/index").permitAll()
                .pathMatchers(AUTH_WHITELIST).permitAll()                
                .anyExchange()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")               
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .logoutUrl("/logout")
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);
        
        return http.build();
    }

    // create two users, admin and user
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {

        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user, admin);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // with new spring security 5
    }
   
}
