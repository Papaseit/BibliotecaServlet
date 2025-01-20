package org.example.crudservlet.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crudservlet.modelo.Prestamo;
import org.example.crudservlet.modelo.PrestamoDAO;
import org.example.crudservlet.modelo.Usuario;
import org.example.crudservlet.modelo.Ejemplar;
import org.example.crudservlet.modelo.UsuarioDAO;
import org.example.crudservlet.modelo.EjemplarDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "prestamoServlet", value = "/api/prestamo")
public class PrestamoServlet extends HttpServlet {

    private PrestamoDAO prestamoDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        prestamoDAO = new PrestamoDAO();
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
                Prestamo prestamo = prestamoDAO.getById(id);
                if (prestamo != null) {
                    String jsonResponse = objectMapper.writeValueAsString(prestamo);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Préstamo no encontrado\"}");
                }
            } else {
                List<Prestamo> listaPrestamos = prestamoDAO.getAll();
                if (listaPrestamos != null && !listaPrestamos.isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(listaPrestamos);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"No se encontraron préstamos\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuarioId = request.getParameter("usuarioId");
        String ejemplarId = request.getParameter("ejemplarId");
        String fechaInicioParam = request.getParameter("fechaInicio");
        String fechaDevolucionParam = request.getParameter("fechaDevolucion");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Usuario usuario = new UsuarioDAO().getById(Integer.parseInt(usuarioId));
        Ejemplar ejemplar = new EjemplarDAO().getById(Integer.parseInt(ejemplarId));

        if (usuario != null && ejemplar != null) {
            try {
                LocalDate fechaInicio = LocalDate.parse(fechaInicioParam);

                LocalDate fechaDevolucion = null;
                if (fechaDevolucionParam != null && !fechaDevolucionParam.isEmpty()) {
                    fechaDevolucion = LocalDate.parse(fechaDevolucionParam);
                }

                Prestamo prestamo = new Prestamo(usuario, ejemplar, fechaInicio);
                prestamo.setFechaDevolucion(fechaDevolucion);

                if (prestamoDAO.add(prestamo)) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.println("{\"message\": \"Préstamo creado con éxito\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"No se pudo crear el préstamo\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Fecha inválida. Asegúrese de que el formato sea correcto (yyyy-MM-dd)\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Usuario o ejemplar no encontrado\"}");
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String fechaDevolucionParam = request.getParameter("fechaDevolucion");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Prestamo prestamoExistente = prestamoDAO.getById(id);

                if (prestamoExistente != null) {
                    if (fechaDevolucionParam != null && !fechaDevolucionParam.isEmpty()) {
                        prestamoExistente.setFechaDevolucion(LocalDate.parse(fechaDevolucionParam));
                    }

                    if (prestamoDAO.update(prestamoExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Préstamo actualizado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al actualizar el préstamo\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Préstamo no encontrado\"}");
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
                Prestamo prestamoExistente = prestamoDAO.getById(id);

                if (prestamoExistente != null) {
                    if (prestamoDAO.delete(prestamoExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Préstamo eliminado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al eliminar el préstamo\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Préstamo no encontrado\"}");
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
        prestamoDAO.close();
    }
}
