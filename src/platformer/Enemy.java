package platformer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy {
    BufferedImage front, left, right;
    Random rand = new Random();
    int x, dx, coins;
    double y, dy, wait;
    double grav = 0.2;
    boolean jumping = false;
    boolean falling = true;
    boolean stop, grounded;
    long time, shootTime;
    Screen s;
    String ID;
    
    public Enemy(Screen s, String ID, int x, int y){
        this.s = s;
        this.ID = ID;
        this.x = x;
        this.y = y;
        dx = -1;
        coins = rand.nextInt(2)+2;
        if(ID.equals("wander")){
            front = s.grabImage(s.SpriteSheet, 1, 4, 30, 30);
            right = s.grabImage(s.SpriteSheet, 2, 4, 30, 30);
            left = s.flipImage(right);
        }else if(ID.equals("turret")){
            wait = 2;
        }else if(ID.equals("fly")){
            front = s.grabImage(s.SpriteSheet, 4, 3, 30, 30);
            wait = 1;
        }
        time = System.nanoTime();
        shootTime = System.nanoTime();
    }
    public void tick(){
        if(ID.equals("wander")){
            if(!stop){
                x += dx;
            } else stop();
        } else if(ID.equals("turret")){
            shootBullets();
        } else if(ID.equals("fly")){
            y += dy;
            followPlayer();
            shootBullets();
        }
    }
    public void paint(Graphics g){
        if(ID.equals("wander")){
            if(stop) g.drawImage(front, x, (int) y, s);
            else if(dx > 0) g.drawImage(right, x, (int) y, s);
            else if(dx < 0) g.drawImage(left, x, (int) y, s);
        } else if(ID.equals("turret")){
            g.drawImage(front, x, (int) y, s);
        } else if(ID.equals("fly")){
            g.drawImage(front, x, (int) y, s);
        }
    }
    public void die(){
        for(int i = 0; i < coins; i++){
            int dropX = rand.nextInt(30)+x-30;
            s.l.c.add(new Coin(s, dropX, (int) y));
        }
        s.l.e.remove(this);
    }
    public void stop(){
        if(System.nanoTime() > time + 1000000000) stop = false;
    }
    
    public void shootBullets(){
        if(System.nanoTime() > shootTime + 300000000 && s.p.x+15 <= x && ID.equals("turret")) front = s.grabImage(s.SpriteSheet, 3, 4, 30, 30);//right
        if(System.nanoTime() > shootTime + 300000000 && s.p.x+15 > x && ID.equals("turret")) front = s.flipImage(s.grabImage(s.SpriteSheet, 3, 4, 30, 30));//left
        
        if(System.nanoTime() > shootTime + 1000000000*wait){
            if(ID.equals("turret")){
                s.PlaySound(s.flame);
                if(s.p.x+15 <= x){
                    s.l.B.add(new Bullet(s, s.grabImage(s.SpriteSheet, 5, 4, 30, 30), "enemy", x, (int) y, -3, 0));
                    front = s.grabImage(s.SpriteSheet, 4, 4, 30, 30);
                }
                if(s.p.x+15 > x){
                    s.l.B.add(new Bullet(s, s.flipImage(s.grabImage(s.SpriteSheet, 5, 4, 30, 30)), "enemy", x, (int) y, 3, 0));
                    front = s.flipImage(s.grabImage(s.SpriteSheet, 4, 4, 30, 30));
                }
            } else if(ID.equals("fly")){
                s.PlaySound(s.drop);
                s.l.B.add(new Bullet(s, s.grabImage(s.SpriteSheet, 5, 3, 30, 30), "enemy", x, (int) y, 0, 4));
            }
            shootTime = System.nanoTime();
        }
    }
    public void followPlayer(){
        if(s.p.x > x) x++;
        if(s.p.x < x) x--;
        
        if(s.p.y > y) dy = 0.1;
        if(s.p.y < y) dy = -0.1;
    }
    
    public Rectangle enemy(){
        return new Rectangle(x, (int) y, 30, 30);
    }
    public Rectangle right(){
        return new Rectangle(x+25, (int) (y+10), 5, 10);
    }
    public Rectangle left(){
        return new Rectangle(x, (int) (y+10), 5, 10);
    }
    public Rectangle leftFoot(){
        return new Rectangle(x-1, (int) (y+30), 1, 10);
    }
    public Rectangle rightFoot(){
        return new Rectangle(x+30, (int) (y+30), 1, 10);
    }
}

