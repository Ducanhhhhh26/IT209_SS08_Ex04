package Excercise_07;

public class Ex03 {
    public static void main(String[] args) {
        double score1 = 6.5;
        double score2 = 8.0;
        double score3 = 4.5;
        double avg = ScoreUtils.calculateAverage(score1, score2, score3);
        boolean pass = ScoreUtils.isPass(avg);

        System.out.println("Điểm trung bình: " + avg);
        if (pass) {
            System.out.println("Kết quả: ĐẠT");
        } else {
            System.out.println("Kết quả: KHÔNG ĐẠT");
        }
    }
}
class ScoreUtils {
    public static boolean isPass(double score) {
        return score >= 5.0;
    }
    public static double calculateAverage(double s1, double s2, double s3) {
        return (s1 + s2 + s3) / 3;
    }
}