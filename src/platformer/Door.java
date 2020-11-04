package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Door {
    BufferedImage door;
    Screen s;
    int x, y;
    int fade = 255;
    boolean needMore;
    
    public Door(Screen s, int x, int y){
        this.s = s;
        this.x = x;
        this.y = y;
        door = s.grabImage(s.SpriteSheet, 3, 1, 30, 60);
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g.drawImage(door, x, y, s);
        needMoreCash(g2);
    }
    public void needMoreCash(Graphics2D g2){
        if(needMore){
            g2.setColor(new Color(255, 0, 0, fade));
            g2.setFont(new Font("arial", Font.BOLD, 20));
            g2.drawString("10¢", x-35, y+25);
            fade-=2;
            if(fade <= 0) needMore = false;
        }
    }
    public void open(int coins){
        s.PlaySound(s.open);
        s.p.money -= coins;
        s.l.d.remove(this);
    }
    public Rectangle door(){
        return new Rectangle(x, y, 30, 60);
    }
}
