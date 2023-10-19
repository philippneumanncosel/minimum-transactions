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
        graph.addEdge(vertexB, vertexC, 0.0);
        graph.addEdge(vertexC, vertexB, 0.0);

        List<Vertex> cycleVertices = graph.getSmallestCycleContainingVertex(vertexA);

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

    @Test
    void itShouldFlipEdgeByCreatingNewEdge() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        WeightedEdge edge = graph.addEdge(vertexA, vertexB, -1.0);

        WeightedEdge flippedEdge = graph.flipEdge(edge);

        assertThat(flippedEdge.getSource()).isEqualTo(vertexB);
        assertThat(flippedEdge.getDestination()).isEqualTo(vertexA);
        assertThat(flippedEdge.getWeight()).isOne();

        assertThat(vertexA.getOutEdgeToVertex(vertexB)).isEmpty();
        assertThat(vertexB.getOutEdgeToVertex(vertexA)).contains(flippedEdge);
    }

    @Test
    void itShouldFlipEdgeByAddingWeightToExistingEdge() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        WeightedEdge existingEdge = graph.addEdge(vertexB, vertexA, 2.0);
        WeightedEdge edge = graph.addEdge(vertexA, vertexB, -1.0);

        WeightedEdge flippedEdge = graph.flipEdge(edge);

        assertThat(flippedEdge.getSource()).isEqualTo(vertexB);
        assertThat(flippedEdge.getDestination()).isEqualTo(vertexA);
        assertThat(flippedEdge.getWeight()).isEqualTo(3.0);

        assertThat(vertexA.getOutEdgeToVertex(vertexB)).isEmpty();
        assertThat(vertexB.getOutEdgeToVertex(vertexA)).contains(flippedEdge);

        assertThat(flippedEdge).isEqualTo(existingEdge);
    }

    @Test
    void itShouldFlipOnlyNegativeEdges() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        WeightedEdge negativeEdge = graph.addEdge(vertexA, vertexB, -2.0);
        WeightedEdge positiveEdge = graph.addEdge(vertexB, vertexC, 3.0);

        graph.flipEdgesWithNegativeWeight(List.of(negativeEdge, positiveEdge));

        assertThat(vertexB.getOutEdgeToVertex(vertexA)).isPresent();
        assertThat(vertexB.getOutEdgeToVertex(vertexA).get().getWeight()).isEqualTo(2.0);

        assertThat(vertexB.getOutEdgeToVertex(vertexC)).isPresent();
        assertThat(vertexB.getOutEdgeToVertex(vertexC).get().getWeight()).isEqualTo(3.0);
    }

    @Test
    void itShouldReturnEmptyListForGraphWithNoChainsWithAtLeastTwoEqualWeightsStartingFromEdge() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        WeightedEdge edge = graph.addEdge(vertexA, vertexB, 1.0);
        graph.addEdge(vertexB, vertexC, 2.0);

        Optional<List<WeightedEdge>> maximumChainFromVertex = graph.getMaximumChainFromStartEdge(edge);
        assertThat(maximumChainFromVertex).isEmpty();
    }

    @Test
    void itShouldReturnMinimalChainWithEqualWeightsStartingFromEdge() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        WeightedEdge firstEdge = graph.addEdge(vertexA, vertexB, 3.0);
        WeightedEdge sedondEdge = graph.addEdge(vertexB, vertexC, 3.0);

        Optional<List<WeightedEdge>> maximumChainFromVertex = graph.getMaximumChainFromStartEdge(firstEdge);

        assertThat(maximumChainFromVertex).isPresent();
        assertThat(maximumChainFromVertex.get()).contains(firstEdge, sedondEdge);
    }

    @Test
    void itShouldReturnChainWithMultipleEqualWeightsInMoreComplexGraphStartingFromEdge() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");
        Vertex vertexD = new Vertex("D");
        Vertex vertexE = new Vertex("E");
        Vertex vertexF = new Vertex("F");
        Vertex vertexG = new Vertex("G");
        Vertex vertexH = new Vertex("H");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addVertex(vertexE);
        graph.addVertex(vertexF);
        graph.addVertex(vertexG);
        graph.addVertex(vertexH);
        WeightedEdge firstChainEdge = graph.addEdge(vertexA, vertexB, 2.0);
        WeightedEdge sedondChainEdge = graph.addEdge(vertexB, vertexG, 2.0);
        WeightedEdge thirdChainEdge = graph.addEdge(vertexG, vertexD, 1.0);
        WeightedEdge fourthChainEdge = graph.addEdge(vertexD, vertexE, 2.0);
        WeightedEdge fithChainEdge = graph.addEdge(vertexE, vertexF, 2.0);

        graph.addEdge(vertexB, vertexC, 2.0);
        graph.addEdge(vertexC, vertexH, 2.0);
        graph.addEdge(vertexH, vertexF, 3.0);

        Optional<List<WeightedEdge>> maximumChainFromVertex = graph.getMaximumChainFromStartEdge(firstChainEdge);

        assertThat(maximumChainFromVertex).isPresent();
        assertThat(maximumChainFromVertex.get()).contains(firstChainEdge, sedondChainEdge, thirdChainEdge, fourthChainEdge, fithChainEdge);
    }

    @Test
    void itShouldReturnChainWithMultipleEqualWeightsInMoreComplexGraph() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");
        Vertex vertexD = new Vertex("D");
        Vertex vertexE = new Vertex("E");
        Vertex vertexF = new Vertex("F");
        Vertex vertexG = new Vertex("G");
        Vertex vertexH = new Vertex("H");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);
        graph.addVertex(vertexD);
        graph.addVertex(vertexE);
        graph.addVertex(vertexF);
        graph.addVertex(vertexG);
        graph.addVertex(vertexH);
        WeightedEdge firstChainEdge = graph.addEdge(vertexA, vertexB, 2.0);
        WeightedEdge sedondChainEdge = graph.addEdge(vertexB, vertexG, 2.0);
        WeightedEdge thirdChainEdge = graph.addEdge(vertexG, vertexD, 1.0);
        WeightedEdge fourthChainEdge = graph.addEdge(vertexD, vertexE, 2.0);
        WeightedEdge fithChainEdge = graph.addEdge(vertexE, vertexF, 2.0);

        graph.addEdge(vertexB, vertexC, 2.0);
        graph.addEdge(vertexC, vertexH, 2.0);
        graph.addEdge(vertexH, vertexF, 3.0);

        Optional<List<WeightedEdge>> chain = graph.getMaximumChain();

        assertThat(chain).isPresent();
        assertThat(chain.get()).contains(firstChainEdge, sedondChainEdge, thirdChainEdge, fourthChainEdge, fithChainEdge);
    }

    @Test
    void itShouldNotFindAlternativePathForDirectNeighbors() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        graph.addEdge(vertexA, vertexB, 1.0);

        Optional<List<Vertex>> alternativePath = graph.getAlternativePath();

        assertThat(alternativePath).isEmpty();
    }

    @Test
    void itShouldNotFindAlternativePathForGraphNotContainingAnAlternativePath() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 1.0);
        graph.addEdge(vertexB, vertexC, 1.0);
        graph.addEdge(vertexC, vertexA, 1.0);

        Optional<List<Vertex>> alternativePath = graph.getAlternativePath();

        assertThat(alternativePath).isEmpty();
    }

    @Test
    void itShouldFindAlternativePath() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");
        Vertex vertexC = new Vertex("C");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);
        graph.addVertex(vertexC);

        graph.addEdge(vertexA, vertexB, 1.0);
        graph.addEdge(vertexA, vertexC, 1.0);
        graph.addEdge(vertexB, vertexC, 2.0);

        Optional<List<Vertex>> alternativePath = graph.getAlternativePath();

        assertThat(alternativePath).isPresent();
        assertThat(alternativePath.get()).contains(vertexA, vertexB, vertexC);
    }
}