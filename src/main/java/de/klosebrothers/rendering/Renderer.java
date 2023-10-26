package de.klosebrothers.rendering;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.squareup.gifencoder.FloydSteinbergDitherer;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import de.klosebrothers.graph.WeightedGraph;
import de.klosebrothers.util.DoubleUtil;
import javax.imageio.ImageIO;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class Renderer {

    private final String renderBasePath;
    private final String fileBaseName;
    private int frameNumber;
    private final Duration frameDuration;

    public Renderer( String renderBasePath, String fileBaseName, int frameRatePerSecond) {
        this.renderBasePath = renderBasePath;
        this.frameNumber = 0;
        this.fileBaseName = fileBaseName;
        this.frameDuration = Duration.ofMillis(1000 / Math.max(frameRatePerSecond, 1));
    }

    public void renderPng(WeightedGraph graph) {
        JGraphXAdapter<String, WeightedEdge> graphAdapter = new JGraphXAdapter<>(convertToJGraphRepresentation(graph));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imageFile = new File(renderBasePath + fileBaseName + frameNumber + ".png");
        try {
            ImageIO.write(image, "PNG", imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frameNumber++;
    }

    public void renderGif() {
        try (FileOutputStream outputStream = new FileOutputStream(renderBasePath + fileBaseName + ".gif")) {
            ImageOptions options = new ImageOptions();

            options.setDelay(frameDuration.toMillis(), TimeUnit.MILLISECONDS);
            options.setDitherer(FloydSteinbergDitherer.INSTANCE);

            File firstFrame = new File(renderBasePath + fileBaseName + 0 + ".png");
            int[][] imageDimensionsFirstFrame = convertImageToArray(firstFrame);
            int height = imageDimensionsFirstFrame.length;
            int width = imageDimensionsFirstFrame[0].length;

            GifEncoder gifEncoder = new GifEncoder(outputStream, width, height, 0);

            try (Stream<Path> filesInFolder = Files.walk(Paths.get(renderBasePath))) {
                filesInFolder
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(Renderer::isPngFile)
                        .sorted(Comparator.comparing(file -> getFileNumber(file.getName())))
                        .forEach(file -> {
                            try {
                                gifEncoder.addImage(convertImageToArray(file), options);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                gifEncoder.finishEncoding();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getFileNumber(String name) {
        return Integer.parseInt(name.replaceAll("[^0-9]",""));
    }

    private static int[][] convertImageToArray(File file) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[][] rgbArray = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                rgbArray[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        return rgbArray;
    }

    private static SimpleDirectedWeightedGraph<String, WeightedEdge> convertToJGraphRepresentation(WeightedGraph inputGraph) {
        SimpleDirectedWeightedGraph<String, WeightedEdge> jGraph = new SimpleDirectedWeightedGraph<>(WeightedEdge.class);
        inputGraph.getVertices().forEach(vertex -> jGraph.addVertex(vertex.getName()));
        inputGraph.getVertices().stream().flatMap(vertex -> vertex.getOutEdges().values().stream())
                .forEach(edge -> {
                    WeightedEdge newEdge = jGraph.addEdge(edge.getSource().getName(), edge.getDestination()
                            .getName());
                    jGraph.setEdgeWeight(newEdge, edge.getWeight());
                });
        return jGraph;
    }

    private static boolean isPngFile(File file) {
        return file.getName().endsWith(".png");
    }

    public static class WeightedEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return String.valueOf(DoubleUtil.roundToTwoPlaces(getWeight()));
        }
    }
}
