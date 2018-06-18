package dikanev.nikita.core.controller.db.groups;

import dikanev.nikita.core.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GroupDBController {

    private static final Logger LOG = LoggerFactory.getLogger(GroupDBController.class);

    private static GroupDBController ourInstance = new GroupDBController();

    private PreparedStatement prStatement;

    public static GroupDBController getInstance() {
        return ourInstance;
    }

    //Создание группы
    public int createGroup(String name) throws SQLException {
        String sql = "INSERT groups(id, name) VALUES (NULL, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, name);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a group with the name: " + name);
            throw new IllegalStateException("Failed to create a group with the name: " + name);
        }

        return getLastId();
    }

    //Создание группы
    public int createGroup(String name, int id) throws SQLException {
        String sql = "INSERT groups(id, name) VALUES (?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setString(2, name);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a group with the name: " + name);
            throw new IllegalStateException("Failed to create a group with the name: " + name);
        }

        return id;
    }

    //Удаление группы
    public boolean deleteGroup(int idGroup) throws SQLException {
        String sql = "DELETE FROM groups " +
                "WHERE id = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete group with id: " + idGroup);
            return false;
        }

        sql = "DELETE FROM groups_privilege " +
                "WHERE id_group = ? ";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.executeUpdate();
        prStatement.close();

        sql = "UPDATE users SET id_group = 3 " +
                "WHERE id_group = ?";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.executeUpdate();
        prStatement.close();

        return true;
    }

    //Получение имени группы
    public String getName(int idGroup) throws SQLException {
        String sql = "SELECT name " +
                "FROM groups " +
                "WHERE id = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        ResultSet res = prStatement.executeQuery();

        String nameGroup = null;
        while (res.next()) {
            nameGroup = res.getString("name");
        }

        res.close();
        return nameGroup;
    }

    //Получение последнего вставленного id
    private int getLastId() throws SQLException{
        String sql = "SELECT LAST_INSERT_ID() AS id";
        int lastId = -1;
        Statement statement = DBStorage.getInstance().getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            lastId = resultSet.getInt("id");
        }

        statement.close();
        return lastId;
    }
}
