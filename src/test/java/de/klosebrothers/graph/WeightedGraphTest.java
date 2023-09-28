package de.klosebrothers.graph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
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

        List<Vertex> cycleVerteces = graph.getCycleContainingVertex(new Vertex("unkwown"));

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

        List<Vertex> cycleVerteces = graph.getCycleContainingVertex(vertexC);

        assertThat(cycleVerteces).isEmpty();
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

        List<Vertex> cycleVerteces = graph.getCycleContainingVertex(vertexA);

        assertThat(cycleVerteces).containsExactly(vertexA, vertexB);
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

        List<Vertex> cycleVerteces = graph.getCycleContainingVertex(vertexA);

        assertThat(cycleVerteces).containsExactly(vertexA, vertexB, vertexC, vertexD);
    }
}