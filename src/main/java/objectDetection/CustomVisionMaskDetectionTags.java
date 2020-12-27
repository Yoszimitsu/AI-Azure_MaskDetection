package objectDetection;

import dto.AzurePassDto;
import dto.MaskDetectionRequestDto;
import error.CredentialsFileNotFound;
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
import java.text.DecimalFormat;
import java.util.ArrayList;

import static openCv.ImageProcessing.mat2Img;
import static services.CustomVisionService.*;

public class CustomVisionMaskDetectionTags {

    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray = new ArrayList<>();
    private AzurePassDto azurePass;

    public CustomVisionMaskDetectionTags() throws CredentialsNotFoundError, CredentialsFileNotFound {
        this.azurePass = getCredentials("./src/main/java/credentials/app.config", "url.customVisionTags", "key.customVisionTags");
    }

    public Image maskDetection(VideoCapture capture) {
        MaskDetectionRequestDto maskDetectionRequestDto = new MaskDetectionRequestDto();

        // GET IMAGE FROM VIDEO
        capture.read(maskDetectionRequestDto.getMat());
        // SEND IMAGE TO OPENCV FACE DETECTION
        OpenCvFaceDetection openCvFaceDetection = new OpenCvFaceDetection(maskDetectionRequestDto.getMat());
        // GET FACE IMAGES DETECTED ON CAPTURE
        ArrayList<Mat> faceImageArray = openCvFaceDetection.getFaceImageArray();
        Rect[] rectArray = openCvFaceDetection.getRectArray();

        for (Mat faceImage : faceImageArray) {
            try {
                MatOfByte bytes = new MatOfByte();
                Imgcodecs.imencode(".jpg", faceImage, bytes);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

                // SEND HTTP REQUEST
                HttpResponse response = sendRequest(
                        maskDetectionRequestDto,
                        inputStream,
                        this.azurePass.getUrl(),
                        this.azurePass.getKey());

                // CONVERT RESPONSE TO STRING AND CONVERT RESPONSE STRING TO JSON ARRAY
                if (response.getEntity() != null) {
                    maskDetectionRequestDto.getHttpEntities().add(response.getEntity());
                }

                maskDetectionJSONObjectArray = jsonParser(maskDetectionRequestDto.getHttpEntities(), maskDetectionJSONObjectArray, threshold);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mat2Img(rectangle(rectArray, maskDetectionRequestDto.getMat(), maskDetectionJSONObjectArray));
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

}
