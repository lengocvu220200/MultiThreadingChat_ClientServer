package ComponentCustomize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MyConnect {
    public Socket socket;
    public BufferedWriter bufferedWriter;
    public BufferedReader bufferedReader;
    
    public MyConnect(){
        try{
            this.socket = new Socket("localhost", 3456);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            closEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void closEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
