package com.prestonparris.paymo.csv;

import com.prestonparris.paymo.models.Payment;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.util.List;
import java.util.function.Consumer;

import static com.prestonparris.paymo.utils.CsvUtil.readCsvFile;
import static com.prestonparris.paymo.utils.FileUtil.closeReader;
import static com.prestonparris.paymo.utils.FileUtil.getFileReaderForPath;

public class PaymentReader {
    private final String filePath;

    public PaymentReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Takes a list of payment consumers,
     * reads in a csv file and applies each row to every consumer
     *
     * @param paymentConsumers A list of payment consumers
     * @return
     */
    public PaymentReader read(List<Consumer<Payment>> paymentConsumers) {
        final FileReader fileReader = getFileReaderForPath(filePath);

        // Pass every Payment to each consumer
        readCsvFile(fileReader, (line) -> {
            Payment payment = parseLine(line);
            paymentConsumers.iterator().forEachRemaining(c -> c.accept(payment));
        });

        closeReader(fileReader);

        return this;
    }

    /**
     * Parse a csv line into a Payment
     * (for now only concerned with the user ids)
     *
     * @param line
     * @return Payment
     */
    private Payment parseLine(String[] line) {
        final int id1 = Integer.parseInt(StringUtils.trim(line[0]));
        final int id2 = Integer.parseInt(StringUtils.trim(line[1]));

        return new Payment()
                .setId1(id1)
                .setId2(id2);
    }

}
