package de.klosebrothers.graph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class WeightedGraphTest {

    @Test
    void itShouldAddVertex() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertex = new Vertex("vertex");

        graph.addVertex(vertex);

        assertThat(graph.getVertices()).contains(vertex);
    }

    @Test
    void itShouldReturnExistingVertexWithGivenName() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertex = new Vertex("vertex");

        graph.addVertex(vertex);

        Optional<Vertex> vertexMaybe = graph.getVertexByName("vertex");

        assertThat(vertexMaybe).contains(vertex);
    }

    @Test
    void itShouldNotReturnNonExistingVertexWithGivenName() {
        WeightedGraph graph = new WeightedGraph();

        Optional<Vertex> vertexMaybe = graph.getVertexByName("vertex");

        assertThat(vertexMaybe).isEmpty();
    }

    @Test
    void itShouldAddEdge() {
        WeightedGraph graph = new WeightedGraph();
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");

        graph.addVertex(sourceVertex);
        graph.addVertex(destinationVertex);
        graph.addEdge(sourceVertex, destinationVertex, 2.0);

        assertThat(sourceVertex.getOutEdges()).containsKey(destinationVertex);
        assertThat(sourceVertex.getOutEdges().get(destinationVertex).getWeight()).isEqualTo(2.0);
        assertThat(sourceVertex.getOutEdges().get(destinationVertex).getSource()).isEqualTo(sourceVertex);

        assertThat(destinationVertex.getInEdges()).containsKey(sourceVertex);
        assertThat(destinationVertex.getInEdges().get(sourceVertex).getWeight()).isEqualTo(2.0);
        assertThat(destinationVertex.getInEdges().get(sourceVertex).getDestination()).isEqualTo(destinationVertex);
    }

    @Test
    void itShouldRemoveExistingEdge() {
        WeightedGraph graph = new WeightedGraph();
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");

        graph.addVertex(sourceVertex);
        graph.addVertex(destinationVertex);
        graph.addEdge(sourceVertex, destinationVertex, 2.0);

        graph.removeEdge(sourceVertex, destinationVertex);

        assertThat(sourceVertex.getOutEdgeToVertex(destinationVertex)).isEmpty();
    }

    @Test
    void itShouldReturnEmptyListForUnconnectedVertex() {
        WeightedGraph graph = new WeightedGraph();

        List<Vertex> cycleVerteces = graph.getSmallestCycleContainingVertex(new Vertex("unkwown"));

        assertThat(cycleVerteces).isEmpty();
    }

    @Test
    void itShouldReturnEmptyListForConnectedVertexThatIsNotPartOfACycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexA, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);

        List<Vertex> cycleVertices = graph.getSmallestCycleContainingVertex(vertexC);

        assertThat(cycleVertices).isEmpty();
    }

    @Test
    void itShouldReturnCycleForVertexContainedInMinimalCycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexA, 0.0);

        List<Vertex> cycleVertices = graph.getSmallestCycleContainingVertex(vertexA);

        assertThat(cycleVertices).containsExactly(vertexA, vertexB);
    }

    @Test
    void itShouldReturnCycleForVertexContainedInLargerCycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");
        Vertex vertexD = new Vertex("D");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);
        graph.addEdge(vertexC, vertexD, 0.0);
        graph.addEdge(vertexD, vertexA, 0.0);

        List<Vertex> cycleVertices = graph.getSmallestCycleContainingVertex(vertexA);

        assertThat(cycleVertices).containsExactly(vertexA, vertexB, vertexC, vertexD);
    }

    @Test
    void itShouldReturnSmallestCycleForVertexContainedInMultipleCycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");
        Vertex vertexD = new Vertex("D");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);
        graph.addEdge(vertexC, vertexD, 0.0);
        graph.addEdge(vertexC, vertexA, 0.0);
        graph.addEdge(vertexD, vertexA, 0.0);

        List<Vertex> cycleVertices = graph.getSmallestCycleContainingVertex(vertexA);

        assertThat(cycleVertices).containsExactly(vertexA, vertexB, vertexC);
    }

    @Test
    void itShouldReturnEmptyListForGraphWithoutCycles() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexA, vertexC, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);

        List<Vertex> cycleVertices = graph.getCycle();

        assertThat(cycleVertices).isEmpty();
    }

    @Test
    void itShouldReturnCycleForGraphWithCycle() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");
        Vertex vertexD = new Vertex("D");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addEdge(vertexA, vertexB, 0.0);
        graph.addEdge(vertexB, vertexC, 0.0);
        graph.addEdge(vertexC, vertexD, 0.0);
        graph.addEdge(vertexD, vertexB, 0.0);

        List<Vertex> cycleVertices = graph.getCycle();

        assertThat(cycleVertices).contains(vertexB, vertexC, vertexD);
    }

    @Test
    void itShouldReturnEmptyListForEmptyCycle() {
        WeightedGraph graph = new WeightedGraph();

        List<WeightedEdge> cycleEdges = graph.getEdgesOfCycle(new ArrayList<>());

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

        List<WeightedEdge> cycleEdges = graph.getEdgesOfCycle(List.of(vertexA, vertexB, vertexC));

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
        WeightedGraph graph = new WeightedGraph();

        double smallestWeightOfCycle = graph.getSmallestWeight(new ArrayList<>());

        assertThat(smallestWeightOfCycle).isZero();
    }

    @Test
    void itShouldReturnSmallestWeightForCycle() {
        WeightedGraph graph = new WeightedGraph();
        WeightedEdge edgeA = new WeightedEdge(null , null, 1.0);
        WeightedEdge edgeB = new WeightedEdge(null , null, 2.0);
        WeightedEdge edgeC = new WeightedEdge(null , null, 3.0);

        double smallestWeightOfCycle = graph.getSmallestWeight(List.of(edgeA, edgeB, edgeC));

        assertThat(smallestWeightOfCycle).isOne();
    }

    @Test
    void itShouldSubtractAmountFromEdgeWeights() {
        WeightedGraph graph = new WeightedGraph();

        WeightedEdge edgeA = new WeightedEdge(null , null, 3.0);
        WeightedEdge edgeB = new WeightedEdge(null , null, 4.0);
        WeightedEdge edgeC = new WeightedEdge(null , null, 5.0);

        graph.reduceEdgeWeights(List.of(edgeA, edgeB, edgeC), 2.0);

        assertThat(edgeA.getWeight()).isOne();
        assertThat(edgeB.getWeight()).isEqualTo(2.0);
        assertThat(edgeC.getWeight()).isEqualTo(3.0);
    }

    @Test
    void itShouldRemoveEdgesWithZeroWeight() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        WeightedEdge edgeA = graph.addEdge(vertexA, vertexB, 0.0);
        WeightedEdge edgeB = graph.addEdge(vertexB, vertexC, 0.0);
        WeightedEdge edgeC = graph.addEdge(vertexC, vertexA, 2.0);

        graph.deleteEdgesWithZeroWeight(List.of(edgeA, edgeB, edgeC));

        assertThat(vertexA.getOutEdgeToVertex(vertexB)).isEmpty();
        assertThat(vertexB.getOutEdgeToVertex(vertexC)).isEmpty();
        assertThat(vertexC.getOutEdgeToVertex(vertexA)).isPresent();
    }
}