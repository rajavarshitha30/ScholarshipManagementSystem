import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    JButton registerButton;
    JButton scholarshipButton;
    JButton eligibilityButton;
    JButton applicationButton;
    JButton myApplicationsButton;
    JButton backButton;

    public StudentDashboard() {

        setTitle("Student Dashboard");
        setSize(500, 520);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ==============================
        // TITLE
        // ==============================

        JLabel title =
                new JLabel("STUDENT DASHBOARD");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        title.setBounds(
                100, 30, 300, 40
        );

        add(title);

        // ==============================
        // STUDENT REGISTRATION
        // ==============================

        registerButton =
                new JButton("Student Registration");

        registerButton.setBounds(
                120, 90, 260, 45
        );

        add(registerButton);

        // ==============================
        // VIEW SCHOLARSHIPS
        // ==============================

        scholarshipButton =
                new JButton("View Scholarships");

        scholarshipButton.setBounds(
                120, 145, 260, 45
        );

        add(scholarshipButton);

        // ==============================
        // CHECK ELIGIBILITY
        // ==============================

        eligibilityButton =
                new JButton("Check Eligibility");

        eligibilityButton.setBounds(
                120, 200, 260, 45
        );

        add(eligibilityButton);

        // ==============================
        // APPLY FOR SCHOLARSHIP
        // ==============================

        applicationButton =
                new JButton("Apply for Scholarship");

        applicationButton.setBounds(
                120, 255, 260, 45
        );

        add(applicationButton);

        // ==============================
        // MY APPLICATIONS
        // ==============================

        myApplicationsButton =
                new JButton("My Applications");

        myApplicationsButton.setBounds(
                120, 310, 260, 45
        );

        add(myApplicationsButton);

        // ==============================
        // BACK BUTTON
        // ==============================

        backButton =
                new JButton("Back");

        backButton.setBounds(
                120, 380, 260, 40
        );

        add(backButton);

        // =================================================
        // BUTTON ACTIONS
        // =================================================

        // Student Registration
        registerButton.addActionListener(e -> {

            new StudentRegistration().setVisible(true);

        });

        // View Scholarships
        scholarshipButton.addActionListener(e -> {

            new StudentScholarships().setVisible(true);

        });

        // Check Eligibility
        eligibilityButton.addActionListener(e -> {

            new EligibilityChecker().setVisible(true);

        });

        // Apply for Scholarship
        applicationButton.addActionListener(e -> {

            new StudentScholarships().setVisible(true);

        });

        // My Applications
        myApplicationsButton.addActionListener(e -> {

            new MyApplications().setVisible(true);

        });

        // Back
        backButton.addActionListener(e -> {

            dispose();

        });
    }

    // =================================================
    // MAIN METHOD
    // =================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new StudentDashboard().setVisible(true);

        });
    }
}