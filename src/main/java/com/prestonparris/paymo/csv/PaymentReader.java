package com.prestonparris.paymo.csv;

import com.prestonparris.paymo.models.Payment;
import com.prestonparris.paymo.utils.CsvUtil;
import com.prestonparris.paymo.utils.FileUtil;
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

    public PaymentReader read(List<Consumer<Payment>> paymentConsumers) {
        final FileReader fileReader = getFileReaderForPath(filePath);

        // Pass every Payment to each consumer
        readCsvFile(fileReader, (line) ->
                paymentConsumers.iterator().forEachRemaining(c -> c.accept(parseLine(line))));

        closeReader(fileReader);

        return this;
    }

    private Payment parseLine(String[] line) {
        final int id1 = Integer.parseInt(StringUtils.trim(line[1]));
        final int id2 = Integer.parseInt(StringUtils.trim(line[2]));

        return new Payment()
                .setId1(id1)
                .setId2(id2);
    }

}
