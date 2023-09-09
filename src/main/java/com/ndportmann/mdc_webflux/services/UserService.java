/**
 * 
 */
package com.ndportmann.mdc_webflux.services;

import com.ndportmann.mdc_webflux.service.model.MfaTokenData;
import com.ndportmann.mdc_webflux.service.model.UserData;

import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public interface UserService {

	Mono<UserData> register(UserData user);

	Mono<MfaTokenData> mfaSetup(String email);

	Mono<Boolean> checkIfUserExist(final String email);

	//Mono<Void> sendRegistrationConfirmationEmail(final UserData user);

	//Mono<Boolean> verifyUser(final String token);// throws InvalidTokenException;

	Mono<UserData> getUserById(final String id);// throws UnkownIdentifierException;

}
