package apps;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import objectDetection.CustomVisionMaskDetection;
import objectDetection.CustomVisionMaskDetectionTags;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;

public class CustomVisionMaskDetectionTagsApp extends Application {

    private VideoCapture capture;
    private CustomVisionMaskDetectionTags customVisionMaskDetectionTags = new CustomVisionMaskDetectionTags();

    public void start(Stage stage) throws Exception {
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
                try {
                    imageView.setImage(customVisionMaskDetectionTags.maskDetection(capture));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}
