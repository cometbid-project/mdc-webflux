/**
 * 
 */
package com.ndportmann.mdc_webflux.controller;

import java.net.URI;

import javax.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.ndportmann.mdc_webflux.client.ReactiveClientInterface;
import com.ndportmann.mdc_webflux.config.AuthClientProperties;
import com.ndportmann.mdc_webflux.helpers.ApiPaths;
import com.ndportmann.mdc_webflux.service.model.EmployeeModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@RestController
@RequestMapping("/employeeClient")
public class EmployeeClientController {

	private final WebClient webClient;

	private String employeeHost;

	private final AuthClientProperties clientProperties;
	private final ReactiveClientInterface reactiveClient;

	@Autowired
	public EmployeeClientController(@Qualifier("employeeWebClient") WebClient webClient,
			ReactiveClientInterface reactiveClient, AuthClientProperties clientProperties) {
		this.webClient = webClient;
		this.clientProperties = clientProperties;
		this.reactiveClient = reactiveClient;
	}
	
	@PostConstruct
	private void init() {		
		employeeHost = clientProperties.getEmployeeHost();
	}

	@GetMapping("/{employeeId}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<EmployeeModel> getEmployeeById(@PathVariable("employeeId") String employeeId) {
		String url = ApiPaths.getEmployeePath(employeeHost) + "/" + employeeId;
		return reactiveClient.performGetToMono(webClient, URI.create(url), EmployeeModel.class, null, null);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<EmployeeModel> getEmployeeList() {
		return reactiveClient.performGetToFlux(webClient, URI.create(ApiPaths.getEmployeePath(employeeHost)),
				EmployeeModel.class, null, null);				
	}
	
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<EmployeeModel> findAll() {
		return reactiveClient.performGetToFlux(webClient, URI.create(ApiPaths.getEmployeePath(employeeHost)),
				EmployeeModel.class, null, null);		
    }

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<EmployeeModel> saveEmployee(@RequestBody Mono<EmployeeModel> employeeModel) {
		return reactiveClient.performPostToMono(webClient, URI.create(ApiPaths.getEmployeePath(employeeHost)),
				employeeModel, EmployeeModel.class, null, null);
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<EmployeeModel> updateEmployee(@RequestBody EmployeeModel employeeModel) {
		return reactiveClient.performPutToMono(webClient, URI.create(ApiPaths.getEmployeePath(employeeHost)),
				employeeModel, EmployeeModel.class, null, null);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<EmployeeModel> deleteEmployee(@PathVariable("id") Long employeeId) {
		String url = ApiPaths.getEmployeePath(employeeHost) + "/" + employeeId;
		return reactiveClient.performDeleteToMono(webClient, URI.create(url), EmployeeModel.class, null, null);
	}
}