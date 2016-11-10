package com.prestonparris.paymo.utils;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class CsvUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtil.class);

    public static void readCsvFile(FileReader fileReader, Consumer<String[]> callback)  {
        final BufferedReader br = new BufferedReader(fileReader);
        final CSVReader reader = new CSVReader(br);

        try {
            // Skip the headers
            String [] nextLine = reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                callback.accept(nextLine);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read csv {}", e);
            System.exit(-1);
        }
    }

}
