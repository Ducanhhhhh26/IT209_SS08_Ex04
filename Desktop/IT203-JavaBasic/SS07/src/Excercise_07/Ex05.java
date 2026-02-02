package Excercise_07;

public class Ex05 {
    public static void main(String[] args) {
        double score = 8.5;
        System.out.println("Điểm: " + score);
        System.out.println("Điểm tối đa: " + Config.MAX_SCORE);
        System.out.println("Điểm tối thiểu: " + Config.MIN_SCORE);
        if (score >= Config.MIN_SCORE && score <= Config.MAX_SCORE) {
            System.out.println("Điểm hợp lệ");
        } else {
            System.out.println("Điểm không hợp lệ");
        }
    }
}
class Config {
    public static final double MAX_SCORE = 10.0;
    public static final double MIN_SCORE = 0.0;
}
