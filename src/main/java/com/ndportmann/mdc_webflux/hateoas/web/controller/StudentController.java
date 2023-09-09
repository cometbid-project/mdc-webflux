/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.ndportmann.mdc_webflux.hateoas.services.StudentService;
import com.ndportmann.mdc_webflux.service.model.Student;

/**
 * @author Gbenga
 *
 */
@RestController
@RequestMapping("/students")
public class StudentController {

	@Autowired
	private StudentService service;

	/**
	 * 
	 * @return
	 */
	@GetMapping("/")
	public List<Student> read() {
		return service.readAll();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Student> read(@PathVariable("id") Long id) {
		Student foundStudent = service.read(id);
		if (foundStudent == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(foundStudent);
		}
	}

	/**
	 * 
	 * @param student
	 * @param uriBuilder
	 * @param webExchange
	 * @return
	 * @throws URISyntaxException
	 */
	@PostMapping("/")
	public ResponseEntity<Student> create(@RequestBody Student student, final UriComponentsBuilder uriBuilder,
			ServerWebExchange webExchange) throws URISyntaxException {
		Student createdStudent = service.create(student);

		URI uri = uriBuilder.path("/{id}").buildAndExpand(createdStudent.getId()).toUri();

		return ResponseEntity.created(uri).body(createdStudent);
	}

	/**
	 * 
	 * @param student
	 * @param id
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Student> update(@RequestBody Student student, @PathVariable Long id) {
		Student updatedStudent = service.update(id, student);
		if (updatedStudent == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(updatedStudent);
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteStudent(@PathVariable Long id) {
		service.delete(id);

		return ResponseEntity.noContent().build();
	}

}
