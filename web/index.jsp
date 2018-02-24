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
        <link rel="stylesheet" type="text/css" href="images/layout.css" media="screen">
        <title>Test Session</title>
    </head>
    <body>
        <form method="post" action="login.jsp">
            <div class="date"><%=new java.util.Date()%></div>
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