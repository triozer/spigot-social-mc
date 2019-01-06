package fr.triozer.smc.utils;

import fr.triozer.smc.SocialMC;

/**
 * @author Cédric / Triozer
 */
public class Settings {

	public static boolean isMySQL() {
		return SocialMC.getInstance().getConfig().getBoolean("options.mysql.allow");
	}

	public static boolean isCertifiable() {
		return SocialMC.getInstance().getConfig().getBoolean("options.allow-certification");
	}

}
