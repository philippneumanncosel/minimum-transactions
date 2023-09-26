package de.klosebrothers.graph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeightedGraphTest {

    @Test
    void itShouldAddVertex() {
        WeightedGraph graph = new WeightedGraph();
        Vertex vertex = new Vertex("vertex");

        graph.addVertex(vertex);

        assertThat(graph.getVertices()).contains(vertex);
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
}