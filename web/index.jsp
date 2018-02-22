<%-- 
    Document   : index
    Created on : 19-Jan-2018, 14:12:18
    Author     : veckardt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="images/acls.css" media="screen">
        <link rel="stylesheet" type="text/css" href="images/aclsprint.css" media="print">        
        <title>Test Session</title>
        <style>
            body {
                background-image: url(./images/IntegrityWithSymbol.png);            
                background-origin: content-box;
                background-repeat: no-repeat;
                background-position: left 10px top 10px;
            }
        </style>        
    </head>
    <body>
        <form method="post" action="login.jsp">
            <div align=right><%=new java.util.Date()%></div>
            <h1>Test Session Loader</h1>                
            <center><br><br>
                <table border="1" width="30%" cellpadding="3">
                    <thead>
                        <tr>
                            <th colspan="2">Login</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>User&nbsp;Name</td>
                            <td><input type="text" name="uname" value="" /></td>
                        </tr>
                        <tr>
                            <td>Password</td>
                            <td><input type="password" name="pword" value="" /></td>
                        </tr>
                        <tr>
                            <td><input type="reset" value="Reset" /></td>
                            <td><input type="submit" value="Login" /></td>
                        </tr>
                    </tbody>
                </table>
            </center>
        </form>
    </body>
</html>