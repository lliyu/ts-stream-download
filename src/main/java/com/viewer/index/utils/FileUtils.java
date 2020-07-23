package com.viewer.index.utils;

import com.alibaba.fastjson.JSON;
import com.viewer.index.entity.ConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件操作相关工具类
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static ConfigEntity settingRead(){
        String target = System.getProperty("user.dir") + "/src/main/resources/settings.json";

        File file = new File(target);
        if(!file.exists()){
            try {
                logger.info("-----配置文件不存在，创建新的配置文件------");
                file.createNewFile();
                ConfigEntity configEntity = new ConfigEntity();
                configEntity.setDefaultPath("G:\\Download");
                //将值写入配置文件中
                writeFile(file, JSON.toJSONString(configEntity));
                return configEntity;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileReader fr = null;
        StringBuilder sb = new StringBuilder();
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            while ((str = br.readLine())!=null){
                sb.append(str);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConfigEntity entity = JSON.parseObject(sb.toString(), ConfigEntity.class);

        return entity;
    }

    public static void writeFile(File file, String value){
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(value.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(settingRead());
    }
}
