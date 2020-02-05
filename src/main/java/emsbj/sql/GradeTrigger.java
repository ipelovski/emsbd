package emsbj.sql;

import org.h2.tools.TriggerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeTrigger extends TriggerAdapter {
    @Override
    public void fire(Connection connection, ResultSet oldRow, ResultSet newRow) throws SQLException {
        Long id = newRow.getLong("id");
        PreparedStatement prep = connection.prepareStatement(
            "UPDATE GRADE SET ORDINAL=ID WHERE ID=?");
        prep.setLong(1, id);
        prep.execute();
    }
}
