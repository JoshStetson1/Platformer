package platformer;
import java.awt.*;
import java.awt.event.*;

public class MainMenu implements MouseListener{
    Screen s;
    Image sky = null;
    
    String state = "main";
    
    public MainMenu(Screen s){
        this.s = s;
        sky = s.loadImage("paint\\sky.jpg").getScaledInstance(750, 450, java.awt.Image.SCALE_SMOOTH);
    }
    public void paint(Graphics g){
        s.s.read();
        g.drawImage(sky, 0, 0, s);
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(0, 0, s.getWidth(), s.getHeight());
        if(state.equals("main")){
            g.setColor(Color.BLACK);
            Font options = new Font("arial", Font.PLAIN, 50);
            FontMetrics metrics = g.getFontMetrics(options);
            int length = metrics.stringWidth("Play");
            g.setFont(options);
            g.drawString("Play", s.getWidth()/2 - length/2, 200);

            g.drawRect(300, 150, 150, 70);
        } else if(state.equals("levelSelect")){
            int x = 100; int y = 50;
            for(int i = 1; i <= 10; i++){
                g.setColor(Color.black);
                
                if(i > s.lm.unlocked){
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(x, y, 200, 50);
                } else{
                    String clock;
                    if(s.lm.bestTime[i-1][1] >= 10) clock = s.lm.bestTime[i-1][0] + ":" + s.lm.bestTime[i-1][1];
                    else clock = s.lm.bestTime[i-1][0] + ":0" + s.lm.bestTime[i-1][1];
                    
                    Font f = new Font("arial", Font.PLAIN, 25);
                    FontMetrics metrics = g.getFontMetrics(f);
                    int length = metrics.stringWidth(clock);
                    g.setFont(f);
                    
                    if(!clock.equals("0:00")) g.drawString(clock, x-length-20, y+35);
                }
                g.drawRect(x, y, 200, 50);
                Font f = new Font("arial", Font.PLAIN, 40);
                FontMetrics metrics = g.getFontMetrics(f);
                int length = metrics.stringWidth("Level " + i);
                g.setFont(f);
                g.drawString("Level " + i, (x+100) - (length/2), y+40);
                
                y += 75;
                if(i == 5){
                    x = 450; y = 50;
                }
            }
            g.setColor(Color.black);
            g.setFont(new Font("arial", Font.BOLD, 25));
            g.drawString("BACK", 5, 25);
        }
    }
    public Rectangle mouse(){
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        
        return new Rectangle(b.x-s.windowX-10, b.y-s.windowY-10, 1, 1);
    }
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        if(state.equals("main")){
            if(mx > 300 && mx < 450){
                if(my > 150 && my < 220){
                    state = "levelSelect";
                }
            }
        } else if(state.equals("levelSelect")){
            if(mx < 75 && my < 30) state = "main";
            
            int x = 100; int y = 50;
            for(int i = 1; i <= 10; i++){
                if(mx > x && mx < x+200 && my > y && my < y+50 && s.lm.unlocked >= i){
                    s.state = "play";
                    if(i < 4) s.p.weaponAmount = 1;
                    else if(i < 7) s.p.weaponAmount = 2;
                    else s.p.weaponAmount = 3;

                    s.nowTime = System.nanoTime();
                    s.minutes = 0; s.seconds = 0;
                    s.lm.makeLevel(i);
                }
                y += 75;
                if(i == 5){
                    x = 450; y = 50;
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
