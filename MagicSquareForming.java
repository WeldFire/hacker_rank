import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.Collectors;


public class MagicSquareForming {
    






    public static boolean verbose = true;

    // Complete the formingMagicSquare function below.
    static int formingMagicSquare(int[][] s) {

        Map<Integer, List<List<Integer>>> possibleMagicRows = generateMagicRows(s.length);
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

    // Generates a map of rows with all possible values in them of size NxN
    // Key is the row number... this could have been just a list... not sure what I was thinking...
    static Map<Integer, List<List<Integer>>> generateMagicRows(int N){
        Map<Integer, List<List<Integer>>> result = new HashMap<>();
        
        int maxNumber = N * N;
        int magicNumber = ((maxNumber*(maxNumber+1))/2) / N; // Limit of summation 1 .. N divided by the number of rows

        List<List<Integer>> magicRows = gimmeAllDemPossibleMagicRows(N, magicNumber, maxNumber);
        // Initialize all of the row containers
        for(int rowId = 1; rowId <= N; rowId++) {
            result.put(rowId, magicRows);
        }

        return result;
    }

    static List<List<Integer>> gimmeAllDemPossibleMagicRows(int N, int magicNumber, int maxNumber) {
        List<List<Integer>> magicRows = new ArrayList<>();
        gimmeAllDemPossibleMagicRows(N, magicNumber, magicRows, new int[N], 1, maxNumber, 0);
        return magicRows;
    }

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

    static Set<List<List<Integer>>> getPossibleMagicSquares(Map<Integer, List<List<Integer>>> possibleMagicRows){
        Set<List<List<Integer>>> possibleMagicSquares = new HashSet<>();

        List<List<Integer>> firstRows = possibleMagicRows.get(1);
        List<List<Integer>> middleRows = possibleMagicRows.get(2);
        List<List<Integer>> lastRows = possibleMagicRows.get(3);

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
