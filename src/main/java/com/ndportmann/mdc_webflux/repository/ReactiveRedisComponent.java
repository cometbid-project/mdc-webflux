/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
public class ReactiveRedisComponent {

	private final ReactiveRedisOperations<String, Object> genericRedisOperations;
	private final ReactiveRedisOperations<String, String> redisListTemplate; 

	// private ReactiveValueOperations<String, Object> reactiveValueOps;
	// private ReactiveListOperations<String, String> reactiveListOps;

	public ReactiveRedisComponent(ReactiveRedisOperations<String, Object> genericRedisOperations,
			ReactiveRedisOperations<String, String> redisListTemplate) {
		this.genericRedisOperations = genericRedisOperations;
		this.redisListTemplate = redisListTemplate;
	}

	/**
	 * 
	 */
	@PostConstruct
	public void init() {
		// reactiveValueOps = redisOperations.opsForValue();
		// reactiveListOps = redisListTemplate.opsForList();
	}

	/**
	 * Prepend value to key, that is add entry in front(stack)
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> stack(@NonNull String key, @NonNull String str) {

		Mono<Long> lPush = redisListTemplate.opsForList().leftPush(key, str)
				.log(String.format("Prepend Key(%s) entry", key));
		return lPush;
	}

	/**
	 * Prepend array of values to key as entry, that is add multiple entries in
	 * front(stack).
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> stack(@NonNull String key, @NonNull String... strValues) {

		Mono<Long> lPush = redisListTemplate.opsForList().leftPushAll(key, strValues)
				.log(String.format("Prepend Key(%s) entries", key));
		return lPush;
	}

	/**
	 * Prepend Collection of values to key as entry, that is add multiple entries in
	 * front(stack).
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> stack(@NonNull String key, @NonNull List<String> listValues) {

		Mono<Long> lPush = redisListTemplate.opsForList().leftPushAll(key, listValues)
				.log(String.format("Prepend Key(%s) entries", key));
		return lPush;
	}

	/**
	 * Append value to key, that is add entry as Last entry(queue).
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Mono<Long> stackIfPresent(@NonNull String key, String value) {

		Mono<Long> lPush = redisListTemplate.opsForList().leftPushIfPresent(key, value)
				.log(String.format("Prepend Key(%s) entry only if Present", key));
		return lPush;
	}

	/**
	 * Append value to key, that is add entry as Last entry(queue).
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Mono<Long> append(@NonNull String key, String value) {

		Mono<Long> rPush = redisListTemplate.opsForList().rightPush(key, value)
				.log(String.format("Append Key(%s) entry", key));
		return rPush;
	}

	/**
	 * Append value to key, that is add entry as Last entry(queue).
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Mono<Long> appendIfPresent(@NonNull String key, String value) {

		Mono<Long> rPush = redisListTemplate.opsForList().rightPushIfPresent(key, value)
				.log(String.format("Append Key(%s) entry only if Present", key));
		return rPush;
	}

	/**
	 * Append array of values to key as entry, that is add multiple entries as
	 * Last(Queue).
	 * 
	 * @param key
	 * @param strValues
	 * @return
	 */
	public Mono<Long> appendToListEntry(@NonNull String key, @NonNull String... strValues) {

		Mono<Long> rPush = redisListTemplate.opsForList().rightPushAll(key, strValues)
				.log(String.format("Append Key(%s) array of entries", key));
		return rPush;
	}

	/**
	 * Append Collection of values to key as entry, that is add multiple entries as
	 * Last(Queue).
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> appendToListEntry(@NonNull String key, @NonNull List<String> list) {

		Mono<Long> rPush = redisListTemplate.opsForList().rightPushAll(key, list)
				.log(String.format("Append Key(%s) Collection of entries", key));
		return rPush;
	}

	/**
	 * Get an entry by key. First locate the index of the entry, and uses the index
	 * to locate the entry. Used mostly to confirm the existence of an entry value
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<String> getListEntry(@NonNull String key, @NonNull String entryValue) {

		Mono<String> result = redisListTemplate.opsForList().indexOf(key, entryValue)
				.log(String.format("find index of %s by Key(%s)", entryValue, key))
				.flatMap(index -> redisListTemplate.opsForList().index(key, index))
				.log(String.format("use index %s to locate Value"));

		return result;
	}

	/**
	 * Remove entries by count of a key
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> deleteListEntry(@NonNull String key, Long count, @NonNull String entryValue) {

		Mono<Long> result = redisListTemplate.opsForList().remove(key, count, entryValue)
				.log(String.format("remove %s entry of %s by Key(%s)", count, entryValue, key));

		return result;
	}

	/**
	 * Removes the given key and it's corresponding entries.
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Long> deleteAllEntries(@NonNull String key, @NonNull List<String> list) {

		Mono<Long> result = redisListTemplate.opsForList().delete(key)
				.log(String.format("Key(%s) entries removed", key)).flatMap(index -> this.appendToListEntry(key, list))
				.log(String.format("Key(%s) re-created, and initialized"));

		return result;
	}

	// ==============================================================================================================

	/**
	 * Remove the last element from list at sourceKey, append it to destinationKey
	 * and return its value.
	 * 
	 * @param srcKey
	 * @param destKey
	 * @return
	 */
	public Mono<String> stackAndPush(@NonNull String srcKey, @NonNull String destKey) {

		Mono<String> lPush = redisListTemplate.opsForList().rightPopAndLeftPush(srcKey, destKey).log(String.format(
				"Removes Source Key(%s) Last entry, Add to Destination Key(%s) as Last entry", srcKey, destKey));
		return lPush;
	}

	/**
	 * Remove the last element from list at srcKey, append it to dstKey and return
	 * its value. Results return once an element available or timeout reached.
	 * 
	 * @param srcKey
	 * @param destKey
	 * @param timeoutInSecs
	 * @return
	 */
	public Mono<String> detachAndPush(@NonNull String srcKey, @NonNull String destKey, long timeoutInSecs) {

		Mono<String> lPush = redisListTemplate.opsForList()
				.rightPopAndLeftPush(srcKey, destKey, Duration.ofSeconds(timeoutInSecs))
				.log(String.format(
						"Removes Source Key(%s) Last entry, Add to Destination Key(%s) as Last entry with timeout %d",
						srcKey, destKey, timeoutInSecs));
		return lPush;
	}

	/**
	 * 
	 * Removes and returns first element in list stored at key.
	 * 
	 * @param key
	 * @return
	 */
	public Mono<String> pop(@NonNull String key) {

		Mono<String> lPop = redisListTemplate.opsForList().leftPop(key)
				.log(String.format("Removes Key(%s) entry in FIFO order", key));
		return lPop;
	}

	/**
	 * Removes and returns first element from lists stored at key. Results return
	 * once an element available or timeout reached.
	 * 
	 * @param key
	 * @param timeout
	 * @return
	 */
	public Mono<String> pop(@NonNull String key, long timeoutInSecs) {

		Mono<String> lPop = redisListTemplate.opsForList().leftPop(key, Duration.ofSeconds(timeoutInSecs))
				.log(String.format("Removes Key(%s) entry in FIFO order", key));
		return lPop;
	}

	/**
	 * Removes and returns last element in list stored at key.
	 * 
	 * @param key
	 * @return
	 */
	public Mono<String> detach(@NonNull String key) {

		Mono<String> rPop = redisListTemplate.opsForList().rightPop(key)
				.log(String.format("Removes Key(%s) entry in LIFO order", key));
		return rPop;
	}

	/**
	 * Removes and returns last element from lists stored at key. Results return
	 * once an element available or timeout reached.
	 * 
	 * @param key
	 * @param timeoutInSecs
	 * @return
	 */
	public Mono<String> detach(@NonNull String key, long timeoutInSecs) {

		Mono<String> rPop = redisListTemplate.opsForList().rightPop(key, Duration.ofSeconds(timeoutInSecs))
				.log(String.format("Removes Key(%s) entry in LIFO order", key));
		return rPop;
	}

	// ========================================================================================================

	/**
	 * Decrements the number stored at key by one.
	 * 
	 * @param key
	 * @return
	 */
	public Mono<Long> decrementThis(@NonNull String key) {
		Mono<Long> lPush = genericRedisOperations.opsForValue().decrement(key)
				.log(String.format("Key(%s) entry Decremented by 1", key));
		return lPush;
	}

	/**
	 * Decrements the number stored at key by delta value.
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */
	public Mono<Long> decrementThis(@NonNull String key, Long delta) {
		Mono<Long> lPush = genericRedisOperations.opsForValue().decrement(key, delta)
				.log(String.format("Key(%s) entry Decremented by %d", key, delta));
		return lPush;
	}

	/**
	 * Increments the number stored at key by one.
	 * 
	 * @param key
	 * @return
	 */
	public Mono<Long> incrementThis(@NonNull String key) {
		Mono<Long> lPush = genericRedisOperations.opsForValue().increment(key)
				.log(String.format("Key(%s) entry Incremented by 1", key));
		return lPush;
	}

	/**
	 * Increments the number stored at key by delta value.
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */
	public Mono<Long> incrementThis(@NonNull String key, Long delta) {
		Mono<Long> lPush = genericRedisOperations.opsForValue().increment(key, delta)
				.log(String.format("Key(%s) entry Incremented by %d", key, delta));
		return lPush;
	}

	/**
	 * Increment the string representing a floating point number stored at key by
	 * 'Double' delta value.
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */
	public Mono<Double> incrementThis(@NonNull String key, Double delta) {
		Mono<Double> lPush = genericRedisOperations.opsForValue().increment(key, delta)
				.log(String.format("Key(%s) entry Incremented by %1$,.2f", key, delta));
		return lPush;
	}

	/**
	 * Set key to hold the string value if key is absent.
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Boolean> putIfAbsent(@NonNull String key, @NonNull String pojo) {

		Mono<Boolean> lPush = genericRedisOperations.opsForValue().setIfAbsent(key, pojo)
				.log(String.format("Key(%s) entry Pushed", key));
		return lPush;
	}

	/**
	 * Set key to hold the string value if key is absent.
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Mono<Boolean> putIfAbsent(@NonNull String key, @NonNull Object pojo, long timeoutInSecs) {

		Mono<Boolean> lPush = genericRedisOperations.opsForValue().setIfAbsent(key, pojo, Duration.ofSeconds(timeoutInSecs))
				.log(String.format("Key(%s) entry Pushed %d sec timeout", key, timeoutInSecs));
		return lPush;
	}

	/**
	 * Set the value and expiration timeout for key entry.
	 * 
	 * @param key
	 * @param pojo
	 * @param timeoutInSecs
	 * @return
	 */
	public Mono<Boolean> putPojo(@NonNull String key, @NonNull Object pojo) {
		Mono<Boolean> lPush = genericRedisOperations.opsForValue().set(key, pojo)
				.log(String.format("Key(%s) entry Pushed", key));
		return lPush;
	}

	/**
	 * Set the value and expiration timeout for key entry.
	 * 
	 * @param key
	 * @param pojo
	 * @param timeoutInSecs
	 * @return
	 */
	public Mono<Boolean> putPojo(@NonNull String key, @NonNull Object pojo, long timeoutInSecs) {
		Mono<Boolean> lPush = genericRedisOperations.opsForValue().set(key, pojo, Duration.ofSeconds(timeoutInSecs))
				.log(String.format("Key(%s) entry Pushed with %d sec timeout", key, timeoutInSecs));
		return lPush;
	}

	/**
	 * Set multiple keys to multiple values using key-value pairs provided in tuple.
	 * 
	 * @param key
	 * @param multiPojo
	 * @return
	 */
	public Mono<Boolean> putMultiPojo(@NonNull Map<String, Object> multiKeyValues) {
		Mono<Boolean> lPush = genericRedisOperations.opsForValue().multiSet(multiKeyValues)
				.log(String.format("Key(%s) entries Pushed", multiKeyValues.keySet()));
		return lPush;
	}

	/**
	 * Get the value of key entry.
	 * 
	 * @param key
	 * @return
	 */
	public Mono<Object> getPojo(@NonNull String key) {

		Mono<Object> lPop = genericRedisOperations.opsForValue().get(key).log(String.format("Key(%s) entry Retrieved", key));
		return lPop;
	}

	/**
	 * Get multiple keys. Values are returned in the order of the requested keys.
	 * 
	 * @param multikeys
	 * @return
	 */
	public Mono<List<Object>> getMultiPojo(@NonNull Collection<String> multikeys) {

		Mono<List<Object>> lPush = genericRedisOperations.opsForValue().multiGet(multikeys)
				.log(String.format("Key(%s) entries Retrieved", multikeys));
		return lPush;
	}

	/**
	 * Set new value for key and return its old value.
	 * 
	 * @param key
	 * @param pojo
	 * @return
	 */
	public Mono<Object> replacePojo(@NonNull String key, @NonNull Object pojo) {

		Mono<Object> lPop = genericRedisOperations.opsForValue().getAndSet(key, pojo)
				.log(String.format("Old Key(%s) entry Retrieved and new entry Saved", key));
		return lPop;
	}

	/**
	 * Removes the given key entry
	 * 
	 * @param key
	 * @return
	 */
	public Mono<Boolean> deletePojo(@NonNull String key) {

		Mono<Boolean> lPop = genericRedisOperations.opsForValue().delete(key).log(String.format("Key(%s) entry Deleted", key));
		return lPop;
	}

	//============================================================================================
	/**
	 * Set key and value into a hash key
	 * 
	 * @param key     key value - must not be null.
	 * @param hashKey hash key value - must not be null.
	 * @param val     Object value
	 * @return Mono of object
	 */
	public Mono<Object> set(String key, String hashKey, Object val) {
		return genericRedisOperations.opsForHash().put(key, hashKey, val).map(b -> val)
				.log(String.format("Key entry(%s) under %s Saved", hashKey, key));
	}

	/**
	 * @param key key value - must not be null.
	 * @return Flux of Object
	 */
	public Flux<Object> get(@NotNull String key) {
		return genericRedisOperations.opsForHash().values(key).log(String.format("All entries under %s Retrieved", key));
	}

	/**
	 * Get value for given hashKey from hash at key.
	 * 
	 * @param key     key value - must not be null.
	 * @param hashKey hash key value - must not be null.
	 * @return Object
	 */
	public Mono<Object> get(String key, Object hashKey) {
		return genericRedisOperations.opsForHash().get(key, hashKey)
				.log(String.format("Key entry(%s) under %s Retrieved", hashKey, key));
	}

	/**
	 * Delete a key that contained in a hash key.
	 * 
	 * @param key     key value - must not be null.
	 * @param hashKey hash key value - must not be null.
	 * @return 1 Success or 0 Error
	 */
	public Mono<Long> remove(String key, Object hashKey) {
		return genericRedisOperations.opsForHash().remove(key, hashKey)
				.log(String.format("Key entry(%s) under %s Deleted", hashKey, key));
	}
}
