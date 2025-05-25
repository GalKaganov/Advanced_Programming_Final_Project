package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * The RequestParser class is responsible for parsing HTTP requests from a BufferedReader.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the provided BufferedReader.
     *
     * @param reader The BufferedReader to read the HTTP request from.
     * @return A RequestInfo object containing parsed request details.
     * @throws IOException If an I/O error occurs while reading or parsing the request.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        try {
            // Read request line
            String[] firstLine = reader.readLine().split(" ");
            String httpCommand = firstLine[0];
            String uri = firstLine[1];

            // Split URI into path and query parameters
            String[] uriParts = uri.split("\\?");
            String[] uriSegments = parseUriSegments(uriParts[0]);
            Map<String, String> parameters = new HashMap<>();
            if (uriParts.length > 1) {
                parameters = parseParameters(uriParts[1]);
            }

            // Parse headers
            Map<String, String> headers = new HashMap<>();
            String line;
            int contentLength = 0;
            while (!(line = reader.readLine()).isEmpty()) {
                String[] headerParts = line.split(": ");
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                    if (headerParts[0].equalsIgnoreCase("Content-Length")) {
                        contentLength = Integer.parseInt(headerParts[1]);
                    }
                }
            }

            // Parse content
            StringBuilder contentBuilder = new StringBuilder();
            if (contentLength > 0) {
                while (!(line = reader.readLine()).isEmpty()) {
                    if (line.contains("filename=")) {
                        parameters.put("filename", line.split("filename=")[1]);
                    }
                }
                while (!(line = reader.readLine()).isEmpty()) {
                    contentBuilder.append(line).append("\n");
                }
                while (reader.ready()) {
                    reader.readLine(); // discard remaining lines
                }
            }

            return new RequestInfo(httpCommand, uri, uriSegments, parameters, contentBuilder.toString().getBytes());
        } catch (Exception e) {
            throw new IOException("Error parsing request invalid format: " + e.getMessage());
        }
    }

    /**
     * Splits the URI into its segments.
     *
     * @param uriPart The part of the URI before the query string.
     * @return Array of URI segments.
     */
    private static String[] parseUriSegments(String uriPart) {
        return uriPart.substring(1).split("/");
    }

    /**
     * Parses query parameters from the URI.
     *
     * @param uriPart The query string part of the URI.
     * @return A map of key-value parameter pairs.
     */
    private static Map<String, String> parseParameters(String uriPart) {
        Map<String, String> parameters = new HashMap<>();
        String[] parameterPairs = uriPart.split("&");
        for (String pair : parameterPairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        return parameters;
    }

    /**
     * Internal class representing parsed request information.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments,
                           Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
