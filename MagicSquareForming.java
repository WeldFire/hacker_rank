import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.Collectors;


/** Forming a Magic Square
 * My NxN solution that solves the hackerrank problem defined here - https://www.hackerrank.com/challenges/magic-square-forming/problem
 * Builds a list of all possible magic squares and then looks for the smallest difference.
 * Created By: WeldFire
 */
public class MagicSquareForming {

    // While 'true' - Enables extra output used to debug
    public static boolean verbose = true;

    /**
     * Hackerrank entry point function
     * Fetches all possible magic rows, then magic squares 
     * Runs the magic squares and the input square through the minifier and returns the result
     */ 
    static int formingMagicSquare(int[][] s) {
        Map<Integer, List<List<Integer>>> possibleMagicRows = generateMagicRows(s.length);
        if(verbose)
            possibleMagicRows.forEach((key, value) -> System.err.println("Row " + key + " can be :" + Arrays.toString(value.toArray())));

        List<List<List<Integer>>> possibleMagicSquares = getPossibleMagicSquares(possibleMagicRows);
        if(verbose)
            possibleMagicSquares.forEach((magicSquare) -> {
                System.err.println("Magic Square: ");
                magicSquare.forEach((magicSquareRow) -> {
                    System.err.println("\t"+Arrays.toString(magicSquareRow.toArray()));
                });
                System.err.println("");
            });

        return determineSmallestChange(s, possibleMagicSquares);
    }

    /**
     * Loops through all of the magic squares to find the one that creates the smallest change
     */
    static int determineSmallestChange(int[][] inputSquare, List<List<List<Integer>>> possibleMagicSquares){
        int minChange = Integer.MAX_VALUE;
        List<List<Integer>> bestFittingSquare = new ArrayList<>();
        
        // For each magic square, find the difference and only keep the smallest one
        for(List<List<Integer>> magicSquare : possibleMagicSquares) {
            int change = 0;
            int rowNumber = 0;
            for(List<Integer> magicSquareRow : magicSquare) {
                Integer magicRow[] = magicSquareRow.toArray(new Integer[0]);
                int incomingRow[] = inputSquare[rowNumber];

                for(int i = 0; i < magicRow.length; i++) {
                    change += Math.abs(magicRow[i] - incomingRow[i]);
                }

                rowNumber++;
            }

            if(change < minChange){
                minChange = change;
                bestFittingSquare = magicSquare;
            }
        }

        if(verbose){
            System.err.println("ANSWER FOUND! :D");
            System.err.println("Smallest change we could find is " + minChange);
            System.err.println("The best fitting square looks like this:");
            bestFittingSquare.forEach((magicSquareRow) -> {
                System.err.println("\t"+Arrays.toString(magicSquareRow.toArray()));
            });
            System.err.println("");
        }

        return minChange;
    }

    /**
     * Generates a map of rows with all possible values in them of size NxN
     * N is the size of either the width or height of the input square we are trying to match
     * Return object is a Map with the key representing the row number... this could have been just a list... not sure what I was thinking... and as you can probably tell from this program... I like lists a lot already
     */
    static Map<Integer, List<List<Integer>>> generateMagicRows(int N){
        Map<Integer, List<List<Integer>>> result = new HashMap<>();

        List<List<Integer>> magicRows = new ArrayList<>();
        gimmeAllDemPossibleMagicRows(N, getMagicNumber(N), magicRows, new int[N], 1, getMaxNumber(N), 0);

        // Initialize all of the row containers and add the possible magic rows
        for(int rowId = 1; rowId <= N; rowId++) {
            result.put(rowId, magicRows);
        }

        return result;
    }

    /**
     * Recursive 'loop' to generate a permutation of all possible rows of size N in between the start and end indicies 
     * This permutation is checked to be 'magic' (all numbers add up to the magic number supplied) before saving the row
     */
    static void gimmeAllDemPossibleMagicRows(int N, int magicNumber, List<List<Integer>> magicRows, int[] data, int start, int end, int index) {
        if(index == N){
            // We finished making a combination! Is it magic?
            List<Integer> cloned_list = Arrays.stream(data).boxed().collect(Collectors.toList());
            if(cloned_list.stream().reduce(0, Integer::sum) == magicNumber){
                magicRows.add(cloned_list);
            }
        } else {
            for (int i = start; i <= end; i++) {
                data[index] = i;
                gimmeAllDemPossibleMagicRows(N, magicNumber, magicRows, data, start, end, index + 1);
            }
        }
    }


    /**
     * Generates a list of magic squares (which are lists of lists of integers)
     */
    static List<List<List<Integer>>> getPossibleMagicSquares(Map<Integer, List<List<Integer>>> possibleMagicRows){        
        int N = possibleMagicRows.size();

        List<List<List<Integer>>> result = new ArrayList<>();
        getPossibleMagicSquares(getMagicNumber(N), possibleMagicRows, result, 0, new ArrayList<>());
        return result;
    }

    /**
     * Recursive 'loop' to generate a permutation of all possible magic squares of size NxN from all possible magic rows provided
     * This permutation is checked to be 'magic' before saving the square
     */
    static void getPossibleMagicSquares(int magicNumber, Map<Integer, List<List<Integer>>> possibleMagicRows, List<List<List<Integer>>> magicSquares, int depth, List<List<Integer>> current) {
        if(depth == possibleMagicRows.size()){
            // Make sure that this is actually a magic square by validating the magic number in every column
            if(isMagic(magicNumber, current)){
                magicSquares.add(current);
            }
            // Else it wasn't magic and we should toss it
        }else{
            List<List<Integer>> currentCollection = possibleMagicRows.get(depth+1);

            for(List<Integer> element : currentCollection) {
                List<List<Integer>> copy = new ArrayList<>(current);
                copy.add(element);
                getPossibleMagicSquares(magicNumber, possibleMagicRows, magicSquares, depth + 1, copy);
            }
        }
        
    }

    /**
     * Determines if a magic square is actually magic or not
     * Ensures that all the columns and diagonals sum to the magic number and makes sure that the numbers aren't used more than once
     * magicNumber passed in just to save some computation time
     */
    static boolean isMagic(int magicNumber, List<List<Integer>> possibleMagicSquare){
        int N = possibleMagicSquare.size();
        
        int[] instanceCount = new int[N*N+1];
        int[] columnSums = new int[N];
        int[] diagonalSums = new int[2];

        boolean isMagic = true;

        // Ensure that all the columns and diagonals are magic (Rows are already magic at this point) and make sure that the numbers aren't used more than once a piece
        for(int rowIndex = 0; isMagic && rowIndex < N; rowIndex++){
            List<Integer> row = possibleMagicSquare.get(rowIndex);
            for(int columnIndex = 0; isMagic && columnIndex < N; columnIndex++){
                int selectedValue = row.get(columnIndex);

                // Sum the columns
                columnSums[columnIndex] += selectedValue;

                // Count the instances
                instanceCount[selectedValue] += 1;
                if(instanceCount[selectedValue] > 1){//If there is a duplicate, then die
                    isMagic = false;
                }

                // Sum the diagonals
                if(rowIndex == columnIndex){// Backslash Summation '\'
                    diagonalSums[0] += selectedValue;
                }

                if((columnIndex + rowIndex) == (N-1)){// Forwardslash Summation '/'
                    diagonalSums[1] += selectedValue;
                }
            }
        }

        // Ensure that all the columns summed to magic numbers
        for(int i = 0; isMagic && i < N; i++){
            if(columnSums[i] != magicNumber){
                isMagic = false;
            }
        }

        // Ensure that all the diagonals summed to magic numbers
        for(int i = 0; isMagic && i < 2; i++){
            if(diagonalSums[i] != magicNumber){
                isMagic = false;
            }
        }

        return isMagic;
    }
    
    /**
     * Determine what the maximum counting number is in a magic square of size N
     */
    static int getMaxNumber(int N){
        return N * N;
    }

    /**
     * Determine what the magic number is for a magic square given the length of either the width or height of said square represented by the path variable 'N'
     */
    static int getMagicNumber(int N){
        int maxNumber = getMaxNumber(N);
        return ((maxNumber*(maxNumber+1))/2) / N; // Limit of summation 1 .. N divided by the number of rows
    }

    /* --------------- HACKERRANK GENERATED CODE BELOW ----------------- */
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        int[][] s = new int[3][3];

        for (int i = 0; i < 3; i++) {
            String[] sRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int j = 0; j < 3; j++) {
                int sItem = Integer.parseInt(sRowItems[j]);
                s[i][j] = sItem;
            }
        }

        int result = formingMagicSquare(s);

        System.out.println(result);

        scanner.close();
    }
    /* --------------- HACKERRANK GENERATED CODE ABOVE ----------------- */
}
