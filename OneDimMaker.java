package isteg;
import java.awt.Point;

public class OneDimMaker{
    //Use for accessing two dimensional array using just one index

    public int totalSize;
    public int w,h;
    public OneDimMaker(int w,int h){
        totalSize = w*h;
        this.h=h;
        this.w=w;
    }
    
    public Point get(int i){
        if(i>=totalSize)
            return null;
        int x,y;
        y = i/w;
        if(y==0)
           x=i;
        else
            x= i%(w*y);
        
        return new Point(x,y);
    }
}
