package platformer;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.JOptionPane;

public class List {
    LinkedList<Block> b = new LinkedList<>();
    LinkedList<Enemy> e = new LinkedList<>();
    LinkedList<Coin> c = new LinkedList<>();
    LinkedList<Door> d = new LinkedList<>();
    LinkedList<Chest> ch = new LinkedList<>();
    LinkedList<Bullet> B = new LinkedList<>();
    Block block; Enemy enemy; Coin coin; Door door; Chest chest; Bullet bullet;
    Screen s;
    
    
    public List(Screen s){
        this.s = s;
    }
    public void tick(){
        for(int i = 0; i < e.size(); i++){
            enemy = e.get(i);
            if(s.p.x+15 < 0+s.getWidth()/2 && enemy.x < s.getWidth()) enemy.tick();
            else if(s.p.x+15 > maxBlockX()-s.getWidth()/2 && enemy.x > s.lm.levelImage.getWidth()*30-s.getWidth()) enemy.tick();
            else if(enemy.x < s.p.x+15+s.getWidth()/2 && enemy.x > s.p.x+15-s.getWidth()/2) enemy.tick();
        }
    }
    public void render(Graphics g){
        s.lm.checkLimits();
        for(int i = s.lm.startBlock; i < s.lm.endBlock; i++){
            block = b.get(i);
            block.paint(g);
        }
        for(int i = 0; i < ch.size(); i++){
            chest = ch.get(i);
            chest.paint(g);
        }
        for(int i = 0; i < e.size(); i++){
            enemy = e.get(i);
            enemy.paint(g);
        }
        for(int i = 0; i < B.size(); i++){
            bullet = B.get(i);
            bullet.paint(g);
        }
        for(int i = 0; i < c.size(); i++){
            coin = c.get(i);
            coin.paint(g);
        }
        for(int i = 0; i < d.size(); i++){
            door = d.get(i);
            door.paint(g);
        }
        collisions(g, s.lm.startBlock, s.lm.endBlock);
    }
    public void collisions(Graphics g, int startBlock, int endBlock){
        Graphics2D g2 = (Graphics2D)g;
        s.p.falling = true;
        for(int i = startBlock; i < endBlock; i++){////////////////////////////////////////////////////
            block = b.get(i);
            if(s.p.boomerang().intersects(block.block()) && s.p.thrown && !s.p.comeBack) s.p.comeBack = true;
            if(block.ID.equals("grass")){
                if(block.block().intersects(s.p.top())){
                    s.p.y = block.y+31;
                    s.p.falling = true;
                    s.p.dy = 0;
                }
                if(block.block().intersects(s.p.right())) s.p.x = block.x-31;
                if(block.block().intersects(s.p.left())) s.p.x = block.x+31;
                if(block.block().intersects(s.p.bottom())){
                    if(!s.p.jumping) s.p.dy = 1;
                    s.p.y = block.y-30;
                    s.p.falling = false;
                    s.p.jumping = false;
                }
            } else if(block.ID.equals("lava")){
                if(s.p.player().intersects(block.block())) s.p.dead = true;
            }
            for(int h = 0; h < B.size(); h++){
                bullet = B.get(h);
                if(bullet.x < b.get(startBlock).x || bullet.x > b.get(endBlock-1).x+30 || bullet.bullet().intersects(block.block()) || bullet.y > s.getWidth()) B.remove(bullet);
                if(bullet.ID.equals("enemy") && s.p.player().intersects(bullet.bullet())){
                    s.p.dead = true;
                    B.remove(bullet);
                }
                for(int j = 0; j < d.size(); j++){
                    door = d.get(j);
                    if(bullet.bullet().intersects(door.door())) B.remove(bullet);
                }
            }
        }
        for(int i = 0; i < e.size(); i++){////////////////////////////////////////////////////////////////
            enemy = e.get(i);
            if(s.p.player().intersects(enemy.enemy())) s.p.dead = true;
            if(enemy.enemy().intersects(s.p.swordBounds()) && s.p.swing) enemy.die();
            if(s.p.boomerang().intersects(enemy.enemy()) && s.p.thrown && !s.p.comeBack){
                enemy.die();
                s.p.comeBack = true;
            }
            for(int h = 0; h < B.size(); h++){
                bullet = B.get(h);
                if(bullet.ID.equals("player") && enemy.enemy().intersects(bullet.bullet())){
                    B.remove(bullet);
                    enemy.die();
                }
            }
            
            boolean stop = true;
            for(int h = startBlock; h < endBlock; h++){
                block = b.get(h);
                if(enemy.ID.equals("fly")){
                    Rectangle top = new Rectangle(enemy.x+5, (int) enemy.y, 20, 5);
                    Rectangle bottom = new Rectangle(enemy.x+5, (int) (enemy.y+25), 20, 5);
                    if(top.intersects(block.block())) enemy.y = block.y+30;
                    if(bottom.intersects(block.block())) enemy.y = block.y-30;
                    if(enemy.left().intersects(block.block())) enemy.x = block.x+30;
                    if(enemy.right().intersects(block.block())) enemy.x = block.x-30;
                }
                if(enemy.ID.equals("wander") && enemy.dx == -1 && enemy.leftFoot().intersects(block.block())) stop = false;
                else if(enemy.ID.equals("wander") && enemy.dx == 1 && enemy.rightFoot().intersects(block.block())) stop = false;
            }
            if(enemy.ID.equals("fly")){
                for(int h = 0; h < d.size(); h++){
                    door = d.get(h);
                    if(enemy.ID.equals("fly") && enemy.left().intersects(door.door())) enemy.x = door.x+31;
                    if(enemy.ID.equals("fly") && enemy.right().intersects(door.door())) enemy.x = door.x-31;
                }
            }else if(enemy.ID.equals("wander") && !stop){
                for(int h = startBlock; h < endBlock; h++){
                    block = b.get(h);
                    if(enemy.dx == -1 && enemy.left().intersects(block.block())) stop = true;
                    else if(enemy.dx == 1 && enemy.right().intersects(block.block())) stop = true;
                    
                    if(enemy.dx == -1 && enemy.leftFoot().intersects(block.block()) && block.ID.equals("lava")) stop = true;
                    else if(enemy.dx == 1 && enemy.rightFoot().intersects(block.block()) && block.ID.equals("lava")) stop = true;
                }
                for(int h = 0; h < d.size(); h++){
                    door = d.get(h);
                    if(enemy.dx == -1 && enemy.left().intersects(door.door())) stop = true;
                    else if(enemy.dx == 1 && enemy.right().intersects(door.door())) stop = true;
                }
            }
            if(enemy.ID.equals("wander") && stop){
                enemy.stop = true;
                enemy.time = System.nanoTime();
                enemy.dx = -enemy.dx;
            }
        }
        for(int i = 0; i < d.size(); i++){/////////////////////////////////////////////////////////////////////////
            door = d.get(i);
            if(door.door().intersects(s.p.right())) s.p.x = door.x-31;
            if(door.door().intersects(s.p.left())) s.p.x = door.x+31;
            //open
            if(s.p.x >= door.x-50 && s.p.x <= door.x+60 && s.p.y > door.y && s.p.y < door.y+60){
                if(s.use){
                    if(s.p.money >= 10) door.open(10);
                    else{
                        door.needMore = true;
                        door.fade = 255;
                    }
                } else if(!door.needMore){
                    g2.setFont(new Font("arial", Font.BOLD, 25));
                    g2.setColor(new Color(255, 150, 0));
                    g2.drawString("'E'", door.x-30, door.y+25);
                }
            }
            
        }
        for(int i = 0; i < ch.size(); i++){/////////////////////////////////////////////////////////////////
            chest = ch.get(i);
            if(s.p.x > chest.x-60 && s.p.x < chest.x+60 && s.p.y == chest.y){
                if(s.use){
                    if(s.lm.level == 3 && s.p.weaponAmount == 1){
                        s.PlaySound(s.chestOpen);
                        s.p.weaponAmount = 2;
                        s.p.selectWeapon = 2;
                    }
                    if(s.lm.level == 6 & s.p.weaponAmount == 2){
                        s.PlaySound(s.chestOpen);
                        s.p.weaponAmount = 3;
                        s.p.selectWeapon = 3;
                    }
                } else{
                    g2.setFont(new Font("arial", Font.BOLD, 25));
                    g2.setColor(new Color(255, 150, 0));
                    g2.drawString("'E'", chest.x, chest.y-10);
                }
            }
        }
        for(int i = 0; i < c.size(); i++){/////////////////////////////////////////////////////////////////
            coin = c.get(i);
            if(coin.inBlock){
                Rectangle playerFeild = new Rectangle(s.p.x-30, s.p.y-30, 90, 90);
                if(coin.coin().intersects(playerFeild)){
                    c.remove(coin);
                    s.p.money+=1;
                    s.PlaySound(s.coinPickup);
                }
            } else if(s.p.player().intersects(coin.coin())){
                c.remove(coin);
                s.p.money+=1;
                s.PlaySound(s.coinPickup);
            }
        }
    }
    public int maxBlockX(){
        return b.get(b.size()-1).x;
    }
    public void removeAll(){
        while(!b.isEmpty() || !ch.isEmpty() || !e.isEmpty() || !c.isEmpty() || !d.isEmpty() || !B.isEmpty()){//have to do this because somethimes it does delete them all
            for(int i = 0; i < b.size(); i++){
                block = b.get(i);
                b.remove(block);
            }
            for(int i = 0; i < ch.size(); i++){
                chest = ch.get(i);
                ch.remove(chest);
            }
            for(int i = 0; i < e.size(); i++){
                enemy = e.get(i);
                e.remove(enemy);
            }
            for(int i = 0; i < c.size(); i++){
                coin = c.get(i);
                c.remove(coin);
            }
            for(int i = 0; i < d.size(); i++){
                door = d.get(i);
                d.remove(door);
            }
            for(int i = 0; i < B.size(); i++){
                bullet = B.get(i);
                B.remove(bullet);
            }
        }
    }
}
