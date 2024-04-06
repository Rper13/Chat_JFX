package JDBC;

import Objects.User;

import java.sql.*;

public class MySQLservice {

    private static final String CONN_STRING = "jdbc:mysql://localhost:3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static MySQLservice instance = null;

    private Connection connection;

    private MySQLservice(){
        try {
            connection = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        }catch (SQLException e){
            System.err.println("Error during establishing connection to SQL server");
        }

    }

    public static synchronized MySQLservice getInstance(){
        if(instance == null){
            instance = new MySQLservice();
        }
        return instance;
    }

    public void closeConn() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                instance = null;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public String AuthRequest(String username, String password){

        try {

            PreparedStatement statement = connection.prepareStatement("" +
                    "SELECT id, username, password " +
                    "FROM chat.users " +
                    "WHERE username = ?");

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                if(resultSet.getString("password").equals(password)){
                    return "Success";
                }
                else{
                    return "Wrong Password";
                }
            }
            else{
                return "Wrong Username";
            }

        }catch(SQLException e){
            System.err.println("Error while retrieving login info");
            return "Authentication error";
        }
    }

    public boolean RegisterRequest(String name, String last_name, String username, String password, String phone_number){

        int inserted = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO chat.users (name,last_name,username,password,phone_number) " +
                            "VALUES (?, ? , ? , ? , ?)");
            statement.setString(1, name);
            statement.setString(2, last_name);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.setString(5, phone_number);

            inserted = statement.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return inserted > 0;
    }

    public User retrieveUser(String username){

        User user = new User();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, name, last_name, username, password, phone_number FROM chat.users WHERE username = ?");

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setLast_name(resultSet.getString("last_name"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone_number"));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return user;
    }

    public byte[] retrieveProfilePicture(int id){
        byte[] profilePic = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT picture FROM chat.prof_pics WHERE userId = ? ORDER BY pictureID desc");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                profilePic = resultSet.getBytes(1);
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return profilePic;
    }

    public void addProfilePicture(int userId, byte[] picture){

        try{

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO chat.prof_pics(userId, picture) VALUES(?,?)");

            preparedStatement.setInt(1, userId);
            preparedStatement.setBytes(2,picture);

            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
