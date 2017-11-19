import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Created by krzysztof on 12.11.17.
 */
public class LotteryAgentEntry extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LotteryAgentEntry.class);

    private List<Integer> createRandomNumbers() {
        return Collections.emptyList();
    }

    private void writeResponseToClient(HttpServletResponse resp, List<Integer> randomNumbers) {

    }


}
