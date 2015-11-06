package edu.cmu.pocketsphinx.demo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;


public class SaveName {
    public void writeName(String name, File file) throws IOException {
//        File file = new File("src/main/assets/sync/digits.gram");
        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        String temp;
        String result = "";
        StringBuilder fileStringBuffer = new StringBuilder();
        temp = br.readLine();
        while (temp!=null){
            fileStringBuffer.append(temp);
            fileStringBuffer.append("\r\n");
            temp = br.readLine();
        }
        br.close();
        String fileString = fileStringBuffer.toString();
        int index = fileString.indexOf("<keyword> = ");
        if (index!=-1){
            for (int j = index; j<fileString.length();j++){
                if (fileString.charAt(j)==';'){
                    result = fileString.substring(0,j)+"|" + name +
                            fileString.substring(j);
                }
            }
        }
        bw.write(result);
        bw.flush();
        bw.close();

    }
}
