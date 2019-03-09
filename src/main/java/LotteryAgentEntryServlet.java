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
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by krzysztof on 12.11.17.
 */
@WebServlet(urlPatterns = "/play")
public class LotteryAgentEntryServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LotteryAgentEntryServlet.class);

    private static final int DEFAULT_LEVEL = 1;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Integer> randomNumbers = getRandomNumbers();
        randomNumbers.sort(Comparator.naturalOrder());
        Integer level = getLevel(req);
        LotteryParameters lotteryParameters = new LotteryParameters(randomNumbers, level);
        try {
            String lotteryBossResponse = getLotteryBossResponse(lotteryParameters);
            writeResponseToClient(resp, randomNumbers, lotteryBossResponse);
        } catch (WrongResponseException e) {
            logger.info("LotteryBoss wrong response: " + e.getResponseStatusCode(), e);
            writeErrorMessage(resp);
        } catch (IOException e1) {
            logger.info("LotteryBoss communication problem !", e1);
            writeErrorMessage(resp);
        }

    }

    private void writeErrorMessage(HttpServletResponse resp) throws IOException {
        resp
                .getWriter()
                .println("Sorry, we had some troubles, try again later");
    }

    private Integer getLevel(HttpServletRequest req) {
        String levelString = req.getParameter("level");
        if (levelString == null) {
            return DEFAULT_LEVEL;
        }
        try {
            Integer level = Integer.valueOf(levelString);
            if (level < 1 || level > 6) {
                logger.warn("Level out of bound: " + levelString + " returning efault level: " + DEFAULT_LEVEL);
                return DEFAULT_LEVEL;
            }
            return level;
        } catch (NumberFormatException e) {
            logger.warn("Exception when parsing level: " + levelString, e);
            return DEFAULT_LEVEL;
        }
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

    private String getLotteryBossResponse(LotteryParameters lotteryParameters) throws IOException, WrongResponseException {
        String lotteryParametersAsJson = new Gson().toJson(lotteryParameters);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://" + getLotteryBossHost() + ":" + getLotteryBossPort() + "/lotteryBoss/api/results");
        httpPost.setEntity(new StringEntity(lotteryParametersAsJson));
        httpPost.setHeader(new BasicHeader("content-type", "application/json"));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse lotteryBossResponse = httpClient.execute(httpPost);
        int statusCode = lotteryBossResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return new BasicResponseHandler()
                    .handleResponse(lotteryBossResponse);
        }
        throw new WrongResponseException(statusCode);

    }

    private String getLotteryBossPort() {
        return System.getenv("LOTTERY_BOSS_PORT");
    }

    private String getLotteryBossHost() {
        return System.getenv("LOTTERY_BOSS_HOST");
    }

    private List<Integer> getRandomNumbers() {
        return new Random()
                .ints(6, 1, 50)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());

    }
}
