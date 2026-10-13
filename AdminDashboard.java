import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    JLabel studentsCount;
    JLabel scholarshipsCount;
    JLabel applicationsCount;
    JLabel approvedCount;
    JLabel rejectedCount;
    JLabel pendingCount;

    JButton viewApplicationsButton;
    JButton verifyDocumentsButton;
    JButton reportsButton;
    JButton refreshButton;
    JButton logoutButton;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(800, 650);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---------------- TITLE ----------------

        JLabel title =
                new JLabel("ADMIN DASHBOARD");

        title.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        title.setBounds(200, 20, 400, 40);

        add(title);

        // ---------------- STAT LABELS ----------------

        studentsCount = createStatLabel("0");
        scholarshipsCount = createStatLabel("0");
        applicationsCount = createStatLabel("0");
        approvedCount = createStatLabel("0");
        rejectedCount = createStatLabel("0");
        pendingCount = createStatLabel("0");

        // ---------------- STAT PANELS ----------------

        add(createStatPanel(
                "TOTAL STUDENTS",
                studentsCount,
                50,
                90
        ));

        add(createStatPanel(
                "SCHOLARSHIPS",
                scholarshipsCount,
                285,
                90
        ));

        add(createStatPanel(
                "APPLICATIONS",
                applicationsCount,
                520,
                90
        ));

        add(createStatPanel(
                "APPROVED",
                approvedCount,
                50,
                190
        ));

        add(createStatPanel(
                "REJECTED",
                rejectedCount,
                285,
                190
        ));

        add(createStatPanel(
                "PENDING",
                pendingCount,
                520,
                190
        ));

        // ---------------- BUTTONS ----------------

        viewApplicationsButton =
                new JButton("View Applications");

        viewApplicationsButton.setBounds(
                100, 330, 250, 45
        );

        add(viewApplicationsButton);

        verifyDocumentsButton =
                new JButton("Verify Documents");

        verifyDocumentsButton.setBounds(
                450, 330, 250, 45
        );

        add(verifyDocumentsButton);

        reportsButton =
                new JButton("Reports");

        reportsButton.setBounds(
                100, 400, 250, 45
        );

        add(reportsButton);

        refreshButton =
                new JButton("Refresh Dashboard");

        refreshButton.setBounds(
                450, 400, 250, 45
        );

        add(refreshButton);

        logoutButton =
                new JButton("Logout");

        logoutButton.setBounds(
                275, 490, 250, 45
        );

        add(logoutButton);

        // ---------------- BUTTON ACTIONS ----------------

        viewApplicationsButton.addActionListener(e -> {

            new ApplicationViewer().setVisible(true);

        });

        verifyDocumentsButton.addActionListener(e -> {

    new DocumentVerification().setVisible(true);

});

        reportsButton.addActionListener(e -> {

            new Reports().setVisible(true);

        });

        refreshButton.addActionListener(e -> {

            loadDashboardData();

        });

        logoutButton.addActionListener(e -> {

            new Login().setVisible(true);

            dispose();

        });

        // Load database information
        loadDashboardData();
    }

    // =====================================================
    // CREATE STAT LABEL
    // =====================================================

    private JLabel createStatLabel(String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        label.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        return label;
    }

    // =====================================================
    // CREATE STAT PANEL
    // =====================================================

    private JPanel createStatPanel(
            String title,
            JLabel value,
            int x,
            int y
    ) {

        JPanel panel =
                new JPanel();

        panel.setLayout(null);

        panel.setBorder(
                BorderFactory.createLineBorder(
                        Color.GRAY
                )
        );

        panel.setBounds(
                x,
                y,
                210,
                80
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        titleLabel.setBounds(
                5,
                8,
                200,
                25
        );

        panel.add(titleLabel);

        value.setBounds(
                5,
                35,
                200,
                35
        );

        panel.add(value);

        return panel;
    }

    // =====================================================
    // LOAD DASHBOARD DATA
    // =====================================================

    private void loadDashboardData() {

        String studentsSQL =
                "SELECT COUNT(*) FROM students";

        String scholarshipsSQL =
                "SELECT COUNT(*) FROM scholarships";

        String applicationsSQL =
                "SELECT COUNT(*) FROM applications";

        String approvedSQL =
                "SELECT COUNT(*) FROM applications " +
                "WHERE application_status = 'Approved'";

        String rejectedSQL =
                "SELECT COUNT(*) FROM applications " +
                "WHERE application_status = 'Rejected'";

        String pendingSQL =
                "SELECT COUNT(*) FROM applications " +
                "WHERE application_status IN " +
                "('Submitted', 'Under Verification', 'Incomplete')";

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

            studentsCount.setText(
                    getCount(con, studentsSQL)
            );

            scholarshipsCount.setText(
                    getCount(con, scholarshipsSQL)
            );

            applicationsCount.setText(
                    getCount(con, applicationsSQL)
            );

            approvedCount.setText(
                    getCount(con, approvedSQL)
            );

            rejectedCount.setText(
                    getCount(con, rejectedSQL)
            );

            pendingCount.setText(
                    getCount(con, pendingSQL)
            );

            con.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading dashboard:\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // GET COUNT FROM DATABASE
    // =====================================================

    private String getCount(
            Connection con,
            String sql
    ) throws SQLException {

        PreparedStatement ps =
                con.prepareStatement(sql);

        ResultSet rs =
                ps.executeQuery();

        String count = "0";

        if (rs.next()) {

            count =
                    String.valueOf(
                            rs.getInt(1)
                    );
        }

        rs.close();

        ps.close();

        return count;
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new AdminDashboard().setVisible(true);

        });
    }
}