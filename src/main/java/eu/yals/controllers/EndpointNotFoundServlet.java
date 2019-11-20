package eu.yals.controllers;

import eu.yals.constants.MimeType;
import eu.yals.json.ErrorJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/aaa", name = "EndpointNotFoundServlet")
public class EndpointNotFoundServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(MimeType.APPLICATION_JSON);
        String response = ErrorJson.createWithMessage("Endpoint not found").toString();
        resp.setStatus(404);
        resp.getWriter().write(response);
    }
}
