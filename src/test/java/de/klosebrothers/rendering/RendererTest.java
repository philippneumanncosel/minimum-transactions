package de.klosebrothers.rendering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import de.klosebrothers.graph.Vertex;
import de.klosebrothers.graph.WeightedGraph;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RendererTest {

    private static final String TEST_GENERATED_RESOURCES_PATH = "src/test/generated/resources/";

    @BeforeAll
    static void beforeAll() {
        try {
            Files.createDirectories(Paths.get(TEST_GENERATED_RESOURCES_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        File testGeneratedResourcesDirectory = new File(TEST_GENERATED_RESOURCES_PATH);
        try {
            FileUtils.cleanDirectory(testGeneratedResourcesDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void itShouldCreateNumberedRenderedImageAtLocation() {
        Renderer renderer = new Renderer(TEST_GENERATED_RESOURCES_PATH, "testRender", 0);
        WeightedGraph graph = new WeightedGraph();

        Vertex testVertex = new Vertex("testVertex");
        Vertex anotherVertex = new Vertex("anotherVertex");

        graph.addVertex(testVertex);
        graph.addVertex(anotherVertex);

        graph.addEdge(testVertex, anotherVertex, 2.0);

        renderer.renderPng(graph);
        renderer.renderPng(graph);

        File testRenderFileZero = new File(TEST_GENERATED_RESOURCES_PATH + "testRender0.png");
        File testRenderFileOne = new File(TEST_GENERATED_RESOURCES_PATH + "testRender1.png");

        assertThat(testRenderFileZero).isFile();
        assertThat(testRenderFileOne).isFile();
    }

    @Test
    void itShouldCreateGif() {
        Renderer renderer = new Renderer(TEST_GENERATED_RESOURCES_PATH, "testRender", 200);
        WeightedGraph graph = new WeightedGraph();

        Vertex testVertex = new Vertex("testVertex");
        Vertex anotherVertex = new Vertex("anotherVertex");

        graph.addVertex(testVertex);
        graph.addVertex(anotherVertex);

        graph.addEdge(testVertex, anotherVertex, 2.0);

        renderer.renderPng(graph);
        renderer.renderGif();

        File testRenderFile = new File(TEST_GENERATED_RESOURCES_PATH + "testRender.gif");

        assertThat(testRenderFile).isFile();

    }
}