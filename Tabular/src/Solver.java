import java.util.*;

public class Solver {

    private class onesComparator implements Comparator<Term> {
        @Override
        public int compare(Term a, Term b) {
            return a.getNumOnes() - b.getNumOnes();
        }
    }

    // minterms+dontcares
    private Term[] terms;
    private ArrayList<Integer> minterms; // minterms only
    private ArrayList<Integer> dc; // dontcares only

    public ArrayList<ArrayList<Term>[]> step1;
    public ArrayList<HashSet<String>> taken_step1;

    public ArrayList<String[][]> step2;
    public ArrayList<String> step3;
    public ArrayList<String> petrickKey;

    private ArrayList<String>[] solution;
    private ArrayList<String> prime;
    private ArrayList<Term> finalTerms;

    private int maxLength;

    // constructor
    public Solver(String mintermsString, String dontCaresString) {
        // sort the 2 arrays
        int[] minterms = isValid(mintermsString);
        int[] dontCares = isValid(dontCaresString);
        if (!check(minterms, dontCares)) {
            throw new RuntimeException("INVALID INPUT");
        }

        Arrays.sort(dontCares);
        Arrays.sort(minterms);

        // calculate maximum length of the prime implicants
        maxLength = Integer.toBinaryString(minterms[minterms.length - 1]).length();

        // initialize instance variables
        this.dc = new ArrayList<Integer>();
        this.minterms = new ArrayList<Integer>();

        prime = new ArrayList<String>();
        step1 = new ArrayList<ArrayList<Term>[]>();
        taken_step1 = new ArrayList<HashSet<String>>();
        step2 = new ArrayList<String[][]>();
        step3 = new ArrayList<String>();
        petrickKey = new ArrayList<String>();

        // combine minterms and dontcares in one array
        Term[] temp = new Term[minterms.length + dontCares.length];
        int k = 0; // index in temp array
        for (int i = 0; i < minterms.length; i++) {
            temp[k++] = new Term(minterms[i], maxLength);
            this.minterms.add(minterms[i]);
        }
        for (int i = 0; i < dontCares.length; i++) {
            // ignore dontcares with bits > max minterm
            if (Integer.toBinaryString(dontCares[i]).length() > maxLength) {
                break;
            }
            temp[k++] = new Term(dontCares[i], maxLength);
            this.dc.add(dontCares[i]);
        }
        terms = new Term[k];
        for (int i = 0; i < k; i++)
            terms[i] = temp[i];

        // sort terms according to num of ones
        Arrays.sort(terms, new onesComparator());
    }

    // checks if there are terms repeated in both minterms and dontcares, if so
    // return false
    boolean check(int[] m, int[] d) {
        HashSet<Integer> temp = new HashSet<>();
        for (int i = 0; i < m.length; i++) {
            temp.add(m[i]);
        }
        for (int i = 0; i < d.length; i++) {
            if (temp.contains(d[i])) {
                return false;
            }
        }
        return true;
    }

    // converts string input to array of terms, or throw exception if invalid
    // input
    private int[] isValid(String s) {
        s = s.replace(",", " ");
        if (s.trim().equals("")) {
            return new int[] {};
        }
        String[] a = s.trim().split(" +");
        int[] t = new int[a.length];
        for (int i = 0; i < t.length; i++) {
            try {
                int temp = Integer.parseInt(a[i]);
                t[i] = temp;
            } catch (Exception e) {
                throw new RuntimeException("Invalid Input!");
            }
        }
        HashSet<Integer> dup = new HashSet<>();
        for (int i = 0; i < t.length; i++) {
            if (dup.contains(t[i])) {
                throw new RuntimeException("Invalid Input!");
            }
            dup.add(t[i]);
        }

        return t;

    }

    // group the minterms according to number of ones in a 2d array
    private ArrayList<Term>[] group(Term[] terms) {
        ArrayList<Term>[] groups = new ArrayList[terms[terms.length - 1].getNumOnes() + 1];

        for (int i = 0; i < groups.length; i++) {
            groups[i] = new ArrayList<>();
        }
        for (int i = 0; i < terms.length; i++) {
            int k = terms[i].getNumOnes();
            groups[k].add(terms[i]);
        }
        return groups;
    }

    // first step of solving
    public void solve() {
        // keep track of untaken terms
        ArrayList<Term> untaken = new ArrayList<>();

        // group the initial list of terms
        ArrayList<Term>[] list = group(terms);
        // store the resulting terms of each iteration
        ArrayList<Term>[] results;

        step1.add(list);

        // loop as long as results is not empty and its length > 1
        boolean inserted;
        do {
            // store taken terms
            HashSet<String> taken = new HashSet<>();

            // set results array to be an empty array
            results = new ArrayList[list.length - 1];

            ArrayList<String> temp;
            inserted = false;

            // loop over each two groups
            for (int i = 0; i < list.length - 1; i++) {

                results[i] = new ArrayList<>();
                // keep track of added terms in results to avoid duplicates
                temp = new ArrayList<>();

                // loop over each element in first group with all elements of
                // second
                for (int j = 0; j < list[i].size(); j++) {

                    // loop over each element in second group
                    for (int k = 0; k < list[i + 1].size(); k++) {

                        // check if valid combination
                        if (isValidCombination(list[i].get(j), list[i + 1].get(k))) {
                            // append the terms to taken
                            taken.add(list[i].get(j).getString());
                            taken.add(list[i + 1].get(k).getString());

                            Term n = new Term(list[i].get(j), list[i + 1].get(k));
                            // check if the resulting term is already in the
                            // results, don't add it
                            if (!temp.contains(n.getString())) {
                                results[i].add(n);
                                inserted = true;
                            }
                            temp.add(n.getString());
                        }
                    }
                }
            }

            // if results not empty
            if (inserted) {
                for (int i = 0; i < list.length; i++) {
                    for (int j = 0; j < list[i].size(); j++) {
                        if (!taken.contains(list[i].get(j).getString())) {
                            // add the untaken terms to untaken
                            untaken.add(list[i].get(j));
                        }
                    }
                }
                list = results;

                // add results and taken to display steps after program finishes
                step1.add(list);
                taken_step1.add(taken);
            }
        } while (inserted && list.length > 1);

        // copy the resulting minterms into new arrayList along with the untaken
        // terms
        finalTerms = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[i].size(); j++) {
                finalTerms.add(list[i].get(j));
            }
        }
        for (int i = 0; i < untaken.size(); i++) {
            finalTerms.add(untaken.get(i));
        }

        solve2();
    }

    // 2nd stage of solving
    // identifying prime implicants -> row dominance -> col dominance
    // keeps calling itself until all minterms taken or we have to go to petrick
    public void solve2() {
        // for displaying steps
        addToTable();

        if (!identifyPrime()) {
            if (!rowDominance()) {
                if (!columnDominance()) {
                    // if none succeeds go to petrick
                    petrick();
                    return;
                }
            }
        }
        // if there are still minterms to be taken call this function again
        if (minterms.size() != 0)
            solve2();
        // if all minterms taken, add to solution
        else {
            // for displaying steps
            addToTable();
            solution = new ArrayList[1];
            solution[0] = prime;
        }
    }

    // petrick's method
    void petrick() {

        HashSet<String>[] temp = new HashSet[minterms.size()];
        // construct a table for petrick
        for (int i = 0; i < minterms.size(); i++) {
            temp[i] = new HashSet<>();
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    char t = (char) ('a' + j);
                    petrickKey.add(t + ": " + finalTerms.get(j).getString());
                    temp[i].add(t + "");
                }
            }
        }

        // multiply petrick terms
        HashSet<String> finalResult = multiply(temp, 0);

        // for displaying steps
        HashSet<String>[] step = new HashSet[1];
        step[0] = finalResult;
        stepPetrick(step, 0);
        step3.add("\nMin:");

        // identify minimum terms in petrick expansion
        int min = -1;
        int count = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String m = t.next();
            if (min == -1 || m.length() < min) {
                min = m.length();
                count = 1;
            } else if (min == m.length()) {
                count++;
            }
        }

        // add minimum terms in petrick to solutions
        solution = new ArrayList[count];
        int k = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String c = t.next();
            if (c.length() == min) {
                solution[k] = new ArrayList<>();
                step3.add(c);
                for (int i = 0; i < c.length(); i++) {
                    solution[k].add(finalTerms.get((int) c.charAt(i) - 'a').getString());
                }
                for (int i = 0; i < prime.size(); i++) {
                    solution[k].add(prime.get(i));
                }
                k++;
            }
        }
    }

    // multiply two terms ex (ABC) * (BCD) = ABCD
    String mix(String s1, String s2) {
        HashSet<Character> r = new HashSet<>();
        for (int i = 0; i < s1.length(); i++)
            r.add(s1.charAt(i));
        for (int i = 0; i < s2.length(); i++)
            r.add(s2.charAt(i));
        String result = "";
        for (Iterator<Character> i = r.iterator(); i.hasNext();)
            result += i.next();
        return result;
    }

    // for displaying petricks steps
    void stepPetrick(HashSet<String>[] p, int k) {
        StringBuilder s3 = new StringBuilder();
        for (int i = k; i < p.length; i++) {
            if (p.length != 1)
                s3.append("(");
            for (Iterator<String> g = p[i].iterator(); g.hasNext();) {
                s3.append(g.next());
                if (g.hasNext()) {
                    s3.append(" + ");
                }
            }
            if (p.length != 1)
                s3.append(")");
        }
        step3.add(s3.toString());
    }

    // multiplication for petrick expressions
    HashSet<String> multiply(HashSet<String>[] p, int k) {
        if (k >= p.length - 1) {
            return p[k];
        }
        stepPetrick(p, k);

        HashSet<String> s = new HashSet<>();
        for (Iterator<String> t = p[k].iterator(); t.hasNext();) {
            String temp2 = t.next();
            for (Iterator<String> g = p[k + 1].iterator(); g.hasNext();) {
                String temp3 = g.next();
                s.add(mix(temp2, temp3));
            }
        }
        p[k + 1] = s;
        return multiply(p, k + 1);
    }

    // deletes dominating columns
    private boolean columnDominance() {
        boolean flag = false;
        // construct a table
        ArrayList<ArrayList<Integer>> cols = new ArrayList<>();
        for (int i = 0; i < minterms.size(); i++) {
            cols.add(new ArrayList<Integer>());
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    cols.get(i).add(j);
                }
            }
        }
        // identify dominating cols and remove them
        for (int i = 0; i < cols.size() - 1; i++) {
            for (int j = i + 1; j < cols.size(); j++) {
                if (cols.get(j).containsAll(cols.get(i)) && cols.get(j).size() > cols.get(i).size()) {
                    cols.remove(j);
                    minterms.remove(j);
                    j--;
                    flag = true;
                } else if (cols.get(i).containsAll(cols.get(j)) && cols.get(i).size() > cols.get(j).size()) {
                    cols.remove(i);
                    minterms.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private boolean rowDominance() {
        boolean flag = false;
        // identify dominated rows and delete them
        for (int i = 0; i < finalTerms.size() - 1; i++) {
            for (int j = i + 1; j < finalTerms.size(); j++) {
                if (contains(finalTerms.get(i), finalTerms.get(j))) {
                    finalTerms.remove(j);
                    j--;
                    flag = true;
                } else if (contains(finalTerms.get(j), finalTerms.get(i))) {
                    finalTerms.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    // identify prime implicants and cross them and the minterms they represent
    private boolean identifyPrime() {
        ArrayList<Integer>[] cols = new ArrayList[minterms.size()];
        for (int i = 0; i < minterms.size(); i++) {
            cols[i] = new ArrayList();
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    cols[i].add(j);
                }
            }
        }
        boolean flag = false;
        for (int i = 0; i < minterms.size(); i++) {
            if (cols[i].size() == 1) {
                flag = true;
                ArrayList<Integer> del = finalTerms.get(cols[i].get(0)).getNums();

                for (int j = 0; j < minterms.size(); j++) {
                    if (del.contains(minterms.get(j))) {
                        dc.add(minterms.get(j));
                        minterms.remove(j);
                        j--;
                    }
                }
                // for displaying steps
                step2.get(step2.size() - 1)[cols[i].get(0).intValue() + 1][0] = "T";

                prime.add(finalTerms.get(cols[i].get(0)).getString());
                finalTerms.remove(cols[i].get(0).intValue());
                break;
            }
        }

        return flag;
    }

    // for displaying steps
    void addToTable() {
        String[][] table = new String[finalTerms.size() + 1][minterms.size() + maxLength + 1];
        for (int i = 0; i < maxLength; i++) {
            table[0][i + 1] = String.valueOf((char) ('A' + i));
        }
        for (int i = maxLength; i < minterms.size() + maxLength; i++) {
            table[0][i + 1] = String.valueOf(minterms.get(i - maxLength));
        }
        for (int i = 1; i < finalTerms.size() + 1; i++) {
            for (int j = 0; j < maxLength; j++) {
                table[i][j + 1] = String.valueOf(finalTerms.get(i - 1).getString().charAt(j));
            }
        }
        for (int i = 1; i < finalTerms.size() + 1; i++) {
            for (int j = maxLength; j < minterms.size() + maxLength; j++) {
                if (finalTerms.get(i - 1).getNums().contains(minterms.get(j - maxLength))) {
                    table[i][j + 1] = "X";
                } else {
                    table[i][j + 1] = " ";
                }
            }
        }
        for (int i = 0; i < finalTerms.size() + 1; i++) {
            table[i][0] = " ";
        }
        step2.add(table);
    }

    /**
     * @param t1
     *            first term
     * @param t2
     *            second term
     * @return whether combination of t1, t2 possible
     */
    boolean isValidCombination(Term t1, Term t2) {
        // if length of t1, t2 not equal return false
        if (t1.getString().length() != t2.getString().length())
            return false;

        // if dashes not in same place or hamming dist != 1, return false
        int k = 0;
        for (int i = 0; i < t1.getString().length(); i++) {
            if (t1.getString().charAt(i) == '-' && t2.getString().charAt(i) != '-')
                return false;
            else if (t1.getString().charAt(i) != '-' && t2.getString().charAt(i) == '-')
                return false;
            else if (t1.getString().charAt(i) != t2.getString().charAt(i))
                k++;
            else
                continue;
        }
        if (k != 1)
            return false;
        else
            return true;
    }

    // checks if term t1 contains same minterms of t2
    boolean contains(Term t1, Term t2) {
        if (t1.getNums().size() <= t2.getNums().size()) {
            return false;
        }
        ArrayList<Integer> a = t1.getNums();
        ArrayList<Integer> b = t2.getNums();
        b.removeAll(dc);

        if (a.containsAll(b))
            return true;
        else
            return false;
    }

    // convert result to symbolic notation, ex: -01- = B'C
    String toSymbolic(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-') {
                continue;
            } else if (s.charAt(i) == '1') {
                r.append((char) ('A' + i));
            } else {
                r.append((char) ('A' + i));
                r.append('\'');
            }
        }
        if (r.toString().length() == 0) {
            r.append("1");
        }
        return r.toString();
    }

    // used to print final results
    public void printResults() {
        for (int i = 0; i < solution.length; i++) {
            System.out.println("Solution #" + (i + 1) + ":");
            for (int j = 0; j < solution[i].size(); j++) {
                System.out.print(solution[i].get(j));
                if (j != solution[i].size() - 1) {
                    System.out.print(" + ");
                }
            }
            System.out.print("\n(");
            for (int j = 0; j < solution[i].size(); j++) {
                System.out.print(toSymbolic(solution[i].get(j)));
                if (j != solution[i].size() - 1) {
                    System.out.print(" + ");
                }
            }
            System.out.print(")");
            System.out.println("\n");
        }
    }
}
