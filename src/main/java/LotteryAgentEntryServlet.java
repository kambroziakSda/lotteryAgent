import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by krzysztof on 12.11.17.
 */
@WebServlet(urlPatterns = "/play")
public class LotteryAgentEntryServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LotteryAgentEntryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Integer> randomNumbers = getRandomNumbers();
        String numbersAsJson = new Gson().toJson(randomNumbers);

        try {
            String lotteryBossResponse = getLotteryBossResponse(numbersAsJson);
            writeResponseToClient(resp, randomNumbers, lotteryBossResponse);
        } catch (WrongResponseException e) {
            writeErrorResponse(resp, e);
        } catch (IOException e1) {
            logger.info("LotteryBoss communication problem !", e1);
            resp
                    .getWriter()
                    .println("Sorry, we had some troubles, try again later");
        }

    }

    private void writeErrorResponse(HttpServletResponse resp, WrongResponseException e) throws IOException {
        logger.info("LotteryBoss wrong response: " + e.getResponseStatusCode(), e);
        resp
                .setContentType("text/plain; charset=utf-8");

        resp
                .getWriter()
                .println("Sorry, we had some troubles, try again later");
    }

    private void writeResponseToClient(HttpServletResponse resp, List<Integer> randomNumbers, String lotteryBossResponseString) throws IOException {
        resp
                .setContentType("text/plain; charset=utf-8");

        StringBuilder responseString = new StringBuilder("Twoje liczby to: ")
                .append(randomNumbers)
                .append("\n")
                .append(lotteryBossResponseString);

        resp.getWriter()
                .println(responseString);
    }

    private String getLotteryBossResponse(String numbersAsJson) throws IOException, WrongResponseException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8082/lotteryBoss/results");
        httpPost.setEntity(new StringEntity(numbersAsJson));
        httpPost.setHeader(new BasicHeader("content-type", "application/json"));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10).build();
        httpPost.setConfig(requestConfig);
        HttpResponse lotteryBossResponse = httpClient.execute(httpPost);
        int statusCode = lotteryBossResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return new BasicResponseHandler()
                    .handleResponse(lotteryBossResponse);
        }
        throw new WrongResponseException(statusCode);

    }

    private List<Integer> getRandomNumbers() {
        return new Random()
                .ints(6, 1, 49)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());

    }
}
