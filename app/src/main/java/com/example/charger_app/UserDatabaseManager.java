package com.example.charger_app;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDatabaseManager {

    private static final String TAG = "UserDatabaseManager";
    private static final String URL = "jdbc:mysql://10.3.0.96:3306/charger_app";
    private static final String USER = "admin";
    private static final String PASSWORD = "voleibol9A";
    private final ExecutorService executorService;

    public UserDatabaseManager(Context context) {
        executorService = Executors.newFixedThreadPool(2);
    }

    public void checkUser(String name, String password, CheckUserCallback callback) {
        executorService.execute(() -> {
            Connection connection = null;
            try {
                Log.e(TAG, "Connecting to database for user check...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Log.e(TAG, "Connected to database");

                String query = "SELECT id, name, email, password FROM users WHERE name = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, password);
                Log.e(TAG, "Executing query: " + statement.toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String userName = resultSet.getString("name");
                    String userEmail = resultSet.getString("email");
                    String userPassword = resultSet.getString("password");
                    Log.e(TAG, "User found: " + userName + ", " + userEmail);
                    resultSet.close();
                    statement.close();
                    callback.onCheckUserCompleted(new User(id, userName, userEmail, userPassword));
                } else {
                    Log.e(TAG, "User not found with provided credentials");
                    callback.onCheckUserCompleted(null);
                }

                resultSet.close();
                statement.close();
            } catch (Exception e) {
                Log.e(TAG, "Error connecting to database: " + e.getMessage(), e);
                callback.onCheckUserCompleted(null);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing connection: " + e.getMessage(), e);
                    }
                }
            }
        });
    }

    public void registerUser(String email, String name, String password, RegisterUserCallback callback) {
        executorService.execute(() -> {
            Connection connection = null;
            try {
                Log.e(TAG, "Connecting to database for user registration...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                String query = "INSERT INTO users (email, name, password) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1, email);
                statement.setString(2, name);
                statement.setString(3, password);
                Log.e(TAG, "Executing query: " + statement.toString());
                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        generatedKeys.close();
                        statement.close();
                        Log.e(TAG, "User registered with ID: " + id);
                        callback.onRegisterUserCompleted(new User(id, name, email, password));
                    } else {
                        Log.e(TAG, "Failed to obtain generated key");
                        callback.onRegisterUserCompleted(null);
                    }
                } else {
                    Log.e(TAG, "No rows affected");
                    callback.onRegisterUserCompleted(null);
                }

                statement.close();
            } catch (Exception e) {
                Log.e(TAG, "Error registering user: " + e.getMessage(), e);
                callback.onRegisterUserCompleted(null);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                        Log.e(TAG, "Database connection closed");
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing connection: " + e.getMessage(), e);
                    }
                }
            }
        });
    }

    public void getUserDetails(int userId, GetUserDetailsCallback callback) {
        executorService.execute(() -> {
            Connection connection = null;
            try {
                Log.e(TAG, "Connecting to database for getting user details...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                String query = "SELECT id, name, email, password FROM users WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                Log.e(TAG, "Executing query: " + statement.toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Log.e(TAG, "User details retrieved for ID: " + id);
                    resultSet.close();
                    statement.close();
                    callback.onGetUserDetailsCompleted(new User(id, name, email, password));
                } else {
                    Log.e(TAG, "User not found for ID: " + userId);
                    callback.onGetUserDetailsCompleted(null);
                }

                resultSet.close();
                statement.close();
            } catch (Exception e) {
                Log.e(TAG, "Error getting user details: " + e.getMessage(), e);
                callback.onGetUserDetailsCompleted(null);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing connection: " + e.getMessage(), e);
                    }
                }
            }
        });
    }

    public interface CheckUserCallback {
        void onCheckUserCompleted(User user);
    }

    public interface RegisterUserCallback {
        void onRegisterUserCompleted(User user);
    }

    public interface GetUserDetailsCallback {
        void onGetUserDetailsCompleted(User user);
    }
}
