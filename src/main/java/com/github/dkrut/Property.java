package com.github.dkrut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {
    private static final String CONFIG_PATH = "src/main/resources/tesseract.properties";

    private final Logger log = LoggerFactory.getLogger(Property.class);
    private final Properties properties;

    public Property() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            log.warn("Error while reading tesseract.properties: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        String propertyValue = properties.getProperty(key);
        log.debug("Property \"{}\" = {}", key, propertyValue);
        return propertyValue;
    }
}