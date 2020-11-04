package platformer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Screen extends JPanel implements ActionListener, KeyListener, MouseListener{
    Timer t = new Timer(10, this);
    SaveFile s = new SaveFile(this);
    MainMenu m = new MainMenu(this);
    List l = new List(this);
    LevelManager lm = new LevelManager(this);
    Player p;
    
    int windowX, windowY, screenX;
    int frames; int fps = 70;
    long nowTime;
    boolean use, press, pause;
    int seconds, minutes;
    
    Image sky = null;
    BufferedImage SpriteSheet = null;
    
    File jump = new File("Sounds\\jump4.wav");
    File coinPickup = new File("Sounds\\coinPickup.wav");
    File die = new File("Sounds\\die.wav");
    File sword = new File("Sounds\\sword.wav");
    File shoot = new File("Sounds\\shot.wav");
    File drop = new File("Sounds\\drop.wav");
    File finish = new File("Sounds\\finish.wav");
    File flame = new File("Sounds\\flame.wav");
    File thrown = new File("Sounds\\throw.wav");
    File open = new File("Sounds\\open.wav");
    
    String state = "menu";
    
    public Screen(int x, int y){
        init(x, y);
        t.start();
    }
    public void init(int x, int y){
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        sky = loadImage("paint\\background.png").getScaledInstance(750, 450, java.awt.Image.SCALE_SMOOTH);
        SpriteSheet = loadImage("paint\\SpriteSheet.png");
        
        windowX = x; windowY = y;
        p = new Player(this);
        s.read();
    }
    public void onScreen(Graphics2D g2){
        frames ++;
        if(System.nanoTime() > nowTime + 1000000000){
            fps = frames;
            frames = 0;
            nowTime = System.nanoTime();
            
            if(!pause && !p.win) seconds++;
            if(seconds == 60){
                minutes += 1;
                seconds = 0;
            }
        }
        g2.setColor(Color.black);
        Font f = new Font("arial", Font.BOLD, 18);
        FontMetrics metrics = g2.getFontMetrics(f);
        int length = metrics.stringWidth("FPS: " + Integer.toString(fps));
        g2.setFont(f);
        g2.drawString("FPS: " + Integer.toString(fps), getWidth()/2-length-50, 40);
        
        if(seconds >= 10) g2.drawString(minutes + ":" + seconds, getWidth()/2+50, 40);
        else g2.drawString(minutes + ":0" + seconds, getWidth()/2+50, 40);
        
        g2.fill(new Rectangle(getWidth()/2-6, 25, 3, 15));
        g2.fill(new Rectangle(getWidth()/2+3, 25, 3, 15));
        g2.draw(new Ellipse2D.Double(getWidth()/2-13, 20, 25, 25));
        if(pause){
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            g2.setColor(Color.white);
            
            f = new Font("arial", Font.PLAIN, 40);
            metrics = g2.getFontMetrics(f);
            length = metrics.stringWidth("Play");
            g2.setFont(f);
            g2.drawString("Play", getWidth()/2 - length/2, 200);
            //restart
            length = metrics.stringWidth("Restart");
            g2.setFont(f);
            g2.drawString("Restart", getWidth()/2 - length/2, 275);
            //mainmenu
            length = metrics.stringWidth("MainMenu");
            g2.setFont(f);
            g2.drawString("MainMenu", getWidth()/2 - length/2, 350);
        }
    }
    public void actionPerformed(ActionEvent e){
        if(state.equals("play") && !pause){
            if(!p.dead && !p.win) p.tick();
            l.tick();
        }
        repaint();
    }
    public void paint(Graphics g){
        g.clearRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        if(state.equals("menu")){
            m.paint(g);
        } else if(state.equals("play")){
            g.drawImage(sky, 0, 0, this);
            int x = 0;
            if(p.x+15 > getWidth()/2) x = -(p.x+15)+getWidth()/2;
            if(p.x+15 > l.maxBlockX()+30-getWidth()/2) x = -l.maxBlockX()+getWidth()-30;
            screenX = x;

            g2.translate(x, 0);
            lm.paint(g);
            l.render(g);
            p.paint(g);
            g2.translate(-x, 0);

            p.stuffOnScreen(g);
            p.drawWin(g);
            
            onScreen(g2);
        }
    }
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(state.equals("play")){
            if(key == KeyEvent.VK_E) use = true;
            if(key == KeyEvent.VK_A) p.dx = -3;
            if(key == KeyEvent.VK_D) p.dx = 3;
            if(key == KeyEvent.VK_SPACE){
                if(!p.falling){
                    p.jumping = true;
                    p.dy = -6;
                    PlaySound(jump);
                }
            }
            if(key == KeyEvent.VK_Q){
                if(p.selectWeapon == p.weaponAmount) p.selectWeapon = 1;
                else p.selectWeapon++;
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(state.equals("play")){
            if(key == KeyEvent.VK_E) use = false;
            if(key == KeyEvent.VK_A || key == KeyEvent.VK_D) p.dx = 0;
            if(key == KeyEvent.VK_SPACE){
                if(p.dy < 0){
                    p.dy = 0;
                }
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
        if(state.equals("menu")){
            m.mouseClicked(e);
        } else if(state.equals("play")){
            if(p.win){
                p.mouseClicked(e);
            }else if(pause){
                if(e.getX() > 250 && e.getX() < 500){
                    if(e.getY() > 150 && e.getY() < 200) pause = false;
                    if(e.getY() > 225 && e.getY() < 275) restart(true, false);
                    if(e.getY() > 300 && e.getY() < 350){
                        state = "menu";
                        m.state = "main";
                        pause = false;
                    }
                }
            }else if(e.getX() > getWidth()/2-15 && e.getX() < getWidth()/2+15 && e.getY() > 17 && e.getY() < 40) pause = true;
            else{//weapons
                if(p.selectWeapon == 2 && p.shoot && p.money > 0){
                    p.shoot = false;
                    PlaySound(shoot);
                    p.money--;
                    p.shootTime = System.nanoTime();
                    if(e.getX() >= p.x+screenX+15) l.B.add(new Bullet(this, grabImage(SpriteSheet, 3, 3, 30, 30), "player", p.x+30, p.y, 10, 0));
                    if(e.getX() < p.x+screenX+15) l.B.add(new Bullet(this, flipImage(grabImage(SpriteSheet, 3, 3, 30, 30)), "player", p.x-30, p.y, -10, 0));
                } else if(p.selectWeapon == 3 && !p.thrown){
                    p.thrown = true;
                    PlaySound(thrown);
                    p.throwBoomerang(e.getX()-screenX, e.getY());
                }
            }
        }
    }
    public void mousePressed(MouseEvent e) {
        if(state.equals("play") && !pause && !(e.getX() > getWidth()/2-15 && e.getX() < getWidth()/2+15 && e.getY() > 17 && e.getY() < 40)){
            if(p.selectWeapon == 1 && !p.win){
                p.swing(e.getX());//swing sword
                PlaySound(sword);
            }
        }
    }
    public void restart(boolean restartTimer, boolean next){
        p.shoot = true; p.shootTime = System.nanoTime(); p.selectWeapon = 1; p.thrown = false; p.comeBack = false; p.win = false; p.dy = 0; p.money = 0;
        pause = false;
        if(restartTimer){
            minutes = 0; seconds = 0; nowTime = System.nanoTime();
        }
        lm.makeLevel(lm.level);
    }
    
    //graphics
    public BufferedImage loadImage(String path){
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(new FileInputStream(path));
        } catch (IOException ex) {
            Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tempImage;
    }
    public Image loadSky(String path){
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(new FileInputStream(path));
        } catch (IOException ex) {
            Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tempImage;
    }
    public BufferedImage grabImage(BufferedImage image, int col, int row, int width, int height){
        BufferedImage img = image.getSubimage((col*30)-30, (row*30)-30, width, height);
        return img;
    }
    public BufferedImage flipImage(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                BufferedImage pixel = new BufferedImage(1, 1, 1);
                if(pixel.getTransparency() > 0) flipped.setRGB((width-1)-x, y, img.getRGB(x, y));
            }
        }
        return flipped;
    }
    public void rotateImage(BufferedImage img, int degree, int x, int y, Graphics2D g2){
        int w = img.getWidth();
        int h = img.getHeight();
        g2.rotate(Math.toRadians(degree), w/2+x, h/2+y);
        g2.drawImage(img, x, y, this);
        g2.rotate(Math.toRadians(-degree), w/2+x, h/2+y);
    }
    public void PlaySound(File sound){
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clip.start();
        } catch(Exception e){}
    }
    
    
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    
}
