package com.viewer.index.download;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.viewer.index.entity.DownItemControl;
import com.viewer.index.entity.DownTask;
import com.viewer.index.entity.IndexPageEntity;
import com.viewer.index.entity.TsEntity;
import com.viewer.index.utils.FileSortUtils;
import com.viewer.index.utils.FileUtils;
import javafx.application.Platform;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

public class BlobDown {

    private static Logger logger = LoggerFactory.getLogger(BlobDown.class);

    private IndexPageEntity pageEntity;

    public BlobDown(IndexPageEntity pageEntity) {
        this.pageEntity = pageEntity;
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ArrayList<ConcurrentLinkedQueue<TsEntity>> queues = Lists.newArrayList();
    private CountDownLatch countDownLatch = new CountDownLatch(4);
    private CopyOnWriteArrayList<String> lists = new CopyOnWriteArrayList<>();
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private volatile int circleCount = 0;

    public int total = 0;

    public int currentIndex = 0;

    private static String  prefix = "https://cdn2.shayubf.com/";

    {
        ConcurrentLinkedQueue<TsEntity> queue1 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue2 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue3 = Queues.newConcurrentLinkedQueue();
        ConcurrentLinkedQueue<TsEntity> queue4 = Queues.newConcurrentLinkedQueue();
        queues.add(queue1);
        queues.add(queue2);
        queues.add(queue3);
        queues.add(queue4);
    }

    public static void main(String[] args) throws IOException {
        long l = System.currentTimeMillis();

        BlobDown blobDown = new BlobDown(null);
//        blobDown.beginParse();
//        blobDown.downLoadItemTs("/Users/liyu/Downloads/",
//                "https://cdn.iicgs.org/20200506/5c8624a0cd19f2f2c1234f2239631174.mp4/segment-8-v1-a1.ts", 1);
        blobDown.mergeFile("806");
        System.out.println("耗时:" + (System.currentTimeMillis()-l) + "ms");
    }

    public void beginParse() throws IOException, InterruptedException {
        pageEntity.getProgressBar().setProgress(0);
        long l = System.currentTimeMillis();
        String name = pageEntity.getName().getText();
        String source = pageEntity.getPath().getText();
        String m3u8 = pageEntity.getM3u8().getText();
        logger.info("===============开始执行‘" + name + "'的下载流程=======================");
        URL url = new URL(m3u8);
        URLConnection urlConnection = url.openConnection();
        Object content = urlConnection.getContent();
        downM3U8File((InputStream) content, name + ".m3u8");
        logger.info("开始从文件中读取:" + name + ".m3u8");
        readM3U8ToList(name + ".m3u8", 0, null);

        totalFinishedCount();
        //读取下载记录
        readDownloadLog(source, name);

        System.out.println("开始下载....");
        logger.info("开始下载ts文件：" + name);
        downloadTsFile(source, name, null);
        countDownLatch.await();
        logger.info("视频文件分段已经下载完成，现在开始合并");
        mergeFile(name);
        pageEntity.getProgressBar().setProgress(1);
        pageEntity.getLog().setText("100%");
        logger.info("视频文件已下载完成，本次下载共耗时：" + (System.currentTimeMillis()-l)/1000 + "s");
    }

    public void mutilBeginParse(DownTask task, DownItemControl control) throws IOException, InterruptedException {
        control.getProgressBar().setProgress(0);
        long l = System.currentTimeMillis();
        String source = FileUtils.settingRead().getDefaultPath();
        logger.info("===============开始执行‘" + task.getName() + "'的下载流程=======================");
        URL url = new URL(task.getM3u8());
        URLConnection urlConnection = url.openConnection();
        Object content = urlConnection.getContent();
        downM3U8File((InputStream) content, task.getName() + ".m3u8");
        logger.info("开始从文件中读取:" + task.getName() + ".m3u8");
        readM3U8ToList(task.getName() + ".m3u8", 0, task);

        totalFinishedCount();
        //读取下载记录
        readDownloadLog(source, task.getName());

        System.out.println("开始下载....");
        logger.info("开始下载ts文件：" + task.getName());
        downloadTsFile(source, task.getName(), control);
        countDownLatch.await();
        logger.info("视频文件分段已经下载完成，现在开始合并");
        mergeFile(task.getName());
        control.getProgressBar().setProgress(1);
        control.getPercentage().setText("100%");
        logger.info("视频文件已下载完成，本次下载共耗时：" + (System.currentTimeMillis()-l)/1000 + "s");
    }

    private void totalFinishedCount() {
        queues.stream().forEach(map -> {
            total += map.size();
        });
    }

    private void readDownloadLog(String source, String name) {
        Set<String> finishedBlock = new HashSet<>();
        File file = new File(source + "//" + name + "//finishedLog.log");
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
//        String source = pageEntity.getPath().getText() + "/" + name;
        String path = "G:\\Download\\";
        String source = path + name;
        File log = new File(source + "/finishedLog.log");
        if(log.exists())
            log.deleteOnExit();
        File file = new File(source);
        if(file.exists()){
            File[] files = file.listFiles();
            FileSortUtils.sort(files);
            File merge = new File(path + name + ".mp4");
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
                    fis.close();
                    boolean delete = sou.delete();
                    if(delete){
                        System.out.println("删除文件成功");
                    }else {
                        System.out.println("删除文件失败");
                    }
                }
            }
            file.delete();
            fos.close();
        }
    }


    private void downloadTsFile(String source, String name, DownItemControl control) {
        File dir = new File(source + "/" + name);
        if(!dir.exists())
            dir.mkdirs();
        for(int i=0;i<queues.size();i++){
            DownLoadTsThread downLoadTsThread =
                    new DownLoadTsThread(i, source + "/" +name, countDownLatch, control);
            executorService.execute(downLoadTsThread);
        }
//        executorService.shutdown();
    }

    private void downLoadItemTs(TsEntity entity, DownItemControl control) {
        if(lists.contains(entity.getTs())){
            return;
        }
        File file = new File(entity.getPath() + "/" + entity.getCount() + ".ts");
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
            HttpGet httpGet = new HttpGet(entity.getM3u8Prefix() + entity.getTs());
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(2000).setConnectTimeout(2000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("accept", "*/*");
            httpGet.addHeader("connection", "Keep-Alive");
            httpGet.addHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            CloseableHttpResponse response = conn.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();

            //download
            byte[] bytes = new byte[1024*10];
            int index = 0;
            while ((index=inputStream.read(bytes))!=-1){
                fos.write(bytes, 0, index);
            }
//            下载完成写入文件中
            wirteDownloadLogToFile(entity, control);

        }catch (IllegalStateException e){
            logger.error("url不合法：" + e.getLocalizedMessage() + "---" + entity.getTs());
        } catch (Exception e) {
            //将错误的文件删除并放到重试队列中
            if (file.delete()) {
                if (entity.getRetry()<=3) {
                    logger.error("error:" + e.getLocalizedMessage() + "--" + entity);
                    queues.get(circleCount++).add(entity);
                    if(circleCount >= (queues.size()-1))
                        circleCount = 0;
                    logger.info(entity.getM3u8Prefix() + entity.getTs() + "第" + (entity.getRetry()+1)
                            +"次下载失败，已加入重试队列");
                }else {
                    logger.info(entity.getM3u8Prefix() + entity.getTs() + "已多次下载失败，已丢弃");
                }
            }
        }
    }

    private void wirteDownloadLogToFile(TsEntity entity, DownItemControl control) throws IOException {
        File file = new File(entity.getPath() + "//finishedLog.log");
        if(!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write((entity.getCount() + "=" + entity.getTs() + "\r\n").getBytes());
        currentIndex++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //更新JavaFX的主线程的代码放在此处
                control.getPercentage().setText(decimalFormat.format((currentIndex*1.0/total)*100) + "%");
                control.getProgressBar().setProgress((currentIndex*1.0)/total);
            }
        });
        fos.close();
    }

    /**
     *
     * @param fileName
     * @param sec 读取指定时长的视频
     * @throws IOException
     */
    public void readM3U8ToList(String fileName, double sec, DownTask task) throws IOException {
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
            tsEntity.setM3u8Prefix(task.getPrefix());
            tsEntities.add(tsEntity);
        }
    }


    public void downM3U8File(InputStream content, String fileName) throws IOException {
        String source = System.getProperty("user.dir") + "/src/test/java/resource/";
        File dir = new File(source);
        if(!dir.exists())
            dir.mkdirs();
        File sourceF = new File(source + fileName);
        if(sourceF.exists()){
            logger.info(sourceF.getName() + "已经存在，不再重复下载");
            return;
        }

        sourceF.createNewFile();

        FileOutputStream fos = new FileOutputStream(sourceF);
        logger.info(sourceF.getName() + "文件不存在，从给定的url地址开始下载");
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

        logger.info("文件解析完成，" + sourceF.getName() + "已经下载");
    }

class DownLoadTsThread implements Runnable{
    private int index;
    private String path;
    private CountDownLatch countDownLatch;
    private DownItemControl control;

    public DownLoadTsThread(int index, String path, CountDownLatch countDownLatch, DownItemControl control) {
        this.index = index;
        this.path = path;
        this.countDownLatch = countDownLatch;
        this.control = control;
    }

    @Override
    public void run() {
        ConcurrentLinkedQueue<TsEntity> tsEntities = queues.get(index);
        while (tsEntities.size() > 0) {
            TsEntity poll = tsEntities.poll();
            poll.setPath(path);
            downLoadItemTs(poll, control);
            System.out.println("queue:" + index + "剩余数量：" + tsEntities.size());
        }
        logger.info("queue:" + index + "-已经全部下载完毕");
        countDownLatch.countDown();
    }
}
}

