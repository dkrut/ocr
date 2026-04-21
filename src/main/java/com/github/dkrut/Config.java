package com.github.dkrut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private final Map<String, String> languages;
    private static Config instance;

    private Config() {
        this.languages = loadLanguages();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Map<String, String> loadLanguages() {
        String env = System.getenv("LANGUAGES");
        if (env != null && !env.isBlank()) {
            Map<String, String> result = new HashMap<>();
            for (String entry : env.split(",")) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    result.put(parts[0].trim(), parts[1].trim());
                } else {
                    log.warn("Parse env LANGUAGES. Invalid entry: '{}', expected format 'code:name'", entry);
                }
            }
            if (!result.isEmpty()) {
                log.info("Using languages from LANGUAGES env: {}", String.join(", ", result.values()));
                return result;
            }
        }

        log.info("Using default languages: English, Russian");
        return Map.of("eng", "English", "rus", "Russian");
    }

    public Map<String, String> getLanguages() {
        return languages;
    }
}