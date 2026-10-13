import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DocumentVerification extends JFrame {

    JTable table;
    DefaultTableModel model;

    JButton verifyButton;
    JButton incompleteButton;
    JButton refreshButton;
    JButton closeButton;

    public DocumentVerification() {

        setTitle("Document Verification");
        setSize(1000, 550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= TITLE =================

        JLabel title =
                new JLabel("DOCUMENT VERIFICATION");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(title, BorderLayout.NORTH);

        // ================= TABLE =================

        String[] columns = {
                "Document ID",
                "Application ID",
                "Student ID",
                "Student Name",
                "Document Name",
                "Document Status"
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

        // ================= BUTTONS =================

        JPanel buttonPanel =
                new JPanel();

        verifyButton =
                new JButton("VERIFY DOCUMENT");

        incompleteButton =
                new JButton("MARK INCOMPLETE");

        refreshButton =
                new JButton("REFRESH");

        closeButton =
                new JButton("CLOSE");

        buttonPanel.add(verifyButton);
        buttonPanel.add(incompleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ================= ACTIONS =================

        verifyButton.addActionListener(e -> {

            updateDocumentStatus("Verified");

        });

        incompleteButton.addActionListener(e -> {

            updateDocumentStatus("Incomplete");

        });

        refreshButton.addActionListener(e -> {

            loadDocuments();

        });

        closeButton.addActionListener(e -> {

            dispose();

        });

        loadDocuments();

        setVisible(true);
    }

    // =====================================================
    // LOAD DOCUMENTS
    // =====================================================

    private void loadDocuments() {

        model.setRowCount(0);

        String sql =
                "SELECT " +
                "d.document_id, " +
                "d.application_id, " +
                "a.student_id, " +
                "s.student_name, " +
                "d.document_name, " +
                "d.document_status " +
                "FROM documents d " +
                "JOIN applications a " +
                "ON d.application_id = a.application_id " +
                "JOIN students s " +
                "ON a.student_id = s.student_id " +
                "ORDER BY d.document_id";

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

            while (rs.next()) {

                model.addRow(
                        new Object[]{

                                rs.getInt(
                                        "document_id"
                                ),

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
                                        "document_name"
                                ),

                                rs.getString(
                                        "document_status"
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
                    "Error loading documents:\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // UPDATE DOCUMENT STATUS
    // =====================================================

    private void updateDocumentStatus(
            String newStatus
    ) {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a document."
            );

            return;
        }

        int documentId =
                Integer.parseInt(
                        model.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        int applicationId =
                Integer.parseInt(
                        model.getValueAt(
                                selectedRow,
                                1
                        ).toString()
                );

        String documentName =
                model.getValueAt(
                        selectedRow,
                        4
                ).toString();

        String sql =
                "UPDATE documents " +
                "SET document_status = ? " +
                "WHERE document_id = ?";

        try {

            Connection con =
                    DBConnection.getConnection();

            if (con == null) {
                return;
            }

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(
                    1,
                    newStatus
            );

            ps.setInt(
                    2,
                    documentId
            );

            int rows =
                    ps.executeUpdate();

            ps.close();

            if (rows > 0) {

                // Update overall application status
                updateApplicationStatus(
                        con,
                        applicationId
                );

                JOptionPane.showMessageDialog(
                        this,
                        documentName +
                        " marked as " +
                        newStatus + "."
                );

                loadDocuments();
            }

            con.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating document:\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // UPDATE APPLICATION STATUS
    // =====================================================

    private void updateApplicationStatus(
            Connection con,
            int applicationId
    ) throws SQLException {

        String sql =
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(" +
                "CASE " +
                "WHEN document_status = 'Verified' " +
                "THEN 1 ELSE 0 END" +
                ") AS verified, " +
                "SUM(" +
                "CASE " +
                "WHEN document_status = 'Incomplete' " +
                "THEN 1 ELSE 0 END" +
                ") AS incomplete " +
                "FROM documents " +
                "WHERE application_id = ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(
                1,
                applicationId
        );

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            int total =
                    rs.getInt("total");

            int verified =
                    rs.getInt("verified");

            int incomplete =
                    rs.getInt("incomplete");

            String newApplicationStatus;

            if (incomplete > 0) {

                newApplicationStatus =
                        "Incomplete";

            } else if (
                    total > 0 &&
                    verified == total
            ) {

                newApplicationStatus =
                        "Under Verification";

            } else {

                newApplicationStatus =
                        "Submitted";
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
                    newApplicationStatus
            );

            updatePs.setString(
                    2,
                    newApplicationStatus.equals(
                            "Under Verification"
                    )
                            ? "Verified"
                            : newApplicationStatus
            );

            updatePs.setInt(
                    3,
                    applicationId
            );

            updatePs.executeUpdate();

            updatePs.close();
        }

        rs.close();
        ps.close();
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () ->
                        new DocumentVerification()
                                .setVisible(true)
        );
    }
}