package de.klosebrothers.algorithm;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import org.junit.jupiter.api.Test;

class AlternativePathDetectorTest {

    @Test
    void itShouldNotFindAlternativePathForDirectNeighbors() {
        WeightedGraph graph = new WeightedGraph();

        Vertex vertexA = new Vertex("A");
        Vertex vertexB = new Vertex("B");

        graph.addVertex(vertexA);
        graph.addVertex(vertexB);

        graph.addEdge(vertexA, vertexB, 1.0);

        Optional<List<Vertex>> alternativePath = AlternativePathDetector.getAlternativePath(graph);

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

        Optional<List<Vertex>> alternativePath = AlternativePathDetector.getAlternativePath(graph);

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

        Optional<List<Vertex>> alternativePath = AlternativePathDetector.getAlternativePath(graph);

        assertThat(alternativePath).isPresent();
        assertThat(alternativePath.get()).contains(vertexA, vertexB, vertexC);
    }
}