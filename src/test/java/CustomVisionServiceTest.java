import dto.AzurePassDto;
import error.CredentialsFileNotFound;
import error.CredentialsNotFoundError;
import error.JsonResponseParserError;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import services.CustomVisionService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomVisionServiceTest {

    @Test
    public void getCredentialsShouldReturnTestCredentials() throws CredentialsNotFoundError, CredentialsFileNotFound {
        AzurePassDto azurePassDto;

        azurePassDto = CustomVisionService.getCredentials("./src/test/java/resources/testApp.config", "url.test", "key.test");

        Assert.assertEquals("url", azurePassDto.getUrl());
        Assert.assertEquals("key", azurePassDto.getKey());
    }

    @Test
    public void getCredentialsShouldThrowCredentialNotFoundError() {
        CredentialsNotFoundError exception = Assert.assertThrows(
                CredentialsNotFoundError.class,
                () -> CustomVisionService.getCredentials("./src/test/java/resources/testApp.config", "url.testNotFound", "key.testNotFound"));

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

        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/response.txt");
        jsonObjectArrayList = CustomVisionService.jsonParser(jsonObject, jsonObjectArrayList, threshold);

        Assert.assertEquals(1, jsonObjectArrayList.size());
        Assert.assertTrue((Double) jsonObjectArrayList.get(0).get("probability") > threshold);
    }

    @Test
    public void jsonParserShouldReturnNoneResult() throws JsonResponseParserError {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        double threshold = 1;

        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/response.txt");
        jsonObjectArrayList = CustomVisionService.jsonParser(jsonObject, jsonObjectArrayList, threshold);

        Assert.assertEquals(0, jsonObjectArrayList.size());
    }

    @Test
    public void jsonParserReturnJsonResponseParserError() {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        double threshold = 0.5;
        JSONObject jsonObject = readJsonFromFile("./src/test/java/resources/badResponse.txt");

        ArrayList<JSONObject> finalJsonObjectArrayList = jsonObjectArrayList;
        JsonResponseParserError exception = Assert.assertThrows(
                JsonResponseParserError.class,
                () -> CustomVisionService.jsonParser(jsonObject, finalJsonObjectArrayList, threshold));

        Assert.assertEquals("Error during parsing JSON response.", exception.getMessage());
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
