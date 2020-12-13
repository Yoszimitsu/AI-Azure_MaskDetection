package objectDetection;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import static openCv.ImageProcessing.mat2Img;

public class OpenCvFaceDetection {

    public static Image getCaptureWithFaceDetection(VideoCapture capture) {
        Mat mat = new Mat();
        capture.read(mat);
        Mat haarClassifiedImg = detectFace(mat);
        return mat2Img(haarClassifiedImg);
    }

    public static Mat detectFace(Mat inputImage) {
        MatOfRect facesDetected = new MatOfRect();
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        int minFaceSize = Math.round(inputImage.rows() * 0.1f);
        cascadeClassifier.load("./src/main/resources/haarcascade_frontalface_default.xml");
        cascadeClassifier.detectMultiScale(inputImage,
                facesDetected,
                1.1,
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                new Size()
        );
        Rect[] facesArray = facesDetected.toArray();
        for (Rect face : facesArray) {
            Rect rect = new Rect(face.x, face.y, (int) (face.width * 1.1), (int) (face.height * 1.1));
//            ImageProcessing.saveImage(inputImage.submat(rect), "./img.jpg");
            Imgproc.rectangle(inputImage, face.tl(), face.br(), new Scalar(0, 255, 255), 1);

        }
        return inputImage;
    }
}
