package org.example.crudservlet.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crudservlet.modelo.DAOGenerico;
import org.example.crudservlet.modelo.Libro;
import org.example.crudservlet.modelo.LibroDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "librosServlet", value = "/api/libro")
public class LibrosServlet extends HttpServlet {

    private LibroDAO libroDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        libroDAO = new LibroDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String isbn = request.getParameter("isbn");

        try {
            if (isbn != null && !isbn.isEmpty()) {
                Libro libro = libroDAO.getById(isbn);
                if (libro != null) {
                    String jsonResponse = objectMapper.writeValueAsString(libro);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Libro no encontrado\"}");
                }
            } else {
                List<Libro> listaLibros = libroDAO.getAll();
                if (listaLibros != null && !listaLibros.isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(listaLibros);
                    out.println(jsonResponse);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"No se encontraron libros\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");


        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Libro libro = new Libro(isbn, titulo, autor);

        if (libroDAO.add(libro)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println("{\"message\": \"Libro creado con éxito\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"No se pudo crear el libro\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");

        try {
            if (isbn != null && !isbn.isEmpty()) {
                Libro libroExistente = libroDAO.getById(isbn);

                if (libroExistente != null) {
                    if (titulo != null && !titulo.isEmpty()) {
                        libroExistente.setTitulo(titulo);
                    }
                    if (autor != null && !autor.isEmpty()) {
                        libroExistente.setAutor(autor);
                    }

                    if (libroDAO.update(libroExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Libro actualizado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al actualizar el libro\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Libro no encontrado\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Se debe proporcionar un ISBN válido\"}");
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

        // Obtener el parámetro ISBN
        String isbn = request.getParameter("isbn");

        try {
            if (isbn != null && !isbn.isEmpty()) {
                // Verificar si el libro existe
                Libro libroExistente = libroDAO.getById(isbn);

                if (libroExistente != null) {
                    // Intentar eliminar el libro
                    if (libroDAO.delete(libroExistente)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println("{\"message\": \"Libro eliminado con éxito\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("{\"error\": \"Error al eliminar el libro\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"Libro no encontrado\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Se debe proporcionar un ISBN válido\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error al procesar la solicitud\"}");
        }
    }


    @Override
    public void destroy() {
        libroDAO.close();
    }
}