import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyApplications extends JFrame {

    JTextField studentIdField;

    JButton searchButton;
    JButton clearButton;
    JButton closeButton;

    JTable table;
    DefaultTableModel model;

    public MyApplications() {

        setTitle("My Scholarship Applications");
        setSize(1000, 550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ==============================
        // TITLE
        // ==============================

        JLabel title =
                new JLabel("MY SCHOLARSHIP APPLICATIONS");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(title, BorderLayout.NORTH);

        // ==============================
        // SEARCH PANEL
        // ==============================

        JPanel searchPanel =
                new JPanel();

        searchPanel.add(
                new JLabel("Student ID:")
        );

        studentIdField =
                new JTextField(10);

        searchPanel.add(
                studentIdField
        );

        searchButton =
                new JButton("SEARCH");

        searchPanel.add(
                searchButton
        );

        clearButton =
                new JButton("CLEAR");

        searchPanel.add(
                clearButton
        );

        add(
                searchPanel,
                BorderLayout.BEFORE_FIRST_LINE
        );

        // ==============================
        // TABLE
        // ==============================

        String[] columns = {
                "Application ID",
                "Scholarship Name",
                "Application Date",
                "Document Status",
                "Application Status",
                "Verification Status",
                "Remarks"
        };

        model =
                new DefaultTableModel(
                        columns,
                        0
                );

        table =
                new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ==============================
        // BOTTOM PANEL
        // ==============================

        JPanel bottomPanel =
                new JPanel();

        closeButton =
                new JButton("CLOSE");

        bottomPanel.add(closeButton);

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ==============================
        // BUTTON ACTIONS
        // ==============================

        searchButton.addActionListener(e -> {

            loadApplications();

        });

        clearButton.addActionListener(e -> {

            studentIdField.setText("");

            model.setRowCount(0);

        });

        closeButton.addActionListener(e -> {

            dispose();

        });

        setVisible(true);
    }

    // =================================================
    // LOAD APPLICATIONS
    // =================================================

    private void loadApplications() {

        model.setRowCount(0);

        String studentText =
                studentIdField.getText().trim();

        if (studentText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Student ID."
            );

            return;
        }

        int studentId;

        try {

            studentId =
                    Integer.parseInt(
                            studentText
                    );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Student ID must be a number."
            );

            return;
        }

        String sql =
                "SELECT " +
                "a.application_id, " +
                "sh.scholarship_name, " +
                "a.application_date, " +
                "a.document_status, " +
                "a.application_status, " +
                "v.verification_status, " +
                "v.remarks " +
                "FROM applications a " +
                "JOIN scholarships sh " +
                "ON a.scholarship_id = " +
                "sh.scholarship_id " +
                "LEFT JOIN verification v " +
                "ON a.application_id = " +
                "v.application_id " +
                "WHERE a.student_id = ? " +
                "ORDER BY a.application_id";

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Database connection failed."
                );

                return;
            }

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(
                    1,
                    studentId
            );

            ResultSet rs =
                    ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                String verificationStatus =
                        rs.getString(
                                "verification_status"
                        );

                String remarks =
                        rs.getString(
                                "remarks"
                        );

                if (verificationStatus == null) {
                    verificationStatus =
                            "Not Verified";
                }

                if (remarks == null) {
                    remarks = "-";
                }

                model.addRow(
                        new Object[]{
                                rs.getInt(
                                        "application_id"
                                ),

                                rs.getString(
                                        "scholarship_name"
                                ),

                                rs.getDate(
                                        "application_date"
                                ),

                                rs.getString(
                                        "document_status"
                                ),

                                rs.getString(
                                        "application_status"
                                ),

                                verificationStatus,

                                remarks
                        }
                );
            }

            if (!found) {

                JOptionPane.showMessageDialog(
                        this,
                        "No applications found " +
                        "for Student ID " +
                        studentId
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading applications:\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =================================================
    // MAIN
    // =================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new MyApplications()
        );
    }
}
