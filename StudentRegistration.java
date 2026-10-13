import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentRegistration extends JFrame {

    JTextField txtId, txtName, txtDob, txtCourse, txtYear;
    JTextField txtIncome, txtMarks, txtAddress, txtContact;

    JComboBox<String> cmbGender, cmbCategory;

    JButton btnRegister, btnClear;

    public StudentRegistration() {

        setTitle("Student Registration");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Student ID
        panel.add(new JLabel("Student ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        // Name
        panel.add(new JLabel("Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        // DOB
        panel.add(new JLabel("DOB (YYYY-MM-DD):"));
        txtDob = new JTextField();
        panel.add(txtDob);

        // Gender
        panel.add(new JLabel("Gender:"));
        cmbGender = new JComboBox<>(
                new String[]{"Male", "Female", "Other"}
        );
        panel.add(cmbGender);

        // Course
        panel.add(new JLabel("Course:"));
        txtCourse = new JTextField();
        panel.add(txtCourse);

        // Year
        panel.add(new JLabel("Year of Study:"));
        txtYear = new JTextField();
        panel.add(txtYear);

        // Category
        panel.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>(
                new String[]{"General", "OBC", "SC", "ST", "EWS"}
        );
        panel.add(cmbCategory);

        // Family Income
        panel.add(new JLabel("Family Income:"));
        txtIncome = new JTextField();
        panel.add(txtIncome);

        // Marks
        panel.add(new JLabel("Marks:"));
        txtMarks = new JTextField();
        panel.add(txtMarks);

        // Address
        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        // Contact
        panel.add(new JLabel("Contact:"));
        txtContact = new JTextField();
        panel.add(txtContact);

        // Buttons
        btnRegister = new JButton("Register");
        btnClear = new JButton("Clear");

        panel.add(btnRegister);
        panel.add(btnClear);

        add(panel);

        // Register button
        btnRegister.addActionListener(e -> registerStudent());

        // Clear button
        btnClear.addActionListener(e -> clearFields());

        setVisible(true);
    }

    private void registerStudent() {

        try {

            int id = Integer.parseInt(txtId.getText());
            String name = txtName.getText();
            String dob = txtDob.getText();
            String gender = cmbGender.getSelectedItem().toString();
            String course = txtCourse.getText();
            int year = Integer.parseInt(txtYear.getText());
            String category = cmbCategory.getSelectedItem().toString();
            double income = Double.parseDouble(txtIncome.getText());
            double marks = Double.parseDouble(txtMarks.getText());
            String address = txtAddress.getText();
            String contact = txtContact.getText();

            String sql = "INSERT INTO students " +
                    "(student_id, student_name, dob, gender, course, " +
                    "year_of_study, category, family_income, marks, address, contact) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setDate(3, Date.valueOf(dob));
            ps.setString(4, gender);
            ps.setString(5, course);
            ps.setInt(6, year);
            ps.setString(7, category);
            ps.setDouble(8, income);
            ps.setDouble(9, marks);
            ps.setString(10, address);
            ps.setString(11, contact);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Student Registered Successfully!"
            );

            clearFields();

            ps.close();
            con.close();

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for ID, Year, Income and Marks."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter DOB in YYYY-MM-DD format."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );
        }
    }

    private void clearFields() {

        txtId.setText("");
        txtName.setText("");
        txtDob.setText("");
        txtCourse.setText("");
        txtYear.setText("");
        txtIncome.setText("");
        txtMarks.setText("");
        txtAddress.setText("");
        txtContact.setText("");

        cmbGender.setSelectedIndex(0);
        cmbCategory.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new StudentRegistration();
    }
}