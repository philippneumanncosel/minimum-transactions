package de.klosebrothers.algorithm;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GraphUtilities {

    private GraphUtilities() {
    }

    public static List<WeightedEdge> getEdgesOfCycle(List<Vertex> cycleVertices) {
        List<WeightedEdge> cycleEdges = getEdgesOfChain(cycleVertices);
        if (!cycleVertices.isEmpty()) {
            Optional<WeightedEdge> closingCycleEdge = cycleVertices.get(cycleVertices.size() - 1)
                    .getOutEdgeToVertex(cycleVertices.get(0));
            closingCycleEdge.ifPresent(cycleEdges::add);
        }
        return cycleEdges;
    }

    public static List<WeightedEdge> getEdgesOfChain(List<Vertex> chainVertices) {
        List<WeightedEdge> chainEdges = new ArrayList<>();
        if (!chainVertices.isEmpty()) {
            for (int vertexIndex = 0; vertexIndex < chainVertices.size() - 1; vertexIndex++) {
                Optional<WeightedEdge> edgeMaybe = chainVertices.get(vertexIndex)
                        .getOutEdgeToVertex(chainVertices.get(vertexIndex + 1));
                if (edgeMaybe.isEmpty()) {
                    continue;
                }
                chainEdges.add(edgeMaybe.get());
            }
        }
        return chainEdges;
    }

    public static double getSmallestWeight(List<WeightedEdge> edges) {
        return edges.stream().map(WeightedEdge::getWeight).min(Comparator.naturalOrder()).orElse(0.0);
    }
}
