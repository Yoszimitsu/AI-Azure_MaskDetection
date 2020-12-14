package objectDetection;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;

import static openCv.ImageProcessing.mat2Img;

public class OpenCvFaceDetection {

    private Mat image;
    private Mat imageWithTickedFaces;
    private Image faceImage;
    private CascadeClassifier cascadeClassifier = new CascadeClassifier();
    private final String cascadeClassifierPath = "./src/main/resources/haarcascade_frontalface_default.xml";
    private MatOfRect facesDetected = new MatOfRect();
    private Rect[] rectArray;
    private ArrayList<Mat> faceImageArray = new ArrayList<>();

    public OpenCvFaceDetection(Mat image) {
        this.image = image;
        this.imageWithTickedFaces = image;
        faceDetection(this.imageWithTickedFaces);
    }

    public Image getCaptureWithFaceDetection(VideoCapture capture) {
        Mat mat = new Mat();
        capture.read(mat);
        Mat haarClassifiedImg = getImageWithTickedFaces();
        return mat2Img(haarClassifiedImg);
    }

    public Mat getImageWithTickedFaces() {
        return imageWithTickedFaces;
    }

    public Mat getImage() {
        return image;
    }

    public ArrayList<Mat> getFaceImageArray() {
        return faceImageArray;
    }

    public Rect[] getRectArray() {
        return rectArray;
    }

    private void faceDetection(Mat image) {
        int minFaceSize = Math.round(image.rows() * 0.1f);
        cascadeClassifier.load(cascadeClassifierPath);
        cascadeClassifier.detectMultiScale(image,
                facesDetected,
                1.1,
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                new Size()
        );
        rectArray = this.facesDetected.toArray();
        for (Rect face : rectArray) {
            Rect rect = new Rect(face.x, face.y, (int) (face.width * 1.1), (int) (face.height * 1.1));
            faceImageArray.add(image.submat(rect));
//            Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 255, 255), 1);
        }
    }
}
