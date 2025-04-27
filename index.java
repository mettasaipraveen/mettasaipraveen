import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class Question {
    String question, optionA, optionB, optionC, optionD, correct;

    public Question(String question, String a, String b, String c, String d, String correct) {
        this.question = question;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correct = correct;
    }
}

public class OnlineQuizGUI {
    JFrame frame;
    JLabel questionLabel, timerLabel;
    JRadioButton optionA, optionB, optionC, optionD;
    ButtonGroup optionsGroup;
    JButton nextButton;
    JPanel contentPanel;
    java.util.List<Question> questions;
    int currentQuestionIndex = 0;
    int score = 0;
    String userName;

    Timer countdownTimer;
    int timeLeft = 15;

    public OnlineQuizGUI() {
        questions = new ArrayList<>();
        populateQuestions();
        Collections.shuffle(questions);

        userName = JOptionPane.showInputDialog("Enter your name:");

        frame = new JFrame("General Knowledge Quiz");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel = new JLabel("Time left: ");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();

        for (JRadioButton rb : new JRadioButton[]{optionA, optionB, optionC, optionD}) {
            rb.setFont(new Font("SansSerif", Font.PLAIN, 16));
            rb.setBackground(Color.WHITE);
        }

        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);

        contentPanel.add(questionLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(timerLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(optionA);
        contentPanel.add(optionB);
        contentPanel.add(optionC);
        contentPanel.add(optionD);
        contentPanel.add(Box.createVerticalStrut(20));

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        nextButton.setBackground(new Color(173, 216, 230));
        nextButton.setFocusPainted(false);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(nextButton);

        frame.setContentPane(contentPanel);

        nextButton.addActionListener(e -> {
            if (!optionA.isSelected() && !optionB.isSelected() && !optionC.isSelected() && !optionD.isSelected()) {
                JOptionPane.showMessageDialog(frame, "Please select an answer before continuing.");
                return;
            }

            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }

            checkAnswer();
            currentQuestionIndex++;

            if (currentQuestionIndex < questions.size()) {
                loadQuestion(currentQuestionIndex);
                if (currentQuestionIndex == questions.size() - 1) {
                    nextButton.setText("Finish");
                }
            } else {
                showResult();
            }
        });

        loadQuestion(currentQuestionIndex);
        frame.setVisible(true);
    }

    public void loadQuestion(int index) {
        Question q = questions.get(index);
        questionLabel.setText("<html><div style='text-align: center;'>" + (index + 1) + ". " + q.question + "</div></html>");
        optionA.setText("A. " + q.optionA);
        optionB.setText("B. " + q.optionB);
        optionC.setText("C. " + q.optionC);
        optionD.setText("D. " + q.optionD);
        optionsGroup.clearSelection();
        startCountdown();
    }

    public void startCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        timeLeft = 15;
        timerLabel.setText("Time left: " + timeLeft + "s");

        countdownTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft + "s");
                if (timeLeft <= 0) {
                    countdownTimer.stop();
                    JOptionPane.showMessageDialog(frame, "Time's up! Moving to next question.");
                    checkAnswer();
                    currentQuestionIndex++;
                    if (currentQuestionIndex < questions.size()) {
                        loadQuestion(currentQuestionIndex);
                        if (currentQuestionIndex == questions.size() - 1) {
                            nextButton.setText("Finish");
                        }
                    } else {
                        showResult();
                    }
                }
            }
        });

        countdownTimer.start();
    }

    public void checkAnswer() {
        Question q = questions.get(currentQuestionIndex);
        String selected = "";

        if (optionA.isSelected()) selected = "A";
        else if (optionB.isSelected()) selected = "B";
        else if (optionC.isSelected()) selected = "C";
        else if (optionD.isSelected()) selected = "D";

        if (selected.equalsIgnoreCase(q.correct)) {
            score++;
        }
    }

    public void showResult() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        JOptionPane.showMessageDialog(frame, userName + ", your final score is: " + score + "/" + questions.size());
        saveToLeaderboard(userName, score);
        showLeaderboard();
        frame.dispose();
    }

    public void populateQuestions() {
        questions.add(new Question("What is the capital of France?", "Berlin", "Madrid", "Paris", "Rome", "C"));
        questions.add(new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", "B"));
        questions.add(new Question("Who painted the Mona Lisa?", "Van Gogh", "Da Vinci", "Picasso", "Michelangelo", "B"));
        questions.add(new Question("Which ocean is the largest?", "Atlantic", "Indian", "Arctic", "Pacific", "D"));
        questions.add(new Question("What is the boiling point of water?", "90°C", "100°C", "80°C", "120°C", "B"));
        questions.add(new Question("What language has the most native speakers?", "English", "Spanish", "Mandarin", "Arabic", "C"));
        questions.add(new Question("Which continent is the Sahara Desert located in?", "Asia", "Africa", "Australia", "Europe", "B"));
        questions.add(new Question("What gas do plants absorb?", "Oxygen", "Hydrogen", "Carbon Dioxide", "Nitrogen", "C"));
        questions.add(new Question("What is the hardest natural substance?", "Iron", "Diamond", "Gold", "Quartz", "B"));
        questions.add(new Question("How many players in a football team?", "9", "10", "11", "12", "C"));
        questions.add(new Question("Who invented the telephone?", "Newton", "Einstein", "Bell", "Edison", "C"));
        questions.add(new Question("Which metal is liquid at room temp?", "Iron", "Mercury", "Zinc", "Aluminum", "B"));
        questions.add(new Question("Which is the longest river?", "Nile", "Amazon", "Yangtze", "Mississippi", "A"));
        questions.add(new Question("Which country hosted the 2016 Olympics?", "China", "UK", "Brazil", "Japan", "C"));
        questions.add(new Question("What is the smallest prime number?", "1", "2", "3", "5", "B"));
    }

    public void saveToLeaderboard(String name, int score) {
        try (FileWriter fw = new FileWriter("leaderboard.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(name + "," + score);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Failed to save leaderboard: " + e.getMessage());
        }
    }

    public void showLeaderboard() {
        StringBuilder sb = new StringBuilder("🏆 Leaderboard:\n\n");
        List<String> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("leaderboard.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                entries.add(line);
            }
        } catch (IOException e) {
            sb.append("No leaderboard found.");
        }

        entries.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.split(",")[1]);
            int scoreB = Integer.parseInt(b.split(",")[1]);
            return Integer.compare(scoreB, scoreA);
        });

        int rank = 1;
        for (String entry : entries) {
            String[] parts = entry.split(",");
            sb.append(rank++).append(". ").append(parts[0]).append(" - ").append(parts[1]).append("/15\n");
            if (rank > 10) break; // show top 10
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OnlineQuizGUI());
    }
}
