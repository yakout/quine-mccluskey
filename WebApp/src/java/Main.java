import java.util.ArrayList;
import java.util.HashSet;

public class Main {

    int[] isValid(String s) {
        s = s.replace(",", " ");
        if(s.length() == 0) return new int[]{};
        String[] a = s.trim().split(" +");
        int[] t = new int[a.length];
        for (int i = 0; i < t.length; i++) {
            try {
                int temp = Integer.parseInt(a[i]);
                t[i] = temp;
            }
            catch (Exception e) {
                throw new RuntimeException("Invalid Expression!");
            }
        }
        HashSet<Integer> dup = new HashSet();
        for (int i = 0; i < t.length; i++) {
            if (dup.contains(t[i])) {
                throw new RuntimeException("Please dont enter duplicate values!");
            }
            dup.add(t[i]);
        }
        
        return t;
        
    }
    
    void valid(int[] m, int[] d) {
        HashSet<Integer> temp = new HashSet<Integer>();
        for (int i = 0; i < m.length; i++) {
            temp.add(m[i]);
        }
        for (int i = 0; i < d.length; i++) {
            if (temp.contains(d[i])) {
                throw new RuntimeException("Duplicates Detected in Minterms and Dont Cares!");
            }
        }
    }
    
    String[] Steps(String m, String d) {
        if (m.length() == 0) throw new RuntimeException("Empty Field, please Enter Minterms!");
        int[] mm = isValid(m);
        int[] dd = isValid(d);
        
        valid(mm, dd);

        StringBuffer html  = new StringBuffer();
        
        Solver s = new Solver(mm , dd);
        double start = System.currentTimeMillis();
        s.solve();
        double duration = (System.currentTimeMillis() - start) / 1000.0;
        
        String[] soln1 = new String[2];
        soln1 = s.printResults();
        
        String forExport = soln1[0]; // without html tags
        String soln = soln1[1]; // with html tags
        
        
        ArrayList<ArrayList<Term>[]> s1 = s.step1;
        
        // to print 1st step
        html.append("<table size='3em'");
        for (int i = 0; i < s1.size(); i++) {
            html.append("<tr><td><b style=\"font-size: 2em\">Step " + (i+1) + "  </td></b><br>");
            for (int j = 0; j < s1.get(i).length; j++) {
                html.append("<td>");
                for (int k = 0; k < s1.get(i)[j].size(); k++) {
                    html.append("<b class='badge'>" + s1.get(i)[j].get(k).getString() + "</b>");
                    if (s.taken_step1.size() > i) {
                        if (s.taken_step1.get(i).contains(s1.get(i)[j].get(k).getString())) {
                            html.append(" <span class=\"glyphicon glyphicon-ok\"></span>  ");
                        } else {
                            html.append(" <span class=\"glyphicon glyphicon-remove\"></span>   ");
                        }
                    }
                    html.append("<br>");
                }
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table><br><hr><b style=\"font-size: 2em\">Coverage Table </b><br>");

        

        // print the coverage table..
        for (int k = 0; k < s.step2.size(); k++) {
            String[][] step2 = s.step2.get(k);
            html.append("<table border='' style='font-size: 2em'>");
            for (int i = 0; i < step2.length; i++) {
                if(step2[i][0] == "T") html.append("<tr  bgcolor=\"#F7B1A9\">");
                else html.append("<tr>");
                for (int j = 1; j < step2[0].length; j++) {
                    if (step2[i][j] == "X")
                    html.append("<td><span class=\"glyphicon glyphicon-remove\"></span> </td>");
                    else 
                    html.append("<td>" + step2[i][j] + "  </td>");
                }
                html.append("</tr>");
            }
            html.append("</table><br>");
        }
        
        html.append("<hr>");
        
        // print the petrick'Key if used..
        boolean flag = true;
        for (int i = 0; i < s.petrickKey.size(); i++) {
            if(flag) html.append("<b style='font-size: 2em'>Solving using Petrick's method</b><a href='https://en.wikipedia.org/wiki/Petrick%27s_method'> Tutorial</a><br>");
            html.append("<b class='badge'>" + s.petrickKey.get(i) + "</b><br>");
            flag = false;
        }
        
        html.append("<br><hr>");
        // print the petrick steps if used..
        for (int i = 0; i < s.step3.size(); i++) {
            html.append(s.step3.get(i) + "<br>");
        }
        
        
        String[] results = new String[3];
        results[0] = forExport;
        results[1] =  soln + "<br>";
        results[2] =  html.toString();
        return results;
    }

}
