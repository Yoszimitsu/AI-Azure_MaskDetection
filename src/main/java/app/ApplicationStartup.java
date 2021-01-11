package app;

import dto.VideoDto;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import objectDetection.CustomVisionMaskDetection;
import objectDetection.CustomVisionMaskDetectionTags;
import openCv.ImageProcessing;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

public class ApplicationStartup extends Application {

    private VideoCapture capture;
    private VideoDto videoDto;

    @Override
    public void start(Stage stage) throws Exception {
        startCustomVisionMaskDetectionTagsApp(stage);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void startCustomVisionMaskDetectionTagsApp(Stage stage) throws Exception {
        CustomVisionMaskDetectionTags customVisionMaskDetectionTags = new CustomVisionMaskDetectionTags();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        this.videoDto = new VideoDto();
        this.capture = new VideoCapture(0);

        stage.setScene(this.videoDto.getScene());
        stage.show();
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                videoDto.getImageView().setImage(customVisionMaskDetectionTags.maskDetection(capture));
            }
        }.start();
    }

    private void startCustomVisionMaskDetectionApp(Stage stage) throws Exception {
        CustomVisionMaskDetection customVisionMaskDetection = new CustomVisionMaskDetection();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        this.videoDto = new VideoDto();
        this.capture = new VideoCapture(0);

        stage.setScene(this.videoDto.getScene());
        stage.show();
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                videoDto.getImageView().setImage(customVisionMaskDetection.maskDetection(capture));
            }
        }.start();
    }

    private void startVideoCaptureApp(Stage stage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        this.videoDto = new VideoDto();
        this.capture = new VideoCapture(0);

        stage.setScene(this.videoDto.getScene());
        stage.show();
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                videoDto.getImageView().setImage(ImageProcessing.getCapture(capture));
            }
        }.start();
    }
}
