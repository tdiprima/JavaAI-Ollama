package com.tdiprima.ollama.java;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * @author tdiprima
 */
@WebServlet(name = "OllamaServlet", urlPatterns = {"/ollama"})
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
        out.println("<form method='POST' action='/ollama-java/ollama'>");
        out.println("Input: <input type='text' name='query'/>");
        out.println("<input type='submit' value='Send to Ollama'/>");
        out.println("</form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userInput = request.getParameter("query");

        // Specify the model to use
        String model = "mistral";

        // Prepare the JSON payload for the POST request
        String jsonPayload = "{ \"model\": \"" + model + "\", \"prompt\": \"" + userInput + "\" }";

        // Create HttpClient and build the POST request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // Send the request and get the response
        HttpResponse<String> httpResponse;
        StringBuilder combinedResponse = new StringBuilder(); // To accumulate the responses
        try {
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = httpResponse.body();

            // Split incoming JSON blocks by spaces or newlines
            String[] jsonBlocks = responseBody.split("(?<=})\\s*(?=\\{)");

            // Loop through each JSON block
            for (String block : jsonBlocks) {
                // Parse each JSON block
                JSONObject jsonObj = new JSONObject(block);

                // Append the 'response' field to the combinedResponse
                combinedResponse.append(jsonObj.getString("response"));

                // Stop if 'done' field is true
                if (jsonObj.getBoolean("done")) {
                    break;
                }
            }
        } catch (InterruptedException | IOException | JSONException e) {
            combinedResponse.append("Error: Unable to process the response.");
        }

        // Display the combined response to the user
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Response from Ollama</h2>");
        out.println("<p>Your Input: " + userInput + "</p>");
        out.println("<p>Ollama Response: <br/>" + combinedResponse.toString() + "</p>");
        out.println("</body></html>");
    }

}
