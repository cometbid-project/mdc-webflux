package com.ndportmann.mdc_webflux.controller;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ndportmann.mdc_webflux.exceptions.CustomHeaderNotFoundException;
import com.ndportmann.mdc_webflux.helpers.LocaleContextUtils;
import com.ndportmann.mdc_webflux.filters.TraceIdFilter;
import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.services.PersonServiceImpl;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.ndportmann.mdc_webflux.filters.MdcHeaderFilter.CONTEXT_MAP;

/**
 * 
 * @author Gbenga
 *
 */
@Log4j2
@RestController
public class DemoController {

	private static final Collector<CharSequence, ?, String> COLLECTOR = Collectors.joining("," + System.lineSeparator(),
			"[", "]");

	private final PersonServiceImpl fooService;
	private final MessageSource messageSource;

	public DemoController(PersonServiceImpl fooService, @Qualifier("messageSource") MessageSource messageSource) {
		this.fooService = fooService;
		this.messageSource = messageSource;
	}

	private static Mono<String> apply(String prefix) {

		return Mono.deferContextual(ct -> Mono.just(ct)).map(x -> x.<Map<String, String>>get(CONTEXT_MAP)).map(
				x -> prefix + x.entrySet().stream().map(kv -> kv.getKey() + ": " + kv.getValue()).collect(COLLECTOR));
	}

	@GetMapping("/demo")
	public Mono<String> demo() {
		return Mono.just("The context contains: " + System.lineSeparator()).flatMap(DemoController::apply);
	}

	@PostMapping("/demo/{clientId}")
	public Mono<String> clientDemo(@PathVariable String clientId) {
		return fooService.processRequestForClient(clientId);
	}

	@GetMapping("/msg")
	public Mono<String> getMessage(ServerWebExchange exchange) {

		String code = "exception.UnavailableService";

		return Mono.justOrEmpty(fooService.getMessage(code, new Object[] {}));
	}
	
	// bulk (patch) operation example with custom request header

	@PatchMapping("/investors/{investorId}/stocks")
	public ResponseEntity<Void> updateStockOfTheInvestorPortfolio(@PathVariable String investorId,
			@RequestHeader(value = "x-bulk-patch") Optional<Boolean> isBulkPatch,
			@RequestBody List<Person> stocksTobeUpdated) throws CustomHeaderNotFoundException {
		// without custom header we are not going to process this bulk operation
		if (!isBulkPatch.isPresent()) {
			throw new CustomHeaderNotFoundException("x-bulk-patch not found in your headers");
		}
		
		//investorService.bulkUpdateOfStocksByInvestorId(investorId, stocksTobeUpdated);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/demo/test")
	public Mono<String> merchantDemo(@RequestHeader("branch_code") String branchCode,
			@RequestHeader("rc_no") String rcNo, ServerWebExchange exchange, Locale locale) {

		log.info("Branch {}, {} {}", rcNo, branchCode, locale);
		log.info("Local locale {}", locale);

		String traceId = (String) exchange.getAttributes().getOrDefault(TraceIdFilter.TRACE_ID_KEY, "");
		log.info("Trace id {}", traceId);
		
		String anotherTraceId = ThreadContext.get(TraceIdFilter.TRACE_ID_KEY);
		log.info("Trace id {}", anotherTraceId); 
		
		String message = getLocalizedMessage("exception.UnavailableService", new Object[] {}, locale);

		return Mono.just(message);
	}
	
	@GetMapping("/locale")
	public Mono<String> getCurrentLocale() {

		Locale locale = LocaleContextUtils.getContextLocale();

		return Mono.just(locale.toLanguageTag()); 
	}

	protected String getLocalizedMessage(String messageKey, Object[] args, Locale locale) {

		return messageSource.getMessage(messageKey, args, locale);
	}
}
