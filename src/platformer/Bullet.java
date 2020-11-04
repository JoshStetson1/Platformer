package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet {
    BufferedImage bullet;
    int x, y, dx, dy;
    Screen s;
    String ID;
    
    public Bullet(Screen s, BufferedImage bullet, String ID, int x, int y, int dx, int dy){
        this.s = s;
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.bullet = bullet;
    }
    public void paint(Graphics g){
        x += dx;
        y += dy;
        g.drawImage(bullet, x, y, s);
    }
    public Rectangle bullet(){
        return new Rectangle(x, y+5, 30, 20);
    }
}
