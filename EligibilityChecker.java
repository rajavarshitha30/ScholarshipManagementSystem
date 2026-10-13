import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EligibilityChecker extends JFrame {

    JTextField txtStudentId;
    JTextField txtScholarshipId;

    JButton btnCheck;

    JTextArea resultArea;

    public EligibilityChecker() {

        setTitle("Scholarship Eligibility Checker");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        topPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        // Student ID
        topPanel.add(new JLabel("Student ID:"));

        txtStudentId = new JTextField();
        topPanel.add(txtStudentId);

        // Scholarship ID
        topPanel.add(new JLabel("Scholarship ID:"));

        txtScholarshipId = new JTextField();
        topPanel.add(txtScholarshipId);

        // Button
        btnCheck = new JButton("Check Eligibility");

        topPanel.add(btnCheck);
        topPanel.add(new JLabel(""));

        // Result area
        resultArea = new JTextArea();

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 15));

        JScrollPane scrollPane =
                new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnCheck.addActionListener(e -> checkEligibility());

        setVisible(true);
    }

    private void checkEligibility() {

        try {

            int studentId =
                    Integer.parseInt(txtStudentId.getText());

            int scholarshipId =
                    Integer.parseInt(txtScholarshipId.getText());

            Connection con = DBConnection.getConnection();

            // Get student details
            String studentSQL =
                    "SELECT * FROM students WHERE student_id = ?";

            PreparedStatement studentPS =
                    con.prepareStatement(studentSQL);

            studentPS.setInt(1, studentId);

            ResultSet studentRS =
                    studentPS.executeQuery();

            if (!studentRS.next()) {

                resultArea.setText(
                        "Student not found!"
                );

                con.close();
                return;
            }

            String studentName =
                    studentRS.getString("student_name");

            String course =
                    studentRS.getString("course");

            String category =
                    studentRS.getString("category");

            double income =
                    studentRS.getDouble("family_income");

            double marks =
                    studentRS.getDouble("marks");

            // Get scholarship details
            String scholarshipSQL =
                    "SELECT * FROM scholarships " +
                    "WHERE scholarship_id = ?";

            PreparedStatement scholarshipPS =
                    con.prepareStatement(scholarshipSQL);

            scholarshipPS.setInt(1, scholarshipId);

            ResultSet scholarshipRS =
                    scholarshipPS.executeQuery();

            if (!scholarshipRS.next()) {

                resultArea.setText(
                        "Scholarship not found!"
                );

                con.close();
                return;
            }

            String scholarshipName =
                    scholarshipRS.getString("scholarship_name");

            double maxIncome =
                    scholarshipRS.getDouble("max_income");

            double minMarks =
                    scholarshipRS.getDouble("min_marks");

            String eligibleCourse =
                    scholarshipRS.getString("eligible_course");

            String eligibleCategory =
                    scholarshipRS.getString("eligible_category");

            Date startDate =
                    scholarshipRS.getDate("start_date");

            Date endDate =
                    scholarshipRS.getDate("end_date");

            Date today =
                    new Date(System.currentTimeMillis());

            // Eligibility checks

            boolean marksEligible =
                    marks >= minMarks;

            boolean incomeEligible =
                    income <= maxIncome;

            boolean courseEligible =
                    eligibleCourse.equalsIgnoreCase("ALL")
                    ||
                    eligibleCourse.equalsIgnoreCase(course);

            boolean categoryEligible =
                    eligibleCategory.equalsIgnoreCase("ALL")
                    ||
                    eligibleCategory.equalsIgnoreCase(category);

            boolean dateEligible =
                    !today.before(startDate)
                    &&
                    !today.after(endDate);

            // Final result

            boolean eligible =
                    marksEligible
                    &&
                    incomeEligible
                    &&
                    courseEligible
                    &&
                    categoryEligible
                    &&
                    dateEligible;

            StringBuilder result =
                    new StringBuilder();

            result.append("===== ELIGIBILITY RESULT =====\n\n");

            result.append("Student Name: ")
                    .append(studentName)
                    .append("\n");

            result.append("Scholarship: ")
                    .append(scholarshipName)
                    .append("\n\n");

            result.append("Marks: ")
                    .append(marks)
                    .append(" / Required: ")
                    .append(minMarks)
                    .append("\n");

            result.append("Income: ")
                    .append(income)
                    .append(" / Maximum: ")
                    .append(maxIncome)
                    .append("\n");

            result.append("Course: ")
                    .append(course)
                    .append(" / Required: ")
                    .append(eligibleCourse)
                    .append("\n");

            result.append("Category: ")
                    .append(category)
                    .append(" / Required: ")
                    .append(eligibleCategory)
                    .append("\n");

            result.append("Application Period: ")
                    .append(startDate)
                    .append(" to ")
                    .append(endDate)
                    .append("\n\n");

            if (eligible) {

                result.append(
                        "STATUS: ELIGIBLE"
                );

            } else {

                result.append(
                        "STATUS: NOT ELIGIBLE"
                );
            }

            resultArea.setText(result.toString());

            studentRS.close();
            scholarshipRS.close();
            studentPS.close();
            scholarshipPS.close();
            con.close();

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid Student ID and Scholarship ID."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        new EligibilityChecker();
    }
}
