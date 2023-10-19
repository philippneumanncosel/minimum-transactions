package de.klosebrothers.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

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

    public void reduceEdgeWeights(List<WeightedEdge> edges, double amountToReduce) {
        edges.forEach(edge -> edge.subtractWeight(amountToReduce));
    }

    public WeightedEdge flipEdge(WeightedEdge edge) {
        removeEdge(edge.getSource(), edge.getDestination());
        Optional<WeightedEdge> flippedEdgeMaybe = edge.getDestination().getOutEdgeToVertex(edge.getSource());
        if (flippedEdgeMaybe.isPresent()) {
            flippedEdgeMaybe.get().subtractWeight(edge.getWeight());
            return flippedEdgeMaybe.get();
        }
        return addEdge(edge.getDestination(), edge.getSource(), -edge.getWeight());
    }

    public void flipEdgesWithNegativeWeight(List<WeightedEdge> edges) {
        edges.stream().filter(edge -> edge.getWeight() < 0.0).forEach(this::flipEdge);
    }

    public void deleteEdgesWithZeroWeight(List<WeightedEdge> edges) {
        edges.stream()
                .filter(edge -> edge.getWeight() == 0)
                .forEach(edge -> removeEdge(edge.getSource(), edge.getDestination()));
    }
}
