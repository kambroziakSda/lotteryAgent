import java.util.List;

/**
 * Created by krzysztof on 23.11.17.
 */
public class LotteryParameters {

    private  List<Integer> userNumbers;

    private Integer level;

    public LotteryParameters() {
    }
    public LotteryParameters(List<Integer> userNumbers, Integer level) {
        this.userNumbers = userNumbers;
        this.level = level;
    }

    public List<Integer> getUserNumbers() {
        return userNumbers;
    }

    public Integer getLevel() {
        return level;
    }
}
