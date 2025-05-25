package servlets;

import graph.Message;
import graph.Topic;
import server.RequestParser;
import graph.TopicManagerSingleton;
import views.HtmlTableWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class TopicDisplayer implements Servlet {

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        if (ri.getParameters().size() == 2) {
            try {
                String topic = ri.getParameters().get("topicName");
                Message message = new Message(ri.getParameters().get("message"));
                if (!TopicManagerSingleton.get().containsTopic(topic)) {
                    throw new Exception("Topic does not exist");
                }
                TopicManagerSingleton.get().getTopic(topic).publish(message);
                Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
                Map<String, String> topicMap = new HashMap<>();
                for (Topic t : topics) {
                    topicMap.put(t.getName(), t.getLastMessage().orElse(new Message("0.0")).asText);
                }
                ArrayList<String> tableHtml = HtmlTableWriter.getTableHtml(topicMap);
                System.out.println("Html generated for table");
                toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
                toClient.write("Content-Type: text/html\r\n".getBytes());
                toClient.write("\r\n".getBytes());
                for (String line : tableHtml) {
                    toClient.write(line.getBytes());
                    toClient.write("\n".getBytes());
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                toClient.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
                toClient.write("Content-Type: text/html\r\n".getBytes());
                toClient.write("\r\n".getBytes());
                toClient.write("<html><body><h1>500 Internal Server Error</h1><p>".getBytes());
                toClient.write(e.getMessage().getBytes());
            }

        } else {
            toClient.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
            toClient.write("Content-Type: text/html\r\n".getBytes());
            toClient.write("\r\n".getBytes());
            toClient.write("<html><body><h1>400 Bad Request</h1><p>No content received in the request.</p></body></html>".getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing TopicDisplayer");
    }
}
