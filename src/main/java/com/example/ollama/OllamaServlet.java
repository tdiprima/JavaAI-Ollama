package com.example.ollama;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/ollama")
public class OllamaServlet extends HttpServlet {

    // The Ollama API endpoint
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Simple HTML form for input
        out.println("<html><body>");
        out.println("<h2>Ollama API Request</h2>");
        out.println("<form method='POST' action='/ollama'>");
        out.println("Input: <input type='text' name='query'/>");
        out.println("<input type='submit' value='Send to Ollama'/>");
        out.println("</form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userInput = request.getParameter("query");

        // Prepare the JSON payload for the POST request
        String jsonPayload = "{ \"prompt\": \"" + userInput + "\" }";

        // Create HttpClient and build the POST request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // Send the request and get the response
        HttpResponse<String> httpResponse;
        String ollamaResponse;
        try {
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ollamaResponse = httpResponse.body();  // API response
        } catch (InterruptedException | IOException e) {
            ollamaResponse = "Error: Unable to connect to Ollama API.";
        }

        // Display the response to the user
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Response from Ollama</h2>");
        out.println("<p>Your Input: " + userInput + "</p>");
        out.println("<p>Ollama Response: <br/>" + ollamaResponse + "</p>");
        out.println("</body></html>");
    }
}
