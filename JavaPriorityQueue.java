import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;


class Student {
    int id;
    String name;
    double cgpa;

    public Student(int id, String name, double cgpa){
        this.id = id;
        this.name = name;
        this.cgpa = cgpa;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public double getCGPA(){
        return this.cgpa;
    }
}

class StudentComparator implements Comparator<Student>{
    // The student having the highest Cumulative Grade Point Average (CGPA) is served first.
    // Any students having the same CGPA will be served by name in ascending case-sensitive alphabetical order.
    // Any students having the same CGPA and name will be served in ascending order of the id.
    public int compare(Student o1, Student o2){
        if(o1.getCGPA() > o2.getCGPA()){
            return -1;
        }else if(o1.getCGPA() == o2.getCGPA()){
            int nameResult = o1.getName().compareTo(o2.getName());
            if(nameResult == 0){
                return o1.getID() > o2.getID() ? -1 : 1;
            }else{
                return nameResult;
            }
        }
        return 1;
    }
}

class Priorities {

    public List<Student> getStudents(List<String> events){
        StudentComparator comparator = new StudentComparator();
        PriorityQueue<Student> queue = new PriorityQueue<Student>(events.size(), comparator);

        for(String event : events){
            if(event.startsWith("ENTER")){
                // Parse the enter string into the Student object
                String[] tokens = event.split(" ");
                String name = tokens[1];
                double cgpa = Double.parseDouble(tokens[2]);
                int id = Integer.parseInt(tokens[3]);
                Student student = new Student(id, name, cgpa);
                queue.add(student);
            }else if(event.startsWith("SERVED")){

                Student nextStudent = queue.poll();
                if(nextStudent != null){
                    System.err.println("Student was served from the queue: " + nextStudent.getName());
                }
            }
        }


        ArrayList<Student> students = new ArrayList<>();
        Student nextStudent = queue.poll();
        while(nextStudent != null){
            students.add(nextStudent);
            nextStudent = queue.poll();
        }

        return students;
    }
}

/* --------------- HACKERRANK GENERATED CODE BELOW ----------------- */
public class Solution {
    private final static Scanner scan = new Scanner(System.in);
    private final static Priorities priorities = new Priorities();

    public static void main(String[] args) {
        int totalEvents = Integer.parseInt(scan.nextLine());
        List<String> events = new ArrayList<>();

        while (totalEvents-- != 0) {
            String event = scan.nextLine();
            events.add(event);
        }

        List<Student> students = priorities.getStudents(events);

        if (students.isEmpty()) {
            System.out.println("EMPTY");
        } else {
            for (Student st: students) {
                System.out.println(st.getName());
            }
        }
    }
}
/* --------------- HACKERRANK GENERATED CODE ABOVE ----------------- */
