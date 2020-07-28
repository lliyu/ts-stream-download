package com.viewer.index;

import com.alibaba.fastjson.JSON;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.viewer.index.download.BlobDown;
import com.viewer.index.entity.ConfigEntity;
import com.viewer.index.entity.IndexPageEntity;
import com.viewer.index.entity.M3U8InfoDTO;
import com.viewer.index.parse.TimesCalculate;
import com.viewer.index.utils.FileUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

        ConfigEntity configEntity = FileUtils.settingRead();

        Stage window = new Stage();
        window.setTitle("settings");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinHeight(300);
        window.setMinWidth(400);

        VBox buttonBox = new VBox(10);
        VBox layout = new VBox(15);
        HBox pathBox = new HBox(10);


        Label lable = new Label("默认路径：");

        Label pathValue = new Label();
        pathValue.setText(configEntity.getDefaultPath());

        JFXButton choosePath = new JFXButton("选择路径");
        choosePath.setStyle("fx-text-fill:WHITE;-fx-background-color:#E8E8E8;-fx-font-size:14px;");
        choosePath.setOnAction(event -> {
            Stage stage = new Stage();

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择默认保存路径");

            File dir = directoryChooser.showDialog(stage);
            if (dir!=null) {
                pathValue.setText(dir.getAbsolutePath());
            }
        });

        JFXButton save = new JFXButton("保存");
        save.setStyle("fx-text-fill:WHITE;-fx-background-color:#E8E8E8;-fx-font-size:14px;");
        save.setOnAction(event -> {
            //将值保存到配置文件中
            configEntity.setDefaultPath(pathValue.getText());
            String target = System.getProperty("user.dir") + "/src/main/resources/settings.json";

            File file = new File(target);
            FileUtils.writeFile(file, JSON.toJSONString(configEntity));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.titleProperty().set("信息");
            alert.headerTextProperty().set("配置保存成功");
            alert.showAndWait();

        });

        save.setLayoutX(100);
        save.setAlignment(Pos.BOTTOM_LEFT);

        pathBox.setSpacing(15);
        pathBox.setAlignment(Pos.CENTER);
        pathBox.setLayoutY(10);
        pathBox.getChildren().addAll(lable, pathValue, choosePath);
        pathBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.getChildren().addAll(save);
        buttonBox.setPadding(new Insets(20, 10, 10, 20));
        layout.getChildren().addAll(pathBox, buttonBox);
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
    }

    public void createNewDownTask(){
        //新建一个面板用于创建下载任务

        Stage downTask = new Stage();
        downTask.setWidth(250);
        downTask.setHeight(250);

        VBox layout = new VBox(10);
        HBox m3u8 = new HBox();
        HBox taskName = new HBox();
        HBox buttons = new HBox();

        Label m3u8Key = new Label("m3u8");
        JFXTextField m3u8Value = new JFXTextField();
        m3u8.getChildren().addAll(m3u8Key, m3u8Value);
        m3u8.setPadding(new Insets(20, 0, 0, 20));
        m3u8.setSpacing(10);

        Label taskKey = new Label("名称");
        JFXTextField taskValue = new JFXTextField();
        taskName.getChildren().addAll(taskKey, taskValue);
        taskName.setPadding(new Insets(0, 0, 0, 20));
        taskName.setSpacing(10);

        JFXButton down = new JFXButton("下载");
        JFXButton close = new JFXButton("退出");

        down.setOnAction(event -> {

        });

        close.setOnAction(event -> downTask.close());
        down.setStyle("fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
        close.setStyle("fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
        buttons.getChildren().addAll(down, close);
        buttons.setPadding(new Insets(0, 0, 0, 20));
        buttons.setSpacing(10);

        layout.getChildren().addAll(m3u8, taskName, buttons);
        Scene scene = new Scene(layout);

        downTask.setScene(scene);

        downTask.showAndWait();

    }


}
