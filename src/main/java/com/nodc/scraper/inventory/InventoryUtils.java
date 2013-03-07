package com.nodc.scraper.inventory;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryUtils
{
	private static final Logger logger = LoggerFactory.getLogger(InventoryUtils.class);
	
	public static final BigDecimal createMoney(String money) throws Exception
	{
		return new BigDecimal(NumberFormat.getCurrencyInstance().parse(money).doubleValue());
	}
	
	public static final String urlDecode(String text)
	{
		String decoded = null;
		try
		{
			decoded = URLDecoder.decode(text, "UTF-8");	
		}
		catch (Exception e)
		{
			logger.error("unable to decode text", e);
		}
		return decoded;
	}
}
