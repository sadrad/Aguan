package Aguan.files;

import Aguan.TheMatrix.TheMatrix;
import java.io.*;
import java.text.*;

public class gro extends coordinate {
       public PrintWriter pgro;
       public String[] spacer = {""," ","  ","   ","    ",
                              "     ","      ","       ",
                              "        ","         ","          ",
                              "           ","            ","             ","              "};
       public gro(String gro_file){
              super(gro_file);
              super.openOutputWriter(gro_file);
              pgro = super.PW;
       }
       public void createGROFile(TheMatrix TM){
          pgro.println("Generated by Aguan");
          if(TM.molType.equals("tip5p") || TM.molType.equals("st2")){
               pgro.printf("%5d\n",(TM.nMol*TM.sitesMol));
               int atomCount = 1;
               int molCount = 0;
               molCount = 1;
               String atomTyp = "";
               boolean hydrogenFlag = true;
               boolean lonePairFlag = true;
               for(int a = 0; a < TM.nMol*TM.sitesMol; a++){
                   if(TM.atomType[a] != 1){
                      if(TM.atomType[a] == 2){
                         atomTyp = "OW";
                      }else if(TM.atomType[a] == 3){
                         if(hydrogenFlag){
                            atomTyp = "HW1";
                            hydrogenFlag = false;
                         }else{
                            atomTyp = "HW2";
                            hydrogenFlag = true;
                         }
                      }else if(TM.atomType[a] == 4){
                         if(lonePairFlag){
                            atomTyp = "LP1";
                            lonePairFlag = false;
                         }else{
                            atomTyp = "LP2";
                            lonePairFlag = true;
                         }
                      }
                      pgro.printf("%5dSOL%7s%5d",molCount,atomTyp,atomCount);
                      pgro.printf("%8.3f%8.3f%8.3f\n",(TM.rxs[a]*TM.ro),(TM.rys[a]*TM.ro),(TM.rzs[a]*TM.ro));
                      atomCount++;
                      if((TM.molType.equals("tip5p") || TM.molType.equals("st2")) && atomTyp.equals("LP2")){
                          molCount++;
                      }
                   }else{
                      molCount++;
                   }
               } 
          }else if(TM.molType.equals("tip3p") || TM.molType.equals("tip4p")){
               pgro.printf("%5d\n",(TM.nMol*TM.nCharges));
               int atomCount = 1;
               int molCount = 0;
               String atomTyp = "";
               boolean hydrogenFlag = true;
               boolean lonePairFlag = true;
               int tip4p_molCount;
               tip4p_molCount = 0;
               double tip4p_X, tip4p_Y, tip4p_Z;
               tip4p_X = tip4p_Y = tip4p_Z = 0;
               for(int a = 0; a < TM.nMol*TM.sitesMol; a++){
                            if(TM.atomType[a] == 1){
                               tip4p_molCount = molCount;
                               tip4p_X = TM.rxs[a]*TM.ro/10;   tip4p_Y = TM.rys[a]*TM.ro/10;  tip4p_Z = TM.rzs[a]*TM.ro/10;
                               molCount++;
                      }else if(TM.atomType[a] == 2){
                            atomTyp = "OW";
                            pgro.printf("%5dSOL%7s%5d",molCount,atomTyp,atomCount);
                            pgro.printf("%8.3f%8.3f%8.3f\n",(TM.rxs[a]*TM.ro/10),(TM.rys[a]*TM.ro/10),(TM.rzs[a]*TM.ro/10));
                            atomCount++;
                      }else if(TM.atomType[a] == 3){
                         if(hydrogenFlag){
                            atomTyp = "HW1";
                            hydrogenFlag = false;
                            pgro.printf("%5dSOL%7s%5d",molCount,atomTyp,atomCount);
                            pgro.printf("%8.3f%8.3f%8.3f\n",(TM.rxs[a]*TM.ro/10),(TM.rys[a]*TM.ro/10),(TM.rzs[a]*TM.ro/10));
                            atomCount++;
                         }else{
                            atomTyp = "HW2";
                            hydrogenFlag = true;
                            pgro.printf("%5dSOL%7s%5d",molCount,atomTyp,atomCount);
                            pgro.printf("%8.3f%8.3f%8.3f\n",(TM.rxs[a]*TM.ro/10),(TM.rys[a]*TM.ro/10),(TM.rzs[a]*TM.ro/10));
                            atomCount++;
                            if(TM.molType.equals("tip4p")){
                               pgro.printf("%5dSOL%7s%5d",tip4p_molCount,"MW",atomCount);
                               pgro.printf("%8.3f%8.3f%8.3f\n",tip4p_X,tip4p_Y,tip4p_Z);
                               atomCount++;
                            }
                         }
                      }
                   }
               }
          
          pgro.printf("%10.5f%10.5f%10.5f\n",TM.regionX*TM.ro/10,TM.regionY*TM.ro/10,TM.regionZ*TM.ro/10);
    }
}
