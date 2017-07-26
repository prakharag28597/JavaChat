
package client;

/**
 *
 * @author prakharag
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame{
    
    private JTextField user_text;
    private JTextArea chat_window;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message;
    private String serverIP;
    private Socket socket;
    
    public Client(String host){
        super("Client App");
        serverIP=host;
        user_text=new JTextField();
        user_text.setEditable(false);
        user_text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand());
                user_text.setText("");
            }
        });
        add(user_text,BorderLayout.SOUTH);
        chat_window=new JTextArea();
        add(new JScrollPane(chat_window),BorderLayout.CENTER);
        setSize(300,200);
        setVisible(true);
    }
    public void start_running(){
        try{
            connect_to_server();
            set_up_streams();
            whileChatting();
        }catch(EOFException e){
            showMessage("\n Client terminated");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            closeConnections();
        }
    }
    private void connect_to_server() throws IOException{
        showMessage("Connecting ..");
        socket=new Socket(InetAddress.getByName(serverIP),8000);
        showMessage("\nConnected.");
    }
    private void set_up_streams() throws IOException{
        output=new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input=new ObjectInputStream(socket.getInputStream());
        showMessage("\nStreams set up");
    }
    private void whileChatting() throws IOException{
        ableToType(true);
        message="";
        do{
           try{
                message=(String)input.readObject();
                showMessage("\n"+message);
            }catch(ClassNotFoundException e){
                showMessage("\nclass not found exception");
            }
                
        
        }while(!message.equals("SERVER-END"));
    }
    private void closeConnections(){
        ableToType(false);
        try{
            output.close();
            input.close();
            socket.close();
        }catch(IOException exception){
            exception.printStackTrace();
        }
    }
    private void sendMessage(String msg){
        try{
            output.writeObject("CLIENT-"+msg);
            output.flush();
            showMessage("\nCLIENT-"+msg);
        }catch(IOException e){
            chat_window.append("Some Error occurred");
        }
    }
    private void showMessage(String text){
        SwingUtilities.invokeLater(() -> {
            chat_window.append(text);
        });
    }
    private void ableToType(boolean permission){
        SwingUtilities.invokeLater(() -> {
            user_text.setEditable(permission);   
        });
    }
}