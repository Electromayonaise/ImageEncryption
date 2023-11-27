package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class HillCipher {
  private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  /**
   * This method is for generate a valid key matrix
   *
   * @return 2x2 array of int with the key matrix
   */
  private static int[][] getKeyMatrix() {
    int[][] keyMatrix = new int[2][2];
    do {
      keyMatrix[0][0] = (int) (Math.random() * 64);
      keyMatrix[0][1] = (int) (Math.random() * 64);
      keyMatrix[1][0] = (int) (Math.random() * 64);
      keyMatrix[1][1] = (int) (Math.random() * 64);
    } while (!isValidReverseMatrix(keyMatrix, reverseMatrix(keyMatrix)));
    return keyMatrix;
  }

  /**
   * This method checks if the key matrix is valid (det=0)
   *
   * @param keyMatrix Original key matrix 2x2
   */
  private static void isValidMatrix(int[][] keyMatrix) {
    int det = keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0];
    // If det=0, throw exception and terminate
    if (det == 0) {
      throw new java.lang.Error("Det equals to zero, invalid key matrix!");
    }
  }

  /**
   * This method checks if the reverse key matrix is valid (matrix mod64 = (1,0,0,1)
   * Just for studying purposes
   *
   * @param keyMatrix     Original key matrix 2x2
   * @param reverseMatrix Reverse key matrix found in previous calls
   */
  private static boolean isValidReverseMatrix(int[][] keyMatrix, int[][] reverseMatrix) {
    int[][] product = new int[2][2];
    // Find the product matrix of key matrix times reverse key matrix
    product[0][0] = (keyMatrix[0][0] * reverseMatrix[0][0] + keyMatrix[0][1] * reverseMatrix[1][0]) % 64;
    product[0][1] = (keyMatrix[0][0] * reverseMatrix[0][1] + keyMatrix[0][1] * reverseMatrix[1][1]) % 64;
    product[1][0] = (keyMatrix[1][0] * reverseMatrix[0][0] + keyMatrix[1][1] * reverseMatrix[1][0]) % 64;
    product[1][1] = (keyMatrix[1][0] * reverseMatrix[0][1] + keyMatrix[1][1] * reverseMatrix[1][1]) % 64;
    // Check if a=1 and b=0 and c=0 and d=1
    // If not, throw exception and terminate
    return product[0][0] == 1 && product[0][1] == 0 && product[1][0] == 0 && product[1][1] == 1;
  }

  /**
   * This method calculates the reverse key matrix
   *
   * @param keyMatrix Original key matrix 2x2
   * @return 2x2 Reverse key matrix
   */
  private static int[][] reverseMatrix(int[][] keyMatrix) {
    int detMod64 = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 64;
    int factor;
    int[][] reverseMatrix = new int[2][2];

    // Find the factor for which is true that
    // factor*det = 1 mod 64
    for (factor = 1; factor < 64; factor++) {
      if ((detMod64 * factor) % 64 == 1) {
        break;
      }
    }

    // Calculate the reverse key matrix elements using the factor found
    reverseMatrix[0][0] = keyMatrix[1][1] * factor % 64;
    reverseMatrix[0][1] = (64 - keyMatrix[0][1]) * factor % 64;
    reverseMatrix[1][0] = (64 - keyMatrix[1][0]) * factor % 64;
    reverseMatrix[1][1] = keyMatrix[0][0] * factor % 64;

    return reverseMatrix;
  }

  /**
   * This method echoes the result of encrypt/decrypt
   *
   * @param phrase Phrase to convert to characters and split
   */
  private static String echoResult(ArrayList<Integer> phrase) {
    int i;
    StringBuilder result = new StringBuilder();
    // Loop the phrase and print the characters
    for (i = 0; i < phrase.size(); i++) {
      result.append(BASE64_ALPHABET.charAt(phrase.get(i)));
    }
    return result.toString();
  }

  /**
   * This method makes the actual encryption
   *
   * @param phrase Original phrase from keyboard to encrypt
   */
  public static String encrypt(String phrase) {
    int i;
    int[][] keyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseEncoded = new ArrayList<>();

    // Delete all unused characters
    phrase = phrase.replaceAll("[^a-zA-Z0-9+/=]", "");

    // If phrase length is not an even number, add "A" to make it even
    // This is for the algorithm to work
    if (phrase.length() % 2 == 1) {
      phrase += "A";
    }

    // Get the 2x2 key matrix from keyboard
    keyMatrix = getKeyMatrix();
    // Check if the matrix is valid (det != 0)
    isValidMatrix(keyMatrix);

    // Convert characters to numbers according to their position in the alphabet
    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(BASE64_ALPHABET.indexOf(phrase.charAt(i)));
    }

    // Find the product per pair of the phrase with the key matrix modulo 64
    for (i = 0; i < phraseToNum.size(); i += 2) {
      int x = (keyMatrix[0][0] * phraseToNum.get(i) + keyMatrix[0][1] * phraseToNum.get(i + 1)) % 64;
      int y = (keyMatrix[1][0] * phraseToNum.get(i) + keyMatrix[1][1] * phraseToNum.get(i + 1)) % 64;
      phraseEncoded.add(x);
      phraseEncoded.add(y);
    }

    // Print the key matrix for the user anotate it
    System.out.println("Key matrix: " + Arrays.deepToString(keyMatrix));
    // Return the result of the encryption
    return echoResult(phraseEncoded);
  }

  /**
   * This method makes the actual decryption
   *
   * @param phrase Original phrase from keyboard to decrypt
   */
  public static String decrypt(String phrase) {
    int i;
    int[][] keyMatrix, revKeyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseDecoded = new ArrayList<>();

    // Delete all unused characters
    phrase = phrase.replaceAll("[^a-zA-Z0-9+/=]", "");

    // Get the 2x2 key matrix from keyboard
    Scanner keyboard = new Scanner(System.in);
    System.out.print("Enter key matrix (4 numbers separated by space): ");
    keyMatrix = new int[2][2];
    keyMatrix[0][0] = keyboard.nextInt();
    keyMatrix[0][1] = keyboard.nextInt();
    keyMatrix[1][0] = keyboard.nextInt();
    keyMatrix[1][1] = keyboard.nextInt();

    // Check if the matrix is valid (det != 0)
    isValidMatrix(keyMatrix);

    // Convert characters to numbers according to their position in the alphabet
    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(BASE64_ALPHABET.indexOf(phrase.charAt(i)));
    }

    // Find the reverse key matrix
    revKeyMatrix = reverseMatrix(keyMatrix);
    // Check if the reverse key matrix is valid (product = 1,0,0,1)
    isValidReverseMatrix(keyMatrix, revKeyMatrix);

    // Find the product per pair of the phrase with the reverse key matrix modulo 64
    for (i = 0; i < phraseToNum.size(); i += 2) {
      phraseDecoded.add((revKeyMatrix[0][0] * phraseToNum.get(i) + revKeyMatrix[0][1] * phraseToNum.get(i + 1)) % 64);
      phraseDecoded.add((revKeyMatrix[1][0] * phraseToNum.get(i) + revKeyMatrix[1][1] * phraseToNum.get(i + 1)) % 64);
    }

    // Return the result of the decryption
    return echoResult(phraseDecoded);
  }

  public static void main(String[] args) throws IOException {
    menu();
  }

  public static void menu() throws IOException {
    String opt, phrase;
    String encryptedWord, decryptedWord;

    Scanner keyboard = new Scanner(System.in);
    FileWriter fileWriter;
    do {
      System.out.println("Hill implementation (2x2) with Base64");
      System.out.println("-------------------------------------");
      System.out.println("1. Encrypt Any Text (A=0,B=1,...Z=25, a=26, b=27, ..., 9=52, /=62, +=63, ==64)");
      System.out.println("2. Decrypt image (A=0,B=1,...Z=25, a=26, b=27, ..., 9=52, /=62, +=63, ==64)");
      System.out.println("3. Encrypt image from folder");
      System.out.println("4. Remove encrypted.txt");
      System.out.println();
      System.out.println("Any other character to exit");
      System.out.println();
      System.out.print("Option: ");
      opt = keyboard.nextLine();
      switch (opt) {
        case "1":
          System.out.print("Enter the text to encrypt: ");
          phrase = keyboard.nextLine();
          System.out.println("Encrypted text: " + encrypt(phrase));
          break;
        case "2":
          File file = new File("src/encrypted.txt");
          BufferedReader br = new BufferedReader(new java.io.FileReader(file));
          phrase = br.readLine();
          if (phrase == null) {
            System.out.print("Enter text to decrypt: ");
            phrase = keyboard.nextLine();
          }
          decryptedWord = decrypt(phrase);
          System.out.println("Decrypted text: " + decryptedWord);
          if (decryptedWord.contains("/")) {
            LoadImage.base64Decode(decryptedWord);
            System.out.println("Image decrypted successfully!");
            System.out.println("Check src/decrypted.png");
          }
          break;
        case "3":
          fileWriter = new FileWriter("src/encrypted.txt");
          String textToEncrypt = LoadImage.base64Encode();
          System.out.println("Encrypting image...");
          encryptedWord = encrypt(textToEncrypt);
          System.out.println("""
              Image encrypted successfully!
              Check src/encrypted.txt
              """);
          fileWriter.write(encryptedWord);
          fileWriter.close();
          break;
        case "4":
          System.out.println("Removing...");
          fileWriter = new FileWriter("src/encrypted.txt");
          fileWriter.write("");
          fileWriter.close();
          break;
      }
    } while (opt.equals("1") || opt.equals("2") || opt.equals("3") || opt.equals("4"));
    fileWriter = new FileWriter("src/encrypted.txt");
    fileWriter.write("");
    fileWriter.close();
  }
}

