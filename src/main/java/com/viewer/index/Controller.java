package com.viewer.index;

import com.viewer.index.download.BlobDown;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Controller {

    public Label path;
    public Label log;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void selectPath(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose the local dirctionary for FTP");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File dir = chooser.showDialog(new Stage());
        if(dir != null){
            path.setText(dir.getAbsolutePath());
        }
    }


    public void download() throws IOException, InterruptedException {
        System.out.println(path.getText());
        BlobDown down = new BlobDown(log);
        down.beginParse();
//        countDownLatch.await();
//        down.mergeFile("");
    }


}
