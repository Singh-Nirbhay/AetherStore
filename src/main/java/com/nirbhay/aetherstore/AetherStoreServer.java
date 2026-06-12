package com.nirbhay.aetherstore;



import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AetherStoreServer {
    public static void main(String[] args) {
        int port = 6379;
        System.out.println("AetherStore starting on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ready to accept connections. Run 'telnet localhost 6379' in your terminal.");
            
            // This blocks until a client connects
            while(true) 
            {
            	Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getRemoteSocketAddress());
                
                ClientHandler  handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
               
            	
            }
            

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
