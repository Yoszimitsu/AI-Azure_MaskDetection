import dto.AzurePassDto;
import error.CredentialsFileNotFound;
import error.CredentialsNotFoundError;
import org.junit.Assert;
import org.junit.Test;
import services.CustomVisionService;

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
}
