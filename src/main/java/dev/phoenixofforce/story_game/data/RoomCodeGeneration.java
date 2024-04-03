package dev.phoenixofforce.story_game.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoomCodeGeneration {

    private final static List<String> adjectives = getAdjectives();
    private final static List<String> nouns = getNouns();
    private final static List<String> verbs = getVerbs();

    private final static String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String lowercaseLetters = uppercaseLetters.toLowerCase();
    private final static String numbers = "1234567890";

    public static String generateRoomCode() {
        String adjective = adjectives.get((int) Math.floor(Math.random() * adjectives.size()));
        String noun = nouns.get((int) Math.floor(Math.random() * nouns.size()));
        String verb = verbs.get((int) Math.floor(Math.random() * verbs.size()));

        return adjective + "-" + noun + "-" + verb;
    }

    public static String getRandomString() {
        String charList = uppercaseLetters + lowercaseLetters + numbers;
        return Stream.generate(() -> (int) Math.floor(Math.random() * charList.length()))
            .limit(20)
            .map(index -> charList.charAt(index) + "")
            .collect(Collectors.joining());
    }

    private static List<String> getAdjectives() {
        return getWordsFromFile("adjectives", 1, 0, 6);
    }

    private static List<String> getNouns() {
        return getWordsFromFile("nouns", 1, 0, 6);
    }

    private static List<String> getVerbs() {
        return getWordsFromFile("verbs", 2, 2, 8);
    }

    private static List<String> getWordsFromFile(String fileName, int startLine, int csvIndex, int maxExclusiveLength)  {
        return getResourceFileAsString("static/" + fileName + ".txt").stream()
                .skip(startLine)
                .map(line -> line.split(",")[csvIndex].trim())
                .filter(line -> line.length() < maxExclusiveLength)
                .toList();
    }

    private static List<String> getResourceFileAsString(String fileName) {
        ClassLoader classLoader = RoomCodeGeneration.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(fileName);
        if (is == null) {
            return new ArrayList<>();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().toList();
    }
}
