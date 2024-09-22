package com.tuspring.file;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileConfigurator {

    private final int numberOfTables;
    private final int numberOfColumns;
    private final List<Integer> rowsPerTable;
    private final List<String> columnTypes;
    private final int numberOfThreads;

    public FileConfigurator(String configFilePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(configFilePath));

        this.numberOfTables = Integer.parseInt(properties.getProperty("tables"));
        this.numberOfColumns = Integer.parseInt(properties.getProperty("columns"));
        this.numberOfThreads = Integer.parseInt(properties.getProperty("threads"));

        this.rowsPerTable = parseRows(properties.getProperty("rowsPerTable"));

        this.columnTypes = Arrays.asList(properties.getProperty("columnTypes").split(","));
    }

    private List<Integer> parseRows(String rows) {
        String[] values = rows.split(",");
        List<Integer> result = new ArrayList<>();
        for (String val : values) {
            result.add(Integer.parseInt(val));
        }
        return result;
    }

    public int getTables() {
        return numberOfTables;
    }

    public int getColumns() {
        return numberOfColumns;
    }

    public List<Integer> getRowsPerTable() {
        return rowsPerTable;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public int getThreads() {
        return numberOfThreads;
    }
}
