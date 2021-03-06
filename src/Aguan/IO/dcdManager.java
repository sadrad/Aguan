/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Aguan.IO;

import java.io.*;
/**
 *
 * @author Noel
 */
public class dcdManager {
    private FileWriter fout;
    private PrintWriter poutdcd;
    private FileOutputStream file_output;
    public DataOutputStream data_out;
    private File fl;
    private InputStream is;
    private DataInputStream dis;
    private long length;
    private boolean is_header_read = false;
    /////////////////////////////////////////////
    public int first_int;
    public int second_int;
    public String header;
    public int NSET;
    public int ISTART;
    public int NSAVC;
    public int LENGTH;
    public int NAMNF;
    public float DELTA;
    public int title_size;
    public int title_size2;
    public int NTITLE;
    public String[] ntitle;
    public int four;
    public int four2;
    public int N = 0;
    public int[] freeIndexes;
    public int[] freeIndexes32;
    public int freeIndex;
    public int freeIndex2;
    //////////////////////////////////////////////
    public int num_fixed;
    public int first;
    public int[] indexes;
    public float[] X;
    public float[] Y;
    public float[] Z;
    int countBytes = 0;
    public dcdManager(String dcdout){
        try{
           file_output = new FileOutputStream (dcdout);
           data_out = new DataOutputStream (file_output);
        }catch( IOException e ){System.err.println( e );}
    }
    public dcdManager(DataOutputStream dout){
        data_out = dout;
    }
    public dcdManager(PrintWriter pw){
           poutdcd = pw;
    }
    public String read_dcdheader(){
        try{
           int temp = 0;
           first_int = dis.readInt();
           if(first_int != 84){return "BAD DCD FORMAT: First Int is not 84";}
           StringBuffer buffer = new StringBuffer();
           buffer.append((char)dis.readByte());
           buffer.append((char)dis.readByte());
           buffer.append((char)dis.readByte());
           buffer.append((char)dis.readByte());
           header = buffer.toString();
           if(!header.equals("CORD")){return "BAD DCD FORMAT: Not CORD header.";}
           NSET = dis.readInt();
           ISTART = dis.readInt();
           NSAVC = dis.readInt();
           int i = 0;
           int j = 0;
           for(i = 0; i < 5; i++){temp = dis.readInt();}
           NAMNF = dis.readInt();
           DELTA = dis.readFloat();
           for(i = 0; i < 10; i++){temp = dis.readInt();}
           second_int = dis.readInt();
           if(second_int != 84){return "BAD DCD FORMAT: Second Int is not 84";}
           title_size = dis.readInt();
           if (((title_size-4)%80) == 0){
              NTITLE = dis.readInt();
              ntitle = new String[NTITLE];
              for(i = 0; i < NTITLE; i++){
                 StringBuffer buffer2 = new StringBuffer();
                 for(j = 0; j < 80; j++){
                     buffer2.append((char)dis.readByte());
                     countBytes++;
                 }
                 ntitle[i] = buffer2.toString();
              }
           }else{return "BAD DCD FORMAT: ((Title Size - 4) % 80) != 0";}
           title_size2 = dis.readInt();
           if(title_size != title_size2){return "BAD DCD FORMAT: Title_size 1 and do not match.";}
           four = dis.readInt();
           if(four != 4){return "BAD DCD FORMAT: Int after title not equal to 4 1.";}
           N = dis.readInt();
           four2 = dis.readInt();
           if(four2 != 4){return "BAD DCD FORMAT: Int after title not equal to 4 2.";}
           if (NAMNF != 0){
               freeIndexes = new int[(N)-(NAMNF)];
               freeIndex = dis.readInt();
               if (freeIndex != ((N)-(NAMNF))*4){return "BAD DCD FORMAT: Inside free indexes1.";}
               for (i=0; i<((N)-(NAMNF)); ++i){
                   freeIndexes[i] = dis.readInt();
               }
               freeIndex2 = dis.readInt();
               if(freeIndex2 != ((N)-(NAMNF))*4){return "BAD DCD FORMAT: Inside free indexes2.";}
           }
        }catch( IOException e ){System.err.println( e );}
        is_header_read = true;
        return "header is correct";
    }
    public void write_header(String a, int nset, int istart,int nsavc,int N){
       try{
          int i;
          data_out.writeInt((int)84);
//          char[] header = a.toCharArray();
          if(a.equals("CORD")){
             data_out.writeByte(67);
             data_out.writeByte(79);
             data_out.writeByte(82);
             data_out.writeByte(68);
          }
          data_out.writeInt((int)nset);     // NSET
          data_out.writeInt((int)istart);   //ISTART
          data_out.writeInt((int)nsavc);    // NSAVC
          for(i = 0; i < 5; i++){data_out.writeInt((int)0);} // Espacio en blanco No usado ??
          data_out.writeInt((int)0);        // NAMNF
          data_out.writeFloat((float)0.040909655); //DELTA
          for(i = 0; i < 10; i++){data_out.writeInt((int)0);} // Espacio en blanco No usado ??}
          data_out.writeInt((int)84);       // Constant 84
          data_out.writeInt((int)164);      // Title size = 244
          data_out.writeInt((int)2);        // Number of lines in title = 3
          for(i = 0; i < 40; i++){data_out.writeInt((int)0);}  // Title text
          data_out.writeInt((int)164);      // Title size = 244
          data_out.writeInt((int)4);        // Just a number 4
          data_out.writeInt((int)N);
          data_out.writeInt((int)4);        // Total number of bytes in header 276 when title = 164 
       //   data_out.flush();
        }catch( IOException e ){System.err.println( e );}
    }
    public void write_dcdStep(int n, double[] Xc, double[] Yc, double[] Zc, double ro, int noPoint){
        try{
            int counter = 0;
            int vdw = noPoint;
            data_out.writeInt((int)(n*4));
            for(int j = 0; j < (Xc.length/4); j++){
                for(int i = 0; i < 4; i++){
                    if(i != vdw){
                       data_out.writeFloat((float)(Xc[counter]*ro));
                    }
                   counter++;
                }
            }
            data_out.writeInt((int)(n*4));
            data_out.writeInt((int)(n*4));
            counter = 0;
            for(int j = 0; j < (Yc.length/4); j++){
                for(int i = 0; i < 4; i++){
                   if(i != vdw){
                       data_out.writeFloat((float)(Yc[counter]*ro));
                   }
                   counter++;
                }
            }
            data_out.writeInt((int)(n*4));
            data_out.writeInt((int)(n*4));
            counter = 0;
            for(int j = 0; j < (Zc.length/4); j++){
               for(int i = 0; i < 4; i++){
                   if(i != vdw){
                       data_out.writeFloat((float)(Zc[counter]*ro));
                   }
                   counter++;
                }
            }
            data_out.writeInt((int)(n*4));
        }catch( IOException e ){System.err.println( e );}
    }
 /*   public void write_dcdStepTIP4P(int n, double[] Xc, double[] Yc, double[] Zc){
    //    try{
            byte[] temp;
            byte[] temp2;
            temp = intToByteArray(n*4);
            poutdcd.write(temp[0]);poutdcd.write(temp[1]);poutdcd.write(temp[2]);poutdcd.write(temp[3]);
            temp2 = intToByteArray(n*4);
        //    poutdcd.write(temp2[0]);pout.write(temp2[1]);pout.write(temp2[2]);pout.write(temp2[3]);
            for(int j = 0; j < Xc.length; j++){
               if((j%4) != 0){
                   temp = floatToByteArray((float)Xc[j]);
                   poutdcd.write(temp[0]);poutdcd.write(temp[1]);poutdcd.write(temp[2]);poutdcd.write(temp[3]);
               }
            }
            poutdcd.write(temp2[0]);poutdcd.write(temp2[1]);poutdcd.write(temp2[2]);poutdcd.write(temp2[3]);
            poutdcd.write(temp2[0]);poutdcd.write(temp2[1]);poutdcd.write(temp2[2]);poutdcd.write(temp2[3]);
            for(int j = 0; j < Yc.length; j++){
               if((j%4) != 0){
                   temp = floatToByteArray((float)Yc[j]);
                   poutdcd.write(temp[0]);poutdcd.write(temp[1]);poutdcd.write(temp[2]);poutdcd.write(temp[3]);
               }
            }
            poutdcd.write(temp2[0]);poutdcd.write(temp2[1]);poutdcd.write(temp2[2]);poutdcd.write(temp2[3]);
            poutdcd.write(temp2[0]);poutdcd.write(temp2[1]);poutdcd.write(temp2[2]);poutdcd.write(temp2[3]);
            for(int j = 0; j < Zc.length; j++){
               if((j%4) != 0){
                   temp = floatToByteArray((float)Zc[j]);
                   poutdcd.write(temp[0]);poutdcd.write(temp[1]);poutdcd.write(temp[2]);poutdcd.write(temp[3]);
               }
            }
            poutdcd.write(temp2[0]);poutdcd.write(temp2[1]);poutdcd.write(temp2[2]);poutdcd.write(temp2[3]);
  //      }catch( IOException e ){System.err.println( e );}
    }*/
    public String read_dcdStep(){
        try{
          int ret_val;
          float[] tmpX;
          int i;
          if((first > 0) && (num_fixed > 0)){
              tmpX = new float[N-num_fixed];
              if(tmpX == null){return "DCD bad tmpX Array initialization.";}
          }
          ret_val = dis.readInt(); countBytes += 4;
          if(ret_val == 0){
              return "We have reached the end of the file.";
          }
          if((num_fixed==0) || (first > 0)){
              if(ret_val != (N*4)){return "BAD DCD: Value BX != N*4";}
              for(i = 0; i < N; i++){
                  X[i] = dis.readFloat(); countBytes += 4;
              }
              ret_val = dis.readInt(); countBytes += 4;
              if(ret_val != (N*4)){return "BAD DCD: Value AX != N*4";}
              ret_val = dis.readInt();  countBytes += 4;
              if(ret_val != (N*4)){return "BAD DCD: Value BY != N*4";}
              for(i = 0; i < N; i++){
                  Y[i] = dis.readFloat();   countBytes += 4;
              }
              ret_val = dis.readInt();  countBytes += 4;
              if(ret_val != (N*4)){return "BAD DCD: Value AY != N*4";}
              ret_val = dis.readInt();  countBytes += 4;
              if(ret_val != (N*4)){return "BAD DCD: Value BZ != N*4";}
              for(i = 0; i < N; i++){
                  Z[i] = dis.readFloat();   countBytes += 4;
              }
              ret_val = dis.readInt();  countBytes += 4;
              if(ret_val != (N*4)){return "BAD DCD: Value AZ != N*4";}
          }else{
              if(ret_val != (4*(N-num_fixed))){
                 return "BAD DCD:";
             }
          }
        }catch( IOException e ){System.err.println( e );}
        return "Read step correctly";
    }
    public void trajquery(PrintWriter pout2){
        pout2.println("TRAJQUERY:");
        if(!is_header_read){
            pout2.println(read_dcdheader());
        }
        pout2.println("first_int = "+first_int);
        pout2.println("NSET = "+NSET);
        pout2.println("ISTART = "+ISTART);
        pout2.println("NT = "+N);
        pout2.println("NSAVC = "+NSAVC);
        pout2.println("NAMNF = "+NAMNF);
        pout2.println("DELTA = "+DELTA);
        pout2.println("NTITLE = "+NTITLE);
        pout2.println("title_size = "+title_size);
        for(int i = 0; i < NTITLE; i++){
            pout2.println(i+" "+ntitle[i]);
        }
    }
    public void trajquery(){
        System.out.println("TRAJQUERY:");
        if(!is_header_read){
           // System.out.println(read_dcdheader());
        }
        System.out.println("first_int = "+first_int);
        System.out.println("NSET = "+NSET);
        System.out.println("ISTART = "+ISTART);
        System.out.println("NT = "+N);
        System.out.println("NSAVC = "+NSAVC);
        System.out.println("NAMNF = "+NAMNF);
        System.out.println("DELTA = "+DELTA);
        System.out.println("NTITLE = "+NTITLE);
        System.out.println("title_size = "+title_size);
        for(int i = 0; i < NTITLE; i++){
            System.out.println(i+" "+ntitle[i]);
        }
    }
    
    public void openReadDcd(String dcd){
        try{
           is = new FileInputStream(fl);
           dis = new DataInputStream(is);
           length = fl.length();
        }catch( IOException e ){System.err.println( e );}
    }
        private static final int MASK = 0xff;
      /**
       * convert byte array (of size 4) to float
       * @param test
       * @return
       */
      public static float byteArrayToFloat(byte test[]) {
          int bits = 0;
          int i = 0;
          for (int shifter = 3; shifter >= 0; shifter--) {
              bits |= ((int) test[i] & MASK) << (shifter * 8);
              i++;
          }

          return Float.intBitsToFloat(bits);
      }
      /**
       * convert float to byte array (of size 4)
       * @param f
       * @return
       */
      public byte[] floatToByteArray(float f) {
          int i = Float.floatToRawIntBits(f);
          return intToByteArray(i);
      }
      /**
       * convert int to byte array (of size 4)
       * @param param
       * @return
       */
      public byte[] intToByteArray2(int param) {
          byte[] result = new byte[4];
          for (int i = 0; i < 4; i++) {
              int offset = (result.length - 1 - i) * 8;
              result[i] = (byte) ((param >>> offset) & MASK);
          }
          return result;
      }
      /**
	 * Returns a byte array containing the two's-complement representation of the integer.<br>
	 * The byte array will be in big-endian byte-order with a fixes length of 4
	 * (the least significant byte is in the 4th element).<br>
	 * <br>
	 * <b>Example:</b><br>
	 * <code>intToByteArray(258)</code> will return { 0, 0, 1, 2 },<br>
	 * <code>BigInteger.valueOf(258).toByteArray()</code> returns { 1, 2 }.
	 * @param integer The integer to be converted.
	 * @return The byte array of length 4.
	 */
	private byte[] intToByteArray (int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}
      /**
       * convert byte array to String.
       * @param byteArray
       * @return
       */
       public static String byteArrayToString(byte[] byteArray) {
          StringBuilder sb = new StringBuilder("[");
          if(byteArray == null) {
              throw new IllegalArgumentException("byteArray must not be null");
          }
          int arrayLen = byteArray.length;
          for(int i = 0; i < arrayLen; i++) {
              sb.append(byteArray[i]);
              if(i == arrayLen - 1) {
                  sb.append("]");
              } else{
                  sb.append(", ");
              }
          }
          return sb.toString();
      }
       private static byte[] intToDWord_BE(int i) {
		byte[] dword = new byte[4];
		dword[0] = (byte) (i & 0x00FF);
		dword[1] = (byte) ((i >> 8) & 0x000000FF);
		dword[2] = (byte) ((i >> 16) & 0x000000FF);
		dword[3] = (byte) ((i >> 24) & 0x000000FF);
		return dword;
	}
    private static byte[] intToDWord_LE(int i) {
		byte[] dword = new byte[4];
		dword[0] = (byte) ((i >> 24) & 0x000000FF);
		dword[1] = (byte) ((i >> 16) & 0x000000FF);
		dword[2] = (byte) ((i >> 8) & 0x000000FF);
		dword[3] = (byte) (i & 0x00FF);
		return dword;
	}
}
