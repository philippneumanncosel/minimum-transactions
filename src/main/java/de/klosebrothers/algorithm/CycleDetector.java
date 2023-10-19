package de.klosebrothers.algorithm;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class CycleDetector {

    private CycleDetector() {
    }

    public static List<Vertex> getCycle(WeightedGraph weightedGraph) {
        for (Vertex vertex : weightedGraph.getVertices()) {
            List<Vertex> cycle = getSmallestCycleContainingVertex(vertex);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }
        return new ArrayList<>();
    }

    public static List<Vertex> getSmallestCycleContainingVertex(Vertex vertex) {
        List<Vertex> visited = new ArrayList<>();
        Stack<Vertex> potentialCycle = new Stack<>();
        processVertexForCyclesSearch(vertex, vertex, visited, potentialCycle);
        return potentialCycle.stream().toList();
    }

    private static boolean processVertexForCyclesSearch(Vertex startVertex, Vertex currentVertex, List<Vertex> visited, Stack<Vertex> potentialCycle) {
        visited.add(currentVertex);
        potentialCycle.push(currentVertex);
        if (currentVertex.getOutVertices().contains(startVertex) && potentialCycle.size() > 1) {
            return true;
        }
        if (currentVertex.getOutVertices().stream()
                .filter(Predicate.not(visited::contains)).toList().stream()
                .anyMatch(nextVertex -> processVertexForCyclesSearch(startVertex, nextVertex, visited, potentialCycle))) {
            return true;
        }
        potentialCycle.pop();
        return false;
    }
}
