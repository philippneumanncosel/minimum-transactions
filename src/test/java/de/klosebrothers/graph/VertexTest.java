package de.klosebrothers.graph;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VertexTest {
    @Test
    void itShouldCreateVertex() {
        String vertexName = "myVertexName";

        Vertex vertex = new Vertex(vertexName);

        assertThat(vertex).isNotNull();
        assertThat(vertex.getName()).isEqualTo(vertexName);
        assertThat(vertex.getOutEdges()).isEmpty();
        assertThat(vertex.getInEdges()).isEmpty();
    }

    @Test
    void itShouldAddOutEdge() {
        Vertex vertex = new Vertex("vertex");
        Vertex connectedVertex = new Vertex("connected vertex");
        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getDestination()).thenReturn(connectedVertex);

        vertex.addOutEdge(edgeMock);

        assertThat(vertex.getOutEdges()).containsEntry(connectedVertex, edgeMock);
    }

    @Test
    void itShouldAddInEdge() {
        Vertex vertex = new Vertex("vertex");
        Vertex connectedVertex = new Vertex("connected vertex");
        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getSource()).thenReturn(connectedVertex);

        vertex.addInEdge(edgeMock);

        assertThat(vertex.getInEdges()).containsEntry(connectedVertex, edgeMock);
    }

    @Test
    void itShouldRemoveOutEdge() {
        Vertex vertex = new Vertex("vertex");
        Vertex connectedVertex = new Vertex("connected vertex");
        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getDestination()).thenReturn(connectedVertex);

        vertex.addOutEdge(edgeMock);
        vertex.removeOutEdge(edgeMock);

        assertThat(vertex.getOutEdges()).doesNotContainEntry(connectedVertex, edgeMock);
    }

    @Test
    void itShouldRemoveInEdge() {
        Vertex vertex = new Vertex("vertex");
        Vertex connectedVertex = new Vertex("connected vertex");
        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getSource()).thenReturn(connectedVertex);

        vertex.addInEdge(edgeMock);
        vertex.removeInEdge(edgeMock);

        assertThat(vertex.getInEdges()).doesNotContainEntry(connectedVertex, edgeMock);
    }

    @Test
    void itShouldNotReturnNonExistingOutEdge() {
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");
        Vertex unrelatedVertex = new Vertex("unrelated vertex");

        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getDestination()).thenReturn(destinationVertex);
        sourceVertex.addOutEdge(edgeMock);

        Optional<WeightedEdge> edge = sourceVertex.getOutEdgeToVertex(unrelatedVertex);

        assertThat(edge).isEmpty();
    }

    @Test
    void itShouldReturnExistingOutEdge() {
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");

        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getDestination()).thenReturn(destinationVertex);
        sourceVertex.addOutEdge(edgeMock);

        Optional<WeightedEdge> edge = sourceVertex.getOutEdgeToVertex(destinationVertex);

        assertThat(edge).contains(edgeMock);
    }

    @Test
    void itShouldNotReturnNonExistingInEdge() {
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");
        Vertex unrelatedVertex = new Vertex("unrelated vertex");

        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getSource()).thenReturn(sourceVertex);
        destinationVertex.addInEdge(edgeMock);

        Optional<WeightedEdge> edge = destinationVertex.getInEdgeFromVertex(unrelatedVertex);

        assertThat(edge).isEmpty();
    }

    @Test
    void itShouldReturnExistingInEdge() {
        Vertex sourceVertex = new Vertex("source vertex");
        Vertex destinationVertex = new Vertex("destination vertex");

        WeightedEdge edgeMock = mock(WeightedEdge.class);
        when(edgeMock.getSource()).thenReturn(sourceVertex);
        destinationVertex.addInEdge(edgeMock);

        Optional<WeightedEdge> edge = destinationVertex.getInEdgeFromVertex(sourceVertex);

        assertThat(edge).contains(edgeMock);
    }

    @Test
    void itShouldReturnCorrectInflux() {
        Vertex vertex = new Vertex("vertex");

        WeightedEdge inEdgeMockOne = mock(WeightedEdge.class);
        when(inEdgeMockOne.getWeight()).thenReturn(10.0);
        when(inEdgeMockOne.getSource()).thenReturn(new Vertex(""));
        WeightedEdge inEdgeMockTwo = mock(WeightedEdge.class);
        when(inEdgeMockTwo.getWeight()).thenReturn(5.0);
        when(inEdgeMockTwo.getSource()).thenReturn(new Vertex(""));

        WeightedEdge outEdgeMockOne = mock(WeightedEdge.class);
        when(outEdgeMockOne.getWeight()).thenReturn(8.0);
        when(outEdgeMockOne.getDestination()).thenReturn(new Vertex(""));
        WeightedEdge outEdgeMockTwo = mock(WeightedEdge.class);
        when(outEdgeMockTwo.getWeight()).thenReturn(6.0);
        when(outEdgeMockTwo.getDestination()).thenReturn(new Vertex(""));

        vertex.addInEdge(inEdgeMockOne);
        vertex.addInEdge(inEdgeMockTwo);
        vertex.addOutEdge(outEdgeMockOne);
        vertex.addOutEdge(outEdgeMockTwo);

        double influx = vertex.getInflux();

        assertThat(influx).isOne();
    }
}