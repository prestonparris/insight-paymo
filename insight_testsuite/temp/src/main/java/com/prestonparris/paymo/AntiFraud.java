package com.prestonparris.paymo;

import com.prestonparris.paymo.models.Payment;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.BidirectionalDijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

public class AntiFraud {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraud.class);
    private static int batchCount = 0;
    private static int streamCount = 0;

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting AntiFraud Detection");

        if (args.length < 5) {
            LOGGER.error("Need all input and output paths in order to start.");
            System.exit(-1);
        }

        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // Setup the initial graph state from the batch input file
        setupInitialGraphStateFromBatch(graph, args[0]);

        // Start reading the stream of inputs and applying features
        readInputStreamPaymentsAndApplyFeatures(graph, args[1], args[2], args[3], args[4]);

        Instant endTime = Instant.now();

        LOGGER.info("Shutting down AntiFraud Detection took:{} to complete.", Duration.between(startTime, endTime));
        System.exit(0);
    }

    private static void setupInitialGraphStateFromBatch(SimpleGraph<Integer, DefaultEdge> graph,
                                                        String batchFilePath) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting load of initial batch graph state");
        FileReader batchFileReader = getFileReaderForPath(batchFilePath);

        readCsvFileAsPayments(batchFileReader, payment -> {
            // The user making the payment
            int userMakingPayment = getOrCreateUser(graph, payment.getId1());

            // The user receiving the payment
            int userReceivingPayment = getOrCreateUser(graph, payment.getId2());

            LOGGER.info("creating user {} from initial setup with idA= {} idB= {}",
                    batchCount++, payment.getId1(), payment.getId2());

            // Record the transaction
            recordPayment(graph, userMakingPayment, userReceivingPayment);
        });

        Instant endTime = Instant.now();

        LOGGER.info("Finished load of initial batch graph state took:{} to complete.",
                Duration.between(startTime, endTime));
    }

    private static void readInputStreamPaymentsAndApplyFeatures(SimpleGraph<Integer, DefaultEdge> graph,
                                                                String streamFilePath,
                                                                String featureOneOutputPath,
                                                                String featureTwoOutputPath,
                                                                String featureThreeOutputPath) {
        FileReader streamFileReader = getFileReaderForPath(streamFilePath);

        FileWriter featureOneCsvWriter = getFileWriterForPath(featureOneOutputPath);
        FileWriter featureTwoCsvWriter = getFileWriterForPath(featureTwoOutputPath);
        FileWriter featureThreeCsvWriter = getFileWriterForPath(featureThreeOutputPath);

        readCsvFileAsPayments(streamFileReader, (payment) -> {
            // The user making the payment
            int userMakingPayment =
                    getOrCreateUser(graph, payment.getId1());

            LOGGER.info("creating user {} from feature application with idA= {} idB= {}",
                    streamCount++, payment.getId1(), payment.getId2());

            // The user receiving the payment
            int userReceivingPayment =
                    getOrCreateUser(graph, payment.getId2());

            // Feature 1
            boolean hasMadeAPaymentToThisUserBefore =
                    hasPathWithinDegree(graph, userMakingPayment, userReceivingPayment, 1);

            writeTrustedStatus(featureOneCsvWriter, hasMadeAPaymentToThisUserBefore);

            // Feature 2
            boolean isFriendOfAFriend =
                    hasPathWithinDegree(graph, userMakingPayment, userReceivingPayment, 2);

            writeTrustedStatus(featureTwoCsvWriter, isFriendOfAFriend);

            // Feature 3
            boolean isWithinFourthDegreeFriendsNetwork =
                    hasPathWithinDegree(graph, userMakingPayment, userReceivingPayment, 4);

            writeTrustedStatus(featureThreeCsvWriter, isWithinFourthDegreeFriendsNetwork);

            // Always record the transaction regardless of being 'unverified'
            recordPayment(graph, userMakingPayment, userReceivingPayment);
        });

        try {
            if (featureOneCsvWriter != null) {
                featureOneCsvWriter.close();
            }
            if (featureTwoCsvWriter != null) {
                featureTwoCsvWriter.close();
            }
            if (featureThreeCsvWriter != null) {
                featureThreeCsvWriter.close();
            }
        } catch (IOException e) {
            LOGGER.error("Encountered problem closing a csv writer {}", e);
            System.exit(-1);
        }
    }

    private static int getOrCreateUser(SimpleGraph<Integer, DefaultEdge> graph, int id) {
        if (!graph.containsVertex(id)) {
            graph.addVertex(id);
        }

        return id;
    }

    private static void recordPayment(SimpleGraph<Integer, DefaultEdge> graph,
                                      int userMakingPayment, int userReceivingPayment) {
        if (userMakingPayment != userReceivingPayment) {
            DefaultEdge edge = new DefaultEdge();
            graph.addEdge(userMakingPayment, userReceivingPayment, edge);
        }
    }

    private static boolean hasPathWithinDegree(SimpleGraph<Integer, DefaultEdge> graph, int userMakingPayment,
                                               int userReceivingPayment, int maxDegree) {
        // Start a bidirectional search from both vertexes.
        BidirectionalDijkstraShortestPath<Integer, DefaultEdge> path =
                new BidirectionalDijkstraShortestPath(graph, userMakingPayment, userReceivingPayment, maxDegree);

        // If we do not have a pathEdgeList then it does not exist or falls outside of the maxDegrees
        return (path.getPathEdgeList() != null);

    }

    private static void readCsvFileAsPayments(FileReader fileReader, Consumer<Payment> callback)  {
        BufferedReader br = new BufferedReader(fileReader);

        try {
            // Skip the headers
            String line = br.readLine();

            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String[] fields = line.split(",");

                if (fields.length >= 5) {
                    Payment payment = new Payment();
                    payment.setId1(Integer.parseInt(StringUtils.trim(fields[1])));
                    payment.setId2(Integer.parseInt(StringUtils.trim(fields[2])));
                    callback.accept(payment);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not read csv {}", e);
            System.exit(-1);
        }
    }

    private static String getTrustedStatus (boolean isTrusted) {
        return (isTrusted) ? "trusted" : "untrusted";
    }

    private static void writeTrustedStatus(FileWriter fileWriter, boolean isTrusted) {
        try {
            fileWriter.write(getTrustedStatus(isTrusted));
            fileWriter.write("\n");
        } catch (IOException e) {
            LOGGER.debug("Error writing trusted status: {}", e);
            System.exit(-1);
        }
    }

    private static FileReader getFileReaderForPath(String filePath) {
        try {
            return new FileReader(filePath);
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file at path {}", filePath, e);
            System.exit(-1);
            return null;
        }
    }

    private static FileWriter getFileWriterForPath(String filePath) {
        try {
            return new FileWriter(filePath);
        } catch (IOException e) {
            LOGGER.error("Could not open file writer to path {}", filePath, e);
            System.exit(-1);
            return null;
        }
    }
}
