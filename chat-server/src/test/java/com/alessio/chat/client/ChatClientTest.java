package com.alessio.chat.client;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.alessio.chat.server.ChatServer;

public class ChatClientTest{

    public static final int PORT = 10000;

    @Test
    public void testClientNotEquals(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        chatClient.setClientName("client1");
        chatClient2.setClientName("client2");
        assertFalse("client1 = client2", chatClient.equals(chatClient2));
    }

    @Test
    public void testClientEquals(){
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ChatClient chatClient2 = new ChatClient(null, chatServer);
        chatClient.setClientName("client1");
        chatClient2.setClientName("client1");
        assertTrue("client1 = client1, same Client name", chatClient.equals(chatClient2));
    }

    @Test
    public void testWriteToOutputStrem() throws IOException{
        ChatServer chatServer = new ChatServer(PORT);
        ChatClient chatClient = new ChatClient(null, chatServer);
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        chatClient.setClientName("client1");
        chatClient.setOutput(new OutputStreamWriter(baos1));
        chatClient.write("new message");
        assertEquals(baos1.toString(), "new message");
    }
}
