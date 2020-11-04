package platformer;
import java.io.File;
import java.util.*;

public class SaveFile {
    private Formatter f;
    private Scanner sc;
    Screen s;
    
    public SaveFile(Screen s){
        this.s = s;
    }
    public void save(){
        try{
            f = new Formatter("SaveFile.txt");
        } catch(Exception e){}
        //stats
        f.format("%s\n", Integer.toString(s.lm.unlocked));//what level you are on
        
        for(int i = 0; i < 10; i++){
            f.format("%s ", Integer.toString(s.lm.bestTime[i][0]));
            f.format("%s\n", Integer.toString(s.lm.bestTime[i][1]));
        }
        
        f.close();
    }
    public void read(){
        try{
            sc = new Scanner(new File("SaveFile.txt"));
        }catch(Exception e){}
        //read it
        s.lm.unlocked = Integer.parseInt(sc.next());
        
        for(int i = 0; i < 9; i++){
            s.lm.bestTime[i][0] = Integer.parseInt(sc.next());
            s.lm.bestTime[i][1] = Integer.parseInt(sc.next());
        }
        
        sc.close();
    }
    
}
