import java.util.List;

public class LotteryBossParameters {

    private List<Integer> randomNumbers;

    private Integer level;

    public List<Integer> getRandomNumbers() {
        return randomNumbers;
    }

    public void setRandomNumbers(List<Integer> randomNumber) {
        this.randomNumbers = randomNumber;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
