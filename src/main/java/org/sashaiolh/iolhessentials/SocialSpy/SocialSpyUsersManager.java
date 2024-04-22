package org.sashaiolh.iolhessentials.SocialSpy;

import org.sashaiolh.iolhessentials.SocialSpy.Utils.SocialSpyUser;
import org.sashaiolh.iolhessentials.IolhEssentials;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class SocialSpyUsersManager {
    static final String CONFIG_FOLDER = "config/" + IolhEssentials.MODID + "/";
    private static final String CONFIG_FILE = "socialSpyUsers.txt";
    private static final Logger LOGGER = Logger.getLogger(SocialSpyUsersManager.class.getName());

    static List<SocialSpyUser> socialSpyUsers = new ArrayList<SocialSpyUser>();


    // Метод для загрузки правил из файла
    public static void loadSocialSpyUsersFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            loadSocialSpyUsersFromReader(reader);
        }
    }

    private static void loadSocialSpyUsersFromReader(BufferedReader reader) throws IOException {
        socialSpyUsers.clear();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                continue;
            }
            // Создаем новый пункт правил и добавляем его в список
            String nickname = line;
            // Используем кодировку UTF-8 при чтении строк
//            word = new String(word.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            SocialSpyUser socialSpyUser = new SocialSpyUser(nickname);
            socialSpyUsers.add(socialSpyUser);
        }
    }


    public static List<SocialSpyUser> getSocialSpyUsers() {
        return socialSpyUsers;
    }


    public static boolean isUserInConfig(String nickname) {
        // Используем Stream API для поиска
        return socialSpyUsers.stream().anyMatch(user -> user.getNickname().equals(nickname));
    }



    // Метод для создания файла конфигурации с шаблоном, если он не существует
    private static void createDefaultConfig(File configFile) {
        try {
            // Создание объекта Gson
            FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8);

            // Создание шаблона конфигурации

            List<String> defaultLines = new ArrayList<>();

            defaultLines.add("123123\n");

            for(String line : defaultLines){
                writer.write(line);
            }
            writer.close();

        } catch (IOException e) {
            LOGGER.warning("Ошибка создания файла конфигурации: " + e.getMessage());
        }
    }





    public static void addSocialSpyUser(String nickname) throws IOException {
        String filePath = CONFIG_FOLDER + CONFIG_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Используем `true` для FileWriter, чтобы добавить строки к существующему файлу
            writer.write(nickname + "\n");
            // Добавляем новый объект в список socialSpyUsers
            socialSpyUsers.add(new SocialSpyUser(nickname));
        }
    }


    public static void removeSocialSpyUser(String nickname) throws IOException {
        String filePath = CONFIG_FOLDER + CONFIG_FILE;

        // Загружаем все существующие строки
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        // Удаляем нужную строку
        lines.removeIf(line -> line.equals(nickname));

        // Перезаписываем файл конфигурации
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }

        // Также удаляем из списка socialSpyUsers
        socialSpyUsers.removeIf(user -> user.getNickname().equals(nickname));
    }






    public static void init(){
        // Путь к файлу конфигурации
        String filePath = CONFIG_FOLDER + CONFIG_FILE;

        // Проверка существования файла и его создание при необходимости
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        try {
            loadSocialSpyUsersFromFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


