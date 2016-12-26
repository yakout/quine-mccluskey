/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * @author ahmedyakout
 */
@WebServlet(urlPatterns = {"/solve"})
@MultipartConfig
public class Server extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //PrintWriter out = response.getWriter();
        

       //RequestDispatcher view = request.getRequestDispatcher("/index1.jsp");
       //view.forward(request, response); 

//        File fM; // minterms file
//        File fD; // dont cares file
//        try {
//            fM = request.getParameter("M");
//            fD = request.getParameter("D");
//        } catch (Exception e) {
//            
//        }
       
        String minTerms = request.getParameter("minterms");
        String dontCares = request.getParameter("dontcares");
               
        Main solve = new Main();
        String[] results;
        results = new String[2];
        try {
            results = solve.Steps(minTerms, dontCares);
            request.setAttribute("utilOutput", results[2]);
            request.setAttribute("FINAL", results[1]);
            request.setAttribute("SOLN", results[0]);
        } catch (Exception e) {
            request.setAttribute("ALERT", "alert('" + e.getMessage() + "')");
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
     
                
        //response.sendRedirect("/Tabular");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part Miterms = request.getPart("MM"); // Retrieves <input type="file" name="MM">
        Part DontCares = request.getPart("DD"); // Retrieves <input type="file" name="DD">
        InputStream MM = Miterms.getInputStream();
        InputStream DD = DontCares.getInputStream();
        String M = convertStreamToString(MM);
        String D = convertStreamToString(DD);
        
        response.setContentType("text/html;charset=UTF-8");


        Main solve = new Main();
        String[] results = new String[3];
        try {
            results = solve.Steps(M, D);
            request.setAttribute("utilOutput", results[2]);
            request.setAttribute("FINAL", results[1]);
            request.setAttribute("SOLN", results[3]);
        } catch (Exception e) {
            request.setAttribute("ALERT", "alert('"+ e.getMessage() + "')");
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);

        
        MM.close();
        DD.close();

    }
    
    static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Ahmed Yakout :D";
    }// </editor-fold>

}
