package API;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Service {

    private static final int PORT = 9999;

    private static Service instance = null;

    private static final HashMap<String,Socket> clients = new HashMap<>();

    private static final HashMap<String, ObjectInputStream> readers = new HashMap<>();

    private static final HashMap<String, ObjectOutputStream> writers = new HashMap<>();

    private Thread serverThread;

    private ServerSocket serverSocket;

    private Service(){}
    /**initializes server. setting up to accept connections.*/
    public void startServer(){


        final Object mapLocker = new Object();

        serverThread = new Thread(() -> {
            try{
                serverSocket = new ServerSocket(PORT);
                System.out.println("server running");
                while(instance != null){

                    Socket clientSocket = serverSocket.accept();

                    String clientId = UUID.randomUUID().toString();
                    clients.put(clientId, clientSocket);

                        synchronized (mapLocker) {

                          clients.put(clientId, clientSocket);
                          writers.put(clientId, new ObjectOutputStream(
                                  new DataOutputStream(clientSocket.getOutputStream())));
                          readers.put(clientId, new ObjectInputStream(
                                  new DataInputStream(clientSocket.getInputStream())));
                        }

                    Thread clientHandler = new Thread(() -> {
                        while(!clientSocket.isClosed()) {
                            APIservice apiservice = new APIservice(clientId);
                            apiservice.handleClient(clientSocket);
                        }
                        clients.remove(clientId);
                        writers.remove(clientId);
                        readers.remove(clientId);
                    });

                    clientHandler.start();

                }
            }catch (IOException e){
                System.err.println("Client Disconnected.");
            }

        });

        serverThread.start();
    }

    /**@return Map of clients*/
    public HashMap<String,Socket> getClients() {
        return clients;
    }

    /**
     * we have single service instance running across the entire jar
     * @return service instance. */
    public static synchronized Service getInstance(){
        if(instance == null){
            instance = new Service();
        }
        return instance;
    }

    public HashMap<String, ObjectOutputStream> getWriters() {
        return writers;
    }

    public HashMap<String, ObjectInputStream> getReaders() {
        return readers;
    }

    /** stops server from running*/
    public void stop() {
        if (instance != null) {
            instance = null;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    for(String client : clients.keySet()){
                        clients.get(client).close();
                    }
                    //checkSockets.interrupt();
                    serverThread.interrupt();
                }
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public static void printClients(){
        System.out.println();
        for(String key : clients.keySet()){
            System.out.print("[" + key + ": " + clients.get(key) + "]; " );
        }
        System.out.println();
    }

}
