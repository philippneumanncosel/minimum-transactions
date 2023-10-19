package de.klosebrothers.algorithm;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import org.junit.jupiter.api.Test;

class CycleDetectorTest {

    @Test
    void itShouldReturnEmptyListForUnconnectedVertex() {
        List<Vertex> cycleVerteces = CycleDetector.getSmallestCycleContainingVertex(new Vertex("unkwown"));

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

        List<Vertex> cycleVertices = CycleDetector.getSmallestCycleContainingVertex(vertexA);

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

        List<Vertex> cycleVertices = CycleDetector.getSmallestCycleContainingVertex(vertexA);

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

        List<Vertex> cycleVertices = CycleDetector.getSmallestCycleContainingVertex(vertexA);

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

        List<Vertex> cycleVertices = CycleDetector.getSmallestCycleContainingVertex(vertexA);

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

        List<Vertex> cycleVertices = CycleDetector.getCycle(graph);

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

        List<Vertex> cycleVertices = CycleDetector.getCycle(graph);

        assertThat(cycleVertices).contains(vertexB, vertexC, vertexD);
    }

}