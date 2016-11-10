package com.prestonparris.paymo;

import org.jgrapht.alg.BidirectionalDijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PaymentGraph: a graph representation of payment history
 */
public class PaymentGraph {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentGraph.class);

    final SimpleGraph<Integer, DefaultEdge> graph;

    public PaymentGraph() {
        graph = new SimpleGraph<>(DefaultEdge.class);
    }

    /**
     * Creates a user node
     *
     * @param id The id of the user
     * @return int The id of the user
     */
    public int createUser(int id) {
        if (!graph.containsVertex(id)) {
            graph.addVertex(id);
        }

        return id;
    }

    /**
     * Record a transaction between two users
     *
     * @param userMakingPayment    the user making the payment
     * @param userReceivingPayment the user receiving the payment
     * @return boolean represents if the payment was successful
     */
    public boolean recordPayment(int userMakingPayment, int userReceivingPayment) {
        if (userMakingPayment == userReceivingPayment) {
            return false;
        }

        final DefaultEdge edge = new DefaultEdge();
        return graph.addEdge(userMakingPayment, userReceivingPayment, edge);
    }

    /**
     * Detects if there exists a path within the provided users
     *
     * @param userMakingPayment    the user making the payment
     * @param userReceivingPayment the user receiving the payment
     * @param maxDegree            the maximum degree of separation allowed
     * @return boolean represents if there is a path between these users within the maxDegree
     */
    public boolean hasPathWithinDegree(int userMakingPayment, int userReceivingPayment, int maxDegree) {
        // Start a bidirectional search from both vertexes.
        final BidirectionalDijkstraShortestPath<Integer, DefaultEdge> path =
                new BidirectionalDijkstraShortestPath<Integer, DefaultEdge>
                        (graph, userMakingPayment, userReceivingPayment, maxDegree);

        // If we do not have a pathEdgeList then it does
        // not exist or falls outside of the maxDegrees
        return (path.getPathEdgeList() != null);
    }

}
