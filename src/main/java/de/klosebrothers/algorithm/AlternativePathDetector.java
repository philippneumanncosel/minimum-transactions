package de.klosebrothers.algorithm;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;

public class AlternativePathDetector {

    private AlternativePathDetector() {
    }

    public static Optional<List<Vertex>> getAlternativePath(WeightedGraph graph) {
        return graph.getVertices().stream()
                .map(AlternativePathDetector::getAlternativePathToNeighborVerticesOfVertex)
                .filter(Optional::isPresent)
                .findAny()
                .orElse(Optional.empty());
    }

    private static Optional<List<Vertex>> getAlternativePathToNeighborVerticesOfVertex(Vertex vertex) {
        List<Vertex> outVertices = vertex.getOutVertices();
        if (outVertices.size() < 2) {
            return Optional.empty();
        }
        return getSmallestAlternativePathToNeighborVertices(vertex);
    }

    private static Optional<List<Vertex>> getSmallestAlternativePathToNeighborVertices(Vertex vertex) {
        Stack<Vertex> potentialChain = new Stack<>();
        if (processVertexForAlternativePathSearch(vertex.getOutVertices(), vertex, potentialChain)) {
            return Optional.of(potentialChain.stream().toList());
        }
        return Optional.empty();
    }

    private static boolean processVertexForAlternativePathSearch(List<Vertex> targetVertices, Vertex currentVertex, Stack<Vertex> potentialChain) {
        potentialChain.push(currentVertex);
        if (potentialChain.size() > 2 && targetVertices.contains(currentVertex)) {
            return true;
        }
        if (currentVertex.getOutVertices().stream()
                .filter(Predicate.not(potentialChain::contains)).toList().stream()
                .anyMatch(nextVertex -> processVertexForAlternativePathSearch(targetVertices, nextVertex, potentialChain))) {
            return true;
        }
        potentialChain.pop();
        return false;
    }
}
