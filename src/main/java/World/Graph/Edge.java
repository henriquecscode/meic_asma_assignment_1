package World.Graph;

import java.util.List;

public class Edge {
    private final Node from;
    private final Node to;
    private List<Edge> fromAdjacent;
    private List<Edge> toAdjacent;

    public Edge(Node from, Node to){
        this.from = from;
        this.to = to;
        this.fromAdjacent = from.getEdges();
        this.toAdjacent = to.getEdges();
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public List<Edge> getFromAdjacent() {
        return fromAdjacent;
    }

    public List<Edge> getToAdjacent() {
        return toAdjacent;
    }

    protected void updateGraph(){
        this.from.addEdge(this);
        this.to.addEdge(this);
    }
}
