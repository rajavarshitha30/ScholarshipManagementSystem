import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    JButton studentButton;
    JButton adminButton;
    JButton exitButton;

    public Main() {

        setTitle("Student Scholarship Eligibility Management System");

        setSize(550, 400);

        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        // ---------------- TITLE ----------------

        JLabel title = new JLabel(
                "<html><center>STUDENT SCHOLARSHIP<br>" +
                "ELIGIBILITY MANAGEMENT SYSTEM</center></html>"
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        title.setBounds(70, 35, 410, 70);

        add(title);

        // ---------------- STUDENT BUTTON ----------------

        studentButton =
                new JButton("STUDENT");

        studentButton.setBounds(
                150, 140, 250, 45
        );

        add(studentButton);

        // ---------------- ADMIN BUTTON ----------------

        adminButton =
                new JButton("ADMIN");

        adminButton.setBounds(
                150, 200, 250, 45
        );

        add(adminButton);

        // ---------------- EXIT BUTTON ----------------

        exitButton =
                new JButton("EXIT");

        exitButton.setBounds(
                150, 260, 250, 45
        );

        add(exitButton);

        // ---------------- ACTIONS ----------------

        studentButton.addActionListener(e -> {

    new StudentDashboard().setVisible(true);

});

        adminButton.addActionListener(e -> {

            new Login().setVisible(true);

        });

        exitButton.addActionListener(e -> {

            System.exit(0);

        });
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new Main().setVisible(true);

        });
    }
}