package org.example.crudservlet.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crudservlet.modelo.Usuario;
import org.example.crudservlet.modelo.UsuarioDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "usuarioServlet", value = "/api/usuario")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Usuario usuario = usuarioDAO.getById(id);
                if (usuario != null) {
                    String jsonResponse = objectMapper.writeValueAsString(usuario);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Usuario no encontrado\"}");
                }
            } else {
                List<Usuario> listaUsuarios = usuarioDAO.getAll();
                if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(listaUsuarios);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"No se encontraron usuarios\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tipo = request.getParameter("tipo");
        String penalizacionHastaParam = request.getParameter("penalizacionHasta");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (dni == null || nombre == null || email == null || password == null || tipo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Todos los campos son obligatorios\"}");
            return;
        }

        try {
            LocalDate penalizacionHasta = null;
            if (penalizacionHastaParam != null && !penalizacionHastaParam.isEmpty()) {
                penalizacionHasta = LocalDate.parse(penalizacionHastaParam);
            }

            Usuario nuevoUsuario = new Usuario(dni, nombre, email, password, tipo, penalizacionHasta);

            boolean creado = usuarioDAO.add(nuevoUsuario);

            if (creado) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("{\"message\": \"Usuario creado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"No se pudo crear el usuario\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Usuario usuarioExistente = usuarioDAO.getById(id);

                if (usuarioExistente != null) {
                    if (dni != null && !dni.isEmpty()) usuarioExistente.setDni(dni);
                    if (nombre != null && !nombre.isEmpty()) usuarioExistente.setNombre(nombre);
                    if (email != null && !email.isEmpty()) usuarioExistente.setEmail(email);

                    if (usuarioDAO.update(usuarioExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Usuario actualizado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al actualizar el usuario\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Usuario no encontrado\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Se debe proporcionar un ID válido\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Usuario usuarioExistente = usuarioDAO.getById(id);

                if (usuarioExistente != null) {
                    if (usuarioDAO.delete(usuarioExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Usuario eliminado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al eliminar el usuario\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Usuario no encontrado\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Se debe proporcionar un ID válido\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    public void destroy() {
        usuarioDAO.close();
    }
}
