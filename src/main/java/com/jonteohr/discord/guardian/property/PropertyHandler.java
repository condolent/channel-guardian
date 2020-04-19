package com.jonteohr.discord.guardian.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHandler {
	
	/**
	 * Load a property from the bot.properties file inside the classpath.
	 * @param property a {@link java.lang.String String} value of the property key to fetch
	 * @return a property value in a {@link java.lang.String String} format
	 */
	public String loadProperty(String property) {
		final Properties prop = new Properties();
		
		try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bot.properties")) {
			prop.load(inputStream);
			
			String buff = prop.getProperty(property);
			
			inputStream.close();
			
			return buff;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
