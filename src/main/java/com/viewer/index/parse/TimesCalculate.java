package com.viewer.index.parse;

import com.viewer.index.entity.M3U8InfoDTO;

import java.io.*;

/**
 * 用于计算获取的文件结构
 */
public class TimesCalculate {

    public static M3U8InfoDTO calAllTimes(String name) throws IOException {
        M3U8InfoDTO infoDTO = new M3U8InfoDTO();
        String source = System.getProperty("user.dir") + "/src/test/java/resource/";
        File dir = new File(source);
        if(!dir.exists())
            dir.mkdirs();

        File sourceF = new File(source + name + ".m3u8");
        if(!sourceF.exists())
            sourceF.createNewFile();

        FileInputStream fis = new FileInputStream(sourceF);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String res = null;
        double sec = 0;
        double size = 0;
        while ((res=reader.readLine())!=null){
            if(res.contains("EXTINF")){
                res = res.replace("#EXTINF:", "").replace(",", "");
                try {
                    sec += Double.parseDouble(res);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }else if(res.contains("FILESIZE")){
                res = res.replace("#EXT-X-PRIVINF:FILESIZE=", "");
                try {
                    size += (Double.parseDouble(res)/1024);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
        infoDTO.setSec(Math.ceil(sec)/60);
        infoDTO.setSize(size/1024);
        return infoDTO;
    }
}
