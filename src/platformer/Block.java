package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Block {
    BufferedImage block;
    Screen s;
    int x, y;
    String ID;
    
    public Block(BufferedImage type, Screen s, int x, int y, String ID){
        this.x = x;
        this.y = y;
        this.s = s;
        this.ID = ID;
        block = type;
    }
    public void paint(Graphics g){
        g.drawImage(block, x, y, s);
    }
    public Rectangle block(){
        if(ID.equals("lava")) return new Rectangle(x, y+10, 30, 20);
        else return new Rectangle(x, y, 30, 30);
    }
}
