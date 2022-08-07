package com.github.dkrut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Denis Krutikov on 26.01.2020.
 */
public class Property {
    private final Logger log = LoggerFactory.getLogger(Property.class);

    public String getProperty(String key)  {
        try (FileInputStream fis = new FileInputStream("src/main/resources/tesseract.properties")) {
            Properties properties = new Properties();
            properties.load(fis);
            String propertyValue = properties.getProperty(key);
            log.debug("Property \"" + key + "\" = " + propertyValue);
            return propertyValue;
        } catch (IOException e) {
            log.warn("Error while reading tesseract.properties: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
