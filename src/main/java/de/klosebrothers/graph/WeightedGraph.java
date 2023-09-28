package de.klosebrothers.graph;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<WeightedEdge> getEdgesOfCycle(List<Vertex> cycleVertices) {
        ArrayList<WeightedEdge> cycleEdges = new ArrayList<>();
        if (!cycleVertices.isEmpty()) {
            for (int vertexIndex = 0; vertexIndex < cycleVertices.size() - 1; vertexIndex++) {
                Optional<WeightedEdge> edgeMaybe = cycleVertices.get(vertexIndex)
                        .getOutEdgeToVertex(cycleVertices.get(vertexIndex + 1));
                if (edgeMaybe.isEmpty()) {
                    continue;
                }
                cycleEdges.add(edgeMaybe.get());
            }
            Optional<WeightedEdge> closingCycleEdge = cycleVertices.get(cycleVertices.size() - 1)
                    .getOutEdgeToVertex(cycleVertices.get(0));
            closingCycleEdge.ifPresent(cycleEdges::add);
        }
        return cycleEdges;
    }

    public double getSmallestWeight(List<WeightedEdge> edges) {
        return edges.stream().map(WeightedEdge::getWeight).min(Comparator.naturalOrder()).orElse(0.0);
    }

    public void reduceEdgeWeights(List<WeightedEdge> edges, double amountToReduce) {
        edges.forEach(edge -> edge.subtractWeight(amountToReduce));
    }

    public void deleteEdgesWithZeroWeight(List<WeightedEdge> edges) {
        edges.stream()
                .filter(edge -> edge.getWeight() == 0)
                .forEach(edge -> removeEdge(edge.getSource(), edge.getDestination()));
    }

    public void flipEdgesWithNegativeWeight(List<WeightedEdge> edges) {
        edges.stream().filter(edge -> edge.getWeight() < 0.0).forEach(this::flipEdge);
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
}
