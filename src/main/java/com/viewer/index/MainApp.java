package com.viewer.index;


import com.viewer.index.entity.ConfigEntity;
import com.viewer.index.utils.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");


        initRootLayout();

        showPersonOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            File file = new File(System.getProperty("user.dir") + "/src/main/java/com/viewer/index/view/RootLayout.fxml");
            loader.setLocation(file.toURL());
            rootLayout = (BorderPane) loader.load();
            Menu menu = new Menu("设置");
            MenuItem setting = new MenuItem("set");

            setting.setOnAction(e -> {
                Controller controller = new Controller();
                controller.openSettingPage();
            });
            menu.getItems().add(setting);

            ((MenuBar)rootLayout.getChildren().get(0)).getMenus().add(menu);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            File file = new File(System.getProperty("user.dir") +
                    "/src/main/java/com/viewer/index/view/PersonOverview.fxml");
            loader.setLocation(file.toURL());
            AnchorPane personOverview = (AnchorPane) loader.load();
            Controller controller = loader.getController();
            controller.init();
            ConfigEntity configEntity = FileUtils.settingRead();
            if (!StringUtils.isEmpty(configEntity.getDefaultPath())) {
                controller.path.setText(configEntity.getDefaultPath());
            }
            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}