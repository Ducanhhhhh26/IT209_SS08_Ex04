package org.example.smart_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class SmartHubApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(SmartHubApplication.class, args);
    }

    public static void loadDotEnv() {
        List<Path> candidatePaths = List.of(
                Path.of(".env"),
                Path.of(System.getProperty("user.dir", "."), ".env"),
                Path.of("..", ".env")
        );

        for (Path envPath : candidatePaths) {
            if (Files.exists(envPath)) {
                try (BufferedReader reader = Files.newBufferedReader(envPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                            continue;
                        }
                        int eqIndex = line.indexOf('=');
                        String key = line.substring(0, eqIndex).trim();
                        String value = line.substring(eqIndex + 1).trim();
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }
}
