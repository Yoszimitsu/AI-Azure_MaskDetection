package dto;

public class AzurePassDto {

    private String url;
    private String key;

    public AzurePassDto() {
    }

    public AzurePassDto(String url, String key) {
        this.url = url;
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
