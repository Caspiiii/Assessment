package at.ac.tuwien.sepr.groupphase.backend;

import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DatabaseCleaner {
    @Autowired
    private DataSource dataSource;


        public void cleanDatabase() throws SQLException {
            Connection c = dataSource.getConnection();
            Statement s = c.createStatement();
            //s.executeUpdate("DROP ALL OBJECTS");

            // Disable FK
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Find all tables and truncate them
            Set<String> tables = new HashSet<>();
            ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs.close();
            for (String table : tables) {
                s.executeUpdate("DELETE FROM " + table);
            }

        /*// Idem for sequences
        Set<String> sequences = new HashSet<>();
        rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            sequences.add(rs.getString(1));
        }
        rs.close();
        for (String seq : sequences) {
            s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }
        */
            // Enable FK
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            s.close();
            c.close();

    }
}
