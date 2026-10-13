import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VerificationModule extends JFrame {

    JTable table;
    DefaultTableModel model;

    JTextArea remarksArea;

    JButton approveButton;
    JButton rejectButton;
    JButton incompleteButton;
    JButton refreshButton;
    JButton closeButton;

    public VerificationModule() {

        setTitle("Final Application Verification");
        setSize(1050, 650);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= TITLE =================

        JLabel title =
                new JLabel("FINAL APPLICATION VERIFICATION");

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
                "Student ID",
                "Student Name",
                "Scholarship",
                "Application Date",
                "Document Status",
                "Application Status"
        };

        model =
                new DefaultTableModel(
                        columns,
                        0
                );

        table =
                new JTable(model);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ================= BOTTOM PANEL =================

        JPanel bottomPanel =
                new JPanel();

        bottomPanel.setLayout(
                new BorderLayout()
        );

        // Remarks

        JPanel remarksPanel =
                new JPanel();

        remarksPanel.add(
                new JLabel("Remarks:")
        );

        remarksArea =
                new JTextArea(3, 35);

        remarksArea.setLineWrap(true);

        remarksArea.setWrapStyleWord(true);

        remarksPanel.add(
                new JScrollPane(remarksArea)
        );

        bottomPanel.add(
                remarksPanel,
                BorderLayout.NORTH
        );

        // Buttons

        JPanel buttonPanel =
                new JPanel();

        approveButton =
                new JButton("APPROVE");

        rejectButton =
                new JButton("REJECT");

        incompleteButton =
                new JButton("MARK INCOMPLETE");

        refreshButton =
                new JButton("REFRESH");

        closeButton =
                new JButton("CLOSE");

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(incompleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        bottomPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ================= ACTIONS =================

        approveButton.addActionListener(e -> {

            processApplication("Approved");

        });

        rejectButton.addActionListener(e -> {

            processApplication("Rejected");

        });

        incompleteButton.addActionListener(e -> {

            processApplication("Incomplete");

        });

        refreshButton.addActionListener(e -> {

            loadApplications();

        });

        closeButton.addActionListener(e -> {

            dispose();

        });

        loadApplications();

        setVisible(true);
    }

    // =====================================================
    // LOAD APPLICATIONS
    // =====================================================

    private void loadApplications() {

        model.setRowCount(0);

        String sql =
                "SELECT " +
                "a.application_id, " +
                "a.student_id, " +
                "s.student_name, " +
                "sh.scholarship_name, " +
                "a.application_date, " +
                "a.document_status, " +
                "a.application_status " +
                "FROM applications a " +
                "JOIN students s " +
                "ON a.student_id = s.student_id " +
                "JOIN scholarships sh " +
                "ON a.scholarship_id = " +
                "sh.scholarship_id " +
                "ORDER BY a.application_id";

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                return;
            }

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                model.addRow(
                        new Object[]{

                                rs.getInt(
                                        "application_id"
                                ),

                                rs.getInt(
                                        "student_id"
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
                                )
                        }
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

    // =====================================================
    // PROCESS APPLICATION
    // =====================================================

    private void processApplication(
            String decision
    ) {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an application."
            );

            return;
        }

        int applicationId =
                Integer.parseInt(
                        model.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String currentDocumentStatus =
                model.getValueAt(
                        selectedRow,
                        5
                ).toString();

        String currentApplicationStatus =
                model.getValueAt(
                        selectedRow,
                        6
                ).toString();

        String remarks =
                remarksArea
                        .getText()
                        .trim();

        // =================================================
        // APPROVAL CHECK
        // =================================================

        if (decision.equals("Approved")) {

            if (!currentDocumentStatus.equalsIgnoreCase(
                    "Verified"
            )) {

                JOptionPane.showMessageDialog(
                        this,
                        "Application cannot be approved.\n\n"
                        + "All documents must be VERIFIED "
                        + "first.\n\n"
                        + "Current Document Status: "
                        + currentDocumentStatus
                );

                return;
            }

            if (!currentApplicationStatus.equalsIgnoreCase(
                    "Under Verification"
            )) {

                JOptionPane.showMessageDialog(
                        this,
                        "Application is not ready "
                        + "for final approval.\n\n"
                        + "Current Status: "
                        + currentApplicationStatus
                );

                return;
            }
        }

        // =================================================
        // REJECTION CHECK
        // =================================================

        if (decision.equals("Rejected")) {

            if (remarks.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a reason "
                        + "for rejection."
                );

                return;
            }
        }

        // =================================================
        // INCOMPLETE CHECK
        // =================================================

        if (decision.equals("Incomplete")) {

            if (remarks.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter remarks "
                        + "for incomplete application."
                );

                return;
            }
        }

        // =================================================
        // CONFIRM
        // =================================================

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to mark "
                        + "this application as "
                        + decision + "?",
                        "Confirm Decision",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {

            return;
        }

        // =================================================
        // UPDATE APPLICATION
        // =================================================

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {

                return;
            }

            String updateSql =
                    "UPDATE applications " +
                    "SET application_status = ?, " +
                    "document_status = ? " +
                    "WHERE application_id = ?";

            PreparedStatement updatePs =
                    con.prepareStatement(
                            updateSql
                    );

            updatePs.setString(
                    1,
                    decision
            );

            if (decision.equals("Approved")) {

                updatePs.setString(
                        2,
                        "Verified"
                );

            } else if (
                    decision.equals("Rejected")
            ) {

                updatePs.setString(
                        2,
                        "Verified"
                );

            } else {

                updatePs.setString(
                        2,
                        "Incomplete"
                );
            }

            updatePs.setInt(
                    3,
                    applicationId
            );

            updatePs.executeUpdate();

            updatePs.close();

            // =================================================
            // INSERT VERIFICATION RECORD
            // =================================================

            String verificationSql =
                    "INSERT INTO verification " +
                    "(application_id, " +
                    "verification_date, " +
                    "verification_status, " +
                    "remarks) " +
                    "VALUES (?, CURDATE(), ?, ?)";

            PreparedStatement verificationPs =
                    con.prepareStatement(
                            verificationSql
                    );

            verificationPs.setInt(
                    1,
                    applicationId
            );

            verificationPs.setString(
                    2,
                    decision
            );

            verificationPs.setString(
                    3,
                    remarks
            );

            verificationPs.executeUpdate();

            verificationPs.close();

            con.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Application " +
                    decision +
                    " successfully!"
            );

            remarksArea.setText("");

            loadApplications();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error processing application:\n"
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
                        new VerificationModule()
                                .setVisible(true)
        );
    }
}