package com.burda.scraper.inventory;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class InventoryUtils
{
	public static final BigDecimal createMoney(String money) throws Exception
	{
		return new BigDecimal(NumberFormat.getCurrencyInstance().parse(money).doubleValue());
	}
}
