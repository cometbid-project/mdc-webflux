/**
 * 
 */
package com.test.springdata.redis.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.test.context.ContextConfiguration;

import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.ndportmann.mdc_webflux.MdcWebfluxApplication;
import com.ndportmann.mdc_webflux.config.RedisConfiguration;
import com.ndportmann.mdc_webflux.repository.PersonRepositoryImpl;
import com.ndportmann.mdc_webflux.repository.ReactiveRedisComponent;
import com.ndportmann.mdc_webflux.service.model.Address;
import com.ndportmann.mdc_webflux.service.model.Gender;
//import com.ndportmann.mdc_webflux.service.model.Gender;
import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.service.model.Point;
import com.test.springdata.redis.condition.EnabledOnRedisAvailable;
//import com.test.springdata.redis.operations.Person;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Gbenga
 *
 */
@Log4j2
@DataRedisTest(properties = { "spring.redis.password=", "spring.redis.host=localhost", "spring.redis.port=8850" })
@DisplayName("Person redis repo test")
@ContextConfiguration(classes = { RedisConfiguration.class, MdcWebfluxApplication.class })
@Import({ PersonRepositoryImpl.class, ReactiveRedisComponent.class })
//@EnabledOnRedisAvailable
class PersonRepositoryTests extends AbstractRedisRepositoryTest {

	/** {@link Charset} for String conversion **/
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	@Autowired
	private PersonRepositoryImpl repository;

	private Faker faker = Faker.instance();
	// private Person savedUser;

	/*
	 * Set of test users
	 */
	private Person eddard = new Person(String.valueOf(1L), "eddard", "stark", Gender.MALE, createAddr(), listPerson());
	private Person robb = new Person(String.valueOf(2L), "robb", "stark", Gender.FEMALE, createAddr(), listPerson());
	private Person sansa = new Person(String.valueOf(3L), "sansa", "stark", Gender.MALE, createAddr(), listPerson());
	private Person arya = new Person(String.valueOf(4L), "arya", "gregory", Gender.FEMALE, createAddr(), listPerson());
	private Person bran = new Person(String.valueOf(5L), "bran", "Heskchel", Gender.MALE, createAddr(), listPerson());
	private Person rickon = new Person(String.valueOf(6L), "rickon", "boss", Gender.FEMALE, createAddr(), listPerson());
	private Person jon = new Person(String.valueOf(7L), "jon", "snow", Gender.MALE, createAddr(), listPerson());

	@BeforeEach
	@AfterEach
	void before() {
		StepVerifier.create(repository.deleteAll()).verifyComplete();

		// StepVerifier.create(operations.execute(it ->
		// it.serverCommands().flushDb())).expectNext("OK").verifyComplete();
	}

	@AfterAll
	static void shutDown() {
		/*
		 * operations.execute((ReactiveRedisConnection connection) -> {
		 * connection.close(); return Mono.just("OK"); });
		 */
	}

	/**
	 * Save a single entity and verify that a key for the given keyspace/prefix
	 * exists. <br />
	 * Print out the hash structure within Redis.
	 */
	@Test
	void saveSingleEntity() {

		// Verify that we can save, store the created user into the savedUser variable
		// and compare the saved user.
		StepVerifier.create(repository.save(eddard))
				.expectNextMatches(createdUser -> assertEqualUser(eddard, createdUser)).verifyComplete();

		// Verify the number of entities in the database
		StepVerifier.create(repository.count()).expectNext(1L).verifyComplete();

		// Verify we can get back the User by using findById method
		StepVerifier.create(repository.findById(eddard.getId()))
				.expectNextMatches(foundUser -> assertEqualUser(eddard, foundUser)).verifyComplete();
	}

	/**
	 * Find entity by a single {@link Indexed} property value.
	 */
	@Test
	void findBySingleProperty() {

		flushTestUsers();

		StepVerifier.create(repository.findByLastname(eddard.getLastname()).collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).contains(eddard, robb, sansa).doesNotContain(jon, rickon, arya, bran);
		}).verifyComplete();
	}

	/**
	 * Find entities by multiple {@link Indexed} properties using {@literal AND}.
	 */
	@Test
	void findByMultipleProperties() {

		flushTestUsers();

		var aryaStark = repository.findByFirstnameAndLastname(arya.getFirstname(), arya.getLastname());

		StepVerifier.create(aryaStark.collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).containsOnly(arya);
		}).verifyComplete();
	}

	/**
	 * Find entities by multiple {@link Indexed} properties using {@literal OR}.
	 */
	@Test
	void findByMultiplePropertiesUsingOr() {

		flushTestUsers();

		var aryaAndJon = repository.findByFirstnameOrLastname(arya.getFirstname(), jon.getLastname());

		StepVerifier.create(aryaAndJon.collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).containsOnly(arya, jon);
		}).verifyComplete();

	}

	/**
	 * Find entities in range defined by {@link Pageable}.
	 */
	@Test
	void findByReturningPage() {

		flushTestUsers();

		Comparator<Person> lastNameComparator = (o1, o2) -> o1.getLastname().compareTo(o2.getLastname());

		var page1 = repository.findByLastnamePaginated(eddard.getLastname(), PageRequest.of(0, 5), lastNameComparator);

		StepVerifier.create(page1).assertNext(page -> {
			log.info("Resulting list: {}", page);
			
			assertThat(page.getNumberOfElements()).isEqualTo(5);
			assertThat(page.getTotalElements()).isEqualTo(6);
		}).verifyComplete();

		// =============================================================================================

		var page2 = repository.findByLastnamePaginated(eddard.getLastname(), PageRequest.of(1, 5), lastNameComparator);

		StepVerifier.create(page2).assertNext(page -> {
			log.info("Resulting list: {}", page);
			assertThat(page.getNumberOfElements()).isEqualTo(1);
			assertThat(page.getTotalElements()).isEqualTo(6);
		}).verifyComplete();

	}

	/**
	 * Find entity by a single {@link Indexed} property on an embedded entity.
	 */
	@Test
	void findByEmbeddedProperty() {

		var winterfell = Address.builder().country("the north").city("winterfell").build();
		// winterfell.setCountry("the north");
		// winterfell.setCity("winterfell");

		eddard.setAddress(winterfell);

		flushTestUsers();

		var eddardStark = repository.findByAddress_City(winterfell.getCity());

		StepVerifier.create(eddardStark.collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).containsOnly(eddard);
		}).verifyComplete();

	}

	/**
	 * Find entity by a {@link GeoIndexed} property on an embedded entity.
	 */
	@Test
	void findByGeoLocationProperty() {

		var winterfell = Address.builder().country("the north").city("winterfell")
				.location(new Point(52.9541053, -1.2401016)).build();

		eddard.setAddress(winterfell);

		var casterlystein = Address.builder().country("Westerland").city("Casterlystein")
				.location(new Point(51.5287352, -0.3817819)).build();

		robb.setAddress(casterlystein);

		flushTestUsers();

		// Point srcPoint = new Point(51.8911912, -0.4979756);
		org.springframework.data.geo.Point targetPoint = new org.springframework.data.geo.Point(51.8911912, -0.4979756);

		var innerCircle = new Circle(targetPoint, new Distance(50, Metrics.KILOMETERS));
		var eddardStark = repository.findByAddress_LocationWithin(innerCircle);

		StepVerifier.create(eddardStark.collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).containsOnly(robb);
		}).verifyComplete();

		var biggerCircle = new Circle(targetPoint, new Distance(200, Metrics.KILOMETERS));
		var eddardAndRobbStark = repository.findByAddress_LocationWithin(biggerCircle);

		StepVerifier.create(eddardAndRobbStark.collectList()).assertNext(starks -> {
			log.info("Resulting list: {}", starks);
			assertThat(starks).hasSize(2).contains(robb, eddard);
		}).verifyComplete();
	}

	/**
	 * Store references to other entities without embedding all data. <br />
	 * Print out the hash structure within Redis.
	 */
	@Test
	void useReferencesToStoreDataToOtherObjects() {

		flushTestUsers();

		eddard.setChildren(Arrays.asList(jon, robb, sansa, arya, bran, rickon));

		repository.save(eddard);

		StepVerifier.create(repository.findById(eddard.getId())).assertNext(it -> {
			log.info("Result: {}", it);

			assertThat(it.getChildren()).contains(jon, robb, sansa, arya, bran, rickon);

		}).verifyComplete();

		/*
		 * Deceased:
		 *
		 * - Robb was killed by Roose Bolton during the Red Wedding. - Jon was stabbed
		 * by brothers or the Night's Watch.
		 */
		repository.deleteAll(Arrays.asList(robb, jon));

		StepVerifier.create(repository.findById(eddard.getId())).assertNext(it -> {
			log.info("Result: {}", it);
			assertThat(it.getChildren()).contains(sansa, arya, bran, rickon);
			assertThat(it.getChildren()).doesNotContain(robb, jon);
		}).verifyComplete();

	}

	private void flushTestUsers() {

		StepVerifier.create(repository.saveAll(Arrays.asList(eddard, robb, sansa, arya, bran, rickon, jon)))
				.expectNextMatches(foundUser -> assertEqualUser(eddard, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(robb, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(sansa, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(arya, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(bran, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(rickon, foundUser))
				.expectNextMatches(foundUser -> assertEqualUser(jon, foundUser)).verifyComplete();

		// repository.saveAll(Arrays.asList(eddard, robb, sansa, arya, bran, rickon,
		// jon));
	}

	// Personal method used in the tests above to compare the User entity.
	private boolean assertEqualUser(Person expectedUser, Person actualUser) {
		Assertions.assertEquals(expectedUser.getId(), actualUser.getId());
		Assertions.assertEquals(expectedUser.getFirstname(), actualUser.getFirstname());
		Assertions.assertEquals(expectedUser.getLastname(), actualUser.getLastname());
		Assertions.assertEquals(expectedUser.getGender(), actualUser.getGender());

		return (expectedUser.getId().equals(actualUser.getId()))
				&& (expectedUser.getFirstname().equals(actualUser.getFirstname()))
				&& (expectedUser.getLastname().equals(actualUser.getLastname()))
				&& (expectedUser.getGender() == actualUser.getGender())
				&& (expectedUser.getVersion() == actualUser.getVersion());
	}

	private Address createAddr() {
		String longitude = faker.address().longitude();
		String latitude = faker.address().latitude();

		Point point = new Point(Double.valueOf(longitude), Double.valueOf(latitude));
		return Address.builder().city(faker.address().city()).country(faker.address().country()).location(point)
				.build();
	}

	private Person createPerson() {
		Gender gender = faker.bool().bool() ? Gender.FEMALE : Gender.MALE;
		return new Person(null, faker.name().firstName(), faker.name().lastName(), gender, createAddr());
	}

	private List<Person> listPerson() {
		return Lists.newArrayList(createPerson(), createPerson());
	}
}
