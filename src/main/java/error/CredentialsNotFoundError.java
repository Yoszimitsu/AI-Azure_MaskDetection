package error;

public class CredentialsNotFoundError extends Exception {

    public CredentialsNotFoundError() {
        super("Credentials not found. Check if app.config file exists.");
    }
}
