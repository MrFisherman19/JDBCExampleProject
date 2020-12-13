package Jdbc;

import java.io.Serializable;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JdbcUtils {

    /**
     * Method to create update query in SQL. There is LinkedHashMap instead of HashMap because
     * entry order should be kept in method.
     * @param tableName it have to be the name of the column to change something on
     * @param valuesToChange Map<String, Serializable> of values that needs to be updated
     *                       For example, to add field to update, should invoke:
     *                       valuesToChange.put("first_name", "Fisherman")
     * @param conditions LinkedHashMap<String, {@link LogicalOperation}> of conditions under which records
     *                   are searched for in the database
     *                   For example, to add condition to check, should invoke:
     *                   conditions.put("id = 23", LogicalOperation.WITHOUT_OPERATION);
     *                   LogicalOperations that can be used:
     *                   OR - logical disjunction
     *                   AND - logical conjunction
     *                   WITHOUT_OPERATION - no logical operation after condition,
     *                   should be applied to last element in map.
     * @return SQL updating query
     */
    public static String createUpdateQuery(String tableName, Map<String, Serializable> valuesToChange,
                                           LinkedHashMap<String, LogicalOperation> conditions) {
        StringBuilder sb = new StringBuilder("UPDATE " + tableName + " SET ");
        for (String fieldToChange : valuesToChange.keySet()) {
            sb.append(fieldToChange).append("=");
            var value = valuesToChange.get(fieldToChange);

            if (value instanceof String) {
                sb.append(String.format("'%s'", value));
            } else {
                sb.append(value);
            }
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");

        addWhereClause(conditions, sb);

        return sb.toString();
    }

    public static String createDeleteQuery(String tableName, LinkedHashMap<String, LogicalOperation> conditions) {
        StringBuilder sb = new StringBuilder("DELETE FROM " + tableName);

        addWhereClause(conditions, sb);

        return sb.toString();
    }

    /**
     * Method for receiving metadata for given jdbc connection
     * @param connection current Jdbc connection for which metadata should be fetched
     * @return DatabaseMetaData object
     */
    public static DatabaseMetaData getDatabaseMetaDataForConnection(Connection connection){
        DatabaseMetaData metaData = null;
        try {
            metaData = connection.getMetaData();
        } catch (SQLException e) {
            System.out.println("Cannot read database metadata!");
            e.printStackTrace();
        }
        return metaData;
    }

    public static Set<String> getTablesNamesForConnection(Connection connection, String catalog, String schemaPattern,
                                                   String tableNamePattern, String[] types) {
        DatabaseMetaData metaData = getDatabaseMetaDataForConnection(connection);

        Set<String> tablesNames = new HashSet<>();
        try(ResultSet resultSet = metaData.getTables(catalog, schemaPattern, tableNamePattern, types)) {

            while (resultSet.next()) {
                tablesNames.add(resultSet.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            System.out.println("Cannot read tables for given connection!");
            e.printStackTrace();
        }

        return tablesNames;
    }

    public static Set<String> getColumnsNamesForTableInConnection(String tableName, Connection connection, String catalog,
                                                                  String schemaPattern, String columnNamePattern) {
        DatabaseMetaData metaData = getDatabaseMetaDataForConnection(connection);

        Set<String> columnsNames = new HashSet<>();
        try(ResultSet resultSet = metaData.getColumns(catalog, schemaPattern, tableName, columnNamePattern)) {

            while (resultSet.next()) {
                columnsNames.add(resultSet.getString("COLUMN_NAME"));
            }

        } catch (SQLException e) {
            System.out.println("Cannot read columns for given connection!");
            e.printStackTrace();
        }

        return columnsNames;
    }

    /**
     * This method is used for reading metadata from result set, query does not affect data base
     * @param connection current connection
     * @param query query which should return resultSet
     * @return ResultSetMetaData for given query
     */
    public static ResultSetMetaData getResultSetMetaDataForQuery(Connection connection, String query) {
        ResultSetMetaData resultSetMetaData = null;
        try {
            boolean autoCommit = connection.getAutoCommit();

            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            resultSetMetaData = resultSet.getMetaData();

            connection.rollback();

            connection.setAutoCommit(autoCommit);

            close(resultSet, statement);
        } catch (SQLException e) {
            System.out.println("Cannot get result set metadata for given query!");
        }
        return resultSetMetaData;
    }

    /**
     * Generic method to close AutoCloseable elements.
     * @param closeables should implement AutoCloseable interface, it may be a ResultSet, Statement, Connection etc.
     * @param <T> generic type bounding by AutoCloseable.
     */
    @SafeVarargs
    public static <T extends AutoCloseable> void close(T... closeables) {
        try {
            for (T toClose : closeables) {
                if (toClose != null) {
                    toClose.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addWhereClause(LinkedHashMap<String, LogicalOperation> conditions, StringBuilder sb) {
        sb.append(" WHERE ");
        for (String condition : conditions.keySet()) {
            sb.append(condition).append(" ");
            if (!conditions.get(condition).equals(LogicalOperation.WITHOUT_OPERATION)) {
                sb.append(conditions.get(condition).name()).append(" ");
            }
        }
    }
}
