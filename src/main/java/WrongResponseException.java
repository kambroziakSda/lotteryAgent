/**
 * Created by krzysztof on 18.11.17.
 */
public class WrongResponseException extends Throwable {

    private int responseStatusCode;

    public WrongResponseException(int responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }
}
