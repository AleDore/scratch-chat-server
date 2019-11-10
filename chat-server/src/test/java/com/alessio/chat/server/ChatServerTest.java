package com.alessio.chat.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.alessio.chat.client.ChatClient;

import org.junit.Test;

public class ChatServerTest {

    public static final int PORT = 10000;

    @Test
    public void testServerSocket() throws IOException{
        ServerSocket serverSocket = null;
        Socket telnetSocket = null;
        try{
            serverSocket = new ServerSocket(PORT);
            telnetSocket = new Socket("localhost", PORT);
            Socket clientSocket = serverSocket.accept();
            assertTrue("accepted new client", clientSocket != null);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (telnetSocket != null){
                telnetSocket.close();
            }
            if (serverSocket != null){
                serverSocket.close();
            }
        }

    }

    @Test
    public void testClientSocket() throws IOException{
        ServerSocket serverSocket = null;
        Socket telnetSocket = null;
        try{
            serverSocket = new ServerSocket(PORT);
            telnetSocket = new Socket("localhost", PORT);
            Socket clientSocket = serverSocket.accept();

            assertNotNull(clientSocket);
        } catch (Exception e){
            throw e;
        } finally {
            if (telnetSocket != null){
                telnetSocket.close();
            }
            if (serverSocket != null){
                serverSocket.close();
            }
        }

    }

    @Test
    public void testRegisterClient(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        chatServer.registerClient(chatClient);
        assertTrue("ChatServerContainsClient", chatServer.getClients().contains(chatClient));
    }

    @Test
    public void testDeregisterClient(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        chatServer.registerClient(chatClient);
        chatServer.deregisterClient(chatClient);
        assertTrue("ChatServer does not contain client", chatServer.getClients().isEmpty());
    }

    @Test
    public void testOnlineClient(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        chatClient.setClientName("client1");
        chatClient2.setClientName("client2");
        chatServer.registerClient(chatClient);
        chatServer.registerClient(chatClient2);
        assertEquals(chatServer.getOnlineUsers(), "2 user(s) online: client1, client2");
    }

    @Test
    public void testBroadcastJoinMsg(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        chatClient.setOutput(new OutputStreamWriter(baos1));
        chatClient2.setOutput(new OutputStreamWriter(baos2));
        chatClient.setClientName("client1");
        chatClient2.setClientName("client2");
        chatServer.registerClient(chatClient);
        chatServer.registerClient(chatClient2);
        chatServer.broadcast(chatClient2, " " + chatClient2.getClientName() + " has joined Alechat! ");
        assertEquals(baos1.toString()," client2 has joined Alechat! \r\n");
    }

    @Test
    public void testBroadcastLeftMsg(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        chatClient.setOutput(new OutputStreamWriter(baos1));
        chatClient2.setOutput(new OutputStreamWriter(baos2));
        chatClient.setClientName("client1");
        chatClient2.setClientName("client2");
        chatServer.registerClient(chatClient);
        chatServer.registerClient(chatClient2);
        chatServer.deregisterClient(chatClient2);
        assertEquals(baos1.toString()," client2 has left AleChat! \r\n");
    }

    @Test
    public void exchangeMessagesBetweenClients(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        chatClient.setOutput(new OutputStreamWriter(baos1));
        chatClient2.setOutput(new OutputStreamWriter(baos2));
        chatClient.setClientName("client1");
        chatClient2.setClientName("client2");
        chatServer.registerClient(chatClient);
        chatServer.registerClient(chatClient2);
        String msgFromClient1 = "Hello! i'm Client1!";
        chatServer.broadcast(chatClient, chatClient.getClientName() + "> " + msgFromClient1);
        assertTrue("Client2 is notified about msg from Client 1", baos2.toString().contains("client1> Hello! i'm Client1!"));
    }



}
