package org.sashaiolh.iolhessentials.SocialSpy;


import org.sashaiolh.iolhessentials.IolhEssentials;
import org.sashaiolh.iolhessentials.SocialSpy.Utils.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


public class SpyCommandsConfigManager {
    private static final String CONFIG_PATH = "config/"+IolhEssentials.MODID+"/spyCommands.txt";
    private static final Logger LOGGER = Logger.getLogger(SpyCommandsConfigManager.class.getName());

    public static List<Command> spyCommands = new ArrayList<Command>();


    // Метод для загрузки правил из файла
    public static void loadCommandsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            loadCommandsFromReader(reader);
        }
    }

    private static void loadCommandsFromReader(BufferedReader reader) throws IOException {
        spyCommands.clear();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                continue;
            }
            // Создаем новый пункт правил и добавляем его в список
            String command = line;
            // Используем кодировку UTF-8 при чтении строк
//            word = new String(word.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            Command spyCommand = new Command(command);
            spyCommands.add(spyCommand);
        }
    }


    public List<Command> getSpyCommands() {
        return spyCommands;
    }



    // Метод для создания файла конфигурации с шаблоном, если он не существует
    private static void createDefaultConfig(File configFile) {
        try {
            // Создание объекта Gson
            FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8);

            // Создание шаблона конфигурации

            List<String> defaultLines = new ArrayList<>();


            defaultLines.add("m\n");
            defaultLines.add("msg\n");
            defaultLines.add("w\n");

            for(String line : defaultLines){
                writer.write(line);
            }
            writer.close();

        } catch (IOException e) {
            LOGGER.warning("Ошибка создания файла конфигурации: " + e.getMessage());
        }
    }
    public static void init(){
        // Путь к файлу конфигурации

        // Проверка существования файла и его создание при необходимости
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        try {
            loadCommandsFromFile(CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



