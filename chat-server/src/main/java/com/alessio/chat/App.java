package com.alessio.chat;
import com.alessio.chat.server.ChatServer;

/**
 * Chat Server App!
 */

public class App {

  public static void main(String[] args){
    int port = 10000;
    new ChatServer(port).run();
  }
}

