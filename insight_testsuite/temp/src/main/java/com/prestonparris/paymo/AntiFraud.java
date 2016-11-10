package com.prestonparris.paymo;

import com.opencsv.CSVReader;
import com.prestonparris.paymo.csv.PaymentReader;
import com.prestonparris.paymo.csv.PaymentWriter;
import com.prestonparris.paymo.models.Payment;
import com.prestonparris.paymo.models.TrustedStatus;
import com.prestonparris.paymo.utils.CsvUtil;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.BidirectionalDijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 *  Runs fraud detection features on a payment graph
 */
public class AntiFraud {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraud.class);

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting PayMo AntiFraud Detection {}", startTime);

        // Validate the input
        if (args.length < 5) {
            LOGGER.error("Need all input and output paths in order to start.");
            System.exit(-1);
        }

        // Batch input
        final String batchCsvPath = args[0];

        // Stream input
        final String streamCsvPath = args[1];

        // Feature outputs
        final String featureOneOutputPath = args[2];
        final String featureTwoOutputPath = args[3];
        final String featureThreeOutputPath = args[4];

        // Initialize the graph
        PaymentGraph paymentGraph = new PaymentGraph();

        Consumer<Payment> userMakingPaymentCreator = (payment) -> paymentGraph.createUser(payment.getId1());
        Consumer<Payment> userReceivingPaymentCreator = (payment) -> paymentGraph.createUser(payment.getId2());

        Consumer<Payment> paymentRecorder = (payment) ->
                paymentGraph.recordPayment(payment.getId1(), payment.getId2());

        // Setup the initial graph state from the batch input file
        List<Consumer<Payment>> batchPaymentConsumers =
                Arrays.asList(userMakingPaymentCreator, userReceivingPaymentCreator, paymentRecorder);

        PaymentReader batchPaymentReader = new PaymentReader(batchCsvPath)
                .read(batchPaymentConsumers);

        // Start reading the stream of inputs and applying features
        PaymentWriter featureOnePaymentWriter = new PaymentWriter(featureOneOutputPath);

        Consumer<Payment> featureOne = (payment) -> {
            boolean hasMadeAPaymentToThisUserBefore =
                    paymentGraph.hasPathWithinDegree(payment.getId1(), payment.getId2(), 1);

            featureOnePaymentWriter.writeTrustedStatus(hasMadeAPaymentToThisUserBefore);
        };

        PaymentWriter featureTwoPaymentWriter = new PaymentWriter(featureTwoOutputPath);

        Consumer<Payment> featureTwo = (payment) -> {
            boolean isFriendOfAFriend =
                    paymentGraph.hasPathWithinDegree(payment.getId1(), payment.getId2(), 2);

            featureTwoPaymentWriter.writeTrustedStatus(isFriendOfAFriend);
        };

        PaymentWriter featureThreePaymentWriter = new PaymentWriter(featureThreeOutputPath);

        Consumer<Payment> featureThree = (payment) -> {
            boolean isWithinFourthDegreeFriendsNetwork =
                    paymentGraph.hasPathWithinDegree(payment.getId1(), payment.getId2(), 4);

            featureThreePaymentWriter.writeTrustedStatus(isWithinFourthDegreeFriendsNetwork);
        };

        List<Consumer<Payment>> streamPaymentConsumers = Arrays.asList(userMakingPaymentCreator,
            userReceivingPaymentCreator, featureOne, featureTwo, featureThree, paymentRecorder);

        PaymentReader streamPaymentReader = new PaymentReader(streamCsvPath)
                .read(streamPaymentConsumers);

        featureOnePaymentWriter.close();
        featureTwoPaymentWriter.close();
        featureThreePaymentWriter.close();

        Instant endTime = Instant.now();

        LOGGER.info("Shutting down AntiFraud Detection took:{} to complete.", Duration.between(startTime, endTime));
        System.exit(0);
    }
}
