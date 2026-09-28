<!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="main.css">
    </head>
    <body>
        <h1>Join our email list</h1>
        <p>To join our email list, enter your name and email address below.</p>
        <p><i>${message}</i></p>
        <form action="emailList" method="post">
            <p name="notify"></p>
            <input type="hidden" name="action" value="add">
            <label>Email:</label>
            <input type="email" name="email" value="${user.email}" required><br>
            <label>First Name:</label>
            <input type="text" name="firstName" value="${user.firstName}" required><br>
            <label>Last Name:</label>
            <input type="text" name="lastName" value="${user.lastName}" required><br>
            <label>&nbsp;</label>
            <input type="submit" value="Join Now" id="submit">
        </form>
    </body>
</html>
