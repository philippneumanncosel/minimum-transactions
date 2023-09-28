package de.klosebrothers.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class Vertex {
    private String name;
    private Map<Vertex, WeightedEdge> outEdges;
    private Map<Vertex, WeightedEdge> inEdges;

    public Vertex(String name) {
        this.name = name;
        outEdges = new HashMap<>();
        inEdges = new HashMap<>();
    }

    public void addOutEdge(WeightedEdge edge) {
        outEdges.put(edge.getDestination(), edge);
    }

    public void addInEdge(WeightedEdge edge) {
        inEdges.put(edge.getSource(), edge);
    }

    public void removeOutEdge(WeightedEdge edge) {
        outEdges.remove(edge.getDestination());
    }

    public void removeInEdge(WeightedEdge edge) {
        inEdges.remove(edge.getSource());
    }

    public Optional<WeightedEdge> getOutEdgeToVertex(Vertex vertex) {
        return Optional.ofNullable(outEdges.getOrDefault(vertex, null));
    }

    public Optional<WeightedEdge> getInEdgeFromVertex(Vertex vertex) {
        return Optional.ofNullable(inEdges.getOrDefault(vertex, null));
    }

    public List<Vertex> getOutVertices() {
        return outEdges.values().stream().map(WeightedEdge::getDestination).toList();
    }

    public double getInflux() {
        return getSumOfEdges(inEdges) - getSumOfEdges(outEdges);
    }

    @NotNull
    private static Double getSumOfEdges(Map<Vertex, WeightedEdge> edges) {
        return edges.values().stream().map(WeightedEdge::getWeight).reduce((double) 0, Double::sum);
    }
}
