package com.tuspring.file;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileConfigurator {

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int numberOfTables;
    private int numberOfColumns;
    private List<Integer> rowsPerTable;
    private List<String> columnTypes;
    private int numberOfThreads;

    public FileConfigurator(String configFilePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(configFilePath));

        this.dbUrl = properties.getProperty("db.url");
        this.dbUser = properties.getProperty("db.user");
        this.dbPassword = properties.getProperty("db.password");
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

    // Getters
    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
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
