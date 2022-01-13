package baithihk1_ungdungchat_client;

import ComponentCustomize.SearchCallBack;
import ComponentCustomize.SearchEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MainFrame extends javax.swing.JFrame {
    String backgroundColorButtonPopopMenu = "#66CC99";
    String hoverColorButtonPopopMenu = "#33CCCC";
    String partnerNickname = null;
    Color textChatColor = new Color(0,0,0);
    Color myNameChatColor = new Color(0,102,255);
    Color partnerChatColor = new Color(255,0,0);
    ArrayList<ChatRecentPanel> arrayChatRecent = new ArrayList<>();
    
    public MainFrame() {
        initComponents();
        searchComponent1.hintText = "Nhập nội dung...";
        PopupMenu.add(jPanel7);
        initMoving(this);
        Button_Back.setBackground(Color.decode(backgroundColorButtonPopopMenu));
        Button_Exit.setBackground(Color.decode(backgroundColorButtonPopopMenu));
        Label_MyNickname.setText(NickNameFrame._NICKNAME + " (Bạn)");
        setPanel_Body(Panel_Cho);
        Label_BanChat.setVisible(false);
        Label_Icon_BanChat.setVisible(false);
        searchComponent1.addEvent(new SearchEvent() {
            @Override
            public void onPressed(SearchCallBack call) {
                if(!searchComponent1.getText().isEmpty()){
                    addItemRight("Bạn", myNameChatColor);
                    addItemRight(searchComponent1.getText(), textChatColor);
                    
                    try {
                        NickNameFrame._Connect.bufferedWriter.write("chat#"+searchComponent1.getText());//chổ này phải truyền bạn chát
                        NickNameFrame._Connect.bufferedWriter.newLine();
                        NickNameFrame._Connect.bufferedWriter.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    searchComponent1.setText(null);
                }
            }

            @Override
            public void onCancel() {
                
            }
        });
        try {
            NickNameFrame._Connect.bufferedWriter.write("ghepdoi#no#null");//chổ này phải truyền bạn chát
            NickNameFrame._Connect.bufferedWriter.newLine();
            NickNameFrame._Connect.bufferedWriter.flush();

        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //lắng nghe phản hồi từ server
        listenForMessage();

    }
    
    private void checkFunction(String functionName){
        System.out.println(functionName);
        String[] mang = functionName.split("#");
        if(mang[0].equals("chat")){
            mang = functionName.split("#", 2);
        }
        switch(mang[0]){
            case "chat":
                addItemLeft(partnerNickname, partnerChatColor);
                addItemLeft(mang[1], textChatColor);
                break;
            case "ghepdoi":
                ghepDoi(mang[1], mang[2]);
                break;
            case "dongy":
                dongYGhepDoi(mang[1], mang[2]);
                break;
            case "roichat":
                roiChat(mang[1]);
                break;
        }
    }
    private void setPanel_Body(JPanel panel){
        Panel_Body.removeAll();
        Panel_Body.add(panel, BorderLayout.CENTER);
        Panel_Body.repaint();
        Panel_Body.revalidate();
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while(NickNameFrame._Connect.socket.isConnected()){
                    try{
                        msgFromGroupChat = NickNameFrame._Connect.bufferedReader.readLine();

                        System.out.println(msgFromGroupChat);
                        checkFunction(msgFromGroupChat);
                    }catch(IOException e){
                        NickNameFrame._Connect.closEverything(NickNameFrame._Connect.socket, NickNameFrame._Connect.bufferedReader, NickNameFrame._Connect.bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void ghepDoi(String status, String partnerNickname){
        try {
            if(status.equals("success")){
                int hoi = JOptionPane.showConfirmDialog(this, "Bạn có muốn chat với " + partnerNickname + " không?", "A Silly Question", JOptionPane.YES_NO_OPTION);
                if (hoi == JOptionPane.YES_OPTION) {
                    NickNameFrame._Connect.bufferedWriter.write("ghepdoi#yes#"+partnerNickname);
                    NickNameFrame._Connect.bufferedWriter.newLine();
                    NickNameFrame._Connect.bufferedWriter.flush();
                }else{
                    NickNameFrame._Connect.bufferedWriter.write("ghepdoi#no#"+partnerNickname);
                    System.out.println("đã bấm từ chối" + partnerNickname);
                    NickNameFrame._Connect.bufferedWriter.newLine();
                    NickNameFrame._Connect.bufferedWriter.flush();
                }
            }
            if(status.equals("fail")){
                setPanel_Body(Panel_Cho);
                Label_BanChat.setVisible(false);
                Label_Icon_BanChat.setVisible(false);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dongYGhepDoi(String status, String partnerNickname){
        if(status.equals("yes")){
            setPanel_Body(Panel_Chat);
            Label_BanChat.setVisible(true);
            Label_Icon_BanChat.setVisible(true);
            Label_BanChat.setText(partnerNickname);
            this.partnerNickname = partnerNickname;
            ChatRecentPanel recent = new ChatRecentPanel();
            recent.setLabel_Ten(partnerNickname);
            arrayChatRecent.add(recent);
        }
        if(status.equals("wait")){
            Label_BanChat.setVisible(true);
            Label_BanChat.setText("Đang chờ phản hồi từ: " + partnerNickname);
            Label_Icon_BanChat.setVisible(true);
        }
        if(status.equals("no")){
            setPanel_Body(Panel_Cho);
            Label_BanChat.setVisible(true);
            Label_BanChat.setText(partnerNickname + " đã từ chối bạn");
            Label_Icon_BanChat.setVisible(true);
        }
    }
    private void addItemLeft(String text, Color color){
        StyledDocument doc = txtPaneBody.getStyledDocument();

        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, color);
        
        try
        {
            doc.insertString(doc.getLength(),  "\n"+ text, left );
            doc.setParagraphAttributes(doc.getLength(), 1, left, false);
            doc.insertString(doc.getLength(),  "\n", left );
            doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        }
        catch(BadLocationException e) { 
            System.out.println(e); 
        }
    }
    
    private void addItemRight(String text, Color color){
        StyledDocument doc = txtPaneBody.getStyledDocument();

        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(right, color);
        
        try
        {
            doc.insertString(doc.getLength(), "\n"+ text, right );
            doc.setParagraphAttributes(doc.getLength(), 1, right, false);
            doc.insertString(doc.getLength(), "\n", right );
            doc.setParagraphAttributes(doc.getLength(), 1, right, false);
        }
        catch(BadLocationException e) { 
            System.out.println(e); 
        }
    }
    public void roiChat(String partnerNickname){
        setPanel_Body(Panel_Cho);
        Label_BanChat.setVisible(true);
        Label_Icon_BanChat.setVisible(true);
        Label_BanChat.setText(partnerNickname + " đã rời khỏi cuộc trò chuyện");
        txtPaneBody.setText(null);
        
        setChatRecent();
    }
    private void setChatRecent(){
        Panel_ChatRecent.removeAll();
        Panel_ChatRecent.repaint();
        Panel_ChatRecent.revalidate();
        
        int y = 0;
        for(int i = arrayChatRecent.size() - 1; i >=0; i--){
            Panel_ChatRecent.add(arrayChatRecent.get(i));
            arrayChatRecent.get(i).setBounds(0, y, 353, 50);
            y += 55;
        }
        Panel_ChatRecent.setLayout(null);
    }
    private int x;
    private int y;
    private void initMoving(JFrame frame){
        HeaderPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                x = e.getX();
                y = e.getY();
            }
        });
        HeaderPanel.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent me){
                frame.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PopupMenu = new javax.swing.JPopupMenu();
        jPanel7 = new javax.swing.JPanel();
        Button_Back = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        Button_Exit = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        Panel_Chat = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPaneBody = new javax.swing.JTextPane();
        jPanel4 = new javax.swing.JPanel();
        searchComponent1 = new baithihk1_ungdungchat_client.SearchComponent();
        Panel_Cho = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        Label_MyNickname = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Panel_ChatRecent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Panel_Body = new javax.swing.JPanel();
        HeaderPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Label_Icon_BanChat = new javax.swing.JLabel();
        Label_BanChat = new javax.swing.JLabel();

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));

        Button_Back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Button_BackMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Button_BackMouseExited(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/undo-30.png"))); // NOI18N
        jLabel6.setText("Rời khỏi cuộc trò chuyện");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel6MouseExited(evt);
            }
        });

        javax.swing.GroupLayout Button_BackLayout = new javax.swing.GroupLayout(Button_Back);
        Button_Back.setLayout(Button_BackLayout);
        Button_BackLayout.setHorizontalGroup(
            Button_BackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
        );
        Button_BackLayout.setVerticalGroup(
            Button_BackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        Button_Exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Button_ExitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Button_ExitMouseExited(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/logout-24.png"))); // NOI18N
        jLabel7.setText("Thoát chương trình");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel7MouseExited(evt);
            }
        });

        javax.swing.GroupLayout Button_ExitLayout = new javax.swing.GroupLayout(Button_Exit);
        Button_Exit.setLayout(Button_ExitLayout);
        Button_ExitLayout.setHorizontalGroup(
            Button_ExitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Button_ExitLayout.setVerticalGroup(
            Button_ExitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Button_Back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Button_Exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(Button_Back, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Button_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtPaneBody.setEditable(false);
        txtPaneBody.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jScrollPane1.setViewportView(txtPaneBody);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        searchComponent1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchComponent1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(searchComponent1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 854, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(searchComponent1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Panel_ChatLayout = new javax.swing.GroupLayout(Panel_Chat);
        Panel_Chat.setLayout(Panel_ChatLayout);
        Panel_ChatLayout.setHorizontalGroup(
            Panel_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_ChatLayout.setVerticalGroup(
            Panel_ChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_ChatLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Panel_Cho.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Chờ ghép đôi");

        javax.swing.GroupLayout Panel_ChoLayout = new javax.swing.GroupLayout(Panel_Cho);
        Panel_Cho.setLayout(Panel_ChoLayout);
        Panel_ChoLayout.setHorizontalGroup(
            Panel_ChoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 854, Short.MAX_VALUE)
        );
        Panel_ChoLayout.setVerticalGroup(
            Panel_ChoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_ChoLayout.createSequentialGroup()
                .addGap(236, 236, 236)
                .addComponent(jLabel4)
                .addContainerGap(462, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(0, 153, 255));

        Label_MyNickname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Label_MyNickname.setForeground(new java.awt.Color(255, 255, 255));
        Label_MyNickname.setText("Lê Ngọc Vũ (Bạn)");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user-50.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Label_MyNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Label_MyNickname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
        );

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        Panel_ChatRecent.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout Panel_ChatRecentLayout = new javax.swing.GroupLayout(Panel_ChatRecent);
        Panel_ChatRecent.setLayout(Panel_ChatRecentLayout);
        Panel_ChatRecentLayout.setHorizontalGroup(
            Panel_ChatRecentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 353, Short.MAX_VALUE)
        );
        Panel_ChatRecentLayout.setVerticalGroup(
            Panel_ChatRecentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(Panel_ChatRecent);

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel1.setText("Chat gần đây");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Panel_Body.setBackground(new java.awt.Color(204, 204, 204));
        Panel_Body.setLayout(new java.awt.BorderLayout());

        HeaderPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/menu-30.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);

        Label_Icon_BanChat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user-50.png"))); // NOI18N
        jPanel6.add(Label_Icon_BanChat);
        Label_Icon_BanChat.setBounds(0, 0, 50, 53);

        Label_BanChat.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        Label_BanChat.setText("Tên bạn chat");
        jPanel6.add(Label_BanChat);
        Label_BanChat.setBounds(57, 0, 710, 53);

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel_Body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(HeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel_Body, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        PopupMenu.show(jPanel5, -180, 50);
        PopupMenu.setVisible(true);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked
        
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        PopupMenu.setVisible(false);
        int hoi = JOptionPane.showConfirmDialog(this, "Bạn muốn thoát khỏi cuộc trò chuyện này?", "A Silly Question", JOptionPane.YES_NO_OPTION);
        if (hoi == JOptionPane.YES_OPTION) {
            setPanel_Body(Panel_Cho);
            Label_BanChat.setVisible(true);
            Label_Icon_BanChat.setVisible(true);
            Label_BanChat.setText("Bạn đã rời khỏi cuộc trò chuyện");
            txtPaneBody.setText(null);
            try {
                NickNameFrame._Connect.bufferedWriter.write("roichat#");
                NickNameFrame._Connect.bufferedWriter.newLine();
                NickNameFrame._Connect.bufferedWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            setChatRecent();
        }
        
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        PopupMenu.setVisible(false);
        int hoi = JOptionPane.showConfirmDialog(this, "Bạn có muốn thoát chương trình không?", "A Silly Question", JOptionPane.YES_NO_OPTION);
        if (hoi == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jLabel7MouseClicked

    private void Button_BackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Button_BackMouseEntered
        
    }//GEN-LAST:event_Button_BackMouseEntered

    private void Button_BackMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Button_BackMouseExited
        
    }//GEN-LAST:event_Button_BackMouseExited

    private void Button_ExitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Button_ExitMouseEntered
        
    }//GEN-LAST:event_Button_ExitMouseEntered

    private void Button_ExitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Button_ExitMouseExited
        
    }//GEN-LAST:event_Button_ExitMouseExited

    private void jLabel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseEntered
        Button_Back.setBackground(Color.decode(hoverColorButtonPopopMenu));
    }//GEN-LAST:event_jLabel6MouseEntered

    private void jLabel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseExited
        Button_Back.setBackground(Color.decode(backgroundColorButtonPopopMenu));
    }//GEN-LAST:event_jLabel6MouseExited

    private void jLabel7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseEntered
        Button_Exit.setBackground(Color.decode(hoverColorButtonPopopMenu));
    }//GEN-LAST:event_jLabel7MouseEntered

    private void jLabel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseExited
        Button_Exit.setBackground(Color.decode(backgroundColorButtonPopopMenu));
    }//GEN-LAST:event_jLabel7MouseExited

    private void searchComponent1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchComponent1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if(!searchComponent1.getText().isEmpty()){
                addItemRight("Bạn", myNameChatColor);
                addItemRight(searchComponent1.getText(), textChatColor);
                try {
                    NickNameFrame._Connect.bufferedWriter.write("chat#"+searchComponent1.getText());//chổ này phải truyền bạn chát
                    NickNameFrame._Connect.bufferedWriter.newLine();
                    NickNameFrame._Connect.bufferedWriter.flush();
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                searchComponent1.setText(null);
            }
        }
    }//GEN-LAST:event_searchComponent1KeyPressed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Button_Back;
    private javax.swing.JPanel Button_Exit;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JLabel Label_BanChat;
    private javax.swing.JLabel Label_Icon_BanChat;
    private javax.swing.JLabel Label_MyNickname;
    private javax.swing.JPanel Panel_Body;
    private javax.swing.JPanel Panel_Chat;
    private javax.swing.JPanel Panel_ChatRecent;
    private javax.swing.JPanel Panel_Cho;
    private javax.swing.JPopupMenu PopupMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private baithihk1_ungdungchat_client.SearchComponent searchComponent1;
    private javax.swing.JTextPane txtPaneBody;
    // End of variables declaration//GEN-END:variables
}
