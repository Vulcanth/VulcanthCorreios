package dev.ortega.correio.database.type;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.ortega.correio.Vulcanth;
import dev.ortega.correio.database.ConnectionBase;

public class SQLite extends ConnectionBase {

    private int query;

    public SQLite() {
        this.query = 0;
    }

    @Override
    public void openConnection() {
        try {
            query++;
            if ((connection != null) && (!connection.isClosed()))
                return;

            File dataFolder = new File(Vulcanth.getInstance().getDataFolder(), "correio.db");

			Class.forName("sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch(ClassNotFoundException | SQLException e) {
            query--;
            e.getStackTrace();
            System.out.println(
                    "Ocorreu um erro ao abrir a conexão!");
        }
    }

    @Override
    public void closeConnection() {
        query--;
        if (query <= 0) {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (Exception e) {
                System.out.println(
                        "Houve um erro ao fechar a conexão!");
            }
        }
    }

}
