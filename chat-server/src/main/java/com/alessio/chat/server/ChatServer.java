package com.alessio.chat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alessio.chat.client.ChatClient;

/**
 * Wrapper Class for Chat Server instance
 */
public class ChatServer implements Runnable {

    private int port = 0;
    private List<ChatClient> clients = new ArrayList<ChatClient>();
    ExecutorService executorService = Executors.newCachedThreadPool();

    public ChatServer(int port) {
        this.port = port;
    }

    /**
     * This method initialize serverSocket with port (default 10000) and listens for incoming client connections.
     * For every connection it instantiates a ChatClient session.
     */
    @Override
    public void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            while (true) {
                Socket s = ss.accept();
                executorService.execute(new ChatClient(s, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ss != null && !ss.isClosed()) {

                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * This method adds to ClientList a new Client that wants to register to this chat instance.
     * @param client: the chatClient that wants to register to this chat
     * @return false if chatClient is already registered (based on clientName), returns true otherwise
     */
    public synchronized boolean registerClient(ChatClient client){
        for (ChatClient otherClient : clients)
        if (otherClient.getClientName().equalsIgnoreCase(client.getClientName()))
            return false;
        clients.add(client);
        return true;
    }

    /**
     * This method removes chatClient that wants to leave this chat instance. If client was already registered, it sends
     * broadcast message in order to notify other clients that a user has left this chat.
     * @param client: the chatClient that wants to leave this chat
     */
    public void deregisterClient(ChatClient client){
        boolean isRegistered = false;
        synchronized (this){
            isRegistered = clients.remove(client);
        }
        if (isRegistered)
            broadcast(client, " " + client.getClientName() + " has left AleChat! ");
    }

    /**
     * This method iterates over the Client List and returns the number of Clients connected and every client name.
     * @return a string containing tha number of users actually connected to this chat instance.
     */
    public synchronized String getOnlineUsers(){
        StringBuilder sb = new StringBuilder();
        sb.append(clients.size()).append(" user(s) online: ");
        for (int i = 0; i < clients.size(); i++)
        sb.append((i > 0) ? ", " : "").append(clients.get(i).getClientName());
        return sb.toString();
    }

    /**
     * This methods notify all other clients connected to this chat about an event or a message.
     * @param fromClient: The client sending a broadcast message to each other
     * @param msg: the message to broadcast
     */
    public void broadcast(ChatClient fromClient, String msg){

        List<ChatClient> clients = null;
        synchronized (this){
            clients = new ArrayList<ChatClient>(this.clients);
        }
        for (ChatClient client : clients){
            if (client.equals(fromClient))
                continue;
            try{
                client.write(msg + "\r\n");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
    * Getter and Setter methods
    */

    public List<ChatClient> getClients() {
        return clients;
    }

    public void setClients(List<ChatClient> clients) {
        this.clients = clients;
    }



}
