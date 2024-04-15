package org.sashaiolh.iolhessentials;

import java.io.*;
import java.util.Properties;
import java.util.Set;

public class ConfigManager {
    private static String CONFIG_PATH = "";
    private static Properties properties;

    public ConfigManager(String path) {
        CONFIG_PATH = path;
        properties = new Properties(); // Всегда создаем новый экземпляр Properties
        loadConfig(); // Загружаем конфигурацию
    }

    private void loadConfig() {
        try (InputStream inputStream = new FileInputStream(CONFIG_PATH)) {
            // Загрузка конфигурации с использованием кодировки UTF-8
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException e) {
            // Если файл конфигурации не существует, создаем его с шаблонным содержимым
            createDefaultConfig();
            // Повторно загружаем конфигурацию после создания файла
            loadConfig();
        }
    }

    public Set<String> getAllKeys() {
        return properties.stringPropertyNames();
    }


    private static void saveConfig() {
        try {
            File configFile = new File(CONFIG_PATH);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            try (OutputStream outputStream = new FileOutputStream(CONFIG_PATH)) {
                // Сохранение конфигурации с использованием кодировки UTF-8
                properties.store(new OutputStreamWriter(outputStream, "UTF-8"), null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getConfig(String key) {
        return properties.getProperty(key);
    }

    public void setConfig(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }

    private static void createDefaultConfig() {
        // Установка шаблонных значений конфигурации
        properties.setProperty("helpopPrefix", "§8[§4Мод-чат§8]");
//        properties.setProperty("time", "20");
        saveConfig(); // Сохраняем шаблонную конфигурацию в файл
    }

    public static void init(){
        // Проверяем существование файла и его создание при необходимости
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            createDefaultConfig();
        }
    }
}
