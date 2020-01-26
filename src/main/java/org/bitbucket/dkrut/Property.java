package org.bitbucket.dkrut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Denis Krutikov on 26.01.2020.
 */

public class Property {

    private Logger log = LoggerFactory.getLogger(Property.class);

    public String getProperty(String key)  {
        String getPropertyResult = null;
        FileInputStream fis = null;
        Properties prop = new Properties();

        try {
            fis = new FileInputStream("src/main/resources/tesseract.properties");
            prop.load(fis);
            getPropertyResult = prop.getProperty(key);
            log.info("Property \"" + key + "\" = " + getPropertyResult);
        } catch (IOException e) {
            log.warn("Error while reading tesseract.properties: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return getPropertyResult;
    }
}
