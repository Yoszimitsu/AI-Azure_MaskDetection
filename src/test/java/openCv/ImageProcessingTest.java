package openCv;

import javafx.scene.image.Image;
import nu.pattern.OpenCV;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.File;

public class ImageProcessingTest {

    @BeforeClass
    public static void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
    }

    @Test
    public void testLoadImagePositive() {
        var object = ImageProcessing.loadImage("./src/test/java/resources/images/testMaskImg.jpg");

        Assert.assertEquals(object.getClass(), Mat.class);
    }

    @Test
    public void testSaveImage() {
        ImageProcessing.saveImage(ImageProcessing.loadImage("./src/test/java/resources/images/testMaskImg.jpg"), "./src/test/java/resources/img.jpg");
        File file = new File("./src/test/java/resources/img.jpg");

        Assert.assertNotNull(file);

        file.delete();
    }

    @Test
    public void testGetCapture() {
        VideoCapture capture = new VideoCapture(0);
        var object = ImageProcessing.getCapture(capture);

        Assert.assertEquals(object.getClass(), Image.class);
    }

    @Test
    public void testMat2Img() {
        var object = ImageProcessing.mat2Img(ImageProcessing.loadImage("./src/test/java/resources/images/testMaskImg.jpg"));

        Assert.assertEquals(object.getClass(), Image.class);
    }
}
