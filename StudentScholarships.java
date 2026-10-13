import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentScholarships extends JFrame {

    JTable table;
    DefaultTableModel model;

    JButton selectButton;
    JButton refreshButton;
    JButton closeButton;

    public StudentScholarships() {

        setTitle("Available Scholarships");
        setSize(1100, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ==============================
        // TITLE
        // ==============================

        JLabel title =
                new JLabel("AVAILABLE SCHOLARSHIPS");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(title, BorderLayout.NORTH);

        // ==============================
        // TABLE
        // ==============================

        String[] columns = {
                "ID",
                "Scholarship Name",
                "Provider",
                "Max Income",
                "Min Marks",
                "Course",
                "Category",
                "Start Date",
                "End Date"
        };

        model =
                new DefaultTableModel(columns, 0);

        table = new JTable(model);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ==============================
        // BUTTONS
        // ==============================

        JPanel buttonPanel =
                new JPanel();

        selectButton =
                new JButton("SELECT SCHOLARSHIP");

        refreshButton =
                new JButton("REFRESH");

        closeButton =
                new JButton("CLOSE");

        buttonPanel.add(selectButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ==============================
        // BUTTON ACTIONS
        // ==============================

        selectButton.addActionListener(e -> {

            int selectedRow =
                    table.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a scholarship."
                );

                return;
            }

            int scholarshipId =
                    Integer.parseInt(
                            model.getValueAt(
                                    selectedRow,
                                    0
                            ).toString()
                    );

            new StudentApplication(
                    scholarshipId
            ).setVisible(true);
        });

        refreshButton.addActionListener(e -> {

            loadScholarships();

        });

        closeButton.addActionListener(e -> {

            dispose();

        });

        // Load scholarships
        loadScholarships();

        setVisible(true);
    }

    // =================================================
    // LOAD SCHOLARSHIPS
    // =================================================

    private void loadScholarships() {

        model.setRowCount(0);

        String sql =
                "SELECT scholarship_id, " +
                "scholarship_name, " +
                "provider, " +
                "max_income, " +
                "min_marks, " +
                "eligible_course, " +
                "eligible_category, " +
                "start_date, " +
                "end_date " +
                "FROM scholarships " +
                "ORDER BY scholarship_id";

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
                                        "scholarship_id"
                                ),

                                rs.getString(
                                        "scholarship_name"
                                ),

                                rs.getString(
                                        "provider"
                                ),

                                rs.getDouble(
                                        "max_income"
                                ),

                                rs.getDouble(
                                        "min_marks"
                                ),

                                rs.getString(
                                        "eligible_course"
                                ),

                                rs.getString(
                                        "eligible_category"
                                ),

                                rs.getDate(
                                        "start_date"
                                ),

                                rs.getDate(
                                        "end_date"
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
                    "Error loading scholarships:\n"
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
                () -> new StudentScholarships()
        );
    }
}