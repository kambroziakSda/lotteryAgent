import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by krzysztof on 12.11.17.
 */
@WebServlet(urlPatterns = "/play")
public class LotteryAgentEntryServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LotteryAgentEntryServlet.class);

    private List<Integer> createRandomNumbers() {
        return new Random().ints(6, 1, 50)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Integer> randomNumbers = createRandomNumbers();
        String level = Optional.ofNullable(req.getParameter("level"))
                .orElse("1");

        LotteryBossParameters parameters = new LotteryBossParameters();
        parameters.setLevel(Integer.valueOf(level));
        parameters.setRandomNumbers(randomNumbers);

        String parametersAsJson = new Gson().toJson(parameters);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:8082/lotteryBoss/api/results");

        HttpEntity entity = EntityBuilder.create().setText(parametersAsJson)
                .setContentType(ContentType.APPLICATION_JSON).build();

        post.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(post);
        BasicResponseHandler basicResponseHandler = new BasicResponseHandler();
        String lotteryBossResponse = basicResponseHandler.handleResponse(response);

        writeResponseToClient(resp, parameters, lotteryBossResponse);
    }

    /**
     * Wypisanie odpowiedzi za pomocą getWriter()
     *
     * @param resp
     * @param lotteryBossResponse
     */
    private void writeResponseToClient(HttpServletResponse resp, LotteryBossParameters parameters, String lotteryBossResponse) throws IOException {
        resp.getWriter().println("Twoje liczby to: " + parameters.getRandomNumbers());
        resp.getWriter().println("Level: "+parameters.getLevel());
        resp.getWriter().println(lotteryBossResponse);
        resp.setContentType("text/plain;charest=utf8");
    }


}
