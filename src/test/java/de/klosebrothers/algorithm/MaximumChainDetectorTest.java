package de.klosebrothers.algorithm;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedEdge;
import de.klosebrothers.graph.WeightedGraph;
import org.junit.jupiter.api.Test;

class MaximumChainDetectorTest {

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

        Optional<List<WeightedEdge>> maximumChainFromVertex = MaximumChainDetector.getMaximumChainFromStartEdge(edge);
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

        Optional<List<WeightedEdge>> maximumChainFromVertex = MaximumChainDetector.getMaximumChainFromStartEdge(firstEdge);

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

        Optional<List<WeightedEdge>> maximumChainFromVertex = MaximumChainDetector.getMaximumChainFromStartEdge(firstChainEdge);

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

        Optional<List<WeightedEdge>> chain = MaximumChainDetector.getMaximumChain(graph);

        assertThat(chain).isPresent();
        assertThat(chain.get()).contains(firstChainEdge, sedondChainEdge, thirdChainEdge, fourthChainEdge, fithChainEdge);
    }

}