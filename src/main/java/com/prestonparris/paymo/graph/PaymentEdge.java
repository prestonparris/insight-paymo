package com.prestonparris.paymo.graph;

import org.jgrapht.graph.DefaultEdge;

public class PaymentEdge<V> extends DefaultEdge {

    private V v1;
    private V v2;
    private String label = "payment";

    public PaymentEdge(V v1, V v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V getV1() {
        return v1;
    }

    public PaymentEdge setV1(V v1) {
        this.v1 = v1;
        return this;
    }

    public V getV2() {
        return v2;
    }

    public PaymentEdge setV2(V v2) {
        this.v2 = v2;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public PaymentEdge setLabel(String label) {
        this.label = label;
        return this;
    }
}
