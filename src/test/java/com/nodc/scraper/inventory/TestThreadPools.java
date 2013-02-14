package com.nodc.scraper.inventory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.collect.Lists;

public class TestThreadPools
{
	private static final ExecutorService asyncResultsThreadPool = Executors.newFixedThreadPool(100);
	
	@Test
	public void testPools() throws Exception
	{
		System.out.println("before");
		final Collection<Callable<Void>> callables = Lists.newArrayList(); 
		final CountDownLatch latch = new CountDownLatch(10);
		for (int i=0; i < 10; i++)
		{
			final int idx = i;
			callables.add(new Callable<Void>(){

				@Override
				public Void call() throws Exception
				{
					Thread.sleep(4000);
					System.out.println(idx);
					latch.countDown();
					return null;
				}});
		}
		System.out.println("created");

		Thread t = new Thread(new Runnable(){

			@Override
			public void run()
			{
				try
				{
					asyncResultsThreadPool.invokeAll(callables);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}});
		t.start();
		System.out.println("after");
		latch.await();
	}
}
