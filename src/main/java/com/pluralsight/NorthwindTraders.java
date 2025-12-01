package com.pluralsight;

import javax.sql.*;

import java.sql.*;

public class NorthwindTraders {
    public static void main(String[] args) {
        if (args.length != 2) {
            // display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            // exit the app due to failure because we dont have a username and password from the command line
            System.exit(1);
        }

        // get the username and password from args[]
        String username = args[0];
        String password = args[1];

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/northwind",
                username,
                password)) {

            displayAllProducts(connection);
        } catch (SQLException e) {
            System.out.println("Could not connect to DB");
            System.exit(1);
        }
    }

    private static void displayAllProducts(Connection connection) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                        select ProductID, ProductName
                        from products
                        order by ProductID;
                        """);

                ResultSet results = statement.executeQuery();

                ) {
            
            printResults(results);

        }
        catch (SQLException e) {
            System.out.println("Could not get products");
        }
    }

    private static void printResults(ResultSet results) throws SQLException {
        //get the meta data so we have access to the field names
        ResultSetMetaData metaData = results.getMetaData();
        //get the number of rows returned
        int columnCount = metaData.getColumnCount();

        //this is looping over all the results from the DB
        while(results.next()){

            //loop over each column in the rown and display the data
            for (int i = 1; i <= columnCount; i++) {
                //gets the current colum name
                String columnName = metaData.getColumnName(i);
                //get the current column value
                String value = results.getString(i);
                //print out the column name and column value
                System.out.println(columnName + ": " + value + " ");
            }

            //print an empty line to make the results prettier
            System.out.println();

        }
    }
}
