package baithihk1_ungdungchat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat_Server {
    private ServerSocket serverSocket;
    public Chat_Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                Chat_Thread chat = new Chat_Thread(socket);
                Thread thread = new Thread(chat);
                thread.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3456);
        Chat_Server chat = new Chat_Server(server);
        chat.startServer();
    }
}
