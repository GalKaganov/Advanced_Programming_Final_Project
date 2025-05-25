package servlets;

import configs.GenericConfig;
import configs.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser;
import views.HtmlGraphWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class ConfLoader implements Servlet {

    private final Path configFilesPath;
    private GenericConfig genericConfig;
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public ConfLoader(Path configFilesPath) {
        this.configFilesPath = configFilesPath;

        // Create directory if it doesn't exist
        if (!configFilesPath.toFile().exists()) {
            configFilesPath.toFile().mkdir();
        }
    }

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        if (ri.getContent().length == 0) {
            sendBadRequest(toClient);
            return;
        }

        try {
            System.out.println("post a config file request");

            // Save uploaded config file
            byte[] fileContent = ri.getContent();
            String fileName = UUID.randomUUID().toString();
            Path filePath = configFilesPath.resolve(fileName);
            Files.write(filePath, fileContent);
            System.out.println("File " + fileName + " saved");

            // Load config and create graph
            createConfig(filePath);
            System.out.println(topicManager.getTopics().size() + " topics created");
            System.out.println("Config created");

            Graph graph = createGraph();
            System.out.println("Graph created");

            // Generate HTML and send to client
            ArrayList<String> html = HtmlGraphWriter.getGraphHTML(graph);
            System.out.println("Html generated for graph");

            sendOkResponse(toClient, html);

        } catch (Exception e) {
            sendServerError(toClient, e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing ConfLoader");

        if (genericConfig != null) {
            genericConfig.close();
        }
    }

    // Initializes and loads configuration from file
    private void createConfig(Path filePath) {
        if (genericConfig != null) {
            genericConfig.close();
        }

        topicManager.clear();

        genericConfig = new GenericConfig();
        genericConfig.setConfFile(filePath.toString());
        genericConfig.create();
    }

    // Builds a Graph from current topics
    private Graph createGraph() {
        Graph graph = new Graph();
        graph.createFromTopics();
        return graph;
    }

    // Sends a 200 OK response with HTML content
    private void sendOkResponse(OutputStream toClient, ArrayList<String> html) throws IOException {
        toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
        toClient.write("Content-Type: text/html\r\n".getBytes());
        toClient.write("\r\n".getBytes());

        for (String line : html) {
            toClient.write(line.getBytes());
            toClient.write("\n".getBytes());
        }
    }

    // Sends a 500 Internal Server Error response
    private void sendServerError(OutputStream toClient, String errorMessage) throws IOException {
        toClient.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
        toClient.write("Content-Type: text/html\r\n".getBytes());
        toClient.write("\r\n".getBytes());
        toClient.write(("<html><body><h1>500 Internal Server Error</h1><p>" + errorMessage + "</p></body></html>").getBytes());
    }

    // Sends a 400 Bad Request response
    private void sendBadRequest(OutputStream toClient) throws IOException {
        toClient.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
        toClient.write("Content-Type: text/html\r\n".getBytes());
        toClient.write("\r\n".getBytes());
        toClient.write("<html><body><h1>400 Bad Request</h1><p>No content received in the request.</p></body></html>".getBytes());
    }
}
