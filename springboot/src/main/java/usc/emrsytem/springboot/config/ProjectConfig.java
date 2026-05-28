package usc.emrsytem.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.util.Map;

public class ProjectConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File configFile = new File(System.getProperty("user.dir") + "/../project-config.json");
            if (!configFile.exists()) return;

            Map<String, Object> config = new ObjectMapper().readValue(configFile, Map.class);
            if (config.containsKey("dbPassword")) {
                System.setProperty("DB_PASSWORD", String.valueOf(config.get("dbPassword")));
            }
            if (config.containsKey("backendPort")) {
                System.setProperty("BACKEND_PORT", String.valueOf(config.get("backendPort")));
            }
        } catch (Exception ignored) {}
    }
}
