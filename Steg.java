package isteg;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Steg {
    public static final int SUCCESS = 0;
    public static final int ERR_BITCOUNT = 1;
    public static final int ERR_FILEREAD = 2;
    public static final int ERR_FILEWRITE = 3;
    public static final int ERR_NOSTEG = 4;
    public static final int ERR_LOWIMGSIZE = 5;
    public static final int ERR_NOTANIMAGE = 6;
	private static FileOutputStream fos;
    
    public static String[] read(Path topFile){
        BufferedImage steg;
        String[] ret = new String[]{"",""};
        try {
            steg = ImageIO.read(topFile.toFile());
        } catch (IOException e) {
            ret[0]+=Steg.ERR_FILEREAD;
            return ret;
        }
        
        int rgb0 = steg.getRGB(0,0);
        int bitCount = (rgb0>>16 & 0b1)+1;
        int type = rgb0>>8 & 0b1;
        int enc = rgb0 & 0b1;
        
        //Verifying if the image has steganographic data in it by signature iSteg0
        
        int bitMaxVal = (bitCount==2)? 0b11:0b1;
        Chunker sign = new Chunker(bitCount*3,Byte.SIZE);
        OneDimMaker odm = new OneDimMaker(steg.getWidth(),steg.getHeight());
        for(int i=2;i<16/bitCount+2;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            sign.add(value);
        }
        
        if(! "iSteg0".equals(new String(sign.getChunkedByteArray()))){
            ret[0]+=Steg.ERR_NOSTEG;
            return ret;
        }
        
        if(type==0){
            ret = readFileSteg(steg,bitCount,enc,2+16/bitCount);
        }
            
        else{
            ret = readStringSteg(steg,bitCount,enc,2+16/bitCount);
        }
           
        return ret;
    }
    
    //hide a file
    public static int write(Path topFile,Path bottomFile,int bitCount,String newFileName){
        if(bitCount!=1 && bitCount!=2)
            return Steg.ERR_BITCOUNT;
        BufferedImage image;
        String fileName = bottomFile.getFileName().toString();
        
        Chunker fileChk = new Chunker(Byte.SIZE,bitCount*3);
        try {
            image = ImageIO.read(topFile.toFile());
	    if(image == null)
            	return Steg.ERR_NOTANIMAGE;
            fileChk.add(Files.readAllBytes(bottomFile));
        } catch (IOException e) {
            Logger.getLogger(Steg.class.getName()).log(Level.SEVERE, null, e);
            return Steg.ERR_FILEREAD;
        }
        
        Chunker sizeChk = new Chunker(30,bitCount*3);
        sizeChk.add(fileChk.getSize());
        Chunker fileNameChk = new Chunker(Byte.SIZE,bitCount*3);
        Chunker fileNameSizeChk = new Chunker(12,bitCount*3);
        fileNameChk.add(fileName.getBytes());
        fileNameSizeChk.add(fileNameChk.getSize());
        
        int totalPixReq = 2+ sizeChk.getSize()+ fileNameSizeChk.getSize() + fileChk.getSize()+fileNameChk.getSize()+48/(3*bitCount); //+48/(3*bitCount) for iSteg0
        if(totalPixReq > image.getHeight()*image.getWidth()) {
        	return Steg.ERR_LOWIMGSIZE;
        }
        
        int i=init1Meta(image,bitCount,0,0);
        i=imageWrite(i,image,sizeChk,bitCount);
        i=imageWrite(i,image,fileNameSizeChk,bitCount);
        i=imageWrite(i,image,fileChk,bitCount);
        imageWrite(i,image,fileNameChk,bitCount);
        
        return fileWrite(image,newFileName);
    }
    
    //hide a text
    public static int write(Path topFile,String text,int bitCount,String newFileName){
        if(bitCount!=1 && bitCount!=2)
            return Steg.ERR_BITCOUNT;
        BufferedImage image;
        Chunker textChk = new Chunker(Byte.SIZE,bitCount*3);
        try {
            image = ImageIO.read(topFile.toFile());
	    if(image == null)
            	return Steg.ERR_NOTANIMAGE;
            textChk.add(text.getBytes());
        } catch (IOException e) {
            return Steg.ERR_FILEREAD;
        }
        
        Chunker sizeChk = new Chunker(18,bitCount*3);
        sizeChk.add(textChk.getSize());
        
        int i=init1Meta(image,bitCount,1,0);

        int totalPixReq = 2+ textChk.getSize() + sizeChk.getSize()+48/(3*bitCount); //+48/(3*bitCount) for iSteg0
        if(totalPixReq > image.getHeight()*image.getWidth()) {
        	return Steg.ERR_LOWIMGSIZE;
        }
        
        i=imageWrite(i,image,sizeChk,bitCount);
        imageWrite(i,image,textChk,bitCount);
        
        return fileWrite(image,newFileName);
    }
    
    
    //changes pixel value of the image according to chunks and bitCount
    private static int imageWrite(int begIndex, BufferedImage image, Chunker chunk, int bitCount){
        OneDimMaker odm = new OneDimMaker(image.getWidth(),image.getHeight());
        int maxValue = (bitCount==2)?0b111111:0b1111111;
        int i=begIndex;
        int dataMaxValue = (bitCount==2)?0b11:0b1;
        for(;chunk.hasNext();i++){
            int rgb = image.getRGB(odm.get(i).x, odm.get(i).y);
            int data = chunk.get();
            int red = (rgb>>(16+bitCount))<<bitCount | data>>(bitCount*2);
            int green = (rgb>>(8+bitCount) & maxValue)<<bitCount | data>>bitCount & dataMaxValue;
            int blue = (rgb>>(bitCount) & maxValue)<<bitCount | data & dataMaxValue;
            rgb=(((rgb>>24 <<8) | red)<<8 | green)<<8 | blue;
            
            image.setRGB(odm.get(i).x, odm.get(i).y, rgb);
        }
        return i;
    }

    //adds metaData like file or text bitCount at 0,0 of image
    private static int init1Meta(BufferedImage image, int bitCount, int type, int enc) {
        Chunker chk = new Chunker(Byte.SIZE,bitCount*3);
        chk.add("iSteg0".getBytes(StandardCharsets.US_ASCII));
        int rgb0 = image.getRGB(0,0);
        int red0 = (rgb0>>17)<<1 | (bitCount-1);
        int green0 = (rgb0>>9 & 0b1111111)<<1 | type;
        int blue0 = (rgb0>>1 & 0b1111111)<<1 | enc;
        rgb0=(((rgb0>>24 <<8) | red0)<<8 | green0)<<8 | blue0;
        image.setRGB(0, 0, rgb0);
        return imageWrite(2,image,chk,bitCount);
    }

    private static int fileWrite(BufferedImage image, String newFileName) {  
        try {
            ImageIO.write(image,"png", new File(newFileName));
        } catch (IOException ex) {
            return Steg.ERR_FILEWRITE; 
        }
        return Steg.SUCCESS;
    }

    private static String[] readFileSteg(BufferedImage steg,int bitCount,int enc,int startIndex) {
        String ret[] = new String[]{"","","0"};
        Chunker fileSizeCalc = new Chunker(bitCount*3,30);
        Chunker nameSizeCalc = new Chunker(bitCount*3,12);
        Chunker file = new Chunker(bitCount*3,Byte.SIZE);
        Chunker fileName = new Chunker(bitCount*3,Byte.SIZE);
        int bitMaxVal = (bitCount==2)? 0b11:0b1;
        int bitMaxVal3 = (bitCount==2)? 0b111111:0b111;
        int fileSizeEnd = 10/bitCount+startIndex;
        int nameSizeEnd = 4/bitCount +fileSizeEnd;
        OneDimMaker odm = new OneDimMaker(steg.getWidth(),steg.getHeight());
        for(int i=startIndex;i<fileSizeEnd;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            fileSizeCalc.add(value & bitMaxVal3);
        }
        for(int i=fileSizeEnd;i<nameSizeEnd;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            nameSizeCalc.add(value);
        }
        
        int fileEnd = nameSizeEnd + fileSizeCalc.get();
        int nameEnd = fileEnd + nameSizeCalc.get();
        
        for(int i=nameSizeEnd;i<fileEnd;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            file.add(value);
        }
        
        for(int i=fileEnd;i<nameEnd;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            fileName.add(value);
        }
        try {
            fos = new FileOutputStream(new String(fileName.getChunkedByteArray()));
            try {
                fos.write(file.getChunkedByteArray());
            } catch (IOException ex) {
                ret[0] += Steg.ERR_FILEWRITE;
                Logger.getLogger(Steg.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            ret[0]+=Steg.ERR_FILEWRITE;
            Logger.getLogger(Steg.class.getName()).log(Level.SEVERE, null, ex);
        }
        ret[0]+=Steg.SUCCESS;
        ret[1] = new String(fileName.getChunkedByteArray());
        return ret;
    }

    private static String[] readStringSteg(BufferedImage steg,int bitCount,int enc,int startIndex) {
        String ret[] = new String[]{"","","1"};
        Chunker textLenCalc = new Chunker(bitCount*3,18);
        int bitMaxVal = (bitCount==2)? 0b11:0b1;
        //int bitMaxVal3 = (bitCount==2)? 0b111111:0b111;
        int textStart = 6/bitCount+startIndex;
        OneDimMaker odm = new OneDimMaker(steg.getWidth(),steg.getHeight());
        for(int i=startIndex;i<textStart;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            textLenCalc.add(value);
        }
        int textEnd = textLenCalc.get() + textStart;
        Chunker toByte = new Chunker(bitCount*3,Byte.SIZE);
        for(int i=textStart;i<textEnd;i++){
            int rgb = steg.getRGB(odm.get(i).x, odm.get(i).y);
            int value= rgb>>16 & bitMaxVal;
            value = value<<bitCount | (rgb>>8 & bitMaxVal);
            value = value<<bitCount | (rgb & bitMaxVal);
            toByte.add(value);
        }
        ret[1] = new String(toByte.getChunkedByteArray());
        ret[0] += Steg.SUCCESS;
        return ret;
    }

   
}

final class OneDimMaker{
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
