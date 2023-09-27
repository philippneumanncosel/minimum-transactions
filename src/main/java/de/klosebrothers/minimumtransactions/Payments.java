package de.klosebrothers.minimumtransactions;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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