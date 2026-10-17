import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static final String URL =
            "jdbc:mysql://localhost:3306/scholarship_db";

    static final String USER = "root";

    static final String PASSWORD ="" ;

    public static Connection getConnection() {

        Connection con = null;

        try {

            con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println(
                    "Database Connected Successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Database Connection Failed!"
            );

            e.printStackTrace();
        }

        return con;
    }
}