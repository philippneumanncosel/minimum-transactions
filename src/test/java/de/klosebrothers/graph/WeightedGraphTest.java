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
}