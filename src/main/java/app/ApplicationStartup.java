package app;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import objectDetection.CustomVisionMaskDetection;
import objectDetection.CustomVisionMaskDetectionTags;
import openCv.ImageProcessing;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

public class ApplicationStartup extends Application {

    private static VideoCapture capture;

    @Override
    public void start(Stage stage) throws Exception {
        startCustomVisionMaskDetectionApp(stage);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static void startCustomVisionMaskDetectionTagsApp(Stage stage) throws Exception {
        CustomVisionMaskDetectionTags customVisionMaskDetectionTags = new CustomVisionMaskDetectionTags();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

        capture = new VideoCapture(0);
        final ImageView imageView = new ImageView();
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                imageView.setImage(customVisionMaskDetectionTags.maskDetection(capture));
            }
        }.start();
    }

    private static void startCustomVisionMaskDetectionApp(Stage stage) throws Exception {
        CustomVisionMaskDetection customVisionMaskDetection = new CustomVisionMaskDetection();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

        capture = new VideoCapture(0);
        final ImageView imageView = new ImageView();
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                imageView.setImage(customVisionMaskDetection.maskDetection(capture));
            }
        }.start();
    }

    private static void startVideoCaptureApp(Stage stage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

        capture = new VideoCapture(0);
        final ImageView imageView = new ImageView();
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                imageView.setImage(ImageProcessing.getCapture(capture));
            }
        }.start();
    }
}
