package de.klosebrothers.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;
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

    public List<Vertex> getCycle() {
        for (Vertex vertex : vertices) {
            List<Vertex> cycle = getSmallestCycleContainingVertex(vertex);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }
        return new ArrayList<>();
    }

    public List<Vertex> getSmallestCycleContainingVertex(Vertex vertex) {
        List<Vertex> visited = new ArrayList<>();
        Stack<Vertex> potentialCycle = new Stack<>();
        processVertexForCyclesSearch(vertex, visited, potentialCycle);
        return potentialCycle.stream().toList();
    }

    private boolean processVertexForCyclesSearch(Vertex vertex, List<Vertex> visited, Stack<Vertex> potentialCycle) {
        visited.add(vertex);
        potentialCycle.push(vertex);
        boolean detectedCycle = vertex.getOutVertices().stream()
                .anyMatch(potentialCycle::contains);
        if (detectedCycle) {
            return true;
        }
        detectedCycle  = vertex.getOutVertices().stream()
                .filter(Predicate.not(visited::contains))
                .anyMatch(nextVertex -> processVertexForCyclesSearch(nextVertex, visited, potentialCycle));
        if (detectedCycle) {
            return true;
        }
        potentialCycle.pop();
        return false;
    }
}
