package views;

import configs.Graph;
import configs.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HtmlGraphWriter {

    // This method generates HTML lines to visualize the graph using a template HTML file
    public static ArrayList<String> getGraphHTML(Graph graph) {
        ArrayList<String> html = new ArrayList<>();

        try {
            // Read all lines from the graph.html template file
            List<String> lines = Files.readAllLines(Paths.get("html_files/graph.html"));

            for (String line : lines) {
                // Insert node data at the placeholder line
                if (line.trim().equals("// Nodes will be inserted here by Java code")) {

                    for (Node node : graph) {
                        // Get the node name without the prefix (e.g. remove 'T' or 'A')
                        String nodeName = node.getName().substring(1);

                        // Create a box for topic nodes (start with 'T'), circle for agent nodes (start with 'A')
                        if (node.getName().startsWith("T")) {
                            html.add(String.format(
                                    "{ id: '%s', label: '%s', shape: 'square', color: { background: '#003366', border: '#001F4D' }, font: { size: 20, color: 'white', vadjust: -60 }, widthConstraint: { minimum: 60, maximum: 60 }, heightConstraint: { minimum: 60, maximum: 60 } },",
                                    nodeName, nodeName));



                        } else {
                            html.add(String.format(
                                    "{ id: '%s', label: '%s', shape: 'circle', color: '#66A3FF', font: { size: 20 } },",
                                    nodeName, nodeName));
                        }
                    }

                    // Insert edge data at the placeholder line
                } else if (line.trim().equals("// Edges will be inserted here by Java code")) {

                    for (Node node : graph) {
                        String fromNodeName = node.getName().substring(1);

                        for (Node edgeNode : node.getEdges()) {
                            String toNodeName = edgeNode.getName().substring(1);
                            String input = node.getInputForEdge(edgeNode);
                            String edgeId = fromNodeName + "_to_" + toNodeName;

                            // Add a connection from this node to each connected node with input label and ID
                            html.add(String.format(
                                "{ id: '%s', from: '%s', to: '%s', label: '%s', font: { size: 14, align: 'middle', color: '#000000', strokeWidth: 0, strokeColor: '#ffffff', vadjust: -10 }, smooth: { type: 'curvedCW', roundness: 0.2 } },",
                                edgeId, fromNodeName, toNodeName, input != null ? input : ""));
                        }
                    }

                } else {
                    // Copy the rest of the lines as-is
                    html.add(line);
                }
            }

        } catch (IOException e) {
            // If the file doesn't exist or can't be read, stop the program
            throw new RuntimeException("Error reading the template HTML file", e);
        }

        return html;
    }
}
