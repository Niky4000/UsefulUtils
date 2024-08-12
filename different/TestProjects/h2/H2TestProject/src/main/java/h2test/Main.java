package h2test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class Main {

	public static void testH2() throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
		for (String sql : new String[]{
			"create table person(id bigint auto_increment, name varchar(100))",
			"insert into person(name) values ('a')",
			"insert into person(name) values ('b')",
			"insert into person(name) values ('c')",}) {
			conn.prepareStatement(sql).executeUpdate();
		}

		String[] queries = new String[]{
			"select * from person",
			"select * from person limit 2",
			"select * from person limit 2 offset 1",
			"select * from person limit " + Integer.MAX_VALUE + " offset 1",
			"select * from person limit " + (Integer.MAX_VALUE - 1) + " offset 1",
			"select * from person limit " + (Integer.MAX_VALUE - 1) + " offset 2",
			"select * from person limit " + (Integer.MAX_VALUE - 2) + " offset 2"
		};

		for (int i = 0; i < queries.length; i++) {
			String query = queries[i];
			System.out.println("query" + i + ": " + query);

			showResultSet(conn.prepareStatement(query).executeQuery());
		}
	}

	private static void showResultSet(ResultSet rs) throws Exception {
		int colCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			StringBuilder b = new StringBuilder(" ");
			for (int i = 1; i < colCount + 1; i++) {
				b.append(rs.getString(i));
				b.append(' ');
			}
			System.out.println(b.toString());
		}
	}
}
