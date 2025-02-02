package excalibur.game.logic.syslogic;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Xc on 2014/4/22.
 */
public class DataUtils {

    private int gameCount;
    private int totalScore;
    private int maxCombo;
    private int maxScore;

    private static String SAVE_PATH = "person.txt";

    private double averageScore;

    private HashMap<Integer, GameRecord> gameRecords = new HashMap<Integer, GameRecord>();

    private static DataUtils du;

    public static void main(String[] args) {
        DataUtils du = DataUtils.getInstance();
        du.addRecord(5600, 34);
        du.saveData();
    }

    /**
     * @return DataUtils 的静态引用
     */
    public static DataUtils getInstance() {
        if (du == null) {
            du = new DataUtils();
        }
        return du;
    }

    /**
     * 保存数据
     */
    private void saveData() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, GameRecord> oneRecord : gameRecords.entrySet()) {
            sb.append(oneRecord.getKey().toString());
            sb.append('\t');
            sb.append(oneRecord.getValue().score);
            sb.append('\t');
            sb.append(oneRecord.getValue().combo);
            sb.append('\t');
            sb.append(oneRecord.getValue().gameCount);
            sb.append('\t');
            sb.append(oneRecord.getValue().maxScore);
            sb.append('\n');
        }

        System.out.print(sb.toString());
        String path = SAVE_PATH;
        try {
            FileWriter fw = new FileWriter(path, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.print(sb.toString());
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于增加记录
     *
     * @param score 一局的得分
     * @param combo 一局的最大combo
     */
    public void addRecord(int score, int combo) {

        Calendar calendar = Calendar.getInstance();
        int day = (int) (calendar.getTimeInMillis() / 1000 / 3600 / 24);

        if (gameRecords.containsKey(day)) {
            GameRecord oldRecord = gameRecords.get(day);
            int newScore = oldRecord.score + score;
            int newMaxCombo = combo > oldRecord.combo ? combo : oldRecord.combo;
            int newMaxScore = score > oldRecord.maxScore ? score : oldRecord.maxScore;

            GameRecord newRecord = new GameRecord(newScore, newMaxCombo,
                    oldRecord.gameCount + 1, newMaxScore);
            gameRecords.put(day, newRecord);
        } else {
            gameRecords.put(day, new GameRecord(score, combo, 1, score));
        }

        update();
        saveData();
    }

    private DataUtils() {
        loadData();
    }

    private void loadData() {
        File saveFile = new File(SAVE_PATH);
        Scanner in = null;
        try {
            in = new Scanner(saveFile);
            while (in.hasNextLine()) {
                String s = in.nextLine();
                String[] data = s.split("\\s");
                int day = Integer.parseInt(data[0]);
                int score = Integer.parseInt(data[1]);
                int combo = Integer.parseInt(data[2]);
                int gameCount = Integer.parseInt(data[3]);
                int maxScore = Integer.parseInt(data[4]);
                gameRecords.put(day, new GameRecord(score, combo, gameCount, maxScore));
            }
            in.close();

        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            System.out.println("找不到存档，新建存档");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }


    }


    private void update() {
        totalScore = 0;
        maxCombo = 0;
        gameCount = 0;
        maxScore = 0;
        for (GameRecord gameRecord : gameRecords.values()) {
            totalScore += gameRecord.score;
            maxCombo = maxCombo >= gameRecord.combo ? maxCombo : gameRecord.combo;
            gameCount += gameRecord.gameCount;
            maxScore = maxScore > gameRecord.maxScore ? maxScore : gameRecord.maxScore;
        }

        averageScore = (double) totalScore / gameCount;

    }


    //getters below
    public int getGameCount() {
        return gameCount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public int getMaxScore() {
        return maxScore;
    }

    class GameRecord {
        int score;
        int combo;
        int gameCount;
        int maxScore;

        public GameRecord(int score, int combo, int gameCount, int maxScore) {
            this.score = score;
            this.combo = combo;
            this.gameCount = gameCount;
            this.maxScore = maxScore;
        }
    }

}
