package com.prestonparris.paymo;

import com.prestonparris.paymo.csv.PaymentReader;
import com.prestonparris.paymo.features.FeatureBuilder;
import com.prestonparris.paymo.models.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

/**
 *  Runs fraud detection features on a payment graph
 */
public class AntiFraud {
    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraud.class);

    public static void main(String[] args) {
        Instant startTime = Instant.now();
        LOGGER.info("Starting PayMo AntiFraud Detection");

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

        // Setup the initial graph state from the batch input file
        FeatureBuilder batchFeatureBuilder = new FeatureBuilder(paymentGraph)
                .addUserMakingPayment()
                .addUserReceivingPayment()
                .addPaymentRecorder();

        List<Consumer<Payment>> batchFeatures = batchFeatureBuilder.build();

        PaymentReader batchPaymentReader = new PaymentReader(batchCsvPath)
                .read(batchFeatures);

        batchFeatureBuilder.closePaymentWriters();

        Instant batchEndTime = Instant.now();

        LOGGER.info("Finished loading batch input file. This took: {} to complete.",
                Duration.between(startTime, batchEndTime));

        // Start reading the stream of inputs and applying features
        FeatureBuilder streamFeatureBuilder = new FeatureBuilder(paymentGraph)
                .addUserMakingPayment()
                .addUserReceivingPayment()
                .addIsWithinDegreeFeature(featureOneOutputPath, 1)
                .addIsWithinDegreeFeature(featureTwoOutputPath, 2)
                .addIsWithinDegreeFeature(featureThreeOutputPath, 4)
                .addPaymentRecorder();

        List<Consumer<Payment>> streamFeatures = streamFeatureBuilder.build();

        PaymentReader streamPaymentReader = new PaymentReader(streamCsvPath)
                .read(streamFeatures);

        streamFeatureBuilder.closePaymentWriters();

        Instant endTime = Instant.now();

        LOGGER.info("Shutting down AntiFraud Detection. This took: {} to complete.",
                Duration.between(startTime, endTime));

        System.exit(0);
    }
}
