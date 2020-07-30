package com.viewer.index.entity;

import com.jfoenix.controls.JFXProgressBar;
import javafx.scene.control.Label;

/**
 * 页面下载项的控件实体
 */
public class DownItemControl {

    private Label tile;//任务的名称
    private JFXProgressBar progressBar;//进度条
    private Label percentage;//百分比进度

    public DownItemControl(Label tile, JFXProgressBar progressBar, Label percentage) {
        this.tile = tile;
        this.progressBar = progressBar;
        this.percentage = percentage;
    }

    public Label getTile() {
        return tile;
    }

    public void setTile(Label tile) {
        this.tile = tile;
    }

    public JFXProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JFXProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Label getPercentage() {
        return percentage;
    }

    public void setPercentage(Label percentage) {
        this.percentage = percentage;
    }
}
