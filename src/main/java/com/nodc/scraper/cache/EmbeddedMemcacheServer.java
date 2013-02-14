package com.nodc.scraper.cache;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

/**
 * Starts up an embedded memcached server (jmemcached). The idea is to not 
 * require a separate standalone memcached server during development. This bean
 * should not be used in a production environment.
 * @author rnodc
 *
 */
public class EmbeddedMemcacheServer implements InitializingBean
{
	private static final Logger logger = LoggerFactory.getLogger(EmbeddedMemcacheServer.class);
	
  private static final MemCacheDaemon<LocalCacheElement> daemon =
      new MemCacheDaemon<LocalCacheElement>();
	
	private void startLocalMemcacheInstance(int port) {
    logger.info("Starting local memcache on port: " + port);
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
