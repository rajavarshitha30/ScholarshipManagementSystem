import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentApplication extends JFrame {

    int scholarshipId;

    JTextField studentIdField;

    JTextArea resultArea;

    JButton checkButton;
    JButton submitButton;
    JButton clearButton;

    // Possible values:
    // ELIGIBLE
    // NOT ELIGIBLE
    // PENDING
    String eligibilityStatus = "";

    public StudentApplication(int scholarshipId) {

        this.scholarshipId = scholarshipId;

        setTitle("Scholarship Application");
        setSize(650, 550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---------------- TITLE ----------------

        JLabel title =
                new JLabel("SCHOLARSHIP APPLICATION");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(title, BorderLayout.NORTH);

        // ---------------- INPUT PANEL ----------------

        JPanel inputPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10
                        )
                );

        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        10,
                        20
                )
        );

        inputPanel.add(
                new JLabel("Student ID:")
        );

        studentIdField =
                new JTextField();

        inputPanel.add(
                studentIdField
        );

        checkButton =
                new JButton("CHECK ELIGIBILITY");

        inputPanel.add(checkButton);

        submitButton =
                new JButton("SUBMIT APPLICATION");

        submitButton.setEnabled(false);

        inputPanel.add(submitButton);

        add(
                inputPanel,
                BorderLayout.NORTH
        );

        // ---------------- RESULT AREA ----------------

        resultArea =
                new JTextArea();

        resultArea.setEditable(false);

        resultArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        resultArea.setLineWrap(true);

        resultArea.setWrapStyleWord(true);

        JScrollPane scrollPane =
                new JScrollPane(resultArea);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ---------------- BOTTOM ----------------

        JPanel bottomPanel =
                new JPanel();

        clearButton =
                new JButton("CLEAR");

        bottomPanel.add(clearButton);

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ---------------- BUTTON ACTIONS ----------------

        checkButton.addActionListener(e -> {

            checkEligibility();

        });

        submitButton.addActionListener(e -> {

            submitApplication();

        });

        clearButton.addActionListener(e -> {

            studentIdField.setText("");

            resultArea.setText("");

            eligibilityStatus = "";

            submitButton.setEnabled(false);

        });

        setVisible(true);
    }

    // =====================================================
    // CHECK ELIGIBILITY
    // =====================================================

    private void checkEligibility() {

        eligibilityStatus = "";

        submitButton.setEnabled(false);

        String studentText =
                studentIdField.getText().trim();

        if (studentText.isEmpty()) {

            resultArea.setText(
                    "Please enter Student ID."
            );

            return;
        }

        int studentId;

        try {

            studentId =
                    Integer.parseInt(studentText);

        } catch (NumberFormatException e) {

            resultArea.setText(
                    "Student ID must be a number."
            );

            return;
        }

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                resultArea.setText(
                        "Database connection failed."
                );

                return;
            }

            // =================================================
            // GET STUDENT
            // =================================================

            String studentSql =
                    "SELECT student_name, course, " +
                    "category, family_income, marks " +
                    "FROM students " +
                    "WHERE student_id = ?";

            PreparedStatement studentPs =
                    con.prepareStatement(studentSql);

            studentPs.setInt(
                    1,
                    studentId
            );

            ResultSet studentRs =
                    studentPs.executeQuery();

            if (!studentRs.next()) {

                resultArea.setText(
                        "Student ID " +
                        studentId +
                        " was not found."
                );

                studentRs.close();
                studentPs.close();
                con.close();

                return;
            }

            String studentName =
                    studentRs.getString(
                            "student_name"
                    );

            String course =
                    studentRs.getString(
                            "course"
                    );

            String category =
                    studentRs.getString(
                            "category"
                    );

            // Check whether required values are missing
            boolean studentDataMissing =
                    course == null ||
                    course.trim().isEmpty() ||
                    category == null ||
                    category.trim().isEmpty() ||
                    studentRs.getObject(
                            "family_income"
                    ) == null ||
                    studentRs.getObject(
                            "marks"
                    ) == null;

            double income = 0;
            double marks = 0;

            if (!studentDataMissing) {

                income =
                        studentRs.getDouble(
                                "family_income"
                        );

                marks =
                        studentRs.getDouble(
                                "marks"
                        );
            }

            // =================================================
            // GET SCHOLARSHIP
            // =================================================

            String scholarshipSql =
                    "SELECT scholarship_name, " +
                    "max_income, min_marks, " +
                    "eligible_course, " +
                    "eligible_category, " +
                    "start_date, end_date " +
                    "FROM scholarships " +
                    "WHERE scholarship_id = ?";

            PreparedStatement scholarshipPs =
                    con.prepareStatement(
                            scholarshipSql
                    );

            scholarshipPs.setInt(
                    1,
                    scholarshipId
            );

            ResultSet scholarshipRs =
                    scholarshipPs.executeQuery();

            if (!scholarshipRs.next()) {

                resultArea.setText(
                        "Scholarship not found."
                );

                studentRs.close();
                studentPs.close();

                scholarshipRs.close();
                scholarshipPs.close();

                con.close();

                return;
            }

            String scholarshipName =
                    scholarshipRs.getString(
                            "scholarship_name"
                    );

            Object maxIncomeObject =
                    scholarshipRs.getObject(
                            "max_income"
                    );

            Object minMarksObject =
                    scholarshipRs.getObject(
                            "min_marks"
                    );

            String eligibleCourse =
                    scholarshipRs.getString(
                            "eligible_course"
                    );

            String eligibleCategory =
                    scholarshipRs.getString(
                            "eligible_category"
                    );

            Date startDate =
                    scholarshipRs.getDate(
                            "start_date"
                    );

            Date endDate =
                    scholarshipRs.getDate(
                            "end_date"
                    );

            boolean scholarshipDataMissing =
                    maxIncomeObject == null ||
                    minMarksObject == null ||
                    eligibleCourse == null ||
                    eligibleCourse.trim().isEmpty() ||
                    eligibleCategory == null ||
                    eligibleCategory.trim().isEmpty() ||
                    startDate == null ||
                    endDate == null;

            // =================================================
            // PENDING CONDITION
            // =================================================

            if (studentDataMissing ||
                    scholarshipDataMissing) {

                eligibilityStatus = "PENDING";

                StringBuilder result =
                        new StringBuilder();

                result.append(
                        "SCHOLARSHIP ELIGIBILITY\n"
                );

                result.append(
                        "========================\n\n"
                );

                result.append(
                        "Student Name: "
                );

                result.append(studentName);

                result.append("\n");

                result.append(
                        "Scholarship: "
                );

                result.append(scholarshipName);

                result.append("\n\n");

                result.append(
                        "STATUS: ELIGIBILITY PENDING\n\n"
                );

                result.append(
                        "The system cannot make a final " +
                        "eligibility decision because " +
                        "required information is missing.\n\n"
                );

                result.append(
                        "Please complete the missing " +
                        "student or scholarship information."
                );

                resultArea.setText(
                        result.toString()
                );

                submitButton.setEnabled(false);

                studentRs.close();
                studentPs.close();

                scholarshipRs.close();
                scholarshipPs.close();

                con.close();

                return;
            }

            // =================================================
            // GET SCHOLARSHIP VALUES
            // =================================================

            double maxIncome =
                    scholarshipRs.getDouble(
                            "max_income"
                    );

            double minMarks =
                    scholarshipRs.getDouble(
                            "min_marks"
                    );

            Date today =
                    new Date(
                            System.currentTimeMillis()
                    );

            // =================================================
            // ELIGIBILITY CONDITIONS
            // =================================================

            boolean marksEligible =
                    marks >= minMarks;

            boolean incomeEligible =
                    income <= maxIncome;

            boolean courseEligible =
                    eligibleCourse.equalsIgnoreCase(
                            "ALL"
                    )
                    ||
                    eligibleCourse.equalsIgnoreCase(
                            course
                    );

            boolean categoryEligible =
                    eligibleCategory.equalsIgnoreCase(
                            "ALL"
                    )
                    ||
                    eligibleCategory.equalsIgnoreCase(
                            category
                    );

            boolean dateEligible =
                    !today.before(startDate)
                    &&
                    !today.after(endDate);

            boolean eligible =
                    marksEligible &&
                    incomeEligible &&
                    courseEligible &&
                    categoryEligible &&
                    dateEligible;

            // =================================================
            // FINAL STATUS
            // =================================================

            if (eligible) {

                eligibilityStatus = "ELIGIBLE";

            } else {

                eligibilityStatus = "NOT ELIGIBLE";

            }

            // =================================================
            // DISPLAY RESULT
            // =================================================

            StringBuilder result =
                    new StringBuilder();

            result.append(
                    "SCHOLARSHIP ELIGIBILITY\n"
            );

            result.append(
                    "========================\n\n"
            );

            result.append(
                    "Student Name: "
            );

            result.append(studentName);

            result.append("\n");

            result.append(
                    "Scholarship: "
            );

            result.append(scholarshipName);

            result.append("\n\n");

            result.append(
                    "Eligibility Conditions\n"
            );

            result.append(
                    "-----------------------\n"
            );

            result.append(
                    "Marks: "
            );

            result.append(marks);

            result.append(
                    " >= "
            );

            result.append(minMarks);

            result.append(
                    " : "
            );

            result.append(
                    marksEligible
                            ? "PASS"
                            : "FAIL"
            );

            result.append("\n");

            result.append(
                    "Family Income: "
            );

            result.append(income);

            result.append(
                    " <= "
            );

            result.append(maxIncome);

            result.append(
                    " : "
            );

            result.append(
                    incomeEligible
                            ? "PASS"
                            : "FAIL"
            );

            result.append("\n");

            result.append(
                    "Course: "
            );

            result.append(course);

            result.append(
                    " / Required: "
            );

            result.append(eligibleCourse);

            result.append(
                    " : "
            );

            result.append(
                    courseEligible
                            ? "PASS"
                            : "FAIL"
            );

            result.append("\n");

            result.append(
                    "Category: "
            );

            result.append(category);

            result.append(
                    " / Required: "
            );

            result.append(eligibleCategory);

            result.append(
                    " : "
            );

            result.append(
                    categoryEligible
                            ? "PASS"
                            : "FAIL"
            );

            result.append("\n");

            result.append(
                    "Application Date: "
            );

            result.append(today);

            result.append(
                    " : "
            );

            result.append(
                    dateEligible
                            ? "PASS"
                            : "FAIL"
            );

            result.append("\n\n");

            if (eligibilityStatus.equals("ELIGIBLE")) {

                result.append(
                        "STATUS: ELIGIBLE\n\n"
                );

                result.append(
                        "You can apply for this scholarship."
                );

                submitButton.setEnabled(true);

            } else {

                result.append(
                        "STATUS: NOT ELIGIBLE\n\n"
                );

                result.append(
                        "You cannot apply for this scholarship."
                );

                submitButton.setEnabled(false);
            }

            resultArea.setText(
                    result.toString()
            );

            studentRs.close();
            studentPs.close();

            scholarshipRs.close();
            scholarshipPs.close();

            con.close();

        } catch (SQLException e) {

            resultArea.setText(
                    "Database Error:\n\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // SUBMIT APPLICATION
    // =====================================================

    private void submitApplication() {

        if (!eligibilityStatus.equals("ELIGIBLE")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Application cannot be submitted."
            );

            return;
        }

        String studentText =
                studentIdField.getText().trim();

        int studentId;

        try {

            studentId =
                    Integer.parseInt(studentText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Student ID."
            );

            return;
        }

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                return;
            }

            // Check duplicate application

            String checkSql =
                    "SELECT application_id " +
                    "FROM applications " +
                    "WHERE student_id = ? " +
                    "AND scholarship_id = ?";

            PreparedStatement checkPs =
                    con.prepareStatement(
                            checkSql
                    );

            checkPs.setInt(
                    1,
                    studentId
            );

            checkPs.setInt(
                    2,
                    scholarshipId
            );

            ResultSet checkRs =
                    checkPs.executeQuery();

            if (checkRs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "You have already applied " +
                        "for this scholarship."
                );

                checkRs.close();
                checkPs.close();
                con.close();

                return;
            }

            checkRs.close();
            checkPs.close();

            // Insert application

            String insertSql =
                    "INSERT INTO applications " +
                    "(student_id, scholarship_id, " +
                    "application_date, " +
                    "document_status, " +
                    "application_status) " +
                    "VALUES (?, ?, CURDATE(), " +
                    "'Submitted', 'Submitted')";

            PreparedStatement insertPs =
                    con.prepareStatement(
                            insertSql
                    );

            insertPs.setInt(
                    1,
                    studentId
            );

            insertPs.setInt(
                    2,
                    scholarshipId
            );

            int rows =
                    insertPs.executeUpdate();

            if (rows > 0) {

                resultArea.append(
                        "\n\nAPPLICATION SUBMITTED " +
                        "SUCCESSFULLY!"
                );

                resultArea.append(
                        "\nApplication Status: Submitted"
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Application submitted successfully!"
                );

                submitButton.setEnabled(false);
            }

            insertPs.close();
            con.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error submitting application:\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new StudentApplication(1)
        );
    }
}