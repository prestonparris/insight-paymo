package com.prestonparris.paymo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Utility helper class for reading and writing to files
 *
 */
public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static void writeLineToFile(FileWriter fileWriter, String stringToWrite) {
        try {
            fileWriter.write(stringToWrite);
            fileWriter.write('\n');
        } catch (IOException e) {
            LOGGER.debug("Error writing line to file: {}", e);
            System.exit(-1);
        }
    }

    public static void closeWriter(FileWriter fileWriter) {
        try {
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.debug("Error closing file writer: {}", e);
            System.exit(-1);
        }
    }

    public static void closeReader(FileReader fileReader) {
        try {
            fileReader.close();
        } catch (IOException e) {
            LOGGER.debug("Error closing file reader: {}", e);
            System.exit(-1);
        }
    }

    public static FileReader getFileReaderForPath(String filePath) {
        try {
            return new FileReader(filePath);
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file at path {}", filePath, e);
            System.exit(-1);
            return null;
        }
    }

    public static FileWriter getFileWriterForPath(String filePath) {
        try {
            return new FileWriter(filePath);
        } catch (IOException e) {
            LOGGER.error("Could not open file writer to path {}", filePath, e);
            System.exit(-1);
            return null;
        }
    }
}
