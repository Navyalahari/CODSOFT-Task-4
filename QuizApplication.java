import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
public class QuizApplication {
    private static final int QUIZ_DURATION_SECONDS = 60;
    private static final int QUESTION_TIME_SECONDS = 15;
    private static int score = 0;
    private static int correctAnswers = 0;
    private static int incorrectAnswers = 0;
    public static void main(String[] args) {
        Timer timer = new Timer();
        Scanner scanner = new Scanner(System.in);
        String[] questions = {
                "What is the main purpose of the public static void main(String[] args) method in Java?",
                "What is the Java keyword used to indicate that a variable doesn't hold any value?",
                "Which of the following is not a valid modifier in Java?"
        };
        String[][] options = {
                {"A. Define variables", "B. Declare constants", "C. Entry point for the program", "D. Perform arithmetic operations"},
                {"A. void", "B. null", "C. empty", "D. none"},
                {"A. public", "B. private", "C. final", "D. static"}
        };
        int[] correctAnswersArray = {2, 1, 3};
        timer.schedule(new QuizTimerTask(timer, scanner), QUIZ_DURATION_SECONDS * 1000);
        for (int i = 0; i < questions.length; i++) {
            displayQuestion(i + 1, questions[i], options[i]);
            Timer questionTimer = new Timer();
            QuestionTimerTask questionTimerTask = new QuestionTimerTask(questionTimer, scanner, correctAnswersArray[i]);
            questionTimer.schedule(questionTimerTask, QUESTION_TIME_SECONDS * 1000);
            questionTimerTask.waitForAnswer();
            questionTimer.cancel();
            if (!questionTimerTask.isQuizFinished()) {
                handleUserAnswer(questionTimerTask, options[i]);
            }
        }
        displayFinalScoreAndSummary();
        timer.cancel();
        scanner.close();
    }
    private static void displayQuestion(int questionNumber, String question, String[] options) {
        System.out.println("Question " + questionNumber + ": " + question);
        for (String option : options) {
            System.out.println(option);
        }
    }
    private static void handleUserAnswer(QuestionTimerTask questionTimerTask, String[] options) {
        boolean answerCorrect = questionTimerTask.isAnswerCorrect();
        System.out.println(answerCorrect ? "Correct!\n" :
                "Incorrect. The correct answer is: " + options[questionTimerTask.getCorrectAnswer()] + "\n");
        score += answerCorrect ? 2 : 0;
        correctAnswers += answerCorrect ? 1 : 0;
        incorrectAnswers += answerCorrect ? 0 : 1;
    }
    private static void displayFinalScoreAndSummary() {
        System.out.println("Quiz completed!");
        System.out.println("Your final score: " + score);
        System.out.println("Correct Answers: " + correctAnswers);
        System.out.println("Incorrect Answers: " + incorrectAnswers);
    }
    private static class QuizTimerTask extends TimerTask {
        private final Timer timer;
        private final Scanner scanner;
        public QuizTimerTask(Timer timer, Scanner scanner) {
            this.timer = timer;
            this.scanner = scanner;
        }
        @Override
        public void run() {
            System.out.println("\nTime's up! Quiz ended.");
            displayFinalScoreAndSummary();
            scanner.close();
            timer.cancel();
        }
    }
    private static class QuestionTimerTask extends TimerTask {
        private final Timer timer;
        private final Scanner scanner;
        private final int correctAnswer;
        private char userAnswer;
        private boolean answerSubmitted;
        private boolean quizFinished;
        public QuestionTimerTask(Timer timer, Scanner scanner, int correctAnswer) {
            this.timer = timer;
            this.scanner = scanner;
            this.correctAnswer = correctAnswer;
        }
        @Override
        public void run() {
            System.out.println("\nTime's up for this question!");
            quizFinished = true;
            timer.cancel();
        }
        public void waitForAnswer() {
            System.out.print("Your answer: ");
            while (!answerSubmitted) {
                if (scanner.hasNextLine()) {
                    userAnswer = scanner.nextLine().toUpperCase().charAt(0);
                    answerSubmitted = true;
                }
            }
        }
        public char getUserAnswer() {
            return userAnswer;
        }
        public int getCorrectAnswer() {
            return correctAnswer;
        }
        public boolean isAnswerCorrect() {
            return userAnswer - 'A' == correctAnswer;
        }
        public boolean isQuizFinished() {
            return quizFinished;
        }
    }
}
