package de.klosebrothers.graph;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class WeightedGraph {
    private List<Vertex> vertices;

    public WeightedGraph() {
        vertices = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public Optional<Vertex> getVertexByName(String vertexName) {
        return vertices.stream().filter(vertex -> vertexName.equals(vertex.getName())).findFirst();
    }

    public WeightedEdge addEdge(Vertex sourceVertex, Vertex destinationVertex, double edgeWeight) {
        WeightedEdge edge = new WeightedEdge(sourceVertex, destinationVertex, edgeWeight);
        sourceVertex.addOutEdge(edge);
        destinationVertex.addInEdge(edge);
        return edge;
    }

    public void removeEdge(Vertex sourceVertex, Vertex destinationVertex) {
        Optional<WeightedEdge> edgeMaybe = sourceVertex.getOutEdgeToVertex(destinationVertex);
        if (edgeMaybe.isEmpty()) {
            return;
        }
        WeightedEdge edge = edgeMaybe.get();
        sourceVertex.removeOutEdge(edge);
        destinationVertex.removeInEdge(edge);
    }
}
