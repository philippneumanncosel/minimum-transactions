package de.klosebrothers.algorithm;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;
import org.junit.jupiter.api.Test;

class GraphUtilitiesTest {

    @Test
    void itShouldReturnEmptyListForEmptyCycle() {
        List<WeightedEdge> cycleEdges = GraphUtilities.getEdgesOfCycle(new ArrayList<>());

        assertThat(cycleEdges).isEmpty();
    }

    @Test
    void itShouldReturnEdgesForGivenCycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);
        graph.addEdge(vertexC, vertexA, 0.0);

        List<WeightedEdge> cycleEdges = GraphUtilities.getEdgesOfCycle(List.of(vertexA, vertexB, vertexC));

        assertThat(cycleEdges).hasSize(3);
        assertThat(cycleEdges.get(0).getSource()).isEqualTo(vertexA);
        assertThat(cycleEdges.get(1).getSource()).isEqualTo(vertexB);
        assertThat(cycleEdges.get(2).getSource()).isEqualTo(vertexC);
        assertThat(cycleEdges.get(0).getDestination()).isEqualTo(vertexB);
        assertThat(cycleEdges.get(1).getDestination()).isEqualTo(vertexC);
        assertThat(cycleEdges.get(2).getDestination()).isEqualTo(vertexA);
    }

    @Test
    void itShouldReturnZeroForEmptyEdgeList() {
        double smallestWeightOfCycle = GraphUtilities.getSmallestWeight(new ArrayList<>());

        assertThat(smallestWeightOfCycle).isZero();
    }

    @Test
    void itShouldReturnSmallestWeightForCycle() {
        WeightedEdge edgeA = new WeightedEdge(null , null, 1.0);
        WeightedEdge edgeB = new WeightedEdge(null , null, 2.0);
        WeightedEdge edgeC = new WeightedEdge(null , null, 3.0);

        double smallestWeightOfCycle = GraphUtilities.getSmallestWeight(List.of(edgeA, edgeB, edgeC));

        assertThat(smallestWeightOfCycle).isOne();
    }
}