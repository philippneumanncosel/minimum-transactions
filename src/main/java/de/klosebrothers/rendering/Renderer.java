package de.klosebrothers.rendering;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import de.klosebrothers.graph.WeightedGraph;
import javax.imageio.ImageIO;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class Renderer {

    private Renderer() {
    }

    public static void renderPng(WeightedGraph graph, String fileName) throws IOException {
        JGraphXAdapter<String, WeightedEdge> graphAdapter =
                new JGraphXAdapter<>(convertToJGraphRepresentation(graph));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/generated/resources/" + fileName + ".png");
        ImageIO.write(image, "PNG", imgFile);
    }

    private static SimpleDirectedWeightedGraph<String, WeightedEdge> convertToJGraphRepresentation(WeightedGraph inputGraph) {
        SimpleDirectedWeightedGraph<String, WeightedEdge> jGraph = new SimpleDirectedWeightedGraph<>(WeightedEdge.class);
        inputGraph.getVertices().forEach(vertex -> jGraph.addVertex(vertex.getName()));
        inputGraph.getVertices().stream().flatMap(vertex -> vertex.getOutEdges().values().stream()).forEach(edge -> {
            WeightedEdge newEdge = jGraph.addEdge(edge.getSource()
                    .getName(), edge.getDestination().getName());
            jGraph.setEdgeWeight(newEdge, edge.getWeight());
        });
        return jGraph;
    }

    public static class WeightedEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return String.valueOf(getWeight());
        }

    }
}
