package com.university.graduation.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisTemplate redisTemplate;

    private final ConcurrentMap<String,Cache> CACHES=new ConcurrentHashMap<>();

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        Cache<K,V> cache=CACHES.get(cacheName);
        if (null == cache) {
            cache = new RedisCache<K, V>(redisTemplate.opsForHash(),cacheName);
            CACHES.put(cacheName, cache);
        }
        return cache;
    }

    public void setRedisTimeout(String cacheName,long timeout) {
        redisTemplate.expire(cacheName, timeout, TimeUnit.SECONDS);
    }

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }
    /**
     * 基于Redis的缓存
     * @param <K>
     * @param <V>
     */
    public static class RedisCache<K,V> implements Cache<K,V>{

        private final HashOperations<String,K,V> redisTemplate;
        private final String cacheName;

        public RedisCache(HashOperations<String,K,V> redisTemplate,String cacheName){
            this.redisTemplate = redisTemplate;
            this.cacheName = cacheName;
        }

        @Override
        public void clear() throws CacheException {
            redisTemplate.delete(cacheName, keys());
        }

        @Override
        public V get(K key) throws CacheException {
            return redisTemplate.get(cacheName, key);
        }

        @Override
        public Set<K> keys() {
            return this.redisTemplate.keys(cacheName);
        }

        public V put(K key, V value) throws CacheException {
            redisTemplate.put(cacheName, key, value);
            return redisTemplate.get(cacheName, key);
        }

        @Override
        public V remove(K key) throws CacheException {
            V v = redisTemplate.get(cacheName, key);
            this.redisTemplate.delete(cacheName, key);
            return v;
        }

        @Override
        public int size() {
            return redisTemplate.size(cacheName).intValue();
        }

        @Override
        public Collection<V> values() {
            return redisTemplate.values(cacheName);
        }

        @Override
        public String toString() {
            return "cacheName:"+cacheName+",size:"+this.size();
        }
    }
}
