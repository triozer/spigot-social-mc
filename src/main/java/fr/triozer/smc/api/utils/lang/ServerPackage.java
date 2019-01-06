package fr.triozer.smc.api.utils.lang;

import org.bukkit.Bukkit;

/**
 * @author Cédric / Triozer
 */
public enum ServerPackage {

    MINECRAFT("net.minecraft.server." + getServerVersion()),
    CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion());

    private final String path;

    ServerPackage(String path) {
        this.path = path;
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static int getServerVersionNumber() {
        return Integer.parseInt(getServerVersion().split("_")[1]);
    }

    @Override
    public String toString() {
        return this.path;
    }

    public Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(this.toString() + "." + className);
    }

}