package de.klosebrothers.graph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeightedEdgeTest {
    @Test
    void itShouldCreateWeightedEdge() {
        final Vertex fromVertex = new Vertex("sourceVertex");
        final Vertex toVertex = new Vertex("destinationVertex");
        final double weight = 3;

        final WeightedEdge weightedEdge = new WeightedEdge(fromVertex, toVertex, weight);

        assertThat(weightedEdge).isNotNull();
        assertThat(weightedEdge.getSource()).isEqualTo(fromVertex);
        assertThat(weightedEdge.getDestination()).isEqualTo(toVertex);
        assertThat(weightedEdge.getWeight()).isEqualTo(weight);
    }
}