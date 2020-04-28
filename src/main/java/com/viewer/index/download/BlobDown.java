package com.viewer.index.download;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viewer.index.entity.TsEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;

public class BlobDown {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static ArrayList<Map<Integer, String>> objects = Lists.newArrayList();
    private static CountDownLatch countDownLatch = new CountDownLatch(5);
    private static ArrayBlockingQueue<TsEntity> blockingQueue = new ArrayBlockingQueue<>(16);
    private static CopyOnWriteArrayList<String> lists = new CopyOnWriteArrayList<>();

    private static String  prefix = "https://ltsbsy.qq.com/uwMROfz2r5zAoaQXGdGnCmdf646YsKpvYbT1SnTPDDjQJcI2/holMnFsnG4rxp01qem9zOQBDqCgL4hEKReeOUOoMBC3meVCQGdpY8g4ysAxypu5k3_Hv_NcNwwRg62Go1anCntXcyQZeln35ajJ0tms9Wem2dLl2ZC0B1uUD0UJ7JKo-GUeJNVc21INulqao9f_SfiiwUW4Kle51XR9MsNx1iYQ/";

    static {
        ConcurrentMap<Integer, String> map1 = Maps.newConcurrentMap();
        ConcurrentMap<Integer, String> map2 = Maps.newConcurrentMap();
        ConcurrentMap<Integer, String> map3 = Maps.newConcurrentMap();
        ConcurrentMap<Integer, String> map4 = Maps.newConcurrentMap();
        ConcurrentMap<Integer, String> map5 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map6 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map7 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map8 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map9 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map10 = Maps.newConcurrentMap();
        objects.add(map1);
        objects.add(map2);
        objects.add(map3);
        objects.add(map4);
        objects.add(map5);
//        objects.add(map6);
//        objects.add(map7);
//        objects.add(map8);
//        objects.add(map9);
//        objects.add(map10);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");
        long l = System.currentTimeMillis();
        beginParse();
//        downLoadItemTs(System.getProperty("user.dir") + "/src/test/java/resource",
//                "https://ltsbsy.qq.com/uwMROfz2r5zAoaQXGdGnCmdf646YsKpvYbT1SnTPDDjQJcI2/holMnFsnG4rxp01qem9zOQBDqCgL4hEKReeOUOoMBC3meVCQGdpY8g4ysAxypu5k3_Hv_NcNwwRg62Go1anCntXcyQZeln35ajJ0tms9Wem2dLl2ZC0B1uUD0UJ7JKo-GUeJNVc21INulqao9f_SfiiwUW4Kle51XR9MsNx1iYQ/0195_b0033tddyn9.321002.7.ts?index=195&start=1968840&end=1979760&brs=6150420&bre=7872687&ver=4", 2000);
        System.out.println("耗时:" + (System.currentTimeMillis()-l) + "ms");
    }

    private static void beginParse() throws IOException, InterruptedException {

        String name = "龙岭";
        String source = "E:\\test\\";
//        URL url = new URL("https://ltsbsy.qq.com/uwMROfz2r5zAoaQXGdGnCmdf646YsKpvYbT1SnTPDDjQJcI2/holMnFsnG4rxp01qem9zOQBDqCgL4hEKReeOUOoMBC3meVCQGdpY8g4ysAxypu5k3_Hv_NcNwwRg62Go1anCntXcyQZeln35ajJ0tms9Wem2dLl2ZC0B1uUD0UJ7JKo-GUeJNVc21INulqao9f_SfiiwUW4Kle51XR9MsNx1iYQ/b0033tddyn9.321002.ts.m3u8?ver=4");
//        URLConnection urlConnection = url.openConnection();
//        Object content = urlConnection.getContent();
//        downM3U8File((InputStream) content, name + ".txt");
        System.out.println("开始从文件中读取....");
        readM3U8ToList(name + ".txt");
        //读取下载记录
        readDownloadLog(source, name);
        System.out.println("注册重试");
//        executorService.execute(new RetryThread());
        System.out.println("开始下载....");
        downloadTsFile(source, name);
        countDownLatch.await();
        System.out.println("开始合并文件....");
        mergeFile(name);
    }

    private static void readDownloadLog(String source, String name) {
        File file = new File(source + name + "\\finishedLog.log");
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String res = null;
                while ((res = br.readLine())!=null){
                    lists.add(res);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void mergeFile(String name) throws IOException {
        String source = "E:\\test\\" + name;
        File file = new File(source);
        if(file.exists()){
            int length = file.list().length;
            File merge = new File(source + "/" + name + ".mp4");
            if(!merge.exists())
                merge.createNewFile();
            FileOutputStream fos = new FileOutputStream(merge, true);
            for(int i=0;i<length;i++){
                System.out.println("开始merge：" + source + "/" + i + ".ts");
                File sou = new File(source + "/" + i + ".ts");
                if(sou.exists()){
                    //merge
                    FileInputStream fis = new FileInputStream(sou);
                    byte[] bytes = new byte[1024];
                    int index = 0;
                    while((index=fis.read(bytes))!=-1){
                        fos.write(bytes, 0, index);
                    }
                }
            }
            fos.close();
        }
    }


    private static void downloadTsFile(String source, String name) {
        File dir = new File(source + name);
        if(!dir.exists())
            dir.mkdirs();

        for(int i=0;i<objects.size();i++){
            DownLoadTsThread downLoadTsThread = new DownLoadTsThread(i, source+name, countDownLatch);
            executorService.execute(downLoadTsThread);
        }
        executorService.shutdown();
    }

    public static void clear(){
        Iterator<Map<Integer, String>> iterator = objects.iterator();
        while (iterator.hasNext()){
            iterator.next().clear();
        }
    }

    private static void downLoadItemTs(String path, String ts, int i) {
        if(lists.contains(ts)){
            System.out.println("该ts文件已经下载完成");
            return;
        }
        File file = new File(path + "/" + i + ".ts");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileOutputStream fos = new FileOutputStream(file)) {
            Thread.sleep(2000);
            CloseableHttpClient conn = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(prefix + ts);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(2000).setConnectTimeout(2000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("accept", "*/*");
            httpGet.addHeader("connection", "Keep-Alive");
//            httpGet.addHeader("Content-Type","application/json");
            httpGet.addHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            CloseableHttpResponse response = conn.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();

            //download
            byte[] bytes = new byte[1024];
            System.out.println("开始下载" + i + ".ts文件");
            int index = 0;
            while ((index=inputStream.read(bytes))!=-1){
                fos.write(bytes, 0, index);
            }
            //下载完成写入文件中
            wirteDownloadLogToFile(path, ts, i);

        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("error:" + e.getLocalizedMessage());
            //将错误的文件删除并放到重试队列中
            if (file.delete()) {
                TsEntity entity = new TsEntity();
                entity.setCount(i);
                entity.setPath(path);
                entity.setTs(ts);
                System.out.println(entity.getCount() + ".ts下载失败，已加入重试队列");
                blockingQueue.add(entity);
            }
        }
    }

    private static void wirteDownloadLogToFile(String path, String ts, int count) throws IOException {
        File file = new File(path + "\\finishedLog.log");
        if(!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write((count + "=" +ts + "\r\n").getBytes());
        fos.close();
    }


    public static void readM3U8ToList(String fileName) throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/java/resource/";
        fileName = source + fileName;
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        ArrayList<String> lists = Lists.newArrayList();
        String ts = null;
        while((ts=reader.readLine())!=null){
            lists.add(ts);
        }
        //获取到所有的url后将其分配到不同的list中
        int mod = 0;
        int size = lists.size();
        for(int i=0;i<size;i++){
            mod = i%objects.size();
            Map<Integer, String> stringMap = objects.get(mod);
            stringMap.put(i, lists.get(i));
        }
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
            if(res.contains("ts")){
                res = res + "\r\n";
                fos.write(res.getBytes());
            }
        }
        fos.close();

        System.out.println("结束m3u8文件解析");
    }

static class DownLoadTsThread implements Runnable{
    private int index;
    private String path;
    private CountDownLatch countDownLatch;

    public DownLoadTsThread(int index, String path, CountDownLatch countDownLatch) {
        this.index = index;
        this.path = path;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        Map<Integer, String> stringMap = objects.get(index);
        stringMap.forEach((key, value) -> {
            downLoadItemTs(path, value, key);
        });
        countDownLatch.countDown();
        clear();
    }
}

static class RetryThread extends Thread {

    @Override
    public void run() {
        super.run();
        while (true){
            if(blockingQueue.isEmpty()){
                try {
                    Thread.sleep(10000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TsEntity entity = blockingQueue.poll();
            System.out.println("开始重试" + entity.getCount() + ".ts");
            downLoadItemTs(entity.getPath(), entity.getTs(), entity.getCount());
        }
    }
}

}

