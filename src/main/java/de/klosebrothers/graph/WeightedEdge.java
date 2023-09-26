package de.klosebrothers.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class WeightedEdge {
    private Vertex source;
    private Vertex destination;
    private double weight;

    public void addWeight(double weightToAdd) {
        weight += weightToAdd;
    }
}
