package com.prestonparris.paymo.features;

import com.prestonparris.paymo.PaymentGraph;
import com.prestonparris.paymo.csv.PaymentWriter;
import com.prestonparris.paymo.models.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Used to build up a list of
 * consumers to be applied to each transaction
 *
 */
public class FeatureBuilder {
    private PaymentGraph paymentGraph;
    private List<Consumer<Payment>> paymentConsumers = new ArrayList<>();
    private List<PaymentWriter> paymentWriters = new ArrayList<>();

    public FeatureBuilder(PaymentGraph paymentGraph) {
        this.paymentGraph = paymentGraph;
    }

    public FeatureBuilder addUserMakingPayment() {
        Consumer<Payment> userMakingPaymentCreator =
                (payment) -> paymentGraph.createUser(payment.getId1());
        paymentConsumers.add(userMakingPaymentCreator);
        return this;
    }

    public FeatureBuilder addUserReceivingPayment() {
        Consumer<Payment> userReceivingPaymentCreator =
                (payment) -> paymentGraph.createUser(payment.getId2());
        paymentConsumers.add(userReceivingPaymentCreator);
        return this;
    }

    public FeatureBuilder addPaymentRecorder() {
        Consumer<Payment> paymentRecorder =
                (payment) -> paymentGraph.recordPayment(payment.getId1(), payment.getId2());
        paymentConsumers.add(paymentRecorder);
        return this;
    }

    public FeatureBuilder addIsWithinDegreeFeature(String outputPath, int maxDegree) {
        PaymentWriter featureOutputWriter = new PaymentWriter(outputPath);
        paymentWriters.add(featureOutputWriter);

        Consumer<Payment> feature = (payment) -> {
            boolean isWithinDegree =
                    paymentGraph.hasPathWithinDegree(payment.getId1(), payment.getId2(), maxDegree);
            featureOutputWriter.writeTrustedStatus(isWithinDegree);
        };

        paymentConsumers.add(feature);

        return this;
    }

    public List<Consumer<Payment>> build() {
        return paymentConsumers;
    }

    public void closePaymentWriters() {
        paymentWriters.forEach(PaymentWriter::close);
    }
}
