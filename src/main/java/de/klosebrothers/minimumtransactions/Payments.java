package de.klosebrothers.minimumtransactions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;

public class Payments {
    private final WeightedGraph graph;

    public Payments() {
        graph = new WeightedGraph();
    }

    public void registerPayment(String giverName, String recipientName, double paymentAmount) {
        Vertex giver = getOrCreatePerson(giverName);
        Vertex recipient = getOrCreatePerson(recipientName);
        WeightedEdge currentPayment = getOrCreatePayment(giver, recipient);
        currentPayment.addWeight(paymentAmount);
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
        return graph.getVertices().stream().collect(Collectors.toMap(Vertex::getName, Vertex::getInflux));
    }

    public String getResolvingPayments() {
        return graph.getVertices().stream()
                .flatMap(vertex -> vertex.getInEdges().values().stream())
                .map(Payments::getPaymentAsString)
                .sorted()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public void simplify() {
        while (!isSimplified()){
            eliminateAllCyclicPayments();
            eliminateAllChainedPayments();
        }
    }

    public boolean isSimplified() {
        return graph.getCycle().isEmpty() && graph.getMaximumChain().isEmpty();
    }

    public void eliminateAllCyclicPayments() {
        List<Vertex> cycle;
        while (!(cycle = graph.getCycle()).isEmpty()) {
            List<WeightedEdge> edgesOfCycle = graph.getEdgesOfCycle(cycle);
            graph.reduceEdgeWeights(edgesOfCycle, graph.getSmallestWeight(edgesOfCycle));
            graph.deleteEdgesWithZeroWeight(edgesOfCycle);
        }
    }

    public void eliminateAllChainedPayments() {
        Optional<List<WeightedEdge>> chainMaybe;
        while ((chainMaybe = graph.getMaximumChain()).isPresent()) {
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
        }
    }

    private static String getPaymentAsString(WeightedEdge edge) {
        return edge.getDestination().getName() + " owes " + edge.getSource().getName() + " " + edge.getWeight();
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
