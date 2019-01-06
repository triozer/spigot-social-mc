package fr.triozer.smc.api.database;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.api.integration.Integration;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author Cédric / Triozer
 */
public class Database {

	private final String host;
	private final int    port;
	private final String database;

	private Connection connection;
	private boolean    connected;

	public Database(String host, int port, String database) {
		this.host = host;
		this.port = port;
		this.database = database;
	}

	public Database auth(String username, String password, Consumer<Database> after) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=true",
					username, password);
			this.connected = true;
			init();
			after.accept(this);
		} catch (ClassNotFoundException | SQLException e) {
			this.connected = false;
		}

		return this;
	}

	private void init() {
		Bukkit.getScheduler().runTaskAsynchronously(SocialMC.getInstance(), () -> {
			try {
				PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `pending` (" +
						"`social-id` VARCHAR(36) NOT NULL, " +
						"`player-id` VARCHAR(36) NOT NULL, " +
						"`name` VARCHAR(36) NOT NULL, " +
						"`token` VARCHAR(8) NOT NULL, " +
						"UNIQUE INDEX `token` (`token`))");
				statement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public void add(SocialInfo info, Integration integration) {
		try {
			PreparedStatement preparedStatement = this.connection
					.prepareStatement("INSERT INTO pending (`social-id`, `player-id`, name, token) VALUES (?, ?, ?, ?) " +
							"ON DUPLICATE KEY UPDATE `social-id` = ?, `player-id`= ?");
			preparedStatement.setString(1, integration.getId());
			preparedStatement.setString(2, info.getUniqueId().toString());
			preparedStatement.setString(3, info.getSocialName(integration));
			preparedStatement.setString(4, info.getToken(integration));
			preparedStatement.setString(5, integration.getId());
			preparedStatement.setString(6, info.getUniqueId().toString());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isCertified(SocialInfo info, Integration integration) {
		try {
			PreparedStatement preparedStatement = this.connection
					.prepareStatement("SELECT * FROM pending WHERE `social-id` = ? AND `player-id` = ?");
			preparedStatement.setString(1, integration.getId());
			preparedStatement.setString(2, info.getUniqueId().toString());
			return !preparedStatement.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
