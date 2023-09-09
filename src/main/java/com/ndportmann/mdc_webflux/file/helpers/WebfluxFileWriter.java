/**
 * 
 */
package com.ndportmann.mdc_webflux.file.helpers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Component
@RequiredArgsConstructor
public class WebfluxFileWriter {

	private final FileProcessor fileProcessor;

	private static final String OUTPUT_FILEPATH = "/some/path/large-output-file.txt";

	public Mono<Void> writeToFile(String filePath) {// input file
		// output file

		Mono<BufferedWriter> monoBw = openAFile(filePath);

		Flux<String> stringFlux = fileProcessor.processFile(filePath);
		
		 monoBw.subscribe(bw -> {
			 stringFlux.subscribe(s -> 			    
				write(bw, s),
				(e) -> close(bw), // close file if error / oncomplete
				() -> close(bw));   			
		 });
		
		return Mono.empty();
	}

	private void close(Closeable closeable) {
		try {
			closeable.close();
			System.out.println("Closed the resource");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void write(BufferedWriter bw, String string) {
		try {
			bw.write(string);
			bw.newLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Mono<BufferedWriter> openAFile(String filePath) {
		Path opPath = Paths.get(filePath);

		return Mono.just(opPath).flatMap((path) -> {

			BufferedWriter buf = null;
			try {
				buf = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();

				return Mono.error(e);
			}

			return Mono.justOrEmpty(buf);
		});

	}
}
