package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HillCipher {
  /**
   * This method is for cosmetic purposes only here
   * and gets the key matrix from keyboard
   *
   * @return 2x2 array of int with the key matrix
   */
  private static int[][] getKeyMatrix() {
    int[][] keyMatrix = new int[2][2];
    do {
      keyMatrix[0][0] = (int) (Math.random() * 25 + 1);
      keyMatrix[0][1] = (int) (Math.random() * 25 + 1);
      keyMatrix[1][0] = (int) (Math.random() * 25 + 1);
      keyMatrix[1][1] = (int) (Math.random() * 25 + 1);
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
   * This method checks if the reverse key matrix is valid (matrix mod26 = (1,0,0,1)
   * Just for studying purposes
   *
   * @param keyMatrix     Original key matrix 2x2
   * @param reverseMatrix Reverse key matrix found in previous calls
   */
  private static boolean isValidReverseMatrix(int[][] keyMatrix, int[][] reverseMatrix) {
    int[][] product = new int[2][2];

    // Find the product matrix of key matrix times reverse key matrix
    product[0][0] = (keyMatrix[0][0] * reverseMatrix[0][0] + keyMatrix[0][1] * reverseMatrix[1][0]) % 26;
    product[0][1] = (keyMatrix[0][0] * reverseMatrix[0][1] + keyMatrix[0][1] * reverseMatrix[1][1]) % 26;
    product[1][0] = (keyMatrix[1][0] * reverseMatrix[0][0] + keyMatrix[1][1] * reverseMatrix[1][0]) % 26;
    product[1][1] = (keyMatrix[1][0] * reverseMatrix[0][1] + keyMatrix[1][1] * reverseMatrix[1][1]) % 26;

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
    int detMod26 = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 26; // Calc det
    int factor;
    int[][] reverseMatrix = new int[2][2];

    // Find the factor for which is true that
    // factor*det = 1 mod 26
    for (factor = 1; factor < 26; factor++) {
      if ((detMod26 * factor) % 26 == 1) {
        break;
      }
    }

    // Calculate the reverse key matrix elements using the factor found
    reverseMatrix[0][0] = keyMatrix[1][1] * factor % 26;
    reverseMatrix[0][1] = (26 - keyMatrix[0][1]) * factor % 26;
    reverseMatrix[1][0] = (26 - keyMatrix[1][0]) * factor % 26;
    reverseMatrix[1][1] = keyMatrix[0][0] * factor % 26;

    return reverseMatrix;
  }

  /**
   * This method echoes the result of encrypt/decrypt
   *
   * @param label  Label (encrypt/decrypt)
   * @param phrase Phrase to convert to characters and split in pairs
   */
  private static void echoResult(String label, ArrayList<Integer> phrase) {
    int i;
    System.out.print(label);

    // Loop the phrase and print the characters
    for (i = 0; i < phrase.size(); i ++) {
      System.out.print(Character.toChars(phrase.get(i) + (65)));
    }
    System.out.println();
  }

  /**
   * This method makes the actual encryption
   *
   * @param phrase Original phrase from keyboard to encrypt
   */
  public static void encrypt(String phrase) {
    int i;
    int[][] keyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseEncoded = new ArrayList<>();

    // Delete all non-english characters, and convert phrase to upper case
    phrase = phrase.replaceAll("[^a-zA-Z]", "").toUpperCase();

    // If phrase length is not an even number, add "Q" to make it even
    if (phrase.length() % 2 == 1) {
      phrase += "Q";
    }

    // Get the 2x2 key matrix from keyboard
    keyMatrix = getKeyMatrix();

    // Check if the matrix is valid (det != 0)
    isValidMatrix(keyMatrix);

    // Convert characters to numbers according to their
    // place in ASCII table minus 65 positions (A=65 in ASCII table)
    // If we use A=0 alphabet, subtract one more (adder)
    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(phrase.charAt(i) - (65));
    }

    // Find the product per pair of the phrase with the key matrix modulo 26
    for (i = 0; i < phraseToNum.size(); i += 2) {
      int x = (keyMatrix[0][0] * phraseToNum.get(i) + keyMatrix[0][1] * phraseToNum.get(i + 1)) % 26;
      int y = (keyMatrix[1][0] * phraseToNum.get(i) + keyMatrix[1][1] * phraseToNum.get(i + 1)) % 26;
      phraseEncoded.add(x);
      phraseEncoded.add(y);
    }

    // Print the result
    echoResult("Encoded phrase: ", phraseEncoded);
    System.out.println("Key matrix: " + Arrays.deepToString(keyMatrix));
  }

  /**
   * This method makes the actual decryption
   *
   * @param phrase Original phrase from keyboard to decrypt
   */
  public static void decrypt(String phrase) {
    int i;
    int[][] keyMatrix, revKeyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseDecoded = new ArrayList<>();

    // Delete all non-english characters
    phrase = phrase.replaceAll("[^a-zA-Z]", "");

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

    // Convert numbers to characters according to their
    // place in ASCII table minus 65 positions (A=65 in ASCII table)
    // If we use A=0 alphabet, subtract one more (adder)
    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(phrase.charAt(i) - (65));
    }

    // Find the reverse key matrix
    revKeyMatrix = reverseMatrix(keyMatrix);

    // Check if the reverse key matrix is valid (product = 1,0,0,1)
    isValidReverseMatrix(keyMatrix, revKeyMatrix);

    // Find the product per pair of the phrase with the reverse key matrix modulo 26
    for (i = 0; i < phraseToNum.size(); i += 2) {
      phraseDecoded.add((revKeyMatrix[0][0] * phraseToNum.get(i) + revKeyMatrix[0][1] * phraseToNum.get(i + 1)) % 26);
      phraseDecoded.add((revKeyMatrix[1][0] * phraseToNum.get(i) + revKeyMatrix[1][1] * phraseToNum.get(i + 1)) % 26);
    }

    // Print the result
    echoResult("Decoded phrase: ", phraseDecoded);
  }

  public static void main(String[] args) {
    menu();
  }

  public static void menu() {
    String opt, phrase;

    Scanner keyboard = new Scanner(System.in);
    do {
      System.out.println("Hill implementation (2x2)");
      System.out.println("-------------------------");
      System.out.println("1. Encrypt phrase (A=0,B=1,...Z=25)");
      System.out.println("2. Decrypt phrase (A=0,B=1,...Z=25)");
      System.out.println();
      System.out.println("Any other character to exit");
      System.out.println();
      System.out.print("Option: ");
      opt = keyboard.nextLine();
      switch (opt) {
        case "1":
          System.out.print("Enter phrase to encrypt: ");
          phrase = keyboard.nextLine();
          encrypt(phrase);
          break;
        case "2":
          System.out.print("Enter phrase to decrypt: ");
          phrase = keyboard.nextLine();
          decrypt(phrase);
          break;
      }
    } while (opt.equals("1") || opt.equals("2"));
  }
}