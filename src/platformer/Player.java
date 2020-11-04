package platformer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class Player implements MouseListener{
    BufferedImage front, left, right, sword, gun, boom;
    Image coin, heart, swordGraphic, gunGraphic, boomGraphic;
    int x, y, dx, swordX, money, mx;
    double dy;
    double grav = 0.2;
    boolean falling = true;
    boolean dead, win, swing, jumping, shoot;
    
    long swingTime, shootTime;
    int selectWeapon = 1;//sword, gun
    int weaponAmount = 1;
    
    int boomX, boomY, boomDX, boomDY, rotate;
    boolean comeBack, thrown;
    
    Screen s;
    
    public Player(Screen s){
        this.s = s;
        front = s.grabImage(s.SpriteSheet, 1, 5, 30, 30);
        right = s.grabImage(s.SpriteSheet, 2, 5, 30, 30);
        left = s.flipImage(right);
        boom = s.grabImage(s.SpriteSheet, 5, 5, 30, 30);
        coin = s.grabImage(s.SpriteSheet, 2, 3, 14, 14).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
        swordGraphic = s.grabImage(s.SpriteSheet, 3, 5, 30, 30).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
        gunGraphic = s.grabImage(s.SpriteSheet, 4, 5, 30, 30).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
        boomGraphic = s.grabImage(s.SpriteSheet, 5, 5, 30, 30).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
    }
    public void tick(){
        x += dx;
        y += dy;
        findMouse();
        sword();
        boomerangTick();
        JumpGrav();
    }
    public void JumpGrav(){
        if(falling || jumping){
            if(dy < 8){
                dy += grav;
            } else{
                dy = 8;
            }
        }
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        checkDead();
        if(!dead && !win){
            if(dx > 0) g.drawImage(right, x, y, s);
            else if(dx < 0) g.drawImage(left, x, y, s);
            else g.drawImage(front, x, y, s);
            
            g.drawImage(sword, x+swordX, y, s);
            if(selectWeapon == 2){
                if(mx >= x) g.drawImage(s.grabImage(s.SpriteSheet, 4, 5, 30, 30), x+30, y, s);
                if(mx < x) g.drawImage(s.flipImage(s.grabImage(s.SpriteSheet, 4, 5, 30, 30)), x-30, y, s);
            }
            if(selectWeapon == 3){
                if(thrown){
                    s.rotateImage(boom, rotate, boomX, boomY, g2);
                    rotate += 10;
                } else{
                    if(mx >= x) g2.drawImage(boom, x+15, y, s);
                    if(mx < x) g2.drawImage(s.flipImage(boom), x-15, y, s);
                }
            }
            waitShoot(g2);
        }
    }
    public void checkDead(){
        if(y > 450) dead = true;
        if(dead){
            s.PlaySound(s.die);
            try{
                synchronized (s){ s.wait(100);} dead = false;
            } catch(Exception e){}
            money = 0;
            s.restart(false, false);
        }
    }
    public void drawWin(Graphics g){
        if(win){
            if(s.lm.level == s.lm.unlocked) s.lm.unlocked = s.lm.level+1;
            //also save time
            if(s.minutes <= s.lm.bestTime[s.lm.level-1][0] && s.seconds < s.lm.bestTime[s.lm.level-1][1] || (s.lm.bestTime[s.lm.level-1][0] == 0 && s.lm.bestTime[s.lm.level-1][1] == 0)){
                s.lm.bestTime[s.lm.level-1][0] = s.minutes;
                s.lm.bestTime[s.lm.level-1][1] = s.seconds;
            }
            s.s.save();
            
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, s.getWidth(), s.getHeight());
            String clock;
            
            if(s.seconds >= 10) clock = s.minutes + ":" + s.seconds;
            else clock = s.minutes + ":0" + s.seconds;
            g.setColor(new Color(255, 255, 255));
            Font f = new Font("arial", Font.PLAIN, 40);
            FontMetrics metrics = g.getFontMetrics(f);
            int length = metrics.stringWidth(clock);
            g.setFont(f);
            g.drawString(clock, s.getWidth()/2 - length/2, 175);
            
            f = new Font("arial", Font.PLAIN, 50);
            metrics = g.getFontMetrics(f);
            length = metrics.stringWidth("Level " + (s.lm.level) + " Complete");
            g.setFont(f);
            g.drawString("Level " + (s.lm.level) + " Complete", s.getWidth()/2 - length/2, 100);
            if(s.lm.level != 10){
                //next
                length = metrics.stringWidth("Next");
                g.setFont(f);
                g.drawString("Next", s.getWidth()/2 - length/2, 250);
            }
            //restart
                length = metrics.stringWidth("Restart");
                g.setFont(f);
                g.drawString("Restart", s.getWidth()/2 - length/2, 325);
                //menu
                length = metrics.stringWidth("MainMenu");
                g.setFont(f);
                g.drawString("MainMenu", s.getWidth()/2 - length/2, 400);
        }
    }
    public void stuffOnScreen(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(0, 0, 0, 100));
        FontMetrics metrics = g2.getFontMetrics(new Font("arial", Font.BOLD, 40));
        g2.fill(new Rectangle(0, 0, 50+metrics.stringWidth(Integer.toString(money)), 38));
        g.drawImage(coin, 0, 0, s);
        g2.setColor(Color.yellow);
        Font f = new Font("arial", Font.BOLD, 40);
        g2.setFont(f);
        g2.drawString(Integer.toString(money), 45, 35);
        
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fill(new Rectangle(s.getWidth()-42*weaponAmount, 0, 42*weaponAmount+1, 42));
        
        g.drawImage(swordGraphic, s.getWidth()-41, 0, s);
        if(weaponAmount > 1) g.drawImage(gunGraphic, s.getWidth()-83, 0, s);
        if(weaponAmount > 2) g.drawImage(boomGraphic, s.getWidth()-126, 1, s);
        
        g2.setColor(Color.white);
        for(int i = 0; i < weaponAmount+1; i++){
            g2.draw(new Rectangle(s.getWidth()-42*i, 0, 41, 42));
        }
        
        g2.setColor(Color.red);
        g2.draw(new Rectangle(s.getWidth()-42*selectWeapon, 0, 41, 42));
        g2.fill(new Rectangle(s.getWidth()-42*selectWeapon, 42, 42, 5));
        
        int imageLength = s.lm.levelImage.getWidth()*30;
        int length = imageLength/15;
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fill(new Rectangle(s.getWidth()/2 - length/2, 5, length, 10));
        g2.setColor(Color.white);
        g2.draw(new Rectangle(s.getWidth()/2 - length/2, 5, length, 10));
        
        int playerPOS = (x-45)/15+s.getWidth()/2 - length/2;
        g2.setColor(Color.red);
        g2.fill(new Rectangle(playerPOS, 3, 15, 15));
    }
    //sword
    public void sword(){
        if(swing){
            if(System.nanoTime() > swingTime + 100000000){
                swing = false; sword = null;
            }
        }
    }
    public void swing(int mx){
        int x;
        swing = true;
        swingTime = System.nanoTime();
        if(this.x+15 < 0+s.getWidth()/2) x = 0;
        else if(this.x+15 > s.l.maxBlockX()-s.getWidth()/2) x = s.l.maxBlockX()-s.getWidth();
        else x = this.x+15-s.getWidth()/2;
        if(mx+x >= this.x+15){
            swordX = 30;
            sword = s.grabImage(s.SpriteSheet, 3, 5, 30, 30);
        } else{
            swordX = -30;
            sword = s.flipImage(s.grabImage(s.SpriteSheet, 3, 5, 30, 30));
        }
    }
    public Rectangle swordBounds(){
        return new Rectangle(x+swordX, y, 30, 30);
    }
    //gun
    public void findMouse(){
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        mx = b.x-s.windowX-10-s.screenX;
    }
    public void waitShoot(Graphics2D g2){
        if(System.nanoTime() > shootTime + 2000000000 && !shoot){
            shoot = true;
        } else if(!shoot){
            long wait = (shootTime+2000000000-System.nanoTime())/10000000;
            g2.setColor(Color.ORANGE);
            if(selectWeapon == 2) g2.fill(new Rectangle(x, y-10, (int)(wait/6.67), 5));
        }
    }
    //boomerang
    public void throwBoomerang(int mx, int my){
        double angle = Math.atan2(my - boomY, mx - boomX);
        if(mx >= x && angle > -0.6) angle = -0.6;//right limit
        if(mx < x && angle < -2.6 || angle > 1.5) angle = -2.6;//left limit
        boomDX = (int) ((7) * Math.cos(angle));
        boomDY = (int) ((7) * Math.sin(angle));
    }
    public void boomerangTick(){
        if(thrown){
            boomX += boomDX;
            boomY += boomDY;
        } else{
            boomX = x; boomY = y; rotate = 0;
        }
        if(comeBack){
            double angle = Math.atan2(y - boomY, x - boomX);
            boomDX = (int) ((7) * Math.cos(angle));
            boomDY = (int) ((7) * Math.sin(angle));
            
            if(boomerang().intersects(player()) && comeBack){
                thrown = false; comeBack = false;
                boomX = x; boomY = y;
            }
        }else{
            if(boomY < 0) comeBack = true;
        }
    }
    public Rectangle boomerang(){
        return new Rectangle(boomX, boomY, 25, 25);
    }
    
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(win){
            if(x > 250 && x < 500){
                if(y < 250 && y > 200 && s.lm.level < 10){
                    s.lm.level++;
                    s.restart(true, true);
                }
                if(y < 325 && y > 275){
                    s.restart(true, false);
                }
                if(y < 400 && y > 350){
                    s.state = "menu";
                    s.m.state = "main";
                    win = false;
                    s.lm.level++;
                }
            }
        }
    }
    
    //bounds
    public Rectangle player(){
        return new Rectangle(x, y, 30, 30);
    }
    public Rectangle top(){
        return new Rectangle(x+5, y, 20, 5);
    }
    public Rectangle bottom(){
        return new Rectangle(x+5, y+25, 20, 5);
    }
    public Rectangle right(){
        return new Rectangle(x+25, y+10, 5, 10);
    }
    public Rectangle left(){
        return new Rectangle(x, y+10, 5, 10);
    }
    
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
