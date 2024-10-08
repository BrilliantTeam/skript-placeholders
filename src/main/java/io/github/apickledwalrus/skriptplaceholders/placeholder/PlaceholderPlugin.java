package io.github.apickledwalrus.skriptplaceholders.placeholder;

import ch.njol.skript.Skript;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A utility enum for the placeholder plugins.
 */
public enum PlaceholderPlugin {

	PLACEHOLDER_API("PlaceholderAPI", Skript.classExists("me.clip.placeholderapi.expansion.PlaceholderExpansion")) {
		private final char[] illegalCharacters = new char[]{'%', '{', '}', '_'};

		@Override
		public @Nullable String validatePrefix(String prefix) {
			if (StringUtils.isBlank(prefix)) {
				return "A prefix cannot be blank";
			}
			for (char character : prefix.toCharArray()) {
				for (char illegalCharacter : illegalCharacters) {
					if (character == illegalCharacter) {
						return getDisplayName() + " prefixes cannot contain the character '" + character + "'";
					}
				}
			}
			return null;
		}

		@Override
		PlaceholderListener registerPlaceholder(Plugin plugin, String placeholder) {
			PlaceholderListener listener = new PlaceholderAPIListener(plugin, placeholder);
			listener.registerListener();
			return listener;
		}

		@Override
		public @Nullable String parsePlaceholder(String placeholder, @Nullable OfflinePlayer player) {
			if (placeholder.indexOf('%') == -1) { // Try to add percentage signs manually
				placeholder = "%" + placeholder + "%";
			}
			String value = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
			if (value.isEmpty() || value.equalsIgnoreCase(placeholder)) {
				return null;
			}
			return value;
		}

		@Override
		public boolean supportsRelationalPlaceholders() {
			return true;
		}

		@Override
		public @Nullable String parseRelationalPlaceholder(String placeholder, Player one, Player two) {
			if (placeholder.indexOf('%') == -1) { // Try to add percentage signs manually
				placeholder = "%" + placeholder + "%";
			}
			String value = me.clip.placeholderapi.PlaceholderAPI.setRelationalPlaceholders(one, two, placeholder);
			if (value.isEmpty() || value.equalsIgnoreCase(placeholder)) {
				return null;
			}
			return value;
		}
	},
	MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI", Skript.classExists("be.maximvdw.placeholderapi.PlaceholderAPI")) {
		@Override
		public @Nullable String validatePrefix(String prefix) {
			if (StringUtils.isBlank(prefix)) {
				return "A placeholder cannot be blank";
			}
			return null;
		}

		@Override
		PlaceholderListener registerPlaceholder(Plugin plugin, String placeholder) {
			PlaceholderListener listener = new MVdWPlaceholderAPIListener(plugin, placeholder);
			listener.registerListener();
			return listener;
		}

		@Override
		public @Nullable String parsePlaceholder(String placeholder, @Nullable OfflinePlayer player) {
			if (placeholder.charAt(0) == '{' && placeholder.charAt(placeholder.length() - 1) == '}') {
				String value = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, placeholder);
				if (value.isEmpty() || value.equalsIgnoreCase(placeholder)) {
					return null;
				}
				return value;
			}
			return null;
		}

		@Override
		public boolean supportsRelationalPlaceholders() {
			return false;
		}
	};

	private static final Collection<PlaceholderPlugin> INSTALLED_PLUGINS = Arrays.stream(values())
			.filter(PlaceholderPlugin::isInstalled)
			.collect(Collectors.toSet());

	/**
	 * @return A list of all installed placeholder plugins.
	 */
	public static Collection<PlaceholderPlugin> getInstalledPlugins() {
		return INSTALLED_PLUGINS;
	}

	private final String displayName;
	private final boolean installed;

	PlaceholderPlugin(String displayName, boolean installed) {
		this.displayName = displayName;
		this.installed = installed;
	}

	/**
	 * @return A display name representing this placeholder plugin.
	 */
	public final String getDisplayName() {
		return displayName;
	}

	/**
	 * @return Whether this placeholder plugin is installed on the server.
	 */
	public final boolean isInstalled() {
		return installed;
	}

	/**
	 * @param prefix The prefix to validate.
	 * @return Null if the prefix is valid, or an error message detailing why the prefix is invalid.
	 */
	public abstract @Nullable String validatePrefix(String prefix);

	/**
	 * Registers a new placeholder with this plugin.
	 * @param plugin The plugin registering the placeholder.
	 * @param placeholder The name of the placeholder to register.
	 * @return The registered listener.
	 */
	abstract PlaceholderListener registerPlaceholder(Plugin plugin, String placeholder);

	/**
	 * @param placeholder The placeholder to obtain the value of.
	 * @param player The player to obtain the placeholder from. For some implementations, a player is not required.
	 * @return The value of the placeholder for the given player (if provided).
	 */
	public abstract @Nullable String parsePlaceholder(String placeholder, @Nullable OfflinePlayer player);

	/**
	 * Relational placeholders are placeholders where the result is based on two players.
	 * @return Whether this placeholder plugin supports the usage of relational placeholders.
	 */
	public abstract boolean supportsRelationalPlaceholders();

	public @Nullable String parseRelationalPlaceholder(String placeholder, Player one, Player two) {
		if (!supportsRelationalPlaceholders()) {
			throw new UnsupportedOperationException("The '" + getDisplayName() + "' placeholder plugin does not support relational placeholders.");
		}
		throw new RuntimeException("The '" + getDisplayName() + "' is missing an implementation for relational placeholders.");
	}

}
