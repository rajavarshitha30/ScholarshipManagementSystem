import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ScholarshipManagement extends JFrame {

    JTextField txtName, txtProvider, txtMaxIncome, txtMinMarks;
    JTextField txtCourse, txtCategory, txtStartDate, txtEndDate;

    JButton btnAdd, btnClear;

    public ScholarshipManagement() {

        setTitle("Scholarship Management");
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Scholarship Name
        panel.add(new JLabel("Scholarship Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        // Provider
        panel.add(new JLabel("Provider:"));
        txtProvider = new JTextField();
        panel.add(txtProvider);

        // Maximum Income
        panel.add(new JLabel("Maximum Income:"));
        txtMaxIncome = new JTextField();
        panel.add(txtMaxIncome);

        // Minimum Marks
        panel.add(new JLabel("Minimum Marks:"));
        txtMinMarks = new JTextField();
        panel.add(txtMinMarks);

        // Eligible Course
        panel.add(new JLabel("Eligible Course:"));
        txtCourse = new JTextField();
        panel.add(txtCourse);

        // Eligible Category
        panel.add(new JLabel("Eligible Category:"));
        txtCategory = new JTextField();
        panel.add(txtCategory);

        // Start Date
        panel.add(new JLabel("Start Date:"));
        txtStartDate = new JTextField();
        panel.add(txtStartDate);

        // End Date
        panel.add(new JLabel("End Date:"));
        txtEndDate = new JTextField();
        panel.add(txtEndDate);

        // Buttons
        btnAdd = new JButton("Add Scholarship");
        btnClear = new JButton("Clear");

        panel.add(btnAdd);
        panel.add(btnClear);

        add(panel);

        // Add button
        btnAdd.addActionListener(e -> addScholarship());

        // Clear button
        btnClear.addActionListener(e -> clearFields());

        setVisible(true);
    }

    private void addScholarship() {

        try {

            String name = txtName.getText();
            String provider = txtProvider.getText();

            double maxIncome =
                    Double.parseDouble(txtMaxIncome.getText());

            double minMarks =
                    Double.parseDouble(txtMinMarks.getText());

            String course = txtCourse.getText();
            String category = txtCategory.getText();

            String startDate = txtStartDate.getText();
            String endDate = txtEndDate.getText();

            String sql =
                    "INSERT INTO scholarships " +
                    "(scholarship_name, provider, max_income, " +
                    "min_marks, eligible_course, eligible_category, " +
                    "start_date, end_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, provider);
            ps.setDouble(3, maxIncome);
            ps.setDouble(4, minMarks);
            ps.setString(5, course);
            ps.setString(6, category);

            ps.setDate(7, Date.valueOf(startDate));
            ps.setDate(8, Date.valueOf(endDate));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Scholarship Added Successfully!"
            );

            clearFields();

            ps.close();
            con.close();

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for income and marks."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter dates in YYYY-MM-DD format."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }

    private void clearFields() {

        txtName.setText("");
        txtProvider.setText("");
        txtMaxIncome.setText("");
        txtMinMarks.setText("");
        txtCourse.setText("");
        txtCategory.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
    }

    public static void main(String[] args) {

        new ScholarshipManagement();
    }
}