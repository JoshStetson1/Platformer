package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LevelManager {
    Random rand = new Random();
    int level = 1; int unlocked = 1;
    int q1, q2, q3;
    int portalX, portalY;
    boolean section1, section2, section3, section4;
    int[][] limits = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
    int startBlock, endBlock;
    BufferedImage levelImage;
    Screen s;
    
    int bestTime[][] = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
    
    public LevelManager(Screen s){
        this.s = s;
    }
    public void makeLevel(int level){
        s.l.removeAll();
        int[][] limits = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        this.limits = limits;
        section1 = false; section2 = false; section3 = false; section4 = false;
        
        this.level = level;
        findLevel();
    }
    public void findLevel(){
        levelImage = s.loadImage("Levels\\Level" + Integer.toString(level) + ".png");
        //levelImage = s.loadImage("Levels\\testLevel.png");
        q1 = (levelImage.getWidth()/4)*30; q2 = (levelImage.getWidth()/2)*30; q3 = (levelImage.getWidth()/4)*90;
        createLevel(levelImage);
    }
    public void createLevel(BufferedImage level){
        int w = level.getWidth();
        int h = level.getHeight();
        
        for(int xx = 0; xx < w; xx++){
            for(int yy = 0; yy < h; yy++){
                int pixel = level.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                //if(blue == 255) System.out.println(red + ", " + green + ", " + blue);
                
                //blocks
                if(red == 0 && green == 255 && blue == 0) s.l.b.add(new Block(s.grabImage(s.SpriteSheet, 1, 1, 30, 30), s, xx*30, yy*30, "grass"));
                if(red == 110 && green == 69 && blue == 0) s.l.b.add(new Block(s.grabImage(s.SpriteSheet, 1, 2, 30, 30), s, xx*30, yy*30, "grass"));
                if(red == 255 && green == 127 && blue == 0) s.l.b.add(new Block(s.grabImage(s.SpriteSheet, 2, 1, 30, 30), s, xx*30, yy*30, "lava"));
                if(red == 255 && green == 0 && blue == 0) s.l.b.add(new Block(s.grabImage(s.SpriteSheet, 2, 2, 30, 30), s, xx*30, yy*30, "lava"));
                //assets
                if(red == 119 && green == 119 && blue == 119) s.l.d.add(new Door(s, xx*30, yy*30));
                if(red == 191 && green == 77 && blue == 0) s.l.ch.add(new Chest(s, xx*30, yy*30));
                if(red == 0 && green == 127 && blue == 255){
                    portalX = xx*30; portalY = yy*30;
                }
                //entities
                if(red == 0 && green == 0 && blue == 0) s.l.e.add(new Enemy(s, "wander", xx*30, yy*30));
                if(red == 255 && green == 255 && blue == 0) s.l.e.add(new Enemy(s, "turret", xx*30, yy*30));
                if(red == 255 && green == 0 && blue == 255) s.l.e.add(new Enemy(s, "fly", xx*30, yy*30));
                if(red == 0 && green == 0 && blue == 255){
                    s.p.x = xx*30; s.p.y = yy*30;
                }
            }
        }
        //finding limits
        for(int i = 0; i < s.l.b.size(); i++){
            s.l.block = s.l.b.get(i);
            if(!section1)
            {
                if(s.l.block.x > q1){
                    limits[0][1] = i; section1 = true;
                }
            }
            if(!section2)
            {
                if(s.l.block.x > q1 && limits[1][0] == 0) limits[1][0] = i;
                if(s.l.block.x > q2){
                    limits[1][1] = i; section2 = true;
                }
            }
            if(!section3)
            {
                if(s.l.block.x > q2 && limits[2][0] == 0) limits[2][0] = i;
                if(s.l.block.x > q3){
                    limits[2][1] = i; section3 = true;
                }
            }
            if(!section4)
            {
                if(s.l.block.x > q3 && limits[3][0] == 0) limits[3][0] = i;
                limits[3][1] = s.l.b.size();
            }
        }
    }
    public void checkLimits(){
        if(s.p.x+15-s.getWidth()/2-30 > q3) startBlock = limits[3][0];
        else if(s.p.x+15-s.getWidth()/2-30 > q2) startBlock = limits[2][0];
        else if(s.p.x+15-s.getWidth()/2-30 > q1) startBlock = limits[1][0];
        else if(s.p.x+15-s.getWidth()/2-30 < q1){
            startBlock = 0;
        }
        
        if(s.p.x+15+s.getWidth()/2 > q3) endBlock = limits[3][1];
        else if(s.p.x+15+s.getWidth()/2 > q2) endBlock = limits[2][1];
        else if(s.p.x+15+s.getWidth()/2 > q1) endBlock = limits[1][1];
        else endBlock = limits[0][1];
        //System.out.println(startBlock + " " + endBlock);
        if(levelImage.getWidth() <= 100){
            startBlock = 0;
            endBlock = s.l.b.size();
        }
    }
    public void paint(Graphics g){
        boolean canPass = false;
        Rectangle portal = new Rectangle(portalX+15, portalY, 1, 60);
        if(level == 3){
            if(s.p.weaponAmount == 2) canPass = true;
        } else if(level == 6){
            if(s.p.weaponAmount == 3) canPass = true;
        } else canPass = true;
        
        if(canPass){
            if(portal.intersects(s.p.player()) && !s.p.win){
                s.p.win = true;
                s.PlaySound(s.finish);
            }
            g.drawImage(s.grabImage(s.SpriteSheet, 4, 1, 30, 60), portalX, portalY, s);
        }
    }
}