import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // main for testing
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Minterms(space or comma separated): ");
        String minterms = sc.nextLine();
        
        System.out.print("Don'tCares(space or comma separated): ");
        String dontCares = sc.nextLine();
        Solver s = new Solver(minterms, dontCares);
        
        System.out.println();
        s.solve();
        s.printResults();

        ArrayList<ArrayList<Term>[]> s1 = s.step1;

        // to print 1st step
        for (int i = 0; i < s1.size(); i++) {
            System.out.println("Step " + (i + 1));
            for (int j = 0; j < s1.get(i).length; j++) {
                for (int k = 0; k < s1.get(i)[j].size(); k++) {
                    System.out.print(s1.get(i)[j].get(k).getString());
                    if (s.taken_step1.size() > i) {
                        if (s.taken_step1.get(i).contains(s1.get(i)[j].get(k).getString())) {
                            System.out.print(" taken");
                        }
                    }
                    System.out.println();
                }
                System.out.println("---------------------------");
            }
            System.out.println("\n");
        }

        for (int k = 0; k < s.step2.size(); k++) {
            String[][] step2 = s.step2.get(k);

            for (int i = 0; i < step2.length; i++) {
                for (int j = 0; j < step2[0].length; j++) {
                    System.out.print(step2[i][j] + "  ");
                }
                System.out.println();
            }
            System.out.println();
        }

        for (int i = 0; i < s.petrickKey.size(); i++) {
            System.out.println(s.petrickKey.get(i));
        }

        for (int i = 0; i < s.step3.size(); i++) {
            System.out.println(s.step3.get(i));
        }
    }

}
