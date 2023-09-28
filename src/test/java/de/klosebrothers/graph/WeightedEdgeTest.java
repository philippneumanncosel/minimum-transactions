package de.klosebrothers.graph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeightedEdgeTest {
    @Test
    void itShouldCreateWeightedEdge() {
        final Vertex fromVertex = new Vertex("sourceVertex");
        final Vertex toVertex = new Vertex("destinationVertex");
        final double weight = 3.0;

        final WeightedEdge weightedEdge = new WeightedEdge(fromVertex, toVertex, weight);

        assertThat(weightedEdge).isNotNull();
        assertThat(weightedEdge.getSource()).isEqualTo(fromVertex);
        assertThat(weightedEdge.getDestination()).isEqualTo(toVertex);
        assertThat(weightedEdge.getWeight()).isEqualTo(weight);
    }

    @Test
    void itShouldAddAmountToEdgeWeight() {
        final WeightedEdge weightedEdge = new WeightedEdge(null, null, 2.0);

        weightedEdge.addWeight(3.0);

        assertThat(weightedEdge.getWeight()).isEqualTo(5.0);
    }

    @Test
    void itShouldSubtractAmountFromEdgeWeight() {
        final WeightedEdge weightedEdge = new WeightedEdge(null, null, 3.0);

        weightedEdge.subtractWeight(1.0);

        assertThat(weightedEdge.getWeight()).isEqualTo(2.0);
    }
}