/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.util.Assert;
import com.ndportmann.mdc_webflux.helpers.ObjectMapperUtils;
import com.ndportmann.mdc_webflux.service.model.Entity;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

/**
 * @author Gbenga
 *
 */
@Log4j2
public abstract class AbstractRedisBaseRepository<T> implements ReactiveCrudRepository<T, String> {
	// ReactiveQueryByExampleExecutor<T>

	private final ReactiveRedisOperations<String, ? extends T> redisOperations;
	private final String KEY;
	private final Class<T> contentClass;

	public AbstractRedisBaseRepository(String key, Class<T> contentClass,
			ReactiveRedisOperations<String, T> redisOperations) {
		this.redisOperations = redisOperations;
		this.KEY = key;
		this.contentClass = contentClass;
	}

	/**
	 * 
	 * @param <S>
	 * @param entity
	 * @return
	 */
	public <S extends T> Mono<S> insert(S entity) {

		Assert.notNull(entity, "Entity must not be null!");

		if (!(entity instanceof Entity)) {
			return Mono.error(new RuntimeException("Must be an instance of Entity.class"));
		}
		// Assuming the Model extends Entity class
		String id = ((Entity) entity).getId();

		if (StringUtils.isBlank(id)) {
			log.info("id is null or empty");

			return generateId().flatMap(entityId -> {
				((Entity) entity).setId(entityId);
				((Entity) entity).setVersion(0);

				return redisOperations.opsForHash().put(KEY, entityId, entity).map(b -> entity);
			});

		} else {
			final String entityId = id;
			((Entity) entity).setId(entityId);
			((Entity) entity).setVersion(0);

			return this.existsById(id).flatMap(exist -> {
				if (exist) {
					return Mono.error(
							new DuplicateKeyException("Duplicate key, object with same id: " + entityId + " exists."));
				} else {
					return redisOperations.opsForHash().put(KEY, entityId, entity).map(b -> entity);
				}
			});
		}
	}

	/**
	 * 
	 */
	@Override
	public <S extends T> Mono<S> save(S entity) {
		Assert.notNull(entity, "Entity must not be null!");

		if (!(entity instanceof Entity)) {
			return Mono.error(new RuntimeException("Must be an instance of Entity.class"));
		}
		// Assuming the Model extends Entity class
		String id = ((Entity) entity).getId();

		if (StringUtils.isBlank(id)) {
			log.info("id is null or empty");
			return generateId().flatMap(entityId -> {
				((Entity) entity).setId(entityId);
				((Entity) entity).setVersion(0);

				return redisOperations.opsForHash().put(KEY, entityId, entity).map(b -> entity);
			});
		} else {
			final String entityId = id;

			return findById(id).flatMap(u -> {
				int version = ((Entity) entity).getVersion();
				int currVersion = ((Entity) u).getVersion();

				if (currVersion != version) {
					return Mono.error(new OptimisticLockingFailureException(
							"This record has already been updated earlier by another object."));
				} else {
					((Entity) entity).setVersion(currVersion + 1);

					return redisOperations.opsForHash().put(KEY, entityId, entity).map(isSaved -> entity);
				}
			}).switchIfEmpty(redisOperations.opsForHash().put(KEY, entityId, entity).map(b -> entity));

		}
	}

	@Override
	public Mono<T> findById(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id), "The given Id must not be empty!");

		return redisOperations.opsForHash().get(KEY, id)
				.flatMap(d -> Mono.just(ObjectMapperUtils.objectMapper(d, contentClass)));
	}

	@Override
	public Flux<T> findAll() {
		return redisOperations.opsForHash().values(KEY).map(b -> ObjectMapperUtils.objectMapper(b, contentClass))
				.collectList().flatMapMany(Flux::fromIterable);
	}

	// @Override
	public Mono<Page<T>> findAllPaginated(Pageable pageable, Comparator<T> comparator) {

		Assert.notNull(pageable, "Pageable must not be null!");

		Mono<List<T>> items = findAll().collectSortedList(comparator);

		return items.flatMap(content -> this.getPage(content, pageable, this.count()));
	}

	Mono<Long> delete(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id), "The given Id must not be empty!");

		return redisOperations.opsForHash().remove(KEY, id);
	}

	// Others... Implements the following methods for your business logic
	@Override
	public Mono<Boolean> existsById(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id), "The given Id must not be empty!");

		return redisOperations.opsForHash().hasKey(KEY, id);
	}

	@Override
	public Mono<Boolean> existsById(Publisher<String> publisher) {
		Assert.notNull(publisher, "The given id must not be null!");

		return Mono.from(publisher).flatMap(id -> this.existsById(id));
	}

	@Override
	public Mono<Long> count() {
		return redisOperations.opsForHash().values(KEY).count();
	}

	@Override
	public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
		Assert.notNull(entities, "The given Iterable of entities must not be null!");

		Flux<S> flux = Flux.fromIterable(entities);
		return flux.flatMap(this::save);
	}

	@Override
	public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
		Assert.notNull(entityStream, "The given Publisher of entities must not be null!");

		return Flux.from(entityStream).flatMap(this::save);
	}

	@Override
	public Mono<T> findById(Publisher<String> publisher) {
		Assert.notNull(publisher, "The given id must not be null!");

		return Mono.from(publisher).flatMap(id -> this.findById(id));
	}

	@Override
	public Flux<T> findAllById(Iterable<String> ids) {
		Assert.notNull(ids, "The given Iterable of Id's must not be null!");

		return findAllById(Flux.fromIterable(ids));
	}

	@Override
	public Flux<T> findAllById(Publisher<String> ids) {
		Assert.notNull(ids, "The given Publisher of Id's must not be null!");

		return Flux.from(ids).buffer().flatMap(this::findAllById);
	}

	@Override
	public Mono<Void> deleteById(Publisher<String> publisher) {
		Assert.notNull(publisher, "Id must not be null!");

		return Mono.from(publisher).flatMap(this::deleteById)//
				.then();
	}

	@Override
	public Mono<Void> deleteAll(Iterable<? extends T> entities) {

		Assert.notNull(entities, "The given Iterable of entities must not be null!");

		return Flux.fromIterable(entities).map(this::getRequiredId)//
				.flatMap(this::deleteById)//
				.then();
	}

	/**
	 * Delete a entry that contained in a hash key.
	 * 
	 * @param key     key value - must not be null.
	 * @param hashKey hash key value - must not be null.
	 * @return 1 Success or 0 Error
	 */
	public Mono<Long> deleteAllById(Object... ids) {
		return redisOperations.opsForHash().remove(KEY, ids)
				.log(String.format("Key entries(%s) under %s Deleted", ids, KEY));
	}

	@Override
	public Mono<Void> deleteAllById(Iterable<? extends String> ids) {
		Assert.notNull(ids, "The given Iterable of Id's must not be null!");

		return Flux.fromIterable(ids).flatMap(this::deleteById)//
				.then();
	}

	@Override
	public Mono<Void> deleteById(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id), "The given Id must not be empty!");

		return redisOperations.opsForHash().remove(KEY, id).then();
	}

	@Override
	public Mono<Void> delete(T entity) {

		return this.getRequiredId(entity).flatMap(id -> redisOperations.opsForHash().remove(KEY, id)).then();
	}

	@Override
	public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
		Assert.notNull(entityStream, "The given Publisher of entities must not be null!");

		return Flux.from(entityStream)//
				.map(this::getRequiredId)//
				.flatMap(this::deleteById)//
				.then();
	}

	@Override
	public Mono<Void> deleteAll() {
		return redisOperations.opsForHash().delete(KEY).then();
	}

	// ===================================================================================
	/**
	 * @Override public <S extends T> Mono<S> findOne(Example<S> example) { return
	 *           null; }
	 * 
	 * @Override public <S extends T> Flux<S> findAll(Example<S> example) { return
	 *           null; }
	 * 
	 * @Override public <S extends T> Flux<S> findAll(Example<S> example, Sort sort)
	 *           { return null; }
	 * 
	 * @Override public <S extends T> Mono<Long> count(Example<S> example) { return
	 *           null; }
	 * 
	 * @Override public <S extends T> Mono<Boolean> exists(Example<S> example) {
	 *           return null; }
	 * 
	 * @Override public <S extends T, R, P extends Publisher<R>> P findBy(Example<S>
	 *           example, Function<ReactiveFluentQuery<S>, P> queryFunction) {
	 *           return null; }
	 **/
	// ===================================================================================

	protected Mono<String> getRequiredId(T entity) {
		if (!(entity instanceof Entity)) {
			return Mono.error(new RuntimeException("Must be an instance of Entity.class"));
		}
		// Assuming the Model extends Entity class
		String id = ((Entity) entity).getId();

		if (!StringUtils.isBlank(id)) {
			return Mono.error(new RuntimeException("Entity id value is not specified"));
		}

		return Mono.just(id);
	}

	protected Mono<Page<T>> getPage(List<T> content, Pageable pageable, Mono<Long> totalSupplier) {

		Assert.notNull(content, "Content must not be null!");
		Assert.notNull(pageable, "Pageable must not be null!");
		Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

		if (pageable.isUnpaged() || pageable.getOffset() == 0) {

			if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
				return Mono.just(new PageImpl<>(content, pageable, content.size()));
			}

			return totalSupplier.map(total -> new PageImpl<>(content, pageable, total));
		}

		if (content.size() != 0 && pageable.getPageSize() > content.size()) {
			return Mono.just(new PageImpl<>(content, pageable, pageable.getOffset() + content.size()));
		}

		return totalSupplier.map(total -> new PageImpl<>(content, pageable, total));
	}

	private Mono<String> generateId() {

		Mono<String> entityId = Mono.fromSupplier(() -> UUID.randomUUID().toString().replaceAll("-", ""));

		return entityId.flatMap(id -> taken(id)).repeatWhenEmpty(Repeat.times(5));
	}

	private Mono<String> taken(String id) {

		return this.existsById(id).flatMap(exist -> {
			log.info("Supplied id already exist? {}", exist);

			if (exist) {
				return Mono.empty();
			}
			return Mono.just(id);
		});
	}

}
