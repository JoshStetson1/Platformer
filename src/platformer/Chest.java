package platformer;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Chest {
    BufferedImage chest;
    int x, y;
    Screen s;
    
    public Chest(Screen s, int x, int y){
        this.s = s;
        this.x = x;
        this.y = y;
        chest = s.grabImage(s.SpriteSheet, 1, 3, 30, 30);
    }
    public void paint(Graphics g){
        g.drawImage(chest, x, y, s);
    }
    public Rectangle chest(){
        return new Rectangle(x, y, 30, 30);
    }
}
