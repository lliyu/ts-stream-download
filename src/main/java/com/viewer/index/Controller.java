package com.viewer.index;

import com.alibaba.fastjson.JSON;
import com.viewer.index.download.BlobDown;
import com.viewer.index.entity.IndexPageEntity;
import com.viewer.index.entity.M3U8InfoDTO;
import com.viewer.index.parse.TimesCalculate;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

public class Controller {

    public Label path;
    public Label log;
    public TextField m3u8;
    public TextField name;
    public TextField prefix;
    public TextArea info;
    public ProgressBar progressBar;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void init(){
        this.path.setText("G:\\Download");
    }

    public void selectPath(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose the local dirctionary for FTP");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File dir = chooser.showDialog(new Stage());
        if(dir != null){
            path.setText(dir.getAbsolutePath());
        }
    }


    public void download() {
        IndexPageEntity pageEntity = new IndexPageEntity();
        pageEntity.setPath(path);
        pageEntity.setM3u8(m3u8);
        pageEntity.setLog(log);
        pageEntity.setName(name);
        pageEntity.setPrefix(prefix);
        pageEntity.setProgressBar(progressBar);
        Runnable runnable = () -> {
            try {
                BlobDown down = new BlobDown(pageEntity);
                down.beginParse();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void parse() throws IOException {
        String m3u8Text = m3u8.getText();
        String nameText = name.getText();
        IndexPageEntity pageEntity = new IndexPageEntity();
        pageEntity.setM3u8(m3u8);
        pageEntity.setName(name);
        BlobDown down = new BlobDown(pageEntity);


        if(!StringUtils.isEmpty(m3u8Text)){
            URL url = new URL(m3u8Text);
            URLConnection urlConnection = url.openConnection();
            Object content = urlConnection.getContent();
            down.downM3U8File((InputStream) content, nameText + ".m3u8");

            M3U8InfoDTO infoDTO = TimesCalculate.calAllTimes(nameText);
            info.setText(JSON.toJSONString(infoDTO));
        }
    }
    
    public void openSettingPage(){
        Stage window = new Stage();
        window.setTitle("settings");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinHeight(150);
        window.setMinWidth(300);

        Label lable = new Label("默认路径：");
        VBox layout = new VBox(10);

        FileChooser defaultPath = new FileChooser();
        Button button = new Button("选择路径");
        button.setOnAction(event -> {
            Stage stage = new Stage();

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择默认保存路径");
//            directoryChooser.setInitialDirectory("");

            File dir = directoryChooser.showDialog(stage);
            if (dir!=null) {
                System.out.println(dir.getAbsolutePath());
            }
        });

        Label pathValue = new Label();

        layout.getChildren().addAll(lable,pathValue, button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
    }


}
