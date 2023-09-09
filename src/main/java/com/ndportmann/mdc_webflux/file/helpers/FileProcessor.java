/**
 * 
 */
package com.ndportmann.mdc_webflux.file.helpers;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Gbenga
 *
 */
@Component
public class FileProcessor {

	private static final String FILE_PATH = "/some/path/large-input-file.txt";
	
	public Flux<String> processFile(String filePath) {// input file
		Path ipPath = Paths.get(filePath);

		Flux<String> stringFlux = Flux.using(
		        () -> Files.lines(ipPath),
		        Flux::fromStream,
		        Stream::close
		)
		.subscribeOn(Schedulers.newParallel("file-copy", 3))
		.share();
		
		return stringFlux;
	}
	
	
	public Flux<String> processFile(URI fileUri) {// input file
		Path ipPath = Paths.get(fileUri);

		Flux<String> stringFlux = Flux.using(
		        () -> Files.lines(ipPath),
		        Flux::fromStream,
		        Stream::close
		)
		.subscribeOn(Schedulers.newParallel("file-copy", 3))
		.share();
		
		return stringFlux;
	}
}
