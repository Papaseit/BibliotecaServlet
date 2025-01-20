package org.example.crudservlet.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "loginJson", value = "/loginJson")
public class loginUsuarioJson extends HttpServlet {
    private String nickname;
    private String password;

    public void init() {
        nickname="";
        password="";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter(); //imprime la respuesta HTTP
        ObjectMapper conversor= new ObjectMapper(); //conversor a JSON


        System.out.println("Nombres de parámetros: "+request.getParameterNames().toString());


        String nickname = request.getParameter("username"); //recogemos los datos del formulario
        String password = request.getParameter("password"); //recogemos los datos del formulario

        ArrayList<String> lista = new ArrayList<>();
        lista.add(nickname);
        lista.add(password);

        out.print(conversor.writeValueAsString(lista));
        System.out.println("En consola: "+conversor.writeValueAsString(lista));
    }

    public void destroy() {
    }
}