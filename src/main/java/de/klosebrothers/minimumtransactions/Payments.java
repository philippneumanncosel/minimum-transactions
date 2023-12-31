package de.klosebrothers.minimumtransactions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import de.klosebrothers.algorithm.AlternativePathDetector;
import de.klosebrothers.algorithm.CycleDetector;
import de.klosebrothers.algorithm.GraphUtilities;
import de.klosebrothers.algorithm.MaximumChainDetector;
import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;
import de.klosebrothers.rendering.Renderer;
import de.klosebrothers.util.DoubleUtil;

public class Payments {

    private final WeightedGraph graph;
    private final Renderer renderer;

    public Payments() {
        graph = new WeightedGraph();
        renderer = new Renderer("src/test/generated/resources/", "", 0);
    }

    public Payments(String name, int frameRatePerSecond) {
        graph = new WeightedGraph();
        renderer = new Renderer("src/test/generated/resources/", name, frameRatePerSecond);
    }

    public void registerPayment(String giverName, double paymentAmount, String... recipientNames) {
        final double paymentAmountPerPerson = paymentAmount / recipientNames.length;
        Arrays.stream(recipientNames).filter(name -> !name.equals(giverName)).forEach(recipientName -> {
            Vertex giver = getOrCreatePerson(giverName);
            Vertex recipient = getOrCreatePerson(recipientName);
            WeightedEdge currentPayment = getOrCreatePayment(giver, recipient);
            currentPayment.addWeight(paymentAmountPerPerson);
        });
    }

    public double getTotalPaymentFromTo(String giverName, String recipientName) {
        Optional<Vertex> giverMaybe = graph.getVertexByName(giverName);
        Optional<Vertex> recipientMaybe = graph.getVertexByName(recipientName);
        if (giverMaybe.isEmpty() || recipientMaybe.isEmpty()) {
            return 0.0;
        }
        return giverMaybe.get().getOutEdgeToVertex(recipientMaybe.get()).map(WeightedEdge::getWeight).orElse(0.0);
    }

    public double getInfluxForPerson(String name) {
        Optional<Vertex> person = graph.getVertexByName(name);
        return person.map(Vertex::getInflux).orElse(0.0);
    }

    public Map<String, Double> getAllInfluxes() {
        return graph.getVertices().stream()
                .collect(Collectors.toMap(Vertex::getName, vertex -> DoubleUtil.roundToTwoPlaces(vertex.getInflux())));
    }

    public String getResolvingPayments() {
        return graph.getVertices().stream()
                .flatMap(vertex -> vertex.getInEdges().values().stream())
                .map(Payments::getPaymentAsString)
                .sorted()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public void simplify(boolean render) {
        if (render) renderer.renderPng(graph);
        while (!isSimplified()){
            eliminateAllCyclicPayments(render);
            eliminateAllChainedPayments(render);
            eliminateAllIndirectPayments(render);
        }
        if (render) renderer.renderGif();
    }

    public boolean isSimplified() {
        return CycleDetector.getCycle(graph).isEmpty() && MaximumChainDetector.getMaximumChain(graph).isEmpty() && AlternativePathDetector.getAlternativePath(graph).isEmpty();
    }

    public void eliminateAllCyclicPayments(boolean render) {
        List<Vertex> cycle;
        while (!(cycle = CycleDetector.getCycle(graph)).isEmpty()) {
            List<WeightedEdge> edgesOfCycle = GraphUtilities.getEdgesOfCycle(cycle);
            graph.reduceEdgeWeights(edgesOfCycle, GraphUtilities.getSmallestWeight(edgesOfCycle));
            graph.deleteEdgesWithZeroWeight(edgesOfCycle);
            if (render) renderer.renderPng(graph);
        }
    }

    public void eliminateAllChainedPayments(boolean render) {
        Optional<List<WeightedEdge>> chainMaybe;
        while ((chainMaybe = MaximumChainDetector.getMaximumChain(graph)).isPresent()) {
            List<WeightedEdge> chain = chainMaybe.get();
            double chainWeight = chain.get(0).getWeight();
            graph.reduceEdgeWeights(chain, chainWeight);
            Vertex chainSource = chain.get(0).getSource();
            Vertex chainDestination = chain.get(chain.size() - 1).getDestination();
            Optional<WeightedEdge> chainSourceDestinationEdgeMaybe = chainSource.getOutEdgeToVertex(chainDestination);
            if (chainSourceDestinationEdgeMaybe.isPresent()) {
                chainSourceDestinationEdgeMaybe.get().addWeight(chainWeight);
            } else {
                graph.addEdge(chainSource, chainDestination, chainWeight);
            }
            graph.flipEdgesWithNegativeWeight(chain);
            graph.deleteEdgesWithZeroWeight(chain);
            if (render) renderer.renderPng(graph);
        }
    }

    public void eliminateAllIndirectPayments(boolean render) {
        Optional<List<Vertex>> indirectPaymentMaybe;
        while ((indirectPaymentMaybe = AlternativePathDetector.getAlternativePath(graph)).isPresent()) {
            List<Vertex> indirectPaymentVertices = indirectPaymentMaybe.get();
            List<WeightedEdge> indirectPaymentEdges = GraphUtilities.getEdgesOfChain(indirectPaymentVertices);
            double smallestIndirectPayment = GraphUtilities.getSmallestWeight(indirectPaymentEdges);
            graph.reduceEdgeWeights(indirectPaymentEdges, smallestIndirectPayment);
            graph.deleteEdgesWithZeroWeight(indirectPaymentEdges);
            Vertex startVertex = indirectPaymentVertices.get(0);
            Vertex targetVertex = indirectPaymentVertices.get(indirectPaymentVertices.size() - 1);
            Optional<WeightedEdge> directPaymentEdgeMaybe = startVertex.getOutEdgeToVertex(targetVertex);
            if (directPaymentEdgeMaybe.isEmpty()) {
                break;
            }
            directPaymentEdgeMaybe.get().addWeight(smallestIndirectPayment);
            if (render) renderer.renderPng(graph);
        }
    }

    private static String getPaymentAsString(WeightedEdge edge) {
        return edge.getDestination().getName() + " owes " + edge.getSource().getName() + " " + DoubleUtil.roundToTwoPlaces(edge.getWeight());
    }

    private Vertex getOrCreatePerson(String name) {
        return graph.getVertexByName(name).orElseGet(() -> createNewPerson(name));
    }

    private Vertex createNewPerson(String name) {
        Vertex vertex = new Vertex(name);
        graph.addVertex(vertex);
        return vertex;
    }

    private WeightedEdge getOrCreatePayment(Vertex giver, Vertex recipient) {
        return giver.getOutEdgeToVertex(recipient).orElseGet(() -> createNewEmptyPayment(giver, recipient));
    }

    private WeightedEdge createNewEmptyPayment(Vertex giver, Vertex recipient) {
        return graph.addEdge(giver, recipient, 0.0);
    }
}
