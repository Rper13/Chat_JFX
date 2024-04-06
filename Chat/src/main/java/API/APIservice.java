package API;

import Controllers.HomePageController;
import Controllers.LoginPageController;
import Controllers.RegisterPageController;
import GlobalScope.Functions;
import GlobalScope.Lockers;
import GlobalScope.Navigation;
import Main.Main;
import Objects.ResponseType;
import Objects.User;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.util.Pair;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

public class APIservice {

    private static final int PORT = 9999;

    private static final String KEY = "brr";

    private static Socket connection;

    private static ObjectInputStream reader = null;
    private static ObjectOutputStream writer = null;

    public static Socket getConnection() {
        return connection;
    }

    public static void retryConnection() {
        if (connection == null) {
            Connect();
        }
    }

    public static void Connect() {

        Properties properties = new Properties();

        try (InputStream input = APIservice.class.getResourceAsStream("/config.properties")){

            properties.load(input);

            String EXTERNAL_SERVER_IP = encrypt(KEY, properties.getProperty("external_server_ip"), false);

            String INTERNAL_SERVER_IP = encrypt(KEY, properties.getProperty("internal_server_ip"), false);

            URL url = new URL("https://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String externalIp = in.readLine();
            String internalIp = InetAddress.getLocalHost().getHostAddress();

            in.close();

            String serverIp;

            if (!externalIp.equals(EXTERNAL_SERVER_IP)) {
                serverIp = EXTERNAL_SERVER_IP;
            } else if (!internalIp.equals(INTERNAL_SERVER_IP)) {
                serverIp = INTERNAL_SERVER_IP;
            } else {
                serverIp = "127.0.0.1";
            }

            connection = new Socket(serverIp, PORT);

            System.out.println("Connected.");

            writer = new ObjectOutputStream(new DataOutputStream(connection.getOutputStream()));
            reader = new ObjectInputStream(new DataInputStream(connection.getInputStream()));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Thread thr = new Thread (()->{
            while(Main.running && connection != null){
                processResponse();
            }
        });

        thr.start();

    }

    private static void processResponse(){
        try{
            String response = reader.readUTF();
            ResponseType responseType = determineResponse(response);

            switch (responseType){

                case LOGIN -> {
                    LoginPageController.response = response.substring(10);
                    Lockers.syncLoginResponse(LoginPageController.response, response, Lockers.login_lock);
                }

                case REGISTER -> {
                    response = response.substring(13);
                    RegisterPageController.response = response;
                }

                case USER_INFO -> {
                    response = response.substring(14);

                    User.setUser((User) reader.readObject());

                }

                case UPDATE_PROFILE -> {
                    response = response.substring(19);

                }
                case GET_PROFILE -> {
                    response = response.substring(16);
                    receiveProfilePicture();

                }
                case MESSAGE ->
                        receiveMessage();

            }

        }catch (Exception e){
            System.out.println("Error processing Response: " + e.getMessage());
        }
    }

    private static ResponseType determineResponse(String resp){

        if(resp.length() >=10 ) {
            if (resp.startsWith("API:LOGIN:")) return ResponseType.LOGIN;
        }
        if(resp.length() >= 13){
            if (resp.startsWith("API:REGISTER:")) return ResponseType.REGISTER;
        }
        if(resp.length() >= 14){
            if(resp.startsWith("API:USER_INFO:")) return ResponseType.USER_INFO;
        }
        if(resp.length() >= 19){
            if(resp.startsWith("API:UPDATE_PROFILE:")) return ResponseType.UPDATE_PROFILE;
        }
        if(resp.length() >= 16){
            if(resp.startsWith("API:GET_PROFILE:")) return ResponseType.GET_PROFILE;
        }

        return ResponseType.MESSAGE;

    }

    public static void sendLoginRequest(String username, String password) {
        retryConnection();
        try {
            String data = "API:LOGIN:" + username + "," + password;
            writer.writeUTF(data);
            writer.flush();

        } catch (Exception e) {
            System.out.println("Exception happened during making auth request: " + e.getMessage());
        }

    }

    public static void sendRegisterRequest(String name, String last_name, String username, String password, String phone_number) {

        try {

            retryConnection();

            String data = "API:REGISTER:" + "%s,%s,%s,%s,%s".formatted(name, last_name, username, password, phone_number);

            Request(data);

        } catch (IOException e) {
            System.out.println("Error while registering: " + e.getMessage());
        }

    }

    private static void Request(String data) throws IOException {

        if (connection == null) {
            retryConnection();
        }

        writer.writeUTF(data);
        writer.flush();
    }

    public static void closeSocket() {
        try {

            if (!(connection == null || connection.isClosed()))
                connection.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String encrypt(String key, String data, boolean b) {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(key);
        if (b) {
            return encryptor.encrypt(data);
        }
        return encryptor.decrypt(data);

    }

    public static void sendUserInfoRequest(String username){
        retryConnection();

        try {
            String data = "API:USER_INFO:" + username;
            writer.writeUTF(data);
            writer.flush();

        } catch (Exception e) {
            System.out.println("Exception happened while retrieving user: " +e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendMessage(String msg){

        try {

            writer.writeUTF(msg);
            writer.flush();

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void receiveMessage(){

        try {
            Object obj = reader.readObject();

            if(obj instanceof Pair<?,?>) {
                @SuppressWarnings("unchecked")
                Pair<String, byte[]> response = (Pair<String, byte[]>) obj;
                HomePageController controller = (HomePageController) Navigation.getController("Home Page");

                byte[] pic = Functions.processPicture(response.getValue());

                Platform.runLater(() -> {
                    controller.updateChatFlow(response.getKey(), pic);
                });
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void updateProfilePicture(byte[] picture){

        try{
            String userId = String.valueOf(User.getInstance().getId());

            String request = "API:UPDATE_PROFILE:" + userId;

            writer.writeUTF(request);
            writer.flush();

            writer.writeObject(picture);
            writer.flush();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void requestProfilePicture(){

        byte[] photo = null;

        try{
            writer.writeUTF("API:GET_PROFILE:" + User.getInstance().getId());
            writer.flush();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void receiveProfilePicture(){
        try {

            byte[] pic = (byte[]) reader.readObject();
            byte[] picture = Functions.processPicture(pic);

            Scene homePage = Navigation.getScene("Home Page");
            HomePageController controller = (HomePageController) Navigation.getController("Home Page");
            controller.fillProfileCircle(picture);

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
