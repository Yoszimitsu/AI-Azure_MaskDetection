import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import openCv.ImageLoader;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

public class MainClass extends Application {

    private VideoCapture capture;

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
                imageView.setImage(ImageLoader.getCapture(capture));
            }
        }.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}
