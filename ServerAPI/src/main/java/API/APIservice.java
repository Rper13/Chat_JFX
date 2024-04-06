package API;

import JDBC.MySQLservice;
import Objects.User;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;


public class APIservice {

    private final String clientId;
    private ObjectInputStream reader = null;
    private ObjectOutputStream writer = null;

    public APIservice(String clientId){
        this.clientId = clientId;
    }

    public synchronized void handleClient(Socket clientSocket){

        try{
            if(reader == null)
                reader = Service.getInstance().getReaders().get(clientId);

            if(writer == null)
                writer = Service.getInstance().getWriters().get(clientId);

            String request = reader.readUTF();

            RequestType requestType = determineRequest(request);

            switch (requestType){

                case LOGIN -> {
                    request = request.substring(10);
                    writer.writeUTF("API:LOGIN:" + processLoginRequest(request));
                    writer.flush();
                }

                case REGISTER -> {
                    request = request.substring(13);
                    writer.writeUTF("API:REGISTER:" + processRegisterRequest(request));
                    writer.flush();
                }

                case USER_INFO -> {
                    request = request.substring(14);
                    User user = MySQLservice.getInstance().retrieveUser(request);
                    writer.writeUTF("API:USER_INFO:");
                    writer.flush();
                    writer.writeObject(user);
                    writer.flush();
                }

                case UPDATE_PROFILE -> {
                    request = request.substring(19);
                    updateProfile(request);
                }
                case GET_PROFILE -> {
                    request = request.substring(16);
                    writer.writeUTF("API:GET_PROFILE:");
                    writer.flush();
                    getProfilePicture(request);
                }
                case MESSAGE ->
                    processMessage(request);
            }



        }catch (IOException e){

            System.err.println("Client disconnected:");
            try{
                clientSocket.close();
                Service.getInstance().getClients().remove(clientId);
                writer = null; reader = null;
            }catch (Exception ex){System.out.println(ex.getMessage());}

        }


    }

    private RequestType determineRequest(String req){

        if(req.length() >=10 ) {
            if (req.startsWith("API:LOGIN:")) return RequestType.LOGIN;
        }
        if(req.length() >= 13){
            if (req.startsWith("API:REGISTER:")) return RequestType.REGISTER;
        }
        if(req.length() >= 14){
            if(req.startsWith("API:USER_INFO:")) return RequestType.USER_INFO;
        }
        if(req.length() >= 19){
            if(req.startsWith("API:UPDATE_PROFILE:")) return RequestType.UPDATE_PROFILE;
        }
        if(req.length() >= 16){
            if(req.startsWith("API:GET_PROFILE:")) return RequestType.GET_PROFILE;
        }

        return RequestType.MESSAGE;
    }

    private String processLoginRequest(String request){

        String[] parts = request.split(",");
        String username = parts[0];
        String password = parts[1];

        MySQLservice SQL = MySQLservice.getInstance();


        String res = SQL.AuthRequest(username,password);
        SQL.closeConn();


        return res;
    }

    private String processRegisterRequest(String request){
        String[] parts = request.split(",");
        String name = parts[0];
        String last_name = parts[1];
        String username = parts[2];
        String password = parts[3];
        String phone_number = parts[4];

        MySQLservice SQL = MySQLservice.getInstance();

        boolean r = SQL.RegisterRequest(name,last_name,username,password,phone_number);
        String res = (r) ? "Successfully Registered" : "Registration Failed";

        SQL.closeConn();

        return res;
    }

    private void processMessage(String request){

        String[] parts = request.split(",");
        int userId = Integer.parseInt(parts[0]);
        String message = parts[1];

        byte[] picture = MySQLservice.getInstance().retrieveProfilePicture(userId);

        Pair<String, byte[]> obj = new Pair<>(message, picture);

        try {
            HashMap<String, ObjectOutputStream> writers = Service.getInstance().getWriters();
            for(String key : writers.keySet()){
                writers.get(key).writeUTF("");
                writers.get(key).writeObject(obj);
                writers.get(key).flush();
            }

        } catch (IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void updateProfile(String request){

        int userId = Integer.parseInt(request);

        try {
            byte[] pic = (byte[]) reader.readObject();
            MySQLservice.getInstance().addProfilePicture(userId, pic);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void getProfilePicture(String request){
        int userId = Integer.parseInt(request);
        byte[] picture = MySQLservice.getInstance().retrieveProfilePicture(userId);
        try {
            writer.writeObject(picture);
            writer.flush();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
