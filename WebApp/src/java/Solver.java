

import java.util.*;
import java.math.*;

public class Solver {
    
    private class onesComparator implements Comparator<Term> {
        
        @Override
        public int compare(Term a, Term b) {
            return a.getNumOnes() - b.getNumOnes();
        }
    }
    static ArrayList<ArrayList<Term>[]> step1;
    static ArrayList<HashSet<String>> taken_step1;
    
    static ArrayList<String[][]> step2;
    
    static ArrayList<String> step3;
    static ArrayList<String> petrickKey;

    private static ArrayList<String>[] solution;
    static ArrayList<String> prime;
    private static ArrayList<Term> finalTerms;
    private static ArrayList<Integer> dc;
    private static ArrayList<Integer> minterms;
    private Term[] terms;
    private int maxLength;
    
    public Solver(int[] minterms, int[] dontCares) {
        // sort the 2 arrays
        Arrays.sort(dontCares);
        Arrays.sort(minterms);
        
        // calculate maximum length
        maxLength = Integer.toBinaryString(minterms[minterms.length-1]).length();
        
        // initialize terms
        dc = new ArrayList();
        this.minterms = new ArrayList();
        prime = new ArrayList();
        step1 = new ArrayList();
        taken_step1 = new ArrayList();
        step2 = new ArrayList();
        step3 = new ArrayList();
        petrickKey = new ArrayList();
        
        Term[] temp = new Term[minterms.length+dontCares.length];
        int k = 0;
        for (int i = 0; i < minterms.length; i++) {
            temp[k++] = new Term(minterms[i], maxLength);
            this.minterms.add(minterms[i]);
        }
        for (int i = 0; i < dontCares.length; i++) {
            if (Integer.toBinaryString(dontCares[i]).length() > maxLength) {
                break;
            }
            temp[k++] = new Term(dontCares[i], maxLength);
            dc.add(dontCares[i]);
        }
        terms = new Term[k];
        for (int i = 0; i < k; i++)
            terms[i] = temp[i];
        
        // sort terms according to num of ones
        Arrays.sort(terms, new onesComparator());
    }
    
    /**
     * 
     * @param 1-D array of terms
     * @return 2-D array of the same terms grouped by num of ones
     * if original array contains n items, new array will be n*(max num of ones - min num of ones + 1)
     */
    private ArrayList<Term>[] group(Term[] terms) {

        ArrayList<Term>[] groups = new ArrayList[terms[terms.length-1].getNumOnes() + 1];
        int j = 0;
        for (int i = 0; i < groups.length; i++) {
            groups[i] = new ArrayList();
            int k = j;
            while (k < terms.length && terms[k].getNumOnes() == terms[j].getNumOnes()) {
                groups[i].add(terms[k]);
                k++;
            }
            j = k;
        }
        return groups;
    }
    
    public void solve() {
        ArrayList<Term> untaken = new ArrayList();;
        ArrayList<Term>[] list = group(terms);
        ArrayList<Term>[] results;
        
        step1.add(list);
        
        boolean inserted;
        do {  
            HashSet<String> taken = new HashSet();
            
            // set results array to be an empty array
            results = new ArrayList[list.length-1];
            ArrayList<String> temp;
            inserted = false;
            
            // loop over each two groups
            for (int i = 0; i < list.length-1; i++) {

                results[i] = new ArrayList();
                temp = new ArrayList();
                
                // loop over each element in first group with all elements of second 
                for (int j = 0; j < list[i].size(); j++) {
                    
                    // loop over each element in second group
                    for (int k = 0; k < list[i+1].size(); k++) {
                        
                        if (isValidCombination(list[i].get(j), list[i+1].get(k))) {
                            taken.add(list[i].get(j).getString());
                            taken.add(list[i+1].get(k).getString());
                            
                            Term n = new Term(list[i].get(j), list[i+1].get(k));
                            if (!temp.contains(n.getString())) {
                                results[i].add(n);
                                inserted = true;
                            }
                            temp.add(n.getString());
                        }
                    }
                }
            }             

            if (inserted) {
                for (int i = 0; i < list.length; i++) {
                    for (int j = 0; j < list[i].size(); j++) {
                        if (!taken.contains(list[i].get(j).getString())) {
                            untaken.add(list[i].get(j));
                        }
                    }
                }
                list = results;
                step1.add(list);
                taken_step1.add(taken);
            }                 
        }
        while (inserted && list.length > 1);
  
        // copy the resulting minterms into new arrayList
        finalTerms = new ArrayList();
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
    public void solve2() {
        addToTable();
        if (!identifyPrime()) {
            if (!rowDominance()) {
                if (!columnDominance()) {
                    petrick();
                    return;
                }
           }
        }
        if (minterms.size() != 0)
            solve2();
        else {
            addToTable();
            solution = new ArrayList[1];
            solution[0] = prime;
        }
    }
    
        void petrick() {
        
        HashSet<String>[] temp = new HashSet[minterms.size()];
        
        for (int i = 0; i < minterms.size(); i++) {
            temp[i] = new HashSet();
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    char t = (char) ('a' + j);
                    petrickKey.add(t + ": " + finalTerms.get(j).getString());
                    temp[i].add(t + "");
                }
            }
        }
        
        
        HashSet<String> finalResult = multiply(temp, 0);
        HashSet<String>[] step = new HashSet[1];
        step[0] = finalResult;
        stepPetrick(step, 0);
        step3.add("\n<b style=\"font-size: 2em\">Min:</b>");
        
        int min = -1;
        int count = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String m = t.next();
            if (min == -1 || m.length() < min) {
                min = m.length();
                count = 1;
            }
            else if (min == m.length()) {
                count++;
            }
        }
        solution = new ArrayList[count];
        int k = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String c = t.next();
            if (c.length() == min) {
                solution[k] = new ArrayList();
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
        
        
  
    
    String mix(String s1, String s2) {
       HashSet<Character> r = new HashSet();
       for (int i = 0; i < s1.length(); i++)
           r.add(s1.charAt(i));
       for (int i = 0; i < s2.length(); i++)
           r.add(s2.charAt(i));
       String result = "";
       for (Iterator<Character> i = r.iterator(); i.hasNext();)
           result += i.next();
       return result;
    }
    
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
    
    HashSet<String> multiply(HashSet<String>[] p, int k) {
        if (k >= p.length-1) {
            return p[k];
        }
        stepPetrick(p, k);
        
        
        HashSet<String> s = new HashSet<String>();
        for (Iterator<String> t = p[k].iterator(); t.hasNext();) {
            String temp2 = t.next();
            for (Iterator<String> g = p[k+1].iterator(); g.hasNext();) {
                String temp3 = g.next();
                s.add(mix(temp2, temp3));
            }
        }
        p[k+1] = s;
        return multiply(p, k+1);
    }
    
    private boolean columnDominance() {
        boolean flag = false;
        
        ArrayList<ArrayList<Integer>> cols = new ArrayList();
        for (int i = 0; i < minterms.size(); i++) {
            cols.add(new ArrayList<Integer>());
            for (int j = 0; j < finalTerms.size(); j++) {
                if (finalTerms.get(j).getNums().contains(minterms.get(i))) {
                    cols.get(i).add(j);
                }
            }
        }

        for (int i = 0; i < cols.size()-1; i++) {
            for (int j = i+1; j < cols.size(); j++) {
                if (cols.get(j).containsAll(cols.get(i))) {
                    cols.remove(j);
                    minterms.remove(j);
                    j--;
                    flag = true;
                }
                else if (cols.get(i).containsAll(cols.get(j))) {
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
        
        for (int i = 0; i < finalTerms.size()-1; i++) {
            for (int j = i+1; j < finalTerms.size(); j++) {
                if (contains(finalTerms.get(i), finalTerms.get(j))) {
                    finalTerms.remove(j);
                    j--;
                    flag = true;
                }
                else if (contains(finalTerms.get(j), finalTerms.get(i))) {
                    finalTerms.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    
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
                
                //System.out.println(finalTerms.get(cols[i].get(0)).getString());
                for (int j = 0; j < minterms.size(); j++) {
                    if (del.contains(minterms.get(j))){
                       // System.out.println("removed: " + minterms.get(j));
                        dc.add(minterms.get(j));
                        minterms.remove(j);
                        j--;
                    }
                }
                step2.get(step2.size()-1)[cols[i].get(0).intValue()+1][0] = "T";
                prime.add(finalTerms.get(cols[i].get(0)).getString());
                finalTerms.remove(cols[i].get(0).intValue());
                break;
            }
        }
        
        return flag;
    }
    
    void addToTable() {
        String[][] table = new String[finalTerms.size()+1][minterms.size()+maxLength+1];
        for (int i = 0; i < maxLength; i++) {
            table[0][i+1] = String.valueOf((char) ('A' + i));
        }
        for (int i = maxLength; i < minterms.size() + maxLength; i++) {
            table[0][i+1] = String.valueOf(minterms.get(i-maxLength));
        }
        for (int i = 1; i < finalTerms.size()+1; i++) {
            for (int j = 0; j < maxLength; j++) {
                table[i][j+1] = String.valueOf(finalTerms.get(i-1).getString().charAt(j));
            }
        }
        for (int i = 1; i < finalTerms.size()+1; i++) {
            for (int j = maxLength; j < minterms.size()+maxLength; j++) {
                if (finalTerms.get(i-1).getNums().contains(minterms.get(j-maxLength))) {
                    table[i][j+1] = "X";
                }
                else {
                    table[i][j+1] = " ";
                }
            }
        }
        for (int i = 0; i < finalTerms.size()+1; i++) {
            table[i][0] = " ";
        }
        step2.add(table);
    }
    
    /**
     * @param t1 first term
     * @param t2 second term
     * @return whether combination of t1, t2 possible
     */
    boolean isValidCombination(Term t1, Term t2) {
        // if length of t1, t2 not equal return false
        if (t1.getString().length() != t2.getString().length()) return false; 

        // if dashes not in same place or hamming dist != 1, return false
        int k = 0;
        for (int i = 0; i < t1.getString().length(); i++) {
            if (t1.getString().charAt(i) == '-' && t2.getString().charAt(i) != '-') return false;
            else if (t1.getString().charAt(i) != '-' && t2.getString().charAt(i) == '-') return false;
            else if (t1.getString().charAt(i) != t2.getString().charAt(i)) k++;
            else continue;
        }   
        if (k != 1) return false;
        else return true;
    }
    
    static boolean contains(Term t1, Term t2) {
        ArrayList<Integer> a = t1.getNums();
        ArrayList<Integer> b = t2.getNums();
        b.removeAll(dc);
        
        if(a.containsAll(b)) return true;
        else return false;
    }
     
    String toSymbolic(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-') {
                continue;
            }
            else if (s.charAt(i) == '1') {
                r.append((char) ('A' + i));
            }
            else {
                r.append((char) ('A' + i));
                r.append('\'');
            }
        }
        if (r.toString().length() == 0) {
            r.append("1");
        }
        return r.toString();
    }
    // test
    public String[] printResults() {
         StringBuffer html = new StringBuffer();
         for (int i = 0; i < solution.length; i++) {
             html.append("Solution #" + (i+1) + ":" + "<br>");
             for (int j = 0; j < solution[i].size(); j++) {
                 html.append(solution[i].get(j));
                 if (j != solution[i].size()-1) {
                     html.append(" + ");
                 }
             }
             html.append("<br><b style=\"font-size: 2em\">(");
             for (int j = 0; j < solution[i].size(); j++) {
                 html.append(toSymbolic(solution[i].get(j)));
                 if (j != solution[i].size()-1) {
                     html.append(" + ");
                 }
             }
             html.append(")</b>");
             html.append("<br>");
         }
         
         StringBuffer html2 = new StringBuffer();
         for (int i = 0; i < solution.length; i++) {
             html2.append("Solution #" + (i+1) + ":" + " ");
             for (int j = 0; j < solution[i].size(); j++) {
                 html2.append(solution[i].get(j));
                 if (j != solution[i].size()-1) {
                     html2.append(" + ");
                 }
             }
             html2.append("(");
             for (int j = 0; j < solution[i].size(); j++) {
                 html2.append(toSymbolic(solution[i].get(j)));
                 if (j != solution[i].size()-1) {
                     html2.append(" + ");
                 }
             }
             html2.append(") ");
             html2.append(" ");
         }
         
         String[] g = new String[2];
         g[0] = html2.toString();
         g[1] = html.toString();
         return g;
     }
}
