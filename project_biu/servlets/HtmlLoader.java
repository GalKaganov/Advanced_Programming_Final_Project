package servlets;

import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlLoader implements Servlet {

    private final Path htmlFilesPath;

    public HtmlLoader(String htmlFilesPath) {
        this.htmlFilesPath = Path.of(htmlFilesPath);

        // Create directory if it doesn't exist
        if (!this.htmlFilesPath.toFile().exists()) {
            this.htmlFilesPath.toFile().mkdir();
        }
    }

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        System.out.println("get html file request");

        String[] uriSegments = ri.getUriSegments();
        String fileName = uriSegments[uriSegments.length - 1];
        System.out.println("get request for file: " + fileName);

        // Check if file exists and respond accordingly
        if (checkFileExists(fileName)) {
            System.out.println("File found");
            sendFile(toClient, fileName, "200 OK");
        } else {
            System.out.println("File not found");
            send404(toClient);
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing HtmlLoader");
    }

    // Returns true if file exists in htmlFilesPath
    private boolean checkFileExists(String fileName) {
        return htmlFilesPath.resolve(fileName).toFile().exists();
    }

    // Sends an HTML file with the given HTTP status
    private void sendFile(OutputStream toClient, String fileName, String status) throws IOException {
        Path filePath = htmlFilesPath.resolve(fileName);
        byte[] fileContent = Files.readAllBytes(filePath);

        toClient.write(("HTTP/1.1 " + status + "\r\n").getBytes());
        toClient.write("Content-Type: text/html\r\n".getBytes());
        toClient.write(("Content-Length: " + fileContent.length + "\r\n").getBytes());
        toClient.write("\r\n".getBytes());
        toClient.write(fileContent);
    }

    // Sends a 404 Not Found response
    private void send404(OutputStream toClient) throws IOException {
        toClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
        toClient.write("Content-Type: text/plain\r\n".getBytes());
        toClient.write("Content-Length: 0\r\n".getBytes());
        toClient.write("\r\n".getBytes());
    }
}
