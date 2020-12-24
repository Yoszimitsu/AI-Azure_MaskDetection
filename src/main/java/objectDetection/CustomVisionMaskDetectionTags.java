package objectDetection;

import dto.MaskDetectionRequestObject;
import error.CredentialsNotFoundError;
import javafx.scene.image.Image;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;

import static openCv.ImageProcessing.mat2Img;
import static services.CustomVisionService.jsonParser;
import static services.CustomVisionService.sendRequest;

public class CustomVisionMaskDetectionTags {

    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray = new ArrayList<>();
    private String url;
    private String key;

    public CustomVisionMaskDetectionTags() throws CredentialsNotFoundError {
        getCredentials();
    }

    public Image maskDetection(VideoCapture capture) {
        MaskDetectionRequestObject maskDetectionRequestObject = new MaskDetectionRequestObject();

        // GET IMAGE FROM VIDEO
        capture.read(maskDetectionRequestObject.getMat());
        // SEND IMAGE TO OPENCV FACE DETECTION
        OpenCvFaceDetection openCvFaceDetection = new OpenCvFaceDetection(maskDetectionRequestObject.getMat());
        // GET FACE IMAGES DETECTED ON CAPTURE
        ArrayList<Mat> faceImageArray = openCvFaceDetection.getFaceImageArray();
        Rect[] rectArray = openCvFaceDetection.getRectArray();

        for (Mat faceImage : faceImageArray) {
            try {
                MatOfByte bytes = new MatOfByte();
                Imgcodecs.imencode(".jpg", faceImage, bytes);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

                // SEND HTTP REQUEST
                HttpResponse response = sendRequest(maskDetectionRequestObject, inputStream, url, key);

                // CONVERT RESPONSE TO STRING AND CONVERT RESPONSE STRING TO JSON ARRAY
                if (response.getEntity() != null) {
                    maskDetectionRequestObject.getHttpEntities().add(response.getEntity());
                }

                maskDetectionJSONObjectArray = jsonParser(maskDetectionRequestObject.getHttpEntities(), maskDetectionJSONObjectArray, threshold);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mat2Img(rectangle(rectArray, maskDetectionRequestObject.getMat(), maskDetectionJSONObjectArray));
    }

    private Mat rectangle(Rect[] rectArray, Mat image, ArrayList<JSONObject> maskDetectionJSONObjectArray) {
        if (!maskDetectionJSONObjectArray.isEmpty()) {
            for (int i = 0; i < rectArray.length; i++) {
                String probability = new DecimalFormat("#.##").format(maskDetectionJSONObjectArray.get(i).getDouble("probability"));
                try {
                    Rect face = rectArray[i];
                    if (maskDetectionJSONObjectArray.get(i).getString("tagName").equals("mask")) {
                        Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 255, 0), 3);
                        Imgproc.putText(image, "mask pr:" + probability, face.tl(), 1, 1, new Scalar(0, 255, 0), 1);
                    }
                    if (maskDetectionJSONObjectArray.get(i).getString("tagName").equals("noMask")) {
                        Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 0, 255), 3);
                        Imgproc.putText(image, "noMask pr:" + probability, face.tl(), 1, 1, new Scalar(0, 0, 255), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    private void getCredentials() throws CredentialsNotFoundError {
        Properties prop = new Properties();
        String fileName = "./src/main/java/credentials/app.config";
        try {
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            this.url = prop.getProperty("url.customVisionTags");
            this.key = prop.getProperty("key.customVisionTags");
        } catch (Exception e) {
            throw new CredentialsNotFoundError();
        }
    }

}
