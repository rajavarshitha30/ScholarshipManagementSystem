
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Reports extends JFrame {

    JTable table;
    DefaultTableModel model;

    JButton refreshButton;
    JButton closeButton;

    public Reports() {

        setTitle("Scholarship Reports");
        setSize(1100, 550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= TITLE =================

        JLabel title =
                new JLabel("SCHOLARSHIP APPLICATION REPORT");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(title, BorderLayout.NORTH);

        // ================= TABLE =================

        String[] columns = {
                "Application ID",
                "Student Name",
                "Scholarship",
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

        // ================= BUTTONS =================

        JPanel buttonPanel =
                new JPanel();

        refreshButton =
                new JButton("REFRESH");

        closeButton =
                new JButton("CLOSE");

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ================= ACTIONS =================

        refreshButton.addActionListener(e -> {

            loadReports();

        });

        closeButton.addActionListener(e -> {

            dispose();

        });

        loadReports();

        setVisible(true);
    }

    // =====================================================
    // LOAD REPORTS
    // =====================================================

    private void loadReports() {

        model.setRowCount(0);

        String sql =
                "SELECT " +
                "a.application_id, " +
                "s.student_name, " +
                "sh.scholarship_name, " +
                "a.application_date, " +
                "a.document_status, " +
                "a.application_status, " +
                "v.verification_status, " +
                "v.remarks " +
                "FROM applications a " +

                "JOIN students s " +
                "ON a.student_id = s.student_id " +

                "JOIN scholarships sh " +
                "ON a.scholarship_id = " +
                "sh.scholarship_id " +

                "LEFT JOIN verification v " +
                "ON v.verification_id = (" +

                "SELECT MAX(v2.verification_id) " +
                "FROM verification v2 " +
                "WHERE v2.application_id = " +
                "a.application_id" +

                ") " +

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

                if (remarks == null ||
                        remarks.trim().isEmpty()) {

                    remarks = "-";
                }

                model.addRow(
                        new Object[]{

                                rs.getInt(
                                        "application_id"
                                ),

                                rs.getString(
                                        "student_name"
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
                        "No applications found."
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading reports:\n"
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
                () ->
                        new Reports().setVisible(true)
        );
    }
}