import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class MagicSquareForming {
    






    public static boolean verbose = false;

    // Complete the formingMagicSquare function below.
    static int formingMagicSquare(int[][] s) {

        Map<Integer, Set<List<Integer>>> possibleMagicRows = generateMagicRows();
        possibleMagicRows.forEach((key, value) -> System.err.println("Row " + key + " can be :" + Arrays.toString(value.toArray())));
        Set<List<List<Integer>>> possibleMagicSquares = getPossibleMagicSquares(possibleMagicRows);
        possibleMagicSquares.forEach((magicSquare) -> {
            System.err.println("Magic Square: ");
            magicSquare.forEach((magicSquareRow) -> {
                System.err.println(Arrays.toString(magicSquareRow.toArray()));
            });
            System.err.println("");
        });


        int totalChange = 0;
        int change = makeChange(s);
        while(change > 0){
            totalChange += change;
            
            System.err.println("Current Diff " + totalChange);
            change = makeChange(s);
        }

        return totalChange;
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

    static public int makeChange(int s[][]){
        HashMap<Integer, String> seen = new HashMap<>();
        Set<String> dupeLocations = new HashSet<>();

        // find dups
        for(int columns=0; columns<s.length; columns++){
            for(int rows=0; rows<s[0].length; rows++){
                int current = s[columns][rows];
                if(seen.getOrDefault(current, "").isEmpty()){
                    seen.put(current, ""+columns+","+rows);
                }else{
                    //record position
                    String seenString = seen.get(current);
                    if(!dupeLocations.contains(seenString)){
                        dupeLocations.add(seenString);
                    }
                    dupeLocations.add(""+columns+","+rows);
                    System.err.println("Duplicate found at "+columns+","+rows);
                }
            }   
        }
        // if(!dupeLocations.contains("1,1")){
        //     dupeLocations.add("1,1");//Always check the middle
        // }

        if(verbose){
            System.err.println("Suspicious locations: "+Arrays.toString(dupeLocations.toArray()));
        }

        // find missing        
        // Create diff list... Is this necessary? 
        Set<Integer> missing = new HashSet<>();
        for(int i=1;i<=Math.pow(s.length,2);i++){
            missing.add(i);
        }

        for(Integer key : seen.keySet()){
            missing.remove(key);
        }

        System.err.println("Missing the following numbers: "+Arrays.toString(missing.toArray()));

        int diff = 0;


        // Loop through all locations to determine if they need to change
        // All of the paths need to add up to 15
        for(int col=0; col<s.length; col++){
            for(int row=0; row<s[0].length; row++){
                int value = s[row][col];

                boolean centered = isCenter(col,row);
                boolean cornered = isCorner(col,row);
                boolean midpoint = isMidpoint(col,row);


                boolean potentiallyCorrect = false;
                String shouldBeString = "";
                if(centered){
                    potentiallyCorrect = value == 5;
                    shouldBeString = "the number 5";
                    
                    //It needs to change
                    if(!potentiallyCorrect){
                        //If the missing list doesn't have the one we are about to kill, then add it here
                        if(!missing.contains(value)){
                            missing.add(value);
                        }
                        //If the missing list contains 5, then remove it
                        if(missing.contains(5)){
                            missing.remove(5);
                        }
                        
                        //Update the diff
                        diff += Math.abs(5-value);
                        System.err.println("Replacing " + value + " with " + 5);

                        // Make the replacement
                        s[row][col] = 5;
                    }
                }else if(cornered){
                    potentiallyCorrect = value%2 == 0;
                    shouldBeString = "even";

                    //It needs to change
                    if(!potentiallyCorrect){
                        //If the missing list doesn't have the one we are about to kill, then add it here
                        if(!missing.contains(value)){
                            missing.add(value);
                        }
                        
                        //Find best missing value
                        int newValue = findBestMissing(missing, value, true);

                        //If the missing list contains newValue, then remove it
                        if(missing.contains(newValue)){
                            missing.remove(newValue);
                        }
                        //Update the diff
                        diff += Math.abs(newValue-value);
                        System.err.println("Replacing " + value + " with " + newValue);

                        // Make the replacement
                        s[row][col] = newValue;
                    }
                }else if(midpoint){
                    potentiallyCorrect = value%2 != 0;
                    shouldBeString = "odd";
                    
                    //It needs to change
                    if(!potentiallyCorrect){
                        //If the missing list doesn't have the one we are about to kill, then add it here
                        if(!missing.contains(value)){
                            missing.add(value);
                        }
                        
                        //Find best missing value
                        int newValue = findBestMissing(missing, value, false);

                        //If the missing list contains newValue, then remove it
                        if(missing.contains(newValue)){
                            missing.remove(newValue);
                        }
                        //Update the diff
                        diff += Math.abs(newValue-value);
                        System.err.println("Replacing " + value + " with " + newValue);

                        // Make the replacement
                        s[row][col] = newValue;
                    }
                }
                String potentiallyCorrectString = potentiallyCorrect ? "potentially correct" : "definitely wrong";

                
                String isCenterString =   centered ? "the center" : "";
                String isCornerString =   cornered ? "a corner"   : "";
                String isMidpointString = midpoint ? "a midpoint" : "";

                if(verbose || !potentiallyCorrect){
                    System.err.println(""+col+","+row + " is located at " + isCenterString + isCornerString + isMidpointString + " and has a value of " + value);
                    System.err.println("It's value is " + potentiallyCorrectString + " and should be " + shouldBeString);
                }
            }

            // If all missing haven't been found here, we need to loop through the duplicates to try and find a fit
            for(String dupeLocation : dupeLocations){
                String split[] = dupeLocation.split(",");
                int xrow = Integer.parseInt(split[0]);
                int xcol = Integer.parseInt(split[1]);
                int value = s[xrow][xcol];
                
                int neededRowValue = calcRowDeficit(s, xrow, xcol);
                int neededColValue = calcColDeficit(s, xrow, xcol);
                
                if(verbose){
                    System.err.println("Looks like " + dupeLocation + " is " + value + " but should be " + neededRowValue + " in the row and " + neededColValue + " in the column do we have this value missing?");
                }

                if(neededRowValue == neededColValue && neededRowValue != value){
                    System.err.println("Looks like " + dupeLocation + " is " + value + " but should be " + neededRowValue);
                    if(missing.contains(neededRowValue)){
                        s[xrow][xcol] = neededRowValue;

                        diff += Math.abs(neededRowValue - value);

                        missing.remove(neededRowValue);
                    }else{
                        System.err.println("We don't seem to be missing this value.... not sure how to proceed :(");
                    }
        
                }
                

            }
        }
        
        return diff;
    }

    static public int findBestMissing(Set<Integer> missing, int target, boolean isEven){
        int smallestDiff = 100;
        int smallestDiffVal = -1;

        //Try to find the closest replacement
        for(Integer missingNo : missing){
            boolean doIt = false;
            if(isEven && missingNo % 2 == 0){
                // Needs to be Even
                doIt = true;
            } else if (!isEven && missingNo % 2 != 0){
                // Needs to be Odd
                doIt = true;
            }

            if(doIt){
                int result = Math.abs(target-missingNo);
                if(result<smallestDiff){
                    smallestDiff = result;
                    smallestDiffVal = missingNo;
                }
            }
        }

        return smallestDiffVal;
    }

    static public boolean isCenter(int col, int row){
        return (col == 1 && row == 1);
    }
    static public boolean isCorner(int col, int row){
        return (col == row || (col == 0 && row == 2) || (col == 2 && row == 0)) && !isCenter(col,row);
    }
    static public boolean isMidpoint(int col, int row){
        return !(isCenter(col,row) || isCorner(col, row));
    }
    // All rows should add up to 15, what is the difference for this row?
    static public int calcRowDeficit(int s[][], int row, int col){
        int sum = 0;
        for(int i = 0; i<3;i++){
            if(i != col){
                sum += s[row][i];
            }
        }
        return 15-sum;
    }
    // All rows should add up to 15, what is the difference for this row?
    static public int calcColDeficit(int s[][], int row, int col){
        int sum = 0;
        for(int i = 0; i<3;i++){
            if(i != row){
                sum += s[i][col];
            }
        }
        return 15-sum;
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
