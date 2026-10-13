import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ApplicationViewer extends JFrame {

    JTable table;
    DefaultTableModel model;

    public ApplicationViewer() {

        setTitle("View Applications");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("ALL SCHOLARSHIP APPLICATIONS");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columns = {
            "Application ID",
            "Student ID",
            "Student Name",
            "Scholarship",
            "Application Date",
            "Document Status",
            "Application Status"
        };

        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadApplications();
    }

    private void loadApplications() {

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                "SELECT a.application_id, " +
                "a.student_id, " +
                "s.student_name, " +
                "sc.scholarship_name, " +
                "a.application_date, " +
                "a.document_status, " +
                "a.application_status " +
                "FROM applications a " +
                "JOIN students s " +
                "ON a.student_id = s.student_id " +
                "JOIN scholarships sc " +
                "ON a.scholarship_id = sc.scholarship_id " +
                "ORDER BY a.application_id DESC";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("application_id"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("scholarship_name"),
                    rs.getDate("application_date"),
                    rs.getString("document_status"),
                    rs.getString("application_status")
                };

                model.addRow(row);
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading applications:\n" +
                e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
