package model;

import java.util.Arrays;

import java.util.Arrays;
import java.util.Random;

public class MatrixUtils {

    public static int[][] multiply(int[][] a, int[][] b, int mod) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;

        int[][] result = new int[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] = (result[i][j] + a[i][k] * b[k][j]) % mod;
                }
            }
        }

        return result;
    }

    public static int[][] inverse(int[][] matrix, int mod) {
        int n = matrix.length;
        int[][] augmentedMatrix = buildAugmentedMatrix(matrix, n);

        applyGaussianElimination(augmentedMatrix, n, mod);

        int[][] inverseMatrix = extractInverseMatrix(augmentedMatrix, n);

        return mod(inverseMatrix, mod);
    }

    private static int[][] buildAugmentedMatrix(int[][] matrix, int n) {
        int[][] augmentedMatrix = new int[n][2 * n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = matrix[i][j];
                augmentedMatrix[i][j + n] = (i == j) ? 1 : 0;
            }
        }

        return augmentedMatrix;
    }

    private static void applyGaussianElimination(int[][] augmentedMatrix, int n, int mod) {
        for (int i = 0; i < n; i++) {
            int pivotIdx = findPivotIndex(augmentedMatrix, i, n);

            if (pivotIdx == -1) {
                throw new IllegalArgumentException("La matriz no es invertible");
            }

            swapRows(augmentedMatrix, i, pivotIdx);
            makePivotOne(augmentedMatrix, i, n, mod);
            eliminateOtherRows(augmentedMatrix, i, n, mod);
        }
    }

    private static int findPivotIndex(int[][] matrix, int col, int n) {
        for (int i = col; i < n; i++) {
            if (matrix[i][col] != 0) {
                return i;
            }
        }
        return -1;
    }

    private static void swapRows(int[][] matrix, int row1, int row2) {
        int[] temp = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = temp;
    }

    private static void makePivotOne(int[][] matrix, int row, int n, int mod) {
        int pivot = matrix[row][row];
        int inversePivot = modInverse(pivot, mod);

        for (int i = 0; i < 2 * n; i++) {
            matrix[row][i] = (matrix[row][i] * inversePivot) % mod;
        }
    }

    private static void eliminateOtherRows(int[][] matrix, int row, int n, int mod) {
        for (int i = 0; i < n; i++) {
            if (i != row) {
                int factor = matrix[i][row];

                for (int j = 0; j < 2 * n; j++) {
                    matrix[i][j] = (matrix[i][j] - factor * matrix[row][j] + mod) % mod;
                }
            }
        }
    }

    private static int[][] extractInverseMatrix(int[][] augmentedMatrix, int n) {
        int[][] inverseMatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(augmentedMatrix[i], n, inverseMatrix[i], 0, n);
        }

        return inverseMatrix;
    }

    public static int[][] mod(int[][] matrix, int mod) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] % mod;
            }
        }

        return result;
    }

    private static int modInverse(int a, int m) {
        for (int i = 1; i < m; i++) {
            if ((a * i) % m == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("No existe el inverso multiplicativo modular para " + a + " modulo " + m);
    }

    public static int[][] generateInvertibleMatrix(int size, int mod) {
        Random random = new Random();
        int[][] matrix;

        do {
            matrix = generateRandomSquareMatrix(size, mod, random);
        } while (!isInvertible(matrix, mod));

        return matrix;
    }

    public static int[][] generateRandomSquareMatrix(int size, int mod, Random random) {
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(mod);
            }
        }

        return matrix;
    }

    public static boolean isInvertible(int[][] matrix, int mod) {
        int determinant = calculateDeterminant(matrix, mod);
        return determinant != 0 && gcd(determinant, mod) == 1;
    }

    private static int calculateDeterminant(int[][] matrix, int mod) {
        int size = matrix.length;

        // Aplicar eliminación de Gauss para triangular la matriz
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                int factor = matrix[j][i] * modInverse(matrix[i][i], mod) % mod;
                for (int k = i; k < size; k++) {
                    matrix[j][k] = (matrix[j][k] - matrix[i][k] * factor + mod) % mod;
                }
            }
        }

        // Calcular el producto de los elementos diagonales
        int determinant = 1;
        for (int i = 0; i < size; i++) {
            determinant = (determinant * matrix[i][i]) % mod;
        }

        return determinant;
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public String toString(int [][] keyMatrix, int mod) {
        return "MatrixUtils{" +
                "keyMatrix=" + Arrays.toString(keyMatrix) +
                ", mod=" + mod +
                '}';
    }
}
