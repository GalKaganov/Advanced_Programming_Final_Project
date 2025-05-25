package configs;

import graph.Agent;
import graph.Topic;
import graph.TopicManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class Graph extends ArrayList<Node> {
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    private boolean nodeExists(String nodeName) {
        for (Node node : this) {
            if (node.getName().equals(nodeName)) {
                return true;
            }
        }
        return false;
    }

    private int getNodeIndex(String nodeName) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getName().equals(nodeName)) {
                return i;
            }
        }
        return -1;
    }

    public void printGraphNodes() {
        for (Node node : this) {
            System.out.print(node.getName() + "  ");
        }
        System.out.println();
    }

    private void reset() {
        this.clear();
    }

    public void printGraphWithNodesAndEdges() {
        for (Node node : this) {
            List<Node> edges = node.getEdges();
            if (!edges.isEmpty()) {
                System.out.print(node.getName() + " -> ");
                for (Node edge : edges) {
                    System.out.print(edge.getName() + " ");
                }
                System.out.println();
            }
        }
    }

    public void createFromTopics() {
        reset();

        for (Topic topic : topicManager.getTopics()) {
            String topicNodeName = "T" + topic.getName();

            if (!nodeExists(topicNodeName)) {
                this.add(new Node(topicNodeName));
            }

            for (Agent subscriber : topic.getSubscribers()) {
                String agentNodeName = "A" + subscriber.getName();
                if (!nodeExists(agentNodeName)) {
                    this.add(new Node(agentNodeName));
                }
                int topicIndex = getNodeIndex(topicNodeName);
                int agentIndex = getNodeIndex(agentNodeName);
                Node topicNode = this.get(topicIndex);
                Node agentNode = this.get(agentIndex);
                topicNode.addEdge(agentNode);
                topicNode.setEdgeInput(agentNode, topic.getLastMessage()
                    .map(msg -> msg.asDouble == 0.0 ? "" : String.format("%.2f", msg.asDouble))
                    .orElse(""));
            }

            for (Agent publisher : topic.getPublishers()) {
                String agentNodeName = "A" + publisher.getName();
                if (!nodeExists(agentNodeName)) {
                    this.add(new Node(agentNodeName));
                }
                int topicIndex = getNodeIndex(topicNodeName);
                int agentIndex = getNodeIndex(agentNodeName);
                Node agentNode = this.get(agentIndex);
                Node topicNode = this.get(topicIndex);
                agentNode.addEdge(topicNode);
                agentNode.setEdgeInput(topicNode, topic.getLastMessage()
                    .map(msg -> msg.asDouble == 0.0 ? "" : String.format("%.2f", msg.asDouble))
                    .orElse(""));
            }
        }
    }
}
