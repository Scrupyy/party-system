package de.scrupy.party.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {

    @NotNull
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @NotNull
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getSimpleName());

    @NotNull
    private static final String FILE_PATH = "plugins/config/";

    public @NotNull <T extends Config> T loadConfig(
            @NotNull String configName, @NotNull Class<T> clazz, @NotNull T defaultConfig) {
        File file = new File(FILE_PATH + configName);
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)) {
                return GSON.fromJson(inputStreamReader, clazz);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not read config file: " + configName, e);
            }
        }

        saveConfig(configName, defaultConfig);
        return defaultConfig;
    }

    public <T extends Config> void saveConfig(@NotNull String configName, @NotNull T config) {
        File configFile = new File(FILE_PATH + configName);

        File parentFile = configFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(configFile);
             OutputStreamWriter outputStreamWriter =
                     new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {

            GSON.toJson(config, outputStreamWriter);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "failed to save config: " + configName, e);
        }
    }
}
