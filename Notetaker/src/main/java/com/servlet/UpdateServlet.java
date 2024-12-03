package com.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.connection.ConnectionProvider;
import com.entites.Note;

public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public UpdateServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Retrieve parameters from the request
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String noteIdParam = request.getParameter("note_id"); // Using "note_id" as the parameter name

            if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title and content cannot be empty.");
                return;
            }

            Session session = ConnectionProvider.getFactory().openSession();
            Transaction tx = session.beginTransaction();

            if (noteIdParam == null || noteIdParam.trim().isEmpty()) {
                // If no note ID is provided, create a new note
                Note newNote = new Note();
                newNote.setTitle(title.trim());
                newNote.setContent(content.trim());
                
                session.save(newNote);
                System.out.println("New note created successfully");

            } else {
                // If a note ID is provided, update the existing note
                try {
                    int noteId = Integer.parseInt(noteIdParam.trim()); // Convert note_id to integer
                    Note existingNote = session.get(Note.class, noteId);

                    if (existingNote == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Note with the given ID does not exist.");
                        return;
                    }

                    // Update the note details
                    existingNote.setTitle(title.trim());
                    existingNote.setContent(content.trim());

                    session.update(existingNote);
                    System.out.println("Note updated successfully with ID: " + noteId);

                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid note ID format.");
                    return;
                }
            }

            tx.commit();
            session.close();
            response.sendRedirect("all_notes.jsp"); // Redirect after successful update/creation

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }
}
