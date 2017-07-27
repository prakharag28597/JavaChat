
package server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{

    private JTextField user_text;
    private JTextArea chat_window;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket socket;

    //constructor
    public Server(){
        super("Server");
        user_text = new JTextField();
        chat_window=new JTextArea();
        user_text.setEditable(false);
        user_text.addActionListener((ActionEvent event) -> {
            sendMessage(event.getActionCommand());
            user_text.setText("");
        });
        
        add(user_text,BorderLayout.SOUTH);
        add(new JScrollPane(chat_window));
        setSize(300,400);
        setVisible(true);
    }

    public void runServer(){
        try{
            server=new ServerSocket(8000,100);
            while(true){
                try{
                    waitForConnection();
                    setUpStreams();
                    whileChatting();
                }catch(EOFException exception){
                    System.out.println(exception);
                    exception.printStackTrace();
                    showMessage("\n SERVER ENDED");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }
    //setting up the socket
    private void waitForConnection() throws IOException{
        showMessage("Waiting for someone to connect");
        socket=server.accept();
        showMessage("\nNow connected to "+socket.getInetAddress().getHostName());
    }
    // setting up the output and input streams
    private void setUpStreams() throws IOException{
        output=new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input=new ObjectInputStream(socket.getInputStream());
        showMessage("\nStreams set up");
    }
    // displays the received message
    private void whileChatting() throws IOException{
        showMessage("\nNow Connected");
        String message="";
        ableToType(true);
        do{
            try{
               message=(String)input.readObject();
               showMessage("\n"+message);
            }catch(ClassNotFoundException e){
                showMessage("Something is wrong");
            }
        }while(!message.equals("CLIENT-END"));
    }
    private void closeCrap(){
        try{
            output.close();
            input.close();
            socket.close();
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }
    private void showMessage(String text){
        System.out.println(text);
        SwingUtilities.invokeLater(() -> {
            chat_window.append(text);
        });
    }
    private void ableToType(boolean permission){
        SwingUtilities.invokeLater(() -> {
            user_text.setEditable(permission);   
        });
    }
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER-"+message);
            output.flush();
            showMessage("\nSERVER-"+message);
        }catch(IOException ioException){
            chat_window.append("Some error");
        }
    }
    
}