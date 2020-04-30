package com.viewer.index.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadUtils {

    public static void getM3U8File(String m3u8, String name) throws IOException {
//        URL url = new URL("https://valipl.cp31.ott.cibntv.net/67743098ABF3D7193E7C83E7F/03000900005EA76DE0401630501705661E4767-CB47-406D-B290-B60F92D8C015.m3u8?ccode=0502&duration=6677&expire=18000&psid=548c78134c5e7b69afc9dddfb241a915&ups_client_netip=db85aaae&ups_ts=1588220666&ups_userid=&utid=r5%2F8FZ0M3lACAduFqkxnP2Du&vid=XNDYxNzc3Njg2MA%3D%3D&vkey=B055bca66c20c9fb71601d2f0bdd6b6a7&sm=1&operate_type=1&dre=u37&si=73&eo=0&dst=1&iv=0&s=f1c87a52dcf011e5b522&type=mp4hd3v3&bc=2");
        URL url = new URL(m3u8);
        URLConnection urlConnection = url.openConnection();
        Object content = urlConnection.getContent();
        downM3U8File((InputStream) content, name + ".m3u8");
    }


    private static void downM3U8File(InputStream content, String fileName) throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/java/resource/";
        File dir = new File(source);
        if(!dir.exists())
            dir.mkdirs();
        File sourceF = new File(source + fileName);
        if(!sourceF.exists())
            sourceF.createNewFile();

        FileOutputStream fos = new FileOutputStream(sourceF);
        System.out.println("开始m3u8文件解析");
        InputStream inputStream = content;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String res = null;
        while((res = reader.readLine()) != null){
            res = res + "\r\n";
            fos.write(res.getBytes());
//            if(res.contains("ts") || res.contains("EXTINF")){
//            }
        }
        fos.close();

        System.out.println("结束m3u8文件解析");
    }


}
