package com.example;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCExecutor {

    public static void main(String[] args) {
        DatabaseConnectionManager dcm =
                new DatabaseConnectionManager(
                        "localhost",
                        "hplussport",
                        "postgres",
                        "0000");
        try {
            Connection connection = dcm.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            customerDAO.findAllSorted(20).forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
