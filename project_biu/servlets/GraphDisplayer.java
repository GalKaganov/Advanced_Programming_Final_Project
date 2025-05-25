package servlets;

import configs.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser;
import views.HtmlGraphWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class GraphDisplayer implements Servlet {
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        try {
            Graph graph = new Graph();
            graph.createFromTopics(); // Re-create graph from current topic states

            ArrayList<String> html = HtmlGraphWriter.getGraphHTML(graph);

            toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
            toClient.write("Content-Type: text/html\r\n".getBytes());
            toClient.write("\r\n".getBytes());
            for (String line : html) {
                toClient.write(line.getBytes());
                toClient.write("\n".getBytes());
            }
        } catch (Exception e) {
            toClient.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
            toClient.write("Content-Type: text/html\r\n".getBytes());
            toClient.write("\r\n".getBytes());
            toClient.write(("<html><body><h1>500 Internal Server Error</h1><p>" + e.getMessage() + "</p></body></html>").getBytes());
            e.printStackTrace(); // For server-side logging
        }
    }

    @Override
    public void close() throws IOException {
        // No specific resources to close for this simple displayer
    }
} 