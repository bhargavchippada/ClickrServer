package webinterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.SynchronousMode;

public class ServletConfig implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public void contextInitialized(ServletContextEvent event) {
		String database = event.getServletContext().getInitParameter("DATABASE");
		String dbpath = System.getenv("CLICKR_DB");

		if (dbpath == null || dbpath.isEmpty()) {
			LOGGER.info("CLICKR_DB ENV VARIABLE IS NOT SET!!");
			String user = System.getProperty("user.name");
			dbpath = "/home/" + user + "/" + database;
			LOGGER.info("username: " + user);
		}

		establishDatabaseConnection(dbpath, event.getServletContext());
	}

	protected static void establishDatabaseConnection(String databasepath,
			ServletContext servcontext) {
		Connection connection;
		// String path = event.getServletContext().getRealPath(database);
		LOGGER.info("path: " + databasepath);
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			SQLiteConfig config = new SQLiteConfig();
			config.enforceForeignKeys(true);
			config.enableFullSync(true);
			config.setSynchronous(SynchronousMode.FULL);
			connection = DriverManager.getConnection("jdbc:sqlite:" + databasepath,
					config.toProperties());
			servcontext.setAttribute("sqliteconn", connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Object connection = sce.getServletContext().getAttribute("sqliteconn");
		try {
			if (connection != null) ((Connection) connection).close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
