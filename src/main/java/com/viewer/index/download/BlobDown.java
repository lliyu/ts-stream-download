package com.viewer.index.download;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.viewer.index.entity.IndexPageEntity;
import com.viewer.index.entity.TsEntity;
import com.viewer.index.utils.FileSortUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
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

    private IndexPageEntity pageEntity;

    public BlobDown(IndexPageEntity pageEntity) {
        this.pageEntity = pageEntity;
    }

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static ArrayList<Map<Integer, String>> objects = Lists.newArrayList();
    private static ArrayList<ConcurrentLinkedQueue<TsEntity>> queues = Lists.newArrayList();
//    private static CountDownLatch countDownLatch = new CountDownLatch(5);
    private static LinkedBlockingQueue<TsEntity> blockingQueue = new LinkedBlockingQueue<TsEntity>(160);
    private static CopyOnWriteArrayList<String> lists = new CopyOnWriteArrayList<>();


    public int total = 0;

    public int currentIndex = 0;

    private static String  prefix = "https://cdn2.shayubf.com/";

    static {
//        ConcurrentMap<Integer, String> map1 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map2 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map3 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map4 = Maps.newConcurrentMap();
//        ConcurrentMap<Integer, String> map5 = Maps.newConcurrentMap();
//        objects.add(map1);
//        objects.add(map2);
//        objects.add(map3);
//        objects.add(map4);
//        objects.add(map5);
        ConcurrentLinkedQueue<TsEntity> queue1 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue2 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue3 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue4 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue5 = Queues.newConcurrentLinkedQueue();
        queues.add(queue1);
        queues.add(queue2);
        queues.add(queue3);
        queues.add(queue4);
        queues.add(queue5);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");
        long l = System.currentTimeMillis();

//        beginParse();
//        downLoadItemTs(System.getProperty("user.dir") + "/src/test/java/resource",
//                "https://videony.rhsj520.com:8091/20191017/ooe7se8h/1500kb/hls/DmksUpK7.ts", 2000);
        BlobDown blobDown = new BlobDown(null);
//        blobDown.beginParse();
        blobDown.downLoadItemTs("/Users/liyu/Downloads/",
                "https://cdn.iicgs.org/20200506/5c8624a0cd19f2f2c1234f2239631174.mp4/segment-8-v1-a1.ts", 1);

        System.out.println("耗时:" + (System.currentTimeMillis()-l) + "ms");
    }

    public void beginParse() throws IOException, InterruptedException {
        long l = System.currentTimeMillis();
        String name = pageEntity.getName().getText();
        String source = pageEntity.getPath().getText();
        String m3u8 = pageEntity.getM3u8().getText();
        URL url = new URL(m3u8);
        URLConnection urlConnection = url.openConnection();
        Object content = urlConnection.getContent();
        downM3U8File((InputStream) content, name + ".m3u8");
        System.out.println("开始从文件中读取....");
        readM3U8ToList(name + ".m3u8", 0);

        totalFinishedCount();
        //读取下载记录
        readDownloadLog(source, name);
        System.out.println("注册重试");
        executorService.execute(new RetryThread());
        System.out.println("开始下载....");
        downloadTsFile(source, name);
//        countDownLatch.await();
//        System.out.println("开始合并文件....");
//        mergeFile(name);
        System.out.println("耗时:" + (System.currentTimeMillis()-l) + "ms");
    }

    private void totalFinishedCount() {
        queues.stream().forEach(map -> {
            total += map.size();
        });
    }

    private void readDownloadLog(String source, String name) {
        Set<String> finishedBlock = new HashSet<>();
        File file = new File(source + "/" + name + "/finishedLog.log");
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String res = null;
                while ((res = br.readLine())!=null){
                    String[] split = res.split("=");
                    lists.add(split[1]);
                    finishedBlock.add(split[0]);
                    currentIndex++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File dir = new File(source + name);
        if(dir.exists() && dir.isDirectory()){
            Set<String> existBlock = new HashSet<>();
            String[] list = dir.list();
            for(String str : list){
                String[] names1 = str.split("\\\\");
                String[] split1 = names1[names1.length-1].split("\\.");
                existBlock.add(split1[0]);
            }

            existBlock.removeAll(finishedBlock);
            existBlock.stream().forEach(path -> {
                File res = new File(source + name + "/" + path + ".ts");
                res.deleteOnExit();
            });
        }
    }

    public void mergeFile(String name) throws IOException {
        //删除日志
        String source = pageEntity.getPath().getText() + "/" + name;
        File log = new File(source + "/finishedLog.log");
        if(log.exists())
            log.deleteOnExit();
        File file = new File(source);
        if(file.exists()){
            File[] files = file.listFiles();
            FileSortUtils.sort(files);
            File merge = new File(source + "/" + ".mp4");
            if(!merge.exists())
                merge.createNewFile();
            FileOutputStream fos = new FileOutputStream(merge, true);
            for(int i=0;i<files.length;i++){
                System.out.println("开始merge：" + files[i].getName());
                File sou = files[i];
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


    private void downloadTsFile(String source, String name) {
        File dir = new File(source + "/" + name);
        if(!dir.exists())
            dir.mkdirs();

        for(int i=0;i<queues.size();i++){
            DownLoadTsThread downLoadTsThread = new DownLoadTsThread(i, source + "/" +name);
            executorService.execute(downLoadTsThread);
        }
        executorService.shutdown();
    }

//    public void clear(){
//        Iterator<Map<Integer, String>> iterator = queues.iterator();
//        while (iterator.hasNext()){
//            iterator.next().clear();
//        }
//    }

    private void downLoadItemTs(String path, String ts, int i) {
        if(lists.contains(ts)){
//            System.out.println("该ts文件已经下载完成");
            return;
        }
        File file = new File(path + "/" + i + ".ts");
//        System.out.println("开始下载" + i + ".ts文件");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileOutputStream fos = new FileOutputStream(file)) {
            Thread.sleep(100);
            CloseableHttpClient conn = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(pageEntity.getPrefix().getText() + ts);
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
            byte[] bytes = new byte[1024*10];
            int index = 0;
            while ((index=inputStream.read(bytes))!=-1){
                fos.write(bytes, 0, index);
            }
            //下载完成写入文件中
//            wirteDownloadLogToFile(path, ts, i);

        }catch (IllegalStateException e){
            System.out.println("url不合法");
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("error:" + e.getLocalizedMessage());
            //将错误的文件删除并放到重试队列中
            if (file.delete()) {
                TsEntity entity = new TsEntity();
                entity.setCount(i);
                entity.setPath(path);
                entity.setTs(ts);
//                System.out.println(entity.getCount() + ".ts下载失败，已加入重试队列");
                blockingQueue.add(entity);
            }
        }
    }

    private void wirteDownloadLogToFile(String path, String ts, int count) throws IOException {
        File file = new File(path + "/finishedLog.log");
        if(!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write((count + "=" +ts + "\r\n").getBytes());
        currentIndex++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //更新JavaFX的主线程的代码放在此处
                pageEntity.getLog().setText(currentIndex + "/" + total);
            }
        });
        fos.close();
    }

    public void readM3U8ToList(String fileName, double sec) throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/java/resource/";
        fileName = source + fileName;
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        ArrayList<String> lists = Lists.newArrayList();
        String ts = null;
        double length = 0;
        while((ts=reader.readLine())!=null){
            if(ts.contains("EXTINF")){
                length += Double.parseDouble(ts.replace("#EXTINF:", "").replace(",", ""));
            }
            if(length<sec)
                continue;
            if(ts.contains("ts"))
                lists.add(ts);
        }
        //获取到所有的url后将其分配到不同的list中
        int mod = 0;
        int size = lists.size();
        for(int i=0;i<size;i++){
            mod = i%queues.size();
            ConcurrentLinkedQueue<TsEntity> tsEntities = queues.get(mod);
            TsEntity tsEntity = new TsEntity();
            tsEntity.setCount(i);
            tsEntity.setTs(lists.get(i));
            tsEntities.add(tsEntity);
        }
    }


    public void downM3U8File(InputStream content, String fileName) throws IOException {
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

class DownLoadTsThread implements Runnable{
    private int index;
    private String path;
    private CountDownLatch countDownLatch;

    public DownLoadTsThread(int index, String path ) {
        this.index = index;
        this.path = path;
//        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
//        Map<Integer, String> stringMap = objects.get(index);
//        stringMap.forEach((key, value) -> {
//            downLoadItemTs(path, value, key);
//        });
        ConcurrentLinkedQueue<TsEntity> tsEntities = queues.get(index);
        while (tsEntities.size() > 0) {
            TsEntity poll = tsEntities.poll();
            downLoadItemTs(path, poll.getTs(), poll.getCount());
            System.out.println("queue:" + index + "剩余数量：" + tsEntities.size());
        }
//        countDownLatch.countDown();
//        clear();
    }
}

class RetryThread extends Thread {

    @Override
    public void run() {
        super.run();
        while (true){
            if(blockingQueue.isEmpty()){
                try {
                    Thread.sleep(5000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TsEntity entity = blockingQueue.poll();
            System.out.println("开始重试" + entity.getCount() + ".ts");
            System.out.println("当前重试队列中剩余：" + blockingQueue.size());
            downLoadItemTs(entity.getPath(), entity.getTs(), entity.getCount());
        }
    }
}

}

