package com.viewer.index.download;

import java.io.*;

public class M3U8Split {
    public static void main(String[] args) {
        double v = calTotalTimes();
        System.out.println("该视频总时长为：");
        System.out.println(v + "s");
        System.out.println(v/60 + "min");
    }

    public static double calTotalTimes(){
        String source = System.getProperty("user.dir") + "/src/test/java/resource/" + "test.m3u8";
        File dir = new File(source);
        if(!dir.exists())
            return 0;
        double sec = 0;
        try(FileInputStream fis = new FileInputStream(dir)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String res = null;
            while((res=br.readLine())!=null){
                if(res.contains("EXTINF")){
                    res = res.replace(",", "").replace("#EXTINF:", "");
                    double v = Double.parseDouble(res);
                    sec += v;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sec = Math.ceil(sec);
        return sec;
    }
}
