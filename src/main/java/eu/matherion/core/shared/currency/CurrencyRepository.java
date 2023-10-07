package eu.matherion.core.shared.currency;

import com.google.common.collect.Maps;
import cz.maku.mommons.ef.Entities;
import cz.maku.mommons.ef.repository.DefaultRepository;
import cz.maku.mommons.ef.statement.CompletedStatement;
import cz.maku.mommons.ef.statement.MySQLStatementImpl;
import cz.maku.mommons.ef.statement.StatementType;
import cz.maku.mommons.storage.database.type.MySQL;
import cz.maku.mommons.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrencyRepository extends DefaultRepository<String, CurrencyData> {

    private final String table = "core_currencies";
    private final String idColumn = "player";
    private final Connection connection = MySQL.getApi().getConnection();

    public CurrencyRepository() {
        super("core_currencies", MySQL.getApi().getConnection(), "player", CurrencyData.class, String.class);
    }

    private String prepareStatement(String query, Object... objects) {
        query = query.replace("{table}", table);
        query = query.replace("{id}", idColumn);
        return String.format(query, objects);
    }

    @NotNull
    private List<CurrencyData> getObjectsFromStatement(MySQLStatementImpl mySQLStatement) {
        CompletedStatement<MySQLStatementImpl> completedStatement = mySQLStatement.complete(connection);
        return completedStatement.getRecords().stream().map(record -> {
            try {
                return fromRecord(record);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<CurrencyData> selectByPlayers(List<String> players) {
        StringBuilder statement = new StringBuilder(prepareStatement("SELECT * FROM {table} WHERE "));

        for (int i = 0; i < players.size(); i++) {
            statement.append("player = '").append(players.get(i)).append("'");
            if (i != players.size() - 1) {
                statement.append(" OR ");
            }
        }

        MySQLStatementImpl mySQLStatement = new MySQLStatementImpl(statement.toString(), StatementType.SELECT);
        return getObjectsFromStatement(mySQLStatement);
    }
}
