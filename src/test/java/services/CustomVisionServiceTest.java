package services;

import dto.AzurePassDto;
import dto.MaskDetectionRequestDto;
import error.CredentialsFileNotFound;
import error.CredentialsNotFoundError;
import error.JsonResponseParserError;
import nu.pattern.OpenCV;
import openCv.ImageProcessing;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomVisionServiceTest {

    @BeforeClass
    public static void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
    }

    @Test
    public void getCredentialsShouldReturnTestCredentials() throws CredentialsNotFoundError, CredentialsFileNotFound {
        AzurePassDto azurePassDto = CustomVisionService.getCredentials("./src/test/java/resources/test.config", "url.test", "key.test");

        Assert.assertEquals("url", azurePassDto.getUrl());
        Assert.assertEquals("key", azurePassDto.getKey());
    }

    @Test
    public void getCredentialsShouldThrowCredentialNotFoundError() {
        CredentialsNotFoundError exception = Assert.assertThrows(
                CredentialsNotFoundError.class,
                () -> CustomVisionService.getCredentials("./src/test/java/resources/test.config", "url.testNotFound", "key.testNotFound"));

        Assert.assertEquals("Credentials not found. Check variable names.", exception.getMessage());
    }

    @Test
    public void getCredentialsShouldThrowCredentialFileNotFoundError() {
        CredentialsFileNotFound exception = Assert.assertThrows(
                CredentialsFileNotFound.class,
                () -> CustomVisionService.getCredentials("./src/test/java/resources/testFileNotFoundApp.config", "url.test", "key.test"));

        Assert.assertEquals("Config file with credentials not found. Check the file path.", exception.getMessage());
    }

    @Test
    public void jsonParserShouldReturnOneResult() throws JsonResponseParserError {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        double threshold = 0.5;

        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/json/response.txt");
        jsonObjectArrayList = CustomVisionService.jsonParser(jsonObject, jsonObjectArrayList, threshold);

        Assert.assertEquals(1, jsonObjectArrayList.size());
        Assert.assertTrue((Double) jsonObjectArrayList.get(0).get("probability") > threshold);
    }

    @Test
    public void jsonParserShouldReturnNoneResult() throws JsonResponseParserError {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        double threshold = 1;

        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/json/response.txt");
        jsonObjectArrayList = CustomVisionService.jsonParser(jsonObject, jsonObjectArrayList, threshold);

        Assert.assertEquals(0, jsonObjectArrayList.size());
    }

    @Test
    public void jsonParserReturnJsonResponseParserError() {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        double threshold = 0.5;
        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/json/badResponse.txt");

        ArrayList<JSONObject> finalJsonObjectArrayList = jsonObjectArrayList;
        JsonResponseParserError exception = Assert.assertThrows(
                JsonResponseParserError.class,
                () -> CustomVisionService.jsonParser(jsonObject, finalJsonObjectArrayList, threshold));

        Assert.assertEquals("Error during parsing JSON response.", exception.getMessage());
    }

    //Before test execution, be sure that in test.config file are correct Azure url and key (url.customVision, key.customVision)
    @Test
    public void sendRequestShouldReturn200StatusCode() throws CredentialsNotFoundError, CredentialsFileNotFound {
        MaskDetectionRequestDto maskDetectionRequestDto = new MaskDetectionRequestDto();
        maskDetectionRequestDto.setMat(ImageProcessing.loadImage("./src/test/java/resources/images/testMaskImg.jpg"));
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", maskDetectionRequestDto.getMat(), bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

        //Before test execution, be sure that in test.config file are correct Azure url and key (url.customVision, key.customVision)
        AzurePassDto azurePassDto = CustomVisionService.getCredentials("./src/test/java/resources/test.config", "url.customVision", "key.customVision");
        HttpResponse response = CustomVisionService.sendRequest(maskDetectionRequestDto, inputStream, azurePassDto.getUrl(), azurePassDto.getKey());

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void sendRequestShouldReturn404StatusCode() throws CredentialsNotFoundError, CredentialsFileNotFound {
        MaskDetectionRequestDto maskDetectionRequestDto = new MaskDetectionRequestDto();
        maskDetectionRequestDto.setMat(ImageProcessing.loadImage("./src/test/java/resources/images/testMaskImg.jpg"));
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", maskDetectionRequestDto.getMat(), bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

        AzurePassDto azurePassDto = CustomVisionService.getCredentials("./src/test/java/resources/test.config", "url.customVisionWrong", "key.customVisionWrong");
        HttpResponse response = CustomVisionService.sendRequest(maskDetectionRequestDto, inputStream, azurePassDto.getUrl(), azurePassDto.getKey());

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    private JSONObject readJsonFromFile(String filePath) {
        File file = new File(filePath);
        String response = "";
        JSONObject jsonObject = null;
        try {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine()) {
                response += scanner.nextLine();
            }
            jsonObject = new JSONObject(response);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
