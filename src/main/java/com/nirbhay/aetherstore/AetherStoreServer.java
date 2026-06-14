package com.nirbhay.aetherstore;



import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AetherStoreServer {
    public static void main(String[] args) {
        int port = 6379;
        System.out.println("AetherStore starting on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ready to accept connections. Run 'telnet localhost 6379' in your terminal.");
            ExecutorService threadPool = Executors.newFixedThreadPool(10);
            // This blocks until a client connects
            while(true) 
            {
            	Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getRemoteSocketAddress());
                
                ClientHandler  handler = new ClientHandler(clientSocket);
                threadPool.execute(handler);
               
            	
            }
            

        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
