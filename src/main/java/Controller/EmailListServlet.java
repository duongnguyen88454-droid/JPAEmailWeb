package Controller;

import Model.User;
import data.MailUtilGmail;
import java.io.*;
import javax.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dao.UserDB;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse respone)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String url = "/index.jsp";
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";
        }

        if (action.equals("join")) {
            url = "/index.jsp";
        } else if (action.equals("add")) {
            String firstName = request.getParameter("firstName");
            if (firstName == null) {
                firstName = request.getParameter("fristName");
            }
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String message = "";

            User user = new User(firstName, lastName, email);

            if (UserDB.emailExists(email)) {
                message = "This email address already exsists.<br> Please enter anthor email address";
                url = "/index.jsp";
            } else {
                message = "";
                url = "/thanks.jsp";
                UserDB.insert(user);

                String to = email;
                String from = "phaty9147@gmail.com";
                String subject = "Welcome to our email list";
                String body = "Dear " + firstName + ",\n\n"
                        + "Thanks for joining our email list. "
                        + "We'll make sure to send "
                        + "you announcements about new products "
                        + "and promotions.\n"
                        + "Have a great day and thanks again!\n\n"
                        + "Kelly Slivkoff\n"
                        + "Mike Murach & Associates";
                boolean isBodyHTML = false;

                try {
                    MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
                } catch (MessagingException e) {
                    String errorMessage = "ERROR: Unable to send email. "
                            + "Check Tomcat logs for details.<br>"
                            + "NOTE: You may need to configure your system "
                            + "as described in chapter 14.<br>"
                            + "ERROR MESSAGE: " + e.getMessage();
                    request.setAttribute("errorMessage", errorMessage);
                    this.log(
                            "Unable to send email. \n"
                                    + "Here is the email you tried to send: \n"
                                    + "=====================================\n"
                                    + "TO: " + email + "\n"
                                    + "FROM: " + from + "\n"
                                    + "SUBJECT: " + subject + "\n\n"
                                    + body + "\n\n");
                }
            }

            request.setAttribute("user", user);
            request.setAttribute("message", message);
        }
        getServletContext().getRequestDispatcher(url).forward(request, respone);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse respone)
            throws ServletException, IOException {
        doPost(request, respone);
    }
}