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
        processVertexForCyclesSearch(vertex, vertex, visited, potentialCycle);
        return potentialCycle.stream().toList();
    }

    private boolean processVertexForCyclesSearch(Vertex startVertex, Vertex currentVertex, List<Vertex> visited, Stack<Vertex> potentialCycle) {
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

    public Optional<List<WeightedEdge>> getMaximumChain() {
        List<List<WeightedEdge>> chains = vertices.stream()
                .flatMap(vertex -> vertex.getOutEdges().values().stream())
                .map(this::getMaximumChainFromStartEdge)
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

    public Optional<List<WeightedEdge>> getMaximumChainFromStartEdge(WeightedEdge edge) {
        double weightToFind = edge.getWeight();
        List<WeightedEdge> edgePath = List.of(edge);
        List<List<WeightedEdge>> edgePaths = new ArrayList<>();
        edgePaths.add(edgePath);
        addAllEdgesToPathsRecursively(edge, edgePaths, edgePath);
        edgePaths.forEach(edges -> removeTrailingEdgesNotHavingWeight(edges, weightToFind));
        return getEdgePathContainingMostEdgesWithCertainWeightAndLeastOther(edgePaths, weightToFind);
    }

    private void addAllEdgesToPathsRecursively(WeightedEdge edge, List<List<WeightedEdge>> edgePaths, List<WeightedEdge> edgePath) {
        edge.getDestination().getOutEdges().values().stream()
                .filter(Predicate.not(edgePath::contains))
                .forEach(nextEdge -> {
                    List<WeightedEdge> nextEdgePath = new ArrayList<>(List.copyOf(edgePath));
                    nextEdgePath.add(nextEdge);
                    edgePaths.add(nextEdgePath);
                    addAllEdgesToPathsRecursively(nextEdge, edgePaths, nextEdgePath);
                });
    }

    public void removeTrailingEdgesNotHavingWeight(List<WeightedEdge> edges, double weight) {
        while (edges.get(edges.size() - 1).getWeight() != weight) {
            edges.remove(edges.size() - 1);
        }
    }

    public Optional<List<WeightedEdge>> getEdgePathContainingMostEdgesWithCertainWeightAndLeastOther(List<List<WeightedEdge>> edgePaths, double weight) {
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

    public long getNumberOfEdgesWithWeight(List<WeightedEdge> edges, double weight) {
        return edges.stream().filter(edge -> edge.getWeight() == weight).count();
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
