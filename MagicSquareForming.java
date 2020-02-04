import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class MagicSquareForming {
    






    public static boolean verbose = true;

    // Complete the formingMagicSquare function below.
    static int formingMagicSquare(int[][] s) {

        Map<Integer, Set<List<Integer>>> possibleMagicRows = generateMagicRows();
        if(verbose)
            possibleMagicRows.forEach((key, value) -> System.err.println("Row " + key + " can be :" + Arrays.toString(value.toArray())));
        Set<List<List<Integer>>> possibleMagicSquares = getPossibleMagicSquares(possibleMagicRows);
        if(verbose)
            possibleMagicSquares.forEach((magicSquare) -> {
                System.err.println("Magic Square: ");
                magicSquare.forEach((magicSquareRow) -> {
                    System.err.println(Arrays.toString(magicSquareRow.toArray()));
                });
                System.err.println("");
            });


        int minChange = Integer.MAX_VALUE;
        
        // For each magic square, find the difference and only keep the smallest one
        for(List<List<Integer>> magicSquare : possibleMagicSquares) {
            int change = 0;
            int rowNumber = 0;
            for(List<Integer> magicSquareRow : magicSquare) {
                Integer magicRow[] = magicSquareRow.toArray(new Integer[0]);
                int incomingRow[] = s[rowNumber];

                for(int i = 0; i < magicRow.length; i++) {
                    change += Math.abs(magicRow[i] - incomingRow[i]);
                }

                rowNumber++;
            }

            if(change < minChange){
                minChange = change;
            }
        }

        return minChange;
    }

    // Generates a map of rows with all possible values in them
    // First and third row should have even corners and an odd number in the middle
    // Second row should be all Odd with a '5' in the middle
    static Map<Integer, Set<List<Integer>>> generateMagicRows(){
        Map<Integer, Set<List<Integer>>> result = new HashMap<>();
        result.put(1, new HashSet<>());
        result.put(2, new HashSet<>());
        result.put(3, new HashSet<>());


        for(int first = 1; first <= 9; first++) {
            for(int second = 1; second <=9; second++) {
                for(int third = 1; third <=9; third++) {
                    boolean isDuped = first == second || first == third || second == third;
                    if(!isDuped && ((first+second+third) == 15)){//We found a magic row! :D
                        List<Integer> magicRow = new ArrayList<>();
                        magicRow.add(first); magicRow.add(second); magicRow.add(third);
                        if(first % 2 == 0 && second % 2 != 0 && third % 2 == 0 && second != 5){
                            // This can be either the first or the third row
                            result.get(1).add(magicRow);
                            result.get(3).add(magicRow);
                        }

                        if(first % 2 != 0 && second == 5 && third % 2 != 0){
                            // This can be the second row!
                            result.get(2).add(magicRow);
                        }
                    }
                }
            }
        }

        return result;
    }

    static Set<List<List<Integer>>> getPossibleMagicSquares(Map<Integer, Set<List<Integer>>> possibleMagicRows){
        Set<List<List<Integer>>> possibleMagicSquares = new HashSet<>();

        Set<List<Integer>> firstRows = possibleMagicRows.get(1);
        Set<List<Integer>> middleRows = possibleMagicRows.get(2);
        Set<List<Integer>> lastRows = possibleMagicRows.get(3);

        for(List<Integer> firstRow : firstRows){
            for(List<Integer> middleRow : middleRows){
                for(List<Integer> lastRow : lastRows){
                    int sums[] = new int[3];
                    int optimal[] = new int[]{15,15,15};

                    for(int i=0;i<3;i++){
                        sums[i] = firstRow.get(i) + middleRow.get(i) + lastRow.get(i);
                    }
                    if(Arrays.equals(sums, optimal)){
                        List<List<Integer>> newMagicSquare = new ArrayList<>();
                        newMagicSquare.add(firstRow);
                        newMagicSquare.add(middleRow);
                        newMagicSquare.add(lastRow);
                        possibleMagicSquares.add(newMagicSquare);
                    }
                }
            }
        }

        return possibleMagicSquares;
    }







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
}
