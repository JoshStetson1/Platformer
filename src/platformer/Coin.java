package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Coin {
    BufferedImage coin;
    boolean grounded, inBlock;
    double y, dy;
    int x;
    Screen s;
    
    public Coin(Screen s, int x, int y){
        this.s = s;
        this.x = x;
        this.y = y;
        coin = s.grabImage(s.SpriteSheet, 2, 3, 14, 14);
    }
    public void paint(Graphics g){
        groundCheck();
        if(!grounded){
            y += dy;
            if(dy < 4) dy += 0.3;
        }
        g.drawImage(coin, x, (int) y, s);
    }
    public void groundCheck(){
        for(int i = s.lm.startBlock; i < s.lm.endBlock; i++){
            if(s.l.b.get(i).block().intersects(coin()) && s.l.b.get(i).ID.equals("grass")) grounded = true;
            else if(s.l.b.get(i).block().intersects(coin()) && s.l.b.get(i).ID.equals("lava")) s.l.c.remove(this);
            
            if(s.l.b.get(i).block().intersects(coinTop()) && s.l.b.get(i).ID.equals("grass")) inBlock = true;
        }
    }
    public Rectangle coin(){
        return new Rectangle(x, (int) y, 14, 14);
    }
    public Rectangle coinTop(){
        return new Rectangle(x, (int) y, 14, 5);
    }
}
