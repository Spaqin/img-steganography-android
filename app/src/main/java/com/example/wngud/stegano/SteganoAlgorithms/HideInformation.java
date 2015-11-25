package com.example.wngud.stegano.SteganoAlgorithms;
import java.nio.charset.StandardCharsets;

public final class HideInformation {
    private int[] picture;
    private byte[] info;
    private byte[] filetype;
    private boolean textOnly;

    /**
     * Constructor for image, byte data plus file extension; makes a copy of the arrays.
     * @param img - array with pixels in RGB or ARGB format
     * @param inf - byte array of information to hide
     * @param filename - string with filename, but only last three characters will be used, and stored in ISO-8859-1 format,
     *                  as four-character and special character extensions are relatively rare.
     */
    public HideInformation(int[] img, byte[] inf, String filename)
    {
        picture = new int[img.length];
        System.arraycopy(img, 0, picture, 0, img.length);
        info = new byte[inf.length];
        System.arraycopy(inf, 0, info, 0, inf.length);
        filetype = filename.substring(filename.length()-3, filename.length()).getBytes(StandardCharsets.ISO_8859_1);
        textOnly = false;
    }

    /**
     * Constructor for image data plus text message
     * @param img - array with pixels in RGB or ARGB format
     * @param inf - byte array of information to hide
     */
    public HideInformation(int[] img, byte[] inf)
    {
        picture = new int[img.length];
        System.arraycopy(img, 0, picture, 0, img.length);
        info = new byte[inf.length];
        System.arraycopy(inf, 0, info, 0, inf.length);
        textOnly = true;
    }

    private HideInformation()
    {
        /* One option to ensure everything necessary is given
         * is not to give setters (as they can be ommited by an inexperienced programmer), but only prepared constructors.
         * This is why I made the default constructor private */
    }
    /**
     * Extracts bits from a byte.
     * @param from - Byte from which we extract bits
     * @param position - bit we begin with, must be lower than 8 minus number of bits to extract and higher than 0.
     *                 other values will cause the function to throw an exception
     * @param bitsToExtract - how many bits to extract, must be lower than 8.
     * @return byte type in form: 000000xx(2), where xx are extracted bytes, for extracting two bits
     */
    public static byte getBits(byte from, int position, int bitsToExtract) throws IllegalArgumentException
    {
        if (position > 8 - bitsToExtract || position < 0 || bitsToExtract > 8)
            throw new IllegalArgumentException("Position argument is out of supported range.");
        //System.out.println(Integer.toBinaryString(from) + " pos " + position);
        byte mask = (byte)(~(0xFF << bitsToExtract));
        return (byte)((from >> (8-position-bitsToExtract)) & mask);
    }

    /**
     * Hides bits in an int
     * @param guest - byte to hide. Number of leading zeroes is not checked, so it's more universal.
     * @param host - the one we're hiding stuff in.
     * @param position - again, one of four values:
     *                 0 - hides guest in bits 0-x
     *                 1 - hides guest in bits 8-8+x
     *                 2 - 16-16+x
     *                 3 - 24-24+x
     *                 x is bits to write.
     *                 Other values will cause an exception.
     *                 3 shouldn't be used, as most of the time it's an alpha channel many pictures don't have.
     * @param bitsToWrite - in other words - number of leading zeroes, if the rest were 1s. Both host and guest will be cleared out of unnecessary bits.
     *                    Also, it cannot be higher than 8 or smaller than 0 (if it's equal, it does nothing).
     * @return integer with hidden bits.
     */
    public static int hideBits(byte guest, int host, int position, int bitsToWrite) throws IllegalArgumentException
    {
        if(position < 0 || position > 3 || bitsToWrite > 8 || bitsToWrite < 0)
            throw new IllegalArgumentException("Position or bits to write argument is invalid.");

        //System.out.println("Hiding guest: " + Integer.toBinaryString(guest & 0xFF) + " into host: " + Integer.toBinaryString(host) + " in position " + position);
        byte mask = (byte)~(0xFF<<bitsToWrite);
        int hostMask = ~((mask & 0xFF) << (position << 3));
        int guestCopy;
        guest &= mask;
        guestCopy = (((int)guest) & 0xFF) << (position << 3);
        host &= hostMask;
        //System.out.println("Returning: " + Integer.toBinaryString(host | guestCopy));
        return host | guestCopy;
    }

    /**
     * @return Concatenated arrays of filetype and message.
     */
    private byte[] getAllData()
    {
        byte[] data;
        if(!textOnly) {
            data = new byte[info.length + filetype.length];
            System.arraycopy(filetype, 0, data, 0, filetype.length);
            System.arraycopy(info, 0, data, filetype.length, info.length);
        }
        else
            data = info;
        return data;
    }

    /**
     * Hides the data given in the constructor in the int[]
     * @param bitsPerSubpixel - how many bits should be replaced; shouldn't be other value than 1, 2, 4, 8 (lol) - things WILL break!
     * @return the array with encoded message.
     */
    public int[] encodeData(int bitsPerSubpixel) throws IllegalArgumentException
    {
        byte[] data = getAllData();
        byte[] dataLength = intToByteArray(data.length);
        int pixelIt = 0; //iterates on picture array
        int dataIt = 0; //iterates on data (and dataLength) array
        int dataBitIt = 0; //iterates on bits of data bytes
        int pixelBitIt = 0; //iterates on bits of picture bytes
        byte temp;

        //first, hide length
        //System.out.println("datal " + data.length);
        if(data.length > ((picture.length*3*bitsPerSubpixel)/8)-4)
            throw new IllegalArgumentException("Size is bigger than possible");

        for (int i = 0; i < (dataLength.length*8)/bitsPerSubpixel; i++)
        {
            temp = getBits(dataLength[dataIt], dataBitIt, bitsPerSubpixel);
            picture[pixelIt] = hideBits(temp, picture[pixelIt], pixelBitIt, bitsPerSubpixel);
            dataBitIt = (dataBitIt + bitsPerSubpixel) % 8;
            pixelBitIt = (pixelBitIt + 1) % 3;
            if(dataBitIt == 0)
                dataIt++;
            if(pixelBitIt == 0)
                pixelIt++;
        } //one important comment - it's saving the byte beginning with most significant bits at first.

        // Then put information if it's only text or some kind of file (1 bit is only necessary); 1 means yes, 0 means its not only text
        if(textOnly)
            picture[pixelIt] = hideBits((byte) 0x01, picture[pixelIt], pixelBitIt, bitsPerSubpixel);
        else
            picture[pixelIt] = hideBits((byte) 0x00, picture[pixelIt], pixelBitIt, bitsPerSubpixel);

        dataIt = 0;
        dataBitIt = 0;
        pixelBitIt = (pixelBitIt + 1) % 3;
        if(pixelBitIt == 0)
            pixelIt++;

        //And finally, hide the rest of the data.
        for (int i = 0; i < (data.length*8)/bitsPerSubpixel; i++)
        {
            temp = getBits(data[dataIt], dataBitIt, bitsPerSubpixel);
            picture[pixelIt] = hideBits(temp, picture[pixelIt], pixelBitIt, bitsPerSubpixel);
            dataBitIt = (dataBitIt + bitsPerSubpixel) % 8;
            pixelBitIt = (pixelBitIt + 1) % 3;
            if(dataBitIt == 0)
                dataIt++;
            if(pixelBitIt == 0)
                pixelIt++;
        }

        return picture;
    }

    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
