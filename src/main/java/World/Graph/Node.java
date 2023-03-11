package World.Graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private static int uid = 0;
    private int id;
    private List<Edge> edges;
    private List<Node> adjacentNodes;

    public Node() {
        this.id = uid;
        this.edges = new ArrayList<>();
        this.adjacentNodes = new ArrayList<>();
        uid++;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
        Node from = edge.getFrom();
        if (from == this) {
            Node to = edge.getTo();
            this.adjacentNodes.add(to);
        } else {
            this.adjacentNodes.add(from);
        }
    }
}
