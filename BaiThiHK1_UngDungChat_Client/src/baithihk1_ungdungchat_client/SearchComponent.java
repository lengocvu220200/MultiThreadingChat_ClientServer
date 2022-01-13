package baithihk1_ungdungchat_client;

import ComponentCustomize.SearchCallBack;
import ComponentCustomize.SearchEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class SearchComponent extends JTextField {
    private Color animationColor = new Color(3,175,255);
    private Color backgroungColor = Color.WHITE;
    private Icon iconSearch;
    public String hintText = "Search ...";
    private SearchEvent event;
    private SearchCallBack callbacck;
    private Thread thread;
    
    public SearchComponent(){
        setBackground(new Color(255,255,255,0));
        setOpaque(false);
        setBorder(new EmptyBorder(10,10,10,50));
        setFont(new Font("sansserif", 0, 16));
        setSelectionColor(new Color(80,199,255));
        iconSearch = new ImageIcon(getClass().getResource("/Icons/send-24.png"));
        //create and check if mouse over button
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me){
                if(checkMouseOver(me.getPoint())){
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }else{
                    setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(SwingUtilities.isLeftMouseButton(e)){
                    if(checkMouseOver(e.getPoint())){
                        if(event!=null){
                            thread=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    event.onPressed(callbacck);
                                }
                            });
                            thread.start();
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hintText, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
    
    @Override
    protected void paintComponent(Graphics gr){
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) gr;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(backgroungColor);
        g2.fillRoundRect(0, 0, width, height, height, height);
        super.paintComponent(gr);
        //create button 
        int marginButton = 5;
        int buttonSize = height-marginButton*2;
        GradientPaint gra = new GradientPaint(0, 0, new Color(255,255,255), width, 0 ,animationColor);
        g2.setPaint(gra);
        g2.fillOval(width-height+3, marginButton, buttonSize, buttonSize);
        
        //create button icon
        int marginImage = 5;
        int imaageSize = buttonSize-marginImage*2;
        Image image = ((ImageIcon) iconSearch).getImage();
        g2.drawImage(image, width-height+marginImage+3, marginButton+marginImage, imaageSize,imaageSize,null);
        g2.dispose();
        
    }
    private boolean checkMouseOver(Point mouse){
        int width = getWidth();
        int height = getHeight();
        int marginButton = 5;
        int buttonSize = height-marginButton*2;
        Point point = new Point(width-height+3,marginButton);
        Ellipse2D.Double circle = new Ellipse2D.Double(point.x,point.y,buttonSize,buttonSize);
        return circle.contains(mouse);
    }
    public void addEvent(SearchEvent event){
        this.event = event;
    }
    
}


