package com.sdrfengmi.study.util;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class PropertiesUtil {

	private static Properties properties;

	public static void init() {
		properties = new Properties();
		InputStream in = PropertiesUtil.class.getResourceAsStream("/server.properties");
		try {
			if (in == null) {
				throw new FileNotFoundException();
			}
			properties.load(in);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				log.error("", e2);
			}
		}
	}

	public static String getConfig(String key) {
		if (properties == null) {
			init();
		}
		return properties.getProperty(key);
	}

	/**
	 * 获取所有属性
	 */
	public static Properties readAll(String fileName) {
		InputStreamReader in = null;
		Properties props = new Properties();
		try {
			in = new InputStreamReader(PropertiesUtil.class.getResourceAsStream(fileName), "utf-8");
			props.load(in);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				log.error("", e2);
			}
		}
		return props;
	}
}
