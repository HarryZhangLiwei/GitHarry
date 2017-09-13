package utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class ImageHelper {
	
	
	public static String IMAGE_TYPE_GIF = "gif";
    public static String IMAGE_TYPE_JPG = "jpg";
    public static String IMAGE_TYPE_JPEG = "jpeg";
    public static String IMAGE_TYPE_BMP = "bmp";
    public static String IMAGE_TYPE_PNG = "png";
    public static String IMAGE_TYPE_PSD = "psd";
    
    public final static String DecodeandGenerate(int type, String text){
    	
    	pressText2(text, "img01.jpg","abc_pressText2.jpg", "黑体", 36, Color.white, 80, 0, 0, 0.5f);
    	
    	return "";
    }
    
    
    public final static void pressText2(String pressText, String srcImageFile,String destImageFile,
            String fontName, int fontStyle, Color color, int fontSize, int x,
            int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            
            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write((BufferedImage) image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
	/** 
     * @Description: combine
     * @param files  
     * @param type1  landscape， 2 long 
     */  
    public static void mergeImage(String[] files, int type, String targetFile) {  
        int len = files.length;  
        if (len < 1) {  
            throw new RuntimeException("at least two pics");  
        }  
        File[] src = new File[len];  
        BufferedImage[] images = new BufferedImage[len];  
        int[][] ImageArrays = new int[len][];  
        for (int i = 0; i < len; i++) {  
            try {  
                src[i] = new File(files[i]);  
                images[i] = ImageIO.read(src[i]);  
            } catch (Exception e) {  
                throw new RuntimeException(e);  
            }  
            int width = images[i].getWidth();  
            int height = images[i].getHeight();  
            ImageArrays[i] = new int[width * height];  
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);  
        }  
        int newHeight = 0;  
        int newWidth = 0;  
        for (int i = 0; i < images.length; i++) {  
            // landscape 
            if (type == 1) {  
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();  
                newWidth += images[i].getWidth();  
            } else if (type == 2) {// long 
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();  
                newHeight += images[i].getHeight();  
            }  
        }  
        if (type == 1 && newWidth < 1) {  
            return;  
        }  
        if (type == 2 && newHeight < 1) {  
            return;  
        }  
  
        // create new image  
        try {  
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);  
            int height_i = 0;  
            int width_i = 0;  
            for (int i = 0; i < images.length; i++) {  
                if (type == 1) {  
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,  
                            images[i].getWidth());  
                    width_i += images[i].getWidth();  
                } else if (type == 2) {  
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);  
                    height_i += images[i].getHeight();  
                }  
            }  
            //target image 
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));  
  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }
    
}
