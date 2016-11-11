package com.prestonparris.paymo.csv;

import com.prestonparris.paymo.models.TrustedStatus;
import com.prestonparris.paymo.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;

import static com.prestonparris.paymo.utils.FileUtil.getFileWriterForPath;
import static com.prestonparris.paymo.utils.FileUtil.writeLineToFile;

public class PaymentWriter {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentWriter.class);
    private final FileWriter fileWriter;

    public PaymentWriter(String filePath) {
        this.fileWriter = getFileWriterForPath(filePath);
    }

    /**
     * Used to write the trusted status
     * of each payment to a file
     *
     * @param isTrusted
     */
    public void writeTrustedStatus(boolean isTrusted) {
        final String isTrustedStatus = TrustedStatus.get(isTrusted);
        writeLineToFile(fileWriter, isTrustedStatus);
    }

    public void close() {
        FileUtil.closeWriter(fileWriter);
    }

}
