
import java.util.ArrayList;

public class Term {
    /**
     * string representation of term (includes '0's, '1's, '_'s)
     * eg: 0010, 0_10, _1_0
     */
    private String term;
    /**
     * num of ones in the binary representation
     */
    private int ones;
    /**
     * numbers this term represents
     */
    private ArrayList<Integer> nums;
    
    /**
     * @param value the integer value of the minterm
     * @param length represents the length of the string to
     * make place for leading zeroes in the binary representation
     * length should be of the maximum of the minterms and don't cares
     * assumes length >= binary value representation
     */
    public Term(int value, int length) {
        // convert value to binary 
        String binary = Integer.toBinaryString(value);
        
        // store it in term with leading zeroes if necessary depending on length
        StringBuffer temp = new StringBuffer(binary);
        while (temp.length() != length) {
            temp.insert(0, 0);
        } 
        this.term = temp.toString();
       
        // initialize nums as array of 1 element and add value to it
        nums = new ArrayList();
        nums.add(value);
        
        // count num of ones
        ones = 0;
        for (int i = 0; i < term.length(); i++) {
            if (term.charAt(i) == '1') {
                ones++;
            }
        }
    }
    
    /**
     * @param t1 first term
     * @param t2 second term
     * initializes new term as combination of t1, t2
     * assumes combination is possible (dashes in same index, hamming distance=1, same length)
     */
    public Term(Term t1, Term t2) {   
        // loop over t1, t2 and initialize "term" by replacing (1/0) by -
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < t1.getString().length(); i++) {
            if (t1.getString().charAt(i) != t2.getString().charAt(i)) {
                temp.append("-");
            } else {
                temp.append(t1.getString().charAt(i));
            }
        }
        
        this.term = temp.toString();
        
        
        // and count num of ones
        ones = 0;
        for (int i = 0; i < term.length(); i++) {
            if (this.term.charAt(i) == '1') {
                ones++;
            }
        }
        
        // initialize nums by merging t1's nums and t2's nums
        nums = new ArrayList();;
        for (int i = 0; i < t1.getNums().size(); i++) {
            nums.add(t1.getNums().get(i)); 
        }
        for (int i = 0; i < t2.getNums().size(); i++) {
            nums.add(t2.getNums().get(i));  
        }
    }
    
    String getString() { return term; }
    ArrayList<Integer> getNums() { return nums; }
    int getNumOnes() { return ones; }
    
}

