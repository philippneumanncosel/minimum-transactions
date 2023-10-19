package de.klosebrothers.algorithm;

import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MaximumChainDetector {

    private MaximumChainDetector() {
    }

    public static Optional<List<WeightedEdge>> getMaximumChain(WeightedGraph graph) {
        List<List<WeightedEdge>> chains = graph.getVertices().stream()
                .flatMap(vertex -> vertex.getOutEdges().values().stream())
                .map(MaximumChainDetector::getMaximumChainFromStartEdge)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        Long highestCountOfEqualWeight = chains.stream()
                .map(chain -> getNumberOfEdgesWithWeight(chain, chain.get(0).getWeight()))
                .max(Comparator.naturalOrder())
                .orElse(0L);
        return chains.stream().filter(chain -> getNumberOfEdgesWithWeight(chain, chain.get(0)
                .getWeight()) == highestCountOfEqualWeight).findFirst();
    }

    public static Optional<List<WeightedEdge>> getMaximumChainFromStartEdge(WeightedEdge edge) {
        double weightToFind = edge.getWeight();
        List<WeightedEdge> edgePath = List.of(edge);
        List<List<WeightedEdge>> edgePaths = new ArrayList<>();
        edgePaths.add(edgePath);
        addAllEdgesToPathsRecursively(edge, edgePaths, edgePath);
        edgePaths.forEach(edges -> removeTrailingEdgesNotHavingWeight(edges, weightToFind));
        return getEdgePathContainingMostEdgesWithCertainWeightAndLeastOther(edgePaths, weightToFind);
    }

    private static void addAllEdgesToPathsRecursively(WeightedEdge edge, List<List<WeightedEdge>> edgePaths, List<WeightedEdge> edgePath) {
        edge.getDestination().getOutEdges().values().stream()
                .filter(Predicate.not(edgePath::contains))
                .forEach(nextEdge -> {
                    List<WeightedEdge> nextEdgePath = new ArrayList<>(List.copyOf(edgePath));
                    nextEdgePath.add(nextEdge);
                    edgePaths.add(nextEdgePath);
                    addAllEdgesToPathsRecursively(nextEdge, edgePaths, nextEdgePath);
                });
    }

    private static void removeTrailingEdgesNotHavingWeight(List<WeightedEdge> edges, double weight) {
        while (edges.get(edges.size() - 1).getWeight() != weight) {
            edges.remove(edges.size() - 1);
        }
    }

    public static Optional<List<WeightedEdge>> getEdgePathContainingMostEdgesWithCertainWeightAndLeastOther(List<List<WeightedEdge>> edgePaths, double weight) {
        Optional<Long> maxOccurrenceOfWeightMaybe = edgePaths.stream()
                .filter(edges -> edges.size() >= 2)
                .map(edges -> getNumberOfEdgesWithWeight(edges, weight))
                .max(Comparator.naturalOrder());
        if (maxOccurrenceOfWeightMaybe.isEmpty()) {
            return Optional.empty();
        }
        List<List<WeightedEdge>> filteredEdges = edgePaths.stream()
                .filter(edges -> getNumberOfEdgesWithWeight(edges, weight) == maxOccurrenceOfWeightMaybe.get()).toList();
        Optional<Integer> minNumberOfEdgesMaybe = filteredEdges.stream().map(List::size).min(Comparator.naturalOrder());
        return minNumberOfEdgesMaybe.flatMap(integer -> filteredEdges.stream().filter(edges -> edges.size() == integer)
                .findFirst());
    }

    private static long getNumberOfEdgesWithWeight(List<WeightedEdge> edges, double weight) {
        return edges.stream().filter(edge -> edge.getWeight() == weight).count();
    }
}
