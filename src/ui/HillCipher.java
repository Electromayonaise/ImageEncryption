package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class HillCipher {
  private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

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

  private static void isValidMatrix(int[][] keyMatrix) {
    int det = keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0];
    if (det == 0) {
      throw new java.lang.Error("Det equals to zero, invalid key matrix!");
    }
  }

  private static boolean isValidReverseMatrix(int[][] keyMatrix, int[][] reverseMatrix) {
    int[][] product = new int[2][2];
    product[0][0] = (keyMatrix[0][0] * reverseMatrix[0][0] + keyMatrix[0][1] * reverseMatrix[1][0]) % 64;
    product[0][1] = (keyMatrix[0][0] * reverseMatrix[0][1] + keyMatrix[0][1] * reverseMatrix[1][1]) % 64;
    product[1][0] = (keyMatrix[1][0] * reverseMatrix[0][0] + keyMatrix[1][1] * reverseMatrix[1][0]) % 64;
    product[1][1] = (keyMatrix[1][0] * reverseMatrix[0][1] + keyMatrix[1][1] * reverseMatrix[1][1]) % 64;
    return product[0][0] == 1 && product[0][1] == 0 && product[1][0] == 0 && product[1][1] == 1;
  }

  private static int[][] reverseMatrix(int[][] keyMatrix) {
    int detMod64 = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 64;
    int factor;
    int[][] reverseMatrix = new int[2][2];

    for (factor = 1; factor < 64; factor++) {
      if ((detMod64 * factor) % 64 == 1) {
        break;
      }
    }

    reverseMatrix[0][0] = keyMatrix[1][1] * factor % 64;
    reverseMatrix[0][1] = (64 - keyMatrix[0][1]) * factor % 64;
    reverseMatrix[1][0] = (64 - keyMatrix[1][0]) * factor % 64;
    reverseMatrix[1][1] = keyMatrix[0][0] * factor % 64;

    return reverseMatrix;
  }

  private static void echoResult(String label, ArrayList<Integer> phrase) {
    int i;
    System.out.print(label);

    for (i = 0; i < phrase.size(); i++) {
      System.out.print(BASE64_ALPHABET.charAt(phrase.get(i)));
    }
    System.out.println();
  }

  public static void encrypt(String phrase) {
    int i;
    int[][] keyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseEncoded = new ArrayList<>();

    phrase = phrase.replaceAll("[^a-zA-Z0-9+/=]", "");

    if (phrase.length() % 2 == 1) {
      phrase += "A";
    }

    keyMatrix = getKeyMatrix();
    isValidMatrix(keyMatrix);

    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(BASE64_ALPHABET.indexOf(phrase.charAt(i)));
    }

    for (i = 0; i < phraseToNum.size(); i += 2) {
      int x = (keyMatrix[0][0] * phraseToNum.get(i) + keyMatrix[0][1] * phraseToNum.get(i + 1)) % 64;
      int y = (keyMatrix[1][0] * phraseToNum.get(i) + keyMatrix[1][1] * phraseToNum.get(i + 1)) % 64;
      phraseEncoded.add(x);
      phraseEncoded.add(y);
    }

    echoResult("Encoded image: ", phraseEncoded);
    System.out.println("Key matrix: " + Arrays.deepToString(keyMatrix));
  }

  public static void decrypt(String phrase) {
    int i;
    int[][] keyMatrix, revKeyMatrix;
    ArrayList<Integer> phraseToNum = new ArrayList<>();
    ArrayList<Integer> phraseDecoded = new ArrayList<>();

    phrase = phrase.replaceAll("[^a-zA-Z0-9+/=]", "");

    Scanner keyboard = new Scanner(System.in);
    System.out.print("Enter key matrix (4 numbers separated by space): ");
    keyMatrix = new int[2][2];
    keyMatrix[0][0] = keyboard.nextInt();
    keyMatrix[0][1] = keyboard.nextInt();
    keyMatrix[1][0] = keyboard.nextInt();
    keyMatrix[1][1] = keyboard.nextInt();

    isValidMatrix(keyMatrix);

    for (i = 0; i < phrase.length(); i++) {
      phraseToNum.add(BASE64_ALPHABET.indexOf(phrase.charAt(i)));
    }

    revKeyMatrix = reverseMatrix(keyMatrix);
    isValidReverseMatrix(keyMatrix, revKeyMatrix);

    for (i = 0; i < phraseToNum.size(); i += 2) {
      phraseDecoded.add((revKeyMatrix[0][0] * phraseToNum.get(i) + revKeyMatrix[0][1] * phraseToNum.get(i + 1)) % 64);
      phraseDecoded.add((revKeyMatrix[1][0] * phraseToNum.get(i) + revKeyMatrix[1][1] * phraseToNum.get(i + 1)) % 64);
    }

    echoResult("Decoded phrase: ", phraseDecoded);
  }

  public static void main(String[] args) {
    menu();
  }

  public static void menu() {
    String opt, phrase;

    Scanner keyboard = new Scanner(System.in);
    do {
      System.out.println("Hill implementation (2x2) with Base64");
      System.out.println("-------------------------------------");
      System.out.println("1. Encrypt image (A=0,B=1,...Z=25, a=26, b=27, ..., 9=52, /=62, +=63, ==64)");
      System.out.println("2. Decrypt image (A=0,B=1,...Z=25, a=26, b=27, ..., 9=52, /=62, +=63, ==64)");
      System.out.println();
      System.out.println("Any other character to exit");
      System.out.println();
      System.out.print("Option: ");
      opt = keyboard.nextLine();
      switch (opt) {
        case "1":
          System.out.print("Enter image to encrypt in base64: ");
          phrase = keyboard.nextLine();
          encrypt(phrase);
          break;
        case "2":
          System.out.print("Enter phrase to decrypt in base64: ");
          phrase = keyboard.nextLine();
          decrypt(phrase);
          break;
      }
    } while (opt.equals("1") || opt.equals("2"));
  }
}
