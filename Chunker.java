/*
This class chunks the input into specified binary length. 
Example-1: 
we have inSize = 8 and outSize = 6 (out<=in) and the input is 0b1101101001
by get() method we will get 0b011010 and 0b010000 [11 at the beginning will be discarded cause inSize = 8

Example-2:
we have inSize = 6 and outSize = 8 (out>in) and the input is 0b110010 and 0b011001
by get() method we will get 0b11001001 [1001 of 2nd input will be added prior to next input]
*/
package isteg;
import java.util.ArrayList;

public class Chunker {
    private final int inSize,outSize,maxPossible;
    private ArrayList<Integer> list;
    private int rem,remSize,currentPos;
    private boolean wasExtra;
    
    public Chunker(int inSize,int outSize){
        this.outSize = outSize;
        this.inSize = inSize;
        this.maxPossible = getMaxPossible(); // get max possible value of given outSize, Example: 0b111 for 3
        init(); //initialize variables
    }
    
    
    private int getMaxPossible(){
        String s="0";
        for(int i=0;i<outSize;i++)
            s = s+"1";
        return Integer.parseInt(s, 2);
    }
    
    private void init() {
        this.rem=0; //remaining of previous chunk
        this.remSize=0; // length of rem
        this.wasExtra=false;
        this.currentPos = 0;
        this.list=new ArrayList<>();
    }
    
    public void add(int x){
        if(wasExtra)
            list.remove(list.size()-1); // We put extra 0's at the end of previous chunk
        rem = rem << inSize | x;
            remSize+=inSize;
        while(remSize >= outSize){
                remSize-=outSize; // new remSize for discarding the value in next line
                list.add((rem>>remSize));
                rem = rem << (Integer.SIZE-remSize) >> (Integer.SIZE-remSize); 
        }
        
        if(remSize>0 && outSize<inSize){
            list.add((rem<<(outSize-remSize)));
            wasExtra = true;
        }
        else
            wasExtra  = false;
    }
    
    public void add(byte[] bytes){
        for(byte b:bytes) {
        	add(b);
        }
    }
    
    public void add(int[] ints){
        for(int i:ints){
            add(i);
        }
    }
    
    public void add(byte b){
        add(b & 0xFF);
    }
    
    public boolean hasNext(){
        return currentPos < list.size();
    }
    
    public int get(){
        int r = list.get(currentPos++);
        return r & maxPossible; //Only Taking Chunk Sized data
    }
    
    public String getBinStr(){
        String str = Integer.toBinaryString(get());
        while(str.length()<outSize){
            str= "0" + str;
        }
        return str;
    }
    
    public void setCurrent(int c){
        currentPos = c;
    }
    
    public int getSize(){
        return list.size();
    }
    
    public void clearRim(){
        this.rem = this.remSize = 0;
    }
    
    public void remove(int index){
        list.remove(index);
    }
    
    public void removeLast(){
        list.remove(list.size()-1);
    }
    
    public int[] getChunkedIntArray(){
        int tempCur = currentPos;
        int[] arr = new int[list.size()];
        currentPos = 0;
        for(int i=0;hasNext();i++){
            arr[i] = get();
        }
        currentPos = tempCur;
        return arr;
    }
    
    public byte[] getChunkedByteArray(){
        if(outSize>Byte.SIZE)
            return null;
        int tempCur = currentPos;
        byte[] arr = new byte[list.size()];
        currentPos = 0;
        for(int i=0;hasNext();i++){
            arr[i] = (byte)get();
        }
        currentPos = tempCur;
        return arr;
    }
}
