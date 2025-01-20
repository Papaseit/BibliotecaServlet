package org.example.crudservlet.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crudservlet.modelo.Ejemplar;
import org.example.crudservlet.modelo.EjemplarDAO;
import org.example.crudservlet.modelo.Libro;
import org.example.crudservlet.modelo.LibroDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ejemplaresServlet", value = "/api/ejemplar")
public class EjemplarServlet extends HttpServlet {

    private EjemplarDAO ejemplarDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        ejemplarDAO = new EjemplarDAO();
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
                Ejemplar ejemplar = ejemplarDAO.getById(id);
                if (ejemplar != null) {
                    String jsonResponse = objectMapper.writeValueAsString(ejemplar);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Ejemplar no encontrado\"}");
                }
            } else {
                List<Ejemplar> listaEjemplares = ejemplarDAO.getAll();
                if (listaEjemplares != null && !listaEjemplares.isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(listaEjemplares);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"No se encontraron ejemplares\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String libroIsbn = request.getParameter("isbn");
        String estado = request.getParameter("estado");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Libro libro = new LibroDAO().getById(libroIsbn);

        if (libro != null) {
            Ejemplar ejemplar = new Ejemplar(libro, estado);

            if (ejemplarDAO.add(ejemplar)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("{\"message\": \"Ejemplar creado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"No se pudo crear el ejemplar\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"El libro con el ISBN proporcionado no existe\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String estado = request.getParameter("estado");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Ejemplar ejemplarExistente = ejemplarDAO.getById(id);

                if (ejemplarExistente != null) {
                    if (estado != null && !estado.isEmpty()) {
                        ejemplarExistente.setEstado(estado);
                    }

                    if (ejemplarDAO.update(ejemplarExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Ejemplar actualizado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al actualizar el ejemplar\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Ejemplar no encontrado\"}");
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
                Ejemplar ejemplarExistente = ejemplarDAO.getById(id);

                if (ejemplarExistente != null) {
                    if (ejemplarDAO.delete(ejemplarExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Ejemplar eliminado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al eliminar el ejemplar\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Ejemplar no encontrado\"}");
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
        ejemplarDAO.close();
    }
}
