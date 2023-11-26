package model;

import java.util.Map;

public class HillCipher {
    private int[][] keyMatrix;
    private int mod;

    public HillCipher(int[][] keyMatrix, int mod) {
        this.keyMatrix = keyMatrix;
        this.mod = mod;
    }

    public String encrypt(String message, Map<Character, Integer> dictionary) {
        System.out.println("Diccionario: " + dictionary);  // Imprimir el diccionario para depuración

        // Paso 1: Transformar cada carácter del mensaje en un número según el diccionario
        int[] messageNumbers = new int[message.length()];
        for (int i = 0; i < message.length(); i++) {
            char currentChar = message.charAt(i);
            Integer value = dictionary.get(currentChar);
            if (value == null) {
                throw new IllegalArgumentException("El carácter '" + currentChar + "' no está en el diccionario");
            }
            messageNumbers[i] = value;
        }

        // Paso 2: Dividir los caracteres en bloques
        int blockSize = keyMatrix.length;
        int numBlocks = messageNumbers.length / blockSize;

        // Paso 3: Construir la matriz del mensaje
        int[][] messageMatrix = new int[blockSize][numBlocks];
        int index = 0;
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < blockSize; j++) {
                messageMatrix[j][i] = messageNumbers[index++];
            }
        }

        // Paso 4: Multiplicar la matriz clave por la matriz del mensaje
        int[][] encryptedMatrix = MatrixUtils.multiply(keyMatrix, messageMatrix, mod);

        // Paso 5: Aplicar el módulo a la matriz resultante
        int[][] encryptedModMatrix = MatrixUtils.mod(encryptedMatrix, mod);

        // Paso 6 y 7: Calcular el módulo de la longitud del diccionario a la matriz resultante
        int[][] encryptedFinalMatrix = MatrixUtils.mod(encryptedModMatrix, dictionary.size());

        // Paso 8 y 9: Escribir la matriz resultante en forma horizontal
        StringBuilder encryptedMessage = new StringBuilder();
        for (int i = 0; i < encryptedFinalMatrix.length; i++) {
            for (int j = 0; j < encryptedFinalMatrix[0].length; j++) {
                encryptedMessage.append(encryptedFinalMatrix[i][j]);
            }
        }

        // Paso 10 y 11: Asignar a cada número su correspondiente según el diccionario
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encryptedMessage.length(); i++) {
            int num = Integer.parseInt(String.valueOf(encryptedMessage.charAt(i)));
            for (Map.Entry<Character, Integer> entry : dictionary.entrySet()) {
                if (entry.getValue() == num) {
                    result.append(entry.getKey());
                    break;
                }
            }
        }

        return result.toString();
    }

    public String decrypt(String encryptedMessage, Map<Character, Integer> dictionary) {
        // Paso 1: Codificar el mensaje recibido
        int[] encryptedNumbers = new int[encryptedMessage.length()];
        for (int i = 0; i < encryptedMessage.length(); i++) {
            char currentChar = encryptedMessage.charAt(i);
            encryptedNumbers[i] = dictionary.get(currentChar);
        }

        // Paso 2: Repartir el mensaje codificado en bloques
        int blockSize = keyMatrix.length;
        int numBlocks = encryptedNumbers.length / blockSize;

        // Paso 3: Introducir en la matriz B donde cada bloque es una columna
        int[][] encryptedMatrix = new int[blockSize][numBlocks];
        int index = 0;
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < blockSize; j++) {
                encryptedMatrix[j][i] = encryptedNumbers[index++];
            }
        }

        // Paso 4 y 5: Calcular la matriz inversa de la llave y aplicar módulo
        int[][] inverseMatrix = MatrixUtils.inverse(keyMatrix, mod);
        int[][] inverseModMatrix = MatrixUtils.mod(inverseMatrix, mod);

        // Paso 6 : Calcular el módulo de la longitud del diccionario a la matriz inversa de la llave
        int[][] inverseFinalMatrix = MatrixUtils.mod(inverseModMatrix, dictionary.size());

        // Paso 7: Multiplicar la matriz inversa por la matriz B
        int[][] decryptedMatrix = MatrixUtils.multiply(inverseFinalMatrix, encryptedMatrix, mod);

        // Paso 8: Calcular el módulo de la longitud del diccionario a la matriz resultante
        int[][] decryptedModMatrix = MatrixUtils.mod(decryptedMatrix, dictionary.size());

        // Paso 8 y 9: Escribir la matriz resultante en forma horizontal y traducir según el diccionario
        StringBuilder decryptedMessage = new StringBuilder();
        for (int i = 0; i < decryptedModMatrix[0].length; i++) {
            for (int j = 0; j < decryptedModMatrix.length; j++) {
                decryptedMessage.append(decryptedModMatrix[j][i]);
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < decryptedMessage.length(); i++) {
            int num = Integer.parseInt(String.valueOf(decryptedMessage.charAt(i)));
            for (Map.Entry<Character, Integer> entry : dictionary.entrySet()) {
                if (entry.getValue() == num) {
                    result.append(entry.getKey());
                    break;
                }
            }
        }

        return result.toString();
    }
}

