
package com.example.wngud.stegano.SteganoAlgorithms;
import java.nio.charset.StandardCharsets;

/**
 * Created by Spag on 2015-10-09.
 */
public class UnhideInformation {
    private int[] picture;
    private byte[] info;
    private byte[] filetype;
    private String fileExtension;
    private boolean textOnly;
    private int size;

    UnhideInformation(int[] pic, int bitsPerSubpixel)
    {
        picture = pic;
        byte[] temp = unhideInfo(bitsPerSubpixel);

        if(textOnly == false) {
            filetype = new byte[3];
            info = new byte[size-3];
            System.arraycopy(temp, 0, filetype, 0, 3);
            System.arraycopy(temp, 3, info, 0, size-3);
            fileExtension = new String(filetype, StandardCharsets.ISO_8859_1);
        }
        else {
            info = new byte[size];
            System.arraycopy(temp, 0, info, 0, size);
        }

    }
    private UnhideInformation(){
        //same as with HideInformation - we don't want others to mess with our data
    }

    public String getFileExtension()
    {
        return fileExtension;
    }

    public int getSize()
    {
        return size;
    }

    public byte[] getInfo()
    {
        return info;
    }

    public boolean getTextOnly()
    {
        return textOnly;
    }
    /**
     * Returns extracted bits from an int from positions:
     * [0]   [1]    [2]     [3]
     * 0-x, 8-8+x, 16-16+x, 24-24+x,
     * where x is bitsToExtract.
     * @param from - integer from which we're taking bits from
     * @param bitsToExtract - can't be lower than 0 and higher than 8.
     * @return Array with extracted bits.
     */
    private byte[] extractBits(int from, int bitsToExtract) throws IllegalArgumentException
    {
        if(bitsToExtract < 0 || bitsToExtract > 8)
            throw new IllegalArgumentException("bitsToExtract value is not correct.");
        byte[] bits = new byte[4];
        int mask = ~(0xFFFFFFFF << bitsToExtract);
        bits[0] = (byte) (from & mask);
        bits[1] = (byte) ((from >> 8) & mask);
        bits[2] = (byte) ((from >> 16) & mask);
        bits[3] = (byte) ((from >> 24) & mask);
        //System.out.println("bits: " + Integer.toBinaryString(bits[0] & 0xFF) + " "+ Integer.toBinaryString(bits[1] & 0xFF) + " "+ Integer.toBinaryString(bits[2] & 0xFF) + " "+ Integer.toBinaryString(bits[3] & 0xFF) + " ");
        return bits;
    }

    /**
     *
     * @param part - a byte array with parts of the entire byte. Parts should be in format 000000xx(b) for parts of two bits.
     * @return Complete byte.
     */
    private byte putBytesTogether(byte[] part)
    {
        byte completeByte = 0;
        for(int i = 0; i < part.length; i++)
        {
            completeByte = (byte)((completeByte << (8 / part.length)) | part[i]);
        }
        //System.out.println("CompleteByte: "+Integer.toBinaryString(completeByte));
        return completeByte;
    }

    /**
     * The main thing that takes out data out from the pixel array.
     * @param bitsPerSubpixel - selfexplanatory.
     * @return array of bytes with data.
     */
    private byte[] unhideInfo(int bitsPerSubpixel) throws IllegalArgumentException
    {
        size = 0;
        byte[] parts = new byte[8/bitsPerSubpixel];
        byte[] temp;
        byte[] inf;

        int picPartCnt = 0;
        int picCnt = 0;
        int partCnt = 0;
        int infoCnt = 0;
        int bitCounter = 0;
        //first things first - we need to take the size.
        for (int i = 0; i < (32/(3*bitsPerSubpixel))+1; i++)
        {
            temp = extractBits(picture[picCnt], bitsPerSubpixel);
            for(int j = 0; j < 3 && bitCounter < 32; j++) {
                //System.out.println("temp["+j+"]: "+Integer.toBinaryString(temp[j]));
                size = (size << bitsPerSubpixel) | (temp[j] & 0xFF);
                bitCounter += bitsPerSubpixel;
                picPartCnt = (picPartCnt + 1) % 3;
            }
            if(picPartCnt == 0)
                picCnt++;
        }

        if(size > ((picture.length*3*bitsPerSubpixel)/8)-4)
            throw new IllegalArgumentException("Size is bigger than possible");
        //get info whether it's a text or a file
        temp = extractBits(picture[picCnt], bitsPerSubpixel);
        textOnly = (temp[picPartCnt] != 0) ? true : false;

        //System.out.println(textOnly);
        //System.out.println(size);

        picPartCnt++;
        if(picPartCnt == 0)
            picCnt++;

        //and now... data.
        inf = new byte[size+1];
        for (; picPartCnt < 3; picPartCnt++) //gather remaining parts from the pixel (if there are any)
        {
            parts[partCnt] = temp[picPartCnt];
            partCnt = (partCnt + 1) % (8/bitsPerSubpixel);
            if(partCnt == 0)
            {
                inf[infoCnt] = putBytesTogether(parts);
                infoCnt++;
            }
        }
        picCnt++;
        while(infoCnt < size)
        {
            temp = extractBits(picture[picCnt], bitsPerSubpixel);
            for (picPartCnt = 0; picPartCnt < 3 && infoCnt < size; picPartCnt++)
            {
                parts[partCnt] = temp[picPartCnt];
                partCnt = (partCnt + 1) % (8/bitsPerSubpixel);
                if(partCnt == 0 && infoCnt < size)
                {
                    //System.out.println("infocnt " + infoCnt);
                    inf[infoCnt] = putBytesTogether(parts);
                    infoCnt++;
                }
            }
            picCnt++;
        }
        return inf;
    }
}
