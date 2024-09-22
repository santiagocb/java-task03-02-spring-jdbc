package com.tuspring.config;

import com.tuspring.file.FileConfigurator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseSetup {

    private final DataSource dataSource;
    private final FileConfigurator config;

    public DatabaseSetup(DataSource dataSource, FileConfigurator fileConfigurator) {
        this.dataSource = dataSource;
        this.config = fileConfigurator;
    }

    public List<String> createTables() throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(config.getThreads());
        List<Future<Void>> futures = new ArrayList<>();

        var tableNames = new ArrayList<String>();

        for (int i = 0; i < config.getTables(); i++) {
            int tableIndex = i;
            futures.add(executor.submit(() -> {
                var tableName = createAndPopulateTable(tableIndex);
                tableNames.add(tableName);
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            var x = future.get();
        }

        executor.shutdown();
        return tableNames.stream().toList();
    }

    public void showTableData(String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;

        try (Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(query);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Print column names
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println();

                // Print rows
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(resultSet.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }
        }
    }


    public void dropTables(List<String> createdTables) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            createdTables.forEach(t -> {
                String dropSQL = String.format("DROP TABLE IF EXISTS %s", t);

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(dropSQL);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private String createAndPopulateTable(int index) throws SQLException {
        String tableName = "table_" + UUID.randomUUID().toString().replace("-", "_");

        try (Connection conn = dataSource.getConnection()) {
            createTable(conn, tableName, config.getColumns(), config.getColumnTypes());
            populateTable(conn, tableName, config.getColumns(), config.getRowsPerTable().get(index), config.getColumnTypes());
        }
        return tableName;
    }

    private static void createTable(Connection conn, String tableName, int columnCount, List<String> columnTypes) throws SQLException {
        StringBuilder createSQL = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");

        for (int i = 0; i < columnCount; i++) {
            String columnType = columnTypes.get(i % columnTypes.size());
            createSQL.append("col_").append(i).append(" ").append(mapType(columnType));

            if (i < columnCount - 1) {
                createSQL.append(", ");
            }
        }
        createSQL.append(");");

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSQL.toString());
        }
    }

    private static void populateTable(Connection conn, String tableName, int columnCount, int rowCount, List<String> columnTypes) throws SQLException {
        StringBuilder insertSQL = new StringBuilder("INSERT INTO ").append(tableName).append(" VALUES ");

        for (int i = 0; i < rowCount; i++) {
            insertSQL.append("(");
            for (int j = 0; j < columnCount; j++) {
                String columnType = columnTypes.get(j % columnTypes.size());

                if ("String".equals(columnType)) {
                    insertSQL.append("'").append(UUID.randomUUID().toString(), 0, 8).append("'");
                } else if ("Integer".equals(columnType)) {
                    insertSQL.append(new Random().nextInt(100));
                } else if ("Date".equals(columnType)) {
                    insertSQL.append("'2024-01-01'");
                }

                if (j < columnCount - 1) {
                    insertSQL.append(", ");
                }
            }
            insertSQL.append(")");
            if (i < rowCount - 1) {
                insertSQL.append(", ");
            }
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(insertSQL.toString());
        }
    }

    private static String mapType(String columnType) {
        return switch (columnType) {
            case "String" -> "VARCHAR(255)";
            case "Integer" -> "INT";
            case "Date" -> "DATE";
            default -> throw new IllegalArgumentException("Unsupported column type: " + columnType);
        };
    }
}
