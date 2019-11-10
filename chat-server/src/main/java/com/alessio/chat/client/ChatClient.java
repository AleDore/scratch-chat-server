package com.alessio.chat.client;

import java.io.*;
import java.net.*;
import com.alessio.chat.server.ChatServer;

/**
 * Wrapper Class for single Chat Client.
 */
public class ChatClient implements Runnable{
    private Socket socket = null;
    private Writer output = null;
    private ChatServer server = null;
    private String clientName = null;

    /**
     * Constructor
     * @param socket: the client Socket accepted by Server Socket
     * @param server: Ref to ChatServer instance
     */
    public ChatClient(Socket socket, ChatServer server){
        this.server = server;
        this.socket = socket;
    }

    /**
     * This method starts a Client Session to ChatServer. It listen for input string, writes to client socket outputstream
     * and send broadcast request to ChatServer.
     */
    public void run(){
      try{
        socket.setSendBufferSize(16384);
        socket.setTcpNoDelay(true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new OutputStreamWriter(socket.getOutputStream());
        write("Welcome to AleChat!. Please enter your nickname: ");
        String line = null;
        while ((line = input.readLine()) != null){
          if (clientName == null){
            line = line.trim();
            if (line.isEmpty()){
              write("Welcome to AleChat!. Please enter your nickname: ");
              continue;
            }
            clientName = line;
            if (!server.registerClient(this)){
              clientName = null;
              write("Nickname already registered. Please enter an other nickname: ");
              continue;
            }
            write(server.getOnlineUsers() + "\r\n");
            server.broadcast(this, " " + clientName + " has joined Alechat! ");
            continue;
          }
          if (line.equalsIgnoreCase("/quit"))
            return;
          server.broadcast(this, clientName + "> " + line);
        }
      }
      catch (Exception e){

      }
      finally{
        server.deregisterClient(this);
        output = null;
        try{
            socket.close();
        }
        catch (Exception e){

        }
        socket = null;
      }
    }

    /**
     * This method writes message in the socket OutputStream by using writer
     * @param msg: the message to write
     * @throws IOException
     */
    public void write(String msg) throws IOException{
      output.write(msg);
      output.flush();
    }

    /**
     *
     * @param client: An other ChatClient to compare with this instance
     * @return true if passed ChatClient instance has the same client name of this instance, returns false otherwise.
     */
    public boolean equals(ChatClient client){
      return (client != null) && (client instanceof ChatClient) && (clientName != null) && (client.clientName != null) && clientName.equals(client.clientName);
    }

    /**
     * Getter and setters methods
     */

    public String getClientName() {
        return clientName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Writer getOutput() {
        return output;
    }

    public void setOutput(Writer output) {
        this.output = output;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


  }
