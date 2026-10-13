import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ApplicationModule extends JFrame {

    JTextField txtStudentId;
    JTextField txtScholarshipId;
    JTextField txtDocumentStatus;

    JButton btnApply;
    JButton btnClear;

    JTextArea resultArea;

    public ApplicationModule() {

        setTitle("Scholarship Application");
        setSize(600, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ==============================
        // TOP PANEL
        // ==============================

        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(4, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        // Student ID
        panel.add(new JLabel("Student ID:"));

        txtStudentId = new JTextField();

        panel.add(txtStudentId);

        // Scholarship ID
        panel.add(new JLabel("Scholarship ID:"));

        txtScholarshipId = new JTextField();

        panel.add(txtScholarshipId);

        // Document Status
        panel.add(new JLabel("Document Status:"));

        txtDocumentStatus =
                new JTextField("Submitted");

        panel.add(txtDocumentStatus);

        // Buttons
        btnApply = new JButton("Apply");

        btnClear = new JButton("Clear");

        panel.add(btnApply);

        panel.add(btnClear);

        // ==============================
        // RESULT AREA
        // ==============================

        resultArea = new JTextArea();

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

        // ==============================
        // ADD COMPONENTS
        // ==============================

        add(panel, BorderLayout.NORTH);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ==============================
        // BUTTON ACTIONS
        // ==============================

        btnApply.addActionListener(
                e -> applyScholarship()
        );

        btnClear.addActionListener(
                e -> clearFields()
        );

        setVisible(true);
    }

    // =========================================================
    // APPLY SCHOLARSHIP
    // =========================================================

    private void applyScholarship() {

        String studentIdText =
                txtStudentId.getText().trim();

        String scholarshipIdText =
                txtScholarshipId.getText().trim();

        String documentStatus =
                txtDocumentStatus.getText().trim();

        // ---------------------------------------------
        // CHECK EMPTY FIELDS
        // ---------------------------------------------

        if (studentIdText.isEmpty()
                || scholarshipIdText.isEmpty()) {

            resultArea.setText(
                    "Please enter Student ID " +
                    "and Scholarship ID."
            );

            return;
        }

        // ---------------------------------------------
        // CONVERT IDs
        // ---------------------------------------------

        int studentId;
        int scholarshipId;

        try {

            studentId =
                    Integer.parseInt(studentIdText);

            scholarshipId =
                    Integer.parseInt(scholarshipIdText);

        } catch (NumberFormatException e) {

            resultArea.setText(
                    "Student ID and Scholarship ID " +
                    "must be numbers."
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
            // GET STUDENT DETAILS
            // =================================================

            String studentSql =
                    "SELECT student_name, course, category, " +
                    "family_income, marks " +
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

            double income =
                    studentRs.getDouble(
                            "family_income"
                    );

            double marks =
                    studentRs.getDouble(
                            "marks"
                    );

            // =================================================
            // GET SCHOLARSHIP DETAILS
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
                        "Scholarship ID " +
                        scholarshipId +
                        " was not found."
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

            double maxIncome =
                    scholarshipRs.getDouble(
                            "max_income"
                    );

            double minMarks =
                    scholarshipRs.getDouble(
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

            // =================================================
            // ELIGIBILITY CHECK
            // =================================================

            Date today =
                    new Date(
                            System.currentTimeMillis()
                    );

            boolean marksEligible =
                    marks >= minMarks;

            boolean incomeEligible =
                    income <= maxIncome;

            boolean courseEligible =
                    eligibleCourse.equalsIgnoreCase("ALL")
                    ||
                    eligibleCourse.equalsIgnoreCase(
                            course
                    );

            boolean categoryEligible =
                    eligibleCategory.equalsIgnoreCase("ALL")
                    ||
                    eligibleCategory.equalsIgnoreCase(
                            category
                    );

            boolean dateEligible =
                    !today.before(startDate)
                    &&
                    !today.after(endDate);

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

            // =================================================
            // DISPLAY ELIGIBILITY RESULT
            // =================================================

            StringBuilder result =
                    new StringBuilder();

            result.append(
                    "SCHOLARSHIP APPLICATION\n"
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
                    "ELIGIBILITY CHECK\n"
            );

            result.append(
                    "-----------------\n"
            );

            // Marks
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

            // Income
            result.append(
                    "Income: "
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

            // Course
            result.append(
                    "Course: "
            );

            result.append(course);

            result.append(
                    " = "
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

            // Category
            result.append(
                    "Category: "
            );

            result.append(category);

            result.append(
                    " = "
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

            // Date
            result.append(
                    "Date: "
            );

            result.append(today);

            result.append(
                    " between "
            );

            result.append(startDate);

            result.append(
                    " and "
            );

            result.append(endDate);

            result.append(
                    " : "
            );

            result.append(
                    dateEligible
                    ? "PASS"
                    : "FAIL"
            );

            result.append("\n\n");

            // =================================================
            // IF NOT ELIGIBLE
            // =================================================

            if (!eligible) {

                result.append(
                        "STATUS: NOT ELIGIBLE\n\n"
                );

                result.append(
                        "APPLICATION NOT ALLOWED."
                );

                resultArea.setText(
                        result.toString()
                );

                studentRs.close();
                studentPs.close();

                scholarshipRs.close();
                scholarshipPs.close();

                con.close();

                return;
            }

            // =================================================
            // ELIGIBLE
            // =================================================

            result.append(
                    "STATUS: ELIGIBLE\n\n"
            );

            result.append(
                    "Student can apply for this scholarship."
            );

            resultArea.setText(
                    result.toString()
            );

            // =================================================
            // CHECK DUPLICATE APPLICATION
            // =================================================

            String duplicateSql =
                    "SELECT application_id " +
                    "FROM applications " +
                    "WHERE student_id = ? " +
                    "AND scholarship_id = ?";

            PreparedStatement duplicatePs =
                    con.prepareStatement(
                            duplicateSql
                    );

            duplicatePs.setInt(
                    1,
                    studentId
            );

            duplicatePs.setInt(
                    2,
                    scholarshipId
            );

            ResultSet duplicateRs =
                    duplicatePs.executeQuery();

            if (duplicateRs.next()) {

                resultArea.append(
                        "\n\nYou have already applied " +
                        "for this scholarship."
                );

                duplicateRs.close();
                duplicatePs.close();

                studentRs.close();
                studentPs.close();

                scholarshipRs.close();
                scholarshipPs.close();

                con.close();

                return;
            }

            duplicateRs.close();
            duplicatePs.close();

            // =================================================
            // INSERT APPLICATION
            // =================================================

            String insertSql =
                    "INSERT INTO applications " +
                    "(student_id, scholarship_id, " +
                    "application_date, " +
                    "document_status, " +
                    "application_status) " +
                    "VALUES (?, ?, ?, ?, ?)";

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

            insertPs.setDate(
                    3,
                    Date.valueOf(
                            LocalDate.now()
                    )
            );

            insertPs.setString(
                    4,
                    documentStatus
            );

            insertPs.setString(
                    5,
                    "Submitted"
            );

            int rows =
                    insertPs.executeUpdate();

            // =================================================
            // SUCCESS MESSAGE
            // =================================================

            if (rows > 0) {

                resultArea.append(
                        "\n\nAPPLICATION SUBMITTED " +
                        "SUCCESSFULLY!"
                );

                resultArea.append(
                        "\n\nApplication Status: Submitted"
                );

                resultArea.append(
                        "\nDocument Status: " +
                        documentStatus
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Application submitted successfully!"
                );
            }

            // =================================================
            // CLOSE RESOURCES
            // =================================================

            insertPs.close();

            studentRs.close();
            studentPs.close();

            scholarshipRs.close();
            scholarshipPs.close();

            con.close();

        } catch (SQLException e) {

            resultArea.setText(
                    "Database Error:\n\n" +
                    e.getMessage()
            );

            e.printStackTrace();

        } catch (Exception e) {

            resultArea.setText(
                    "Error:\n\n" +
                    e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // CLEAR BUTTON
    // =========================================================

    private void clearFields() {

        txtStudentId.setText("");

        txtScholarshipId.setText("");

        txtDocumentStatus.setText(
                "Submitted"
        );

        resultArea.setText("");
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ApplicationModule();

        });
    }
}