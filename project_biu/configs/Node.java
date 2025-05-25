package configs;

import graph.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;
    private Map<Node, String> edgeInputs; // Map to store input values for edges

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.msg = null;
        this.edgeInputs = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void addEdge(Node n) {
        for (Node node : edges) {
            if (node.name.equals(n.name)) {
                return;
            }
        }
        edges.add(n);
    }

    public void setEdgeInput(Node target, String input) {
        edgeInputs.put(target, input);
    }

    public String getInputForEdge(Node target) {
        return edgeInputs.get(target);
    }

    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> recursionStack = new HashSet<>();
        return dfs(this, visited, recursionStack);
    }

    private boolean dfs(Node current, Set<Node> visited, Set<Node> stack) {
        visited.add(current);
        stack.add(current);

        for (Node neighbor : current.edges) {
            if (!visited.contains(neighbor)) {
                if (dfs(neighbor, visited, stack)) {
                    return true;
                }
            } else if (stack.contains(neighbor)) {
                return true;
            }
        }

        stack.remove(current);
        return false;
    }
}
