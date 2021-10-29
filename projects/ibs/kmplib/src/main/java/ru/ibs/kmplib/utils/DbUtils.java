package ru.ibs.kmplib.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author me
 */
public class DbUtils {

	public static <T> List<T> ex(Connection connection, String sql, Consumer<CallableStatement> statementConsumer, Function<ResultSet, T> resultSetFunction) {
		return ex(connection, sql, s -> statementConsumer.accept(s), rs -> resultSetFunction.apply(rs), null);
	}

	public static <T> List<T> ex(Connection сonnection, String sql, Consumer<CallableStatement> statementConsumer, Function<ResultSet, T> resultSetFunction, Integer limit) {
		try (CallableStatement statement = сonnection.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			statementConsumer.accept(statement);
			try (ResultSet resultSet = statement.executeQuery()) {
				List<T> entityList = new ArrayList<>();
				while (resultSet.next()) {
					T entity = resultSetFunction.apply(resultSet);
					entityList.add(entity);
					if (limit != null && entityList.size() >= limit) {
						break;
					}
				}
				return entityList;
			}
		} catch (Exception sqlex) {
			throw new RuntimeException("Execution of the query " + sql + " Exception!", sqlex);
		}
	}
}
