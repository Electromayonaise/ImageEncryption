package ui;

import model.HillCipher;

import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {

        String base64Image = "test";
        Map<Character, Integer> dictionary = buildDictionary();

        int[][] keyMatrix = {{1, 2}, {3, 4}}; // Matriz q toca cambiar
        int mod = dictionary.size();

        HillCipher hillCipher = new HillCipher(keyMatrix, mod);

        String encryptedMessage = hillCipher.encrypt(base64Image, dictionary);
        System.out.println("Mensaje encriptado: " + encryptedMessage);

        String decryptedMessage = hillCipher.decrypt(encryptedMessage, dictionary);
        System.out.println("Mensaje desencriptado: " + decryptedMessage);
    }

    private static Map<Character, Integer> buildDictionary() {
        Map<Character, Integer> dictionary = new HashMap<>();

        // Asignar números a letras minúsculas (0-25)
        for (int i = 0; i < 26; i++) {
            dictionary.put((char) ('a' + i), i);
        }

        // Asignar números a letras mayúsculas (26-51)
        for (int i = 0; i < 26; i++) {
            dictionary.put((char) ('A' + i), i + 26);
        }

        // Asignar números a los primeros 10 dígitos (52-61)
        for (int i = 0; i < 10; i++) {
            dictionary.put((char) ('0' + i), i + 52);
        }

        // Caracteres especiales (62-64)
        dictionary.put('+', 62);
        dictionary.put('/', 63);
        dictionary.put('=', 64);

        return dictionary;
    }

}
