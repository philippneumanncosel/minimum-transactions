package de.klosebrothers.rendering;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import org.junit.jupiter.api.Test;

class RendererTest {

    @Test
    void itShouldRenderWithJGraph() throws IOException {
        WeightedGraph graph = new WeightedGraph();

        Vertex testVertex = new Vertex("testVertex");
        Vertex anotherVertex = new Vertex("anotherVertex");

        graph.addVertex(testVertex);
        graph.addVertex(anotherVertex);

        graph.addEdge(testVertex, anotherVertex, 2.0);

        Renderer.renderPng(graph, "testRender");

        File testRenderFile = new File("src/generated/resources/testRender.png");

        assertThat(testRenderFile).isFile();
    }
}