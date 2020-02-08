import java.util.*;
import java.util.stream.*;


class Result {

    /*
     * Complete the 'pickingNumbers' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY a as parameter.
     */

    public static int pickingNumbers(List<Integer> a) {
        HashMap<Integer, Integer> result = new HashMap<>();
        List<Integer> sortedList = a.stream().sorted().collect(Collectors.toList());

        System.err.println("Operating on " + Arrays.toString(sortedList.toArray()));

        int largest = 0;

        for(int i = 0; i < sortedList.size(); i++) {
            Integer pick = sortedList.get(i);
            if(!result.containsKey(pick)){
                result.put(pick, 0);
                for(int l = i; l < sortedList.size(); l++) {
                    Integer secondPick = sortedList.get(l);
                    //If what we picked is <= 1
                    if(secondPick - pick <= 1){
                        int picked = result.get(pick) + 1;
                        result.put(pick, picked); //Increment the count
                        if(picked > largest){
                            largest = picked;
                        }
                    }else{
                        //We are done
                        break;
                    }
                }
            }
        }

        result.forEach((key, value) -> System.err.println("Pick is " + key + " has a result of " + value));


        return largest;
    }

}
