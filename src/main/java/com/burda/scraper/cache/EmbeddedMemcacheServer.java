package com.burda.scraper.cache;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.InitializingBean;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

public class EmbeddedMemcacheServer implements InitializingBean
{
  private static final MemCacheDaemon<LocalCacheElement> daemon =
      new MemCacheDaemon<LocalCacheElement>();
	
	private void startLocalMemcacheInstance(int port) {
    System.out.println("Starting local memcache");
    CacheStorage<Key, LocalCacheElement> storage =
            ConcurrentLinkedHashMap.create(
                    ConcurrentLinkedHashMap.EvictionPolicy.FIFO, 100, 1024*500);
    daemon.setCache(new CacheImpl(storage));
    daemon.setAddr(new InetSocketAddress("localhost", port));
    daemon.start();
	}
	
	@Override
	public void afterPropertiesSet()
	{
		startLocalMemcacheInstance(11211);
	}
}
