<%-- 
    Document   : index
    Created on : Apr 30, 2016, 12:50:39 AM
    Author     : nesmayakout
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tabular Method</title>
    </head>
    <body>
        <h1>Welcome to Tabular Solver!</h1>
        <form action="/solve" method="Get">
        <input type="text" name="minterms" value="" placeholder="Enter your minterms here"><br>
        <input type="text" name="dontcares" value="" placeholder="Enter your don't cares here"><br>
        <input type="submit" name="submit" value="submit">
        </form>
    </body>
</html>
