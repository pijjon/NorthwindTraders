package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class NorthwindTraders {
    public static Scanner scanner = new Scanner(System.in);

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

        try (
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/northwind",
                        username,
                        password);

        ) {

            while (true) {

                int response = askUserInt("""
                        What do you want to do?
                            1) Display All Products
                            2) Display All Customers
                            3) Display All Categories
                            0) Exit
                        Select an option:
                        
                        """);

                switch (scanner.nextInt()) {
                    case 1:
                        displayAllProducts(connection);
                        break;
                    case 2:
                        displayAllCustomers(connection);
                        break;
                    case 3:
                        displayAllCategories(connection);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("invalid choice");
                }

            }
        } catch (SQLException e) {
            System.out.println("Could not connect to DB");
            System.exit(1);
        }
    }

    public static void displayAllCategories(Connection connection) {
        try (
                PreparedStatement categoryQuery = connection.prepareStatement("""
                        select CategoryID, CategoryName
                        from categories
                        order by CategoryID
                        """)
        ) {

            try (
                    ResultSet categories = categoryQuery.executeQuery();
            ) {

                printResults(categories);


                int response = askUserInt("""
                        Select a category by ID to filter products:
                        
                        """);

                displayProductByCategory(connection, response);
            } catch (SQLException e) {
                System.out.println("Could not query for categories");
            }

        } catch (SQLException e) {
            System.out.println("Could not create prepared statement for querying for categories");
        }
    }

    public static int askUserInt(String question) {
        while (true) {
            System.out.println(question);
            try {
                int response = scanner.nextInt();
                scanner.nextLine();
                return response;
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a number!");
                scanner.nextLine();
            }
        }
    }

    public static void displayProductByCategory(Connection connection, int response) {
        try (
                PreparedStatement preparedStatement = connection.prepareStatement("""
                        select ProductID, ProductName, UnitPrice, UnitsInStock
                        from products
                        where CategoryID = ?
                        order by ProductID;
                        """)
        ) {

            preparedStatement.setString(1, Integer.toString(response));

            try (
                    ResultSet results = preparedStatement.executeQuery();
            ) {

                printResults(results);

            } catch (SQLException e) {
                System.out.println("Could not query products by category");
            }
        } catch (SQLException e) {
            System.out.println("Could not create prepared statement query");
        }
    }

    public static void displayAllCustomers(Connection connection) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                        select ContactName, CompanyName, City, Country, Phone
                        from customers
                        order by Country;
                        """);

                ResultSet results = statement.executeQuery();

        ) {

            printResults(results);

        } catch (SQLException e) {
            System.out.println("Could not get products");
        }
    }

    public static void displayAllProducts(Connection connection) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                        select ProductID, ProductName, UnitPrice, UnitsInStock
                        from products
                        order by ProductID;
                        """);

                ResultSet results = statement.executeQuery();

        ) {

            printResults(results);

        } catch (SQLException e) {
            System.out.println("Could not get products");
        }
    }

    public static void printResults(ResultSet results) throws SQLException {
        // get the meta data so we have access to the field names
        ResultSetMetaData metaData = results.getMetaData();
        // get the number of rows returned
        int columnCount = metaData.getColumnCount();

        // this is looping over all the results from the DB
        while (results.next()) {

            // loop over each column in the row and display the data
            for (int i = 1; i <= columnCount; i++) {
                // gets the current colum name
                String columnName = metaData.getColumnName(i);
                // get the current column value
                String value = results.getString(i);
                // print out the column name and column value
                System.out.println(columnName + ": " + value + " ");
            }

            // print an empty line to make the results prettier
            System.out.println("""
                    
                    --------------------
                    """);

        }
    }
}
