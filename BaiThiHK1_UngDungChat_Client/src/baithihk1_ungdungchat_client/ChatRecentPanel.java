/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baithihk1_ungdungchat_client;

import javax.swing.JLabel;

/**
 *
 * @author Vu
 */
public class ChatRecentPanel extends javax.swing.JPanel {

    /**
     * Creates new form ChatRecentPanel
     */
    public ChatRecentPanel() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Label_Icon = new javax.swing.JLabel();
        Label_Ten = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        Label_Icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user-50.png"))); // NOI18N

        Label_Ten.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Label_Ten.setText("Tên người chát");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Label_Icon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Label_Ten, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Label_Ten, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Label_Icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Label_Icon;
    private javax.swing.JLabel Label_Ten;
    // End of variables declaration//GEN-END:variables

    public JLabel getLabel_Icon() {
        return Label_Icon;
    }

    public void setLabel_Icon(JLabel Label_Icon) {
        this.Label_Icon = Label_Icon;
    }

    public JLabel getLabel_Ten() {
        return Label_Ten;
    }

    public void setLabel_Ten(String ten) {
        this.Label_Ten.setText(ten);
    }
}
