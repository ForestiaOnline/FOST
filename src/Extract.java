/*
Extract.java: this file is part of the FOST program.

Copyright (C) 2020 Sean Stafford (a.k.a. PyroSamurai)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
*/
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import static java.lang.System.out;
/**
Class Description:
Self-explanatory. This is a very simple program.

Dev Notes:

*/
public class Extract
{
// class variables
private static int w=0,h=0,bpp=16,spzType,numSprites,offset;
private static byte[] rawBytes,pixels,bmp;
private static String Name;

// constructor for Extract class
public Extract(byte[] ba, String dir, String name, int fLen)
{
    try
    {
        Name=name;
        // Make the directory where we will extract the bmp to
        String extractDir = dir+name+File.separator;
        Files.createDirectories((new File(extractDir)).toPath());

        // byte array to bytebuffer, load JBL
        ByteBuffer fbb = ByteBuffer.wrap(ba).order(ByteOrder.LITTLE_ENDIAN);
        JBL bl = new JBL();

        bl.setFileVars(extractDir,name);
        getSPZSpecs(fbb);
        bl.setImgsetSize(numSprites);
        bl.set16BitFmtIn("RGB565");

        for(int i=0; i < numSprites; i++)
        {
            getSpriteSpecs(fbb);
            bl.setBitmapVars(w,h,bpp);

            // Get image data
            rawBytes = getPixelByteArray(fbb);
            // Convert to RGB24
            pixels = bl.toStdRGB(rawBytes);
            // Prepare the BMP
            bmp = bl.setBMP(bl.reverseRows(pixels),false);
            // Write the new BMP into existence
            bl.makeBMP(bmp,i,"");
        }
    }
    catch(Exception ex)
    {
        out.println("Error in (EM): "+name);
        ex.printStackTrace(System.out);
    }
}

// Assign the SPZ header info
private static void getSPZSpecs(ByteBuffer bb)
{
    bb.position(18);
    spzType    = (int)bb.getChar();
    numSprites = (int)bb.getChar();
    switch(spzType)
    {
    case 1:
        offset = 366;
        break;
    case 8:
        offset = 2718;
        break;
    case 9:
        offset = 3054;
        break;
    case 24:
        offset = 8094;
        break;
    default:
        out.println("Unknown Type in "+Name);
        System.exit(1);
        break;
    }
    bb.position(offset);
}

private static void getSpriteSpecs(ByteBuffer bb)
{
    w = (int)bb.getChar();
    h = (int)bb.getChar();
    bb.getChar();// Unknown, but stored in Sprites.Def as an int value
    bb.getChar();// Unknown, but stored in Sprites.Def as an int value
    bb.getInt(); // Completely unknown
    int bmpStructSize = bb.getInt(); // bmpDataSize
    bb.getInt(); // width again
    bb.getInt(); // height again
    bb.getInt(); // bmpDataSize again
    bb.getInt(); // Completely unknown
    bb.getInt(); // Completely unknown
    bb.getInt(); // always 16
    bb.getInt(); // always 28
    bb.position(bb.position()+(4*h));//skip unidentified data
}

// Extract Pixel bytes from sprite format
private static byte[] getPixelByteArray(ByteBuffer bb)
{
    int arrayW = w*2,idx,cPixels,emptyPixels;
    byte[][] grid = new byte[h][arrayW];
    for(int i=0; i < h; i++)
    {
        idx=0;
        while(idx < arrayW)
        {
            // Empty pixel run
            emptyPixels = bb.getInt();
            idx += (emptyPixels*2);
            // Colored pixels
            cPixels = bb.getInt();
            bb.get(grid[i],idx,(cPixels*2));
            idx += (cPixels*2);
        }
    }    
    // turn grid[][] into a single byte array for processing by JBL
    int a = w*h*2;
    byte[] pixelArray = new byte[a];
    ByteBuffer pbb = ByteBuffer.wrap(pixelArray).order(ByteOrder.LITTLE_ENDIAN);
    for(int i=0; i < h; i++)
    {
        pbb.put(grid[i]);    
    }

    return pixelArray;
}

}
