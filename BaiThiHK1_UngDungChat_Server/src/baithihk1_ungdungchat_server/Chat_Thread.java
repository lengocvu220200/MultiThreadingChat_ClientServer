package baithihk1_ungdungchat_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Chat_Thread implements Runnable{
    public static ArrayList<Chat_Thread> arraylist_Thread = new ArrayList<>();
    public Socket socket;
    public BufferedWriter bufferedWriter;
    public BufferedReader bufferedReader;
    public String myNickname, partnerNickname = null;
    public ArrayList<Chat_Thread> arraylist_dsTuChoi = new ArrayList<>();
    public boolean trangThaiGhepDoi = false, dangNhanLoiMoiGhepDoi = false;
    public Chat_Thread banChat_Thread = null;
    public Chat_Thread(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }catch(IOException e){
            closEverything(this.socket, this.bufferedReader, this.bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                checkFunction(messageFromClient);
            }catch(IOException e){
                closEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void checkFunction(String functionName){
        System.out.println(functionName);
        String[] mang = functionName.split("#");
        if(mang[0].equals("chat")){
            mang = functionName.split("#", 2);
        }
        switch(mang[0]){
            case "nickname":
                checkUsername(mang[1]);
                break;
            case "ghepdoi":
                ghepDoi(mang[1], mang[2]);
                break;
            case "chat":
                broadcasMessage(banChat_Thread, "chat#"+mang[1]);
                break;
            case "roichat":
                roiChat();
                break;
        }
    }
    public boolean checkUsername(String username){
        for(Chat_Thread chat : arraylist_Thread){
            if(chat.myNickname.equals(username)){
                broadcasMessage(this, "nickname#fail");
                return false;
            }
        }
        myNickname = username;
        arraylist_Thread.add(this);
        broadcasMessage(this, "nickname#success");

        return true;
    }
    public void ghepDoi(String ketqua, String nickname){
        if(ketqua.equals("yes")){
            dongYGhepDoi(nickname);
        }else{
            if(arraylist_Thread.size() > 1){
                banChat_Thread = null;
                System.out.println("t??? ch???i 1 :"+nickname);
                if(!nickname.equals("null")){
                    System.out.println("t??? ch???i 2 :"+nickname);
                    for(Chat_Thread chat : arraylist_Thread){
                        if(chat.myNickname.equals(nickname)){
                            arraylist_dsTuChoi.add(chat);
                            dangNhanLoiMoiGhepDoi = false;
                            trangThaiGhepDoi = false;
                            partnerNickname = null;
                            broadcasMessage(chat, "dongy#no#" + myNickname);
                            chat.trangThaiGhepDoi = false;
                            chat.dangNhanLoiMoiGhepDoi = false;
                            chat.partnerNickname =null;
                            System.out.println(nickname + " ???? b??? t??? ch???i");
                            break;
                        }
                    }
                }
                System.out.println("t??? ch???i 3 :"+nickname);
                
                randomGhepDoi();
                
            }else{
                broadcasMessage(this, "ghepdoi#fail#" + myNickname);
            }
        }
    }
    public void dongYGhepDoi(String nickname){
        //n???u t??i ch???p nh???n
        if(banChat_Thread != null){
            if(nickname.equals(partnerNickname) && !checkTuChoi(banChat_Thread)){
                System.out.println("c?? 1: "+ myNickname);
                trangThaiGhepDoi = true;
                dangNhanLoiMoiGhepDoi = false;
                partnerNickname = nickname;
                if(banChat_Thread.trangThaiGhepDoi){
                    //g???i l???i t??i ph???n h???i hai b??n ?????ng ??
                    broadcasMessage(this, "dongy#yes#" + partnerNickname);
                    //g???i cho b???n chat
                    broadcasMessage(banChat_Thread, "dongy#yes#" + myNickname);
                    banChat_Thread.dangNhanLoiMoiGhepDoi = false;

                }else{
                    //g???i ph???n h???i ch??? b???n ch??t
                    broadcasMessage(this, "dongy#wait#" + partnerNickname);
                }
            }else{
                System.out.println("kh??ng 1: "+ myNickname);
                System.out.println("B??? " + nickname + " t??? ch???i");
                broadcasMessage(this, "dongy#no#" + nickname);
                trangThaiGhepDoi = false;
                dangNhanLoiMoiGhepDoi = false;
                banChat_Thread = null;
                
                //t??m l???i b???n chat m???i 
                randomGhepDoi();
            }
        }else{
            trangThaiGhepDoi = true;//c?? th??? b???
            for(Chat_Thread chat : arraylist_Thread){
                if(chat.myNickname.equals(nickname)){
                    banChat_Thread = chat;
                    break;
                }
            }
            if(!checkTuChoi(banChat_Thread)){
                dangNhanLoiMoiGhepDoi = false;
                partnerNickname = nickname;

                if(banChat_Thread.trangThaiGhepDoi){
                    System.out.println("8");
                    //g???i l???i t??i ph???n h???i hai b??n ?????ng ??
                    broadcasMessage(this, "dongy#yes#" + partnerNickname);
                    //g???i cho b???n chat
                    broadcasMessage(banChat_Thread, "dongy#yes#" + myNickname);
                    banChat_Thread.dangNhanLoiMoiGhepDoi = false;

                }else{
                    System.out.println("9");
                    //g???i ph???n h???i ch??? b???n ch??t
                    broadcasMessage(this, "dongy#wait#" + partnerNickname);
                }
            }else{
                System.out.println("kh??ng 2: "+ myNickname);
                System.out.println("B??? " + nickname + " t??? ch???i");
                broadcasMessage(this, "dongy#no#" + nickname);
                trangThaiGhepDoi = false;
                dangNhanLoiMoiGhepDoi = false;
                banChat_Thread = null;
                
                //t??m l???i b???n chat m???i 
                randomGhepDoi();
            }
        }
    }
    public void randomGhepDoi(){
        for(Chat_Thread chat : arraylist_Thread){
            if(!chat.trangThaiGhepDoi && !chat.dangNhanLoiMoiGhepDoi && !chat.equals(this)){
                int int_random;
                boolean checkBreak = false;
                while(true){
                    checkBreak = false;
                    Random rand = new Random();
                    int_random = rand.nextInt(arraylist_Thread.size());//t??? 0 ?????n arraylist_Thread.size() - 1(l???y ra v??? tr?? m???ng)
                    try{
                        while(arraylist_Thread.get(int_random).equals(this) || arraylist_Thread.size()<= int_random || arraylist_Thread.get(int_random).trangThaiGhepDoi || arraylist_Thread.get(int_random).dangNhanLoiMoiGhepDoi || trangThaiGhepDoi || dangNhanLoiMoiGhepDoi){//s???a l???i
                            int_random = rand.nextInt(arraylist_Thread.size());//t??? 0 ?????n arraylist_Thread.size() - 1
                        }
                    }catch (IndexOutOfBoundsException e){
                        System.out.println("m???ng client ch??? c??n l???i m??nh b???n! " +e.getMessage());
                        break;
                    }
                    Chat_Thread newThreak = arraylist_Thread.get(int_random);

                    for(Chat_Thread chat_thread : arraylist_dsTuChoi){
                        if(chat_thread.equals(newThreak)){
                            checkBreak = true;
                            break;
                        }
                    }
                    if(!checkBreak){
                        banChat_Thread = newThreak;
                        banChat_Thread.banChat_Thread = this;
                        partnerNickname = banChat_Thread.myNickname;
                        banChat_Thread.partnerNickname = myNickname;
                        broadcasMessage(this, "ghepdoi#success#" + partnerNickname);
                        broadcasMessage(banChat_Thread, "ghepdoi#success#" + myNickname);
                        dangNhanLoiMoiGhepDoi = true;
                        banChat_Thread.dangNhanLoiMoiGhepDoi = true;
                        break;
                    }
                    
                }
                break;
            }
        }
    }
    public boolean checkTuChoi(Chat_Thread thread){
        for(Chat_Thread chat : thread.arraylist_dsTuChoi){
            if(this.equals(chat)){
                return true;
            }
        }
        return false;
    }
    public void broadcasMessage(Chat_Thread thread, String messageToSend){
        try{
            thread.bufferedWriter.write(messageToSend);
            thread.bufferedWriter.newLine();
            thread.bufferedWriter.flush();
        }catch(Exception e){
            closEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void roiChat(){
        arraylist_dsTuChoi.add(banChat_Thread);
        banChat_Thread.arraylist_dsTuChoi.add(this);
        System.out.println("1000");
        banChat_Thread.partnerNickname = null;
        banChat_Thread.trangThaiGhepDoi = false;
        banChat_Thread.dangNhanLoiMoiGhepDoi = false;
        banChat_Thread.banChat_Thread = null;
        broadcasMessage(banChat_Thread, "roichat#"+myNickname);
        //banChat_Thread.randomGhepDoi();
        System.out.println("1001");
        partnerNickname = null;
        trangThaiGhepDoi = false;
        dangNhanLoiMoiGhepDoi = false;
        banChat_Thread = null;
        System.out.println("1002");
        randomGhepDoi();
        System.out.println("hahaha t??m b???n m???i");
    }
    public void removeChat_Thread(){
        try{
            if(banChat_Thread != null){
                if(banChat_Thread.socket.isConnected()){
                    broadcasMessage(banChat_Thread, "roichat#"+myNickname);
                    banChat_Thread.ghepDoi("no", "null");
                    banChat_Thread.trangThaiGhepDoi = false;
                    banChat_Thread.banChat_Thread = null;
                }
            }
        }catch(Exception e){
            
        }
        arraylist_Thread.remove(this);
        System.out.println("???? x??a");
        System.out.println("size c???a m???ng: "+arraylist_Thread.size());
    }
    public void closEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeChat_Thread();
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