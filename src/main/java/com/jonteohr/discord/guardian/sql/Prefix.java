package com.jonteohr.discord.guardian.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.api.entities.Guild;

public class Prefix {
	
	/**
	 * Retrieves the current set prefix for the specified guild.
	 * @param guild a {@link net.dv8tion.jda.api.entities.Guild Guild} object to look for.
	 * @return {@link java.lang.String String} prefix
	 * @see #setPrefix(Guild, String)
	 */
	public String getPrefix(Guild guild) {
		String guildId = guild.getId();
		Query sql = new Query();
		String prefix = null;
		
		try {
			ResultSet res = sql.queryGet("SELECT prefix FROM guilds WHERE guild_id='" + guildId + "';");
			while(res.next()) {
				prefix = res.getString(1);
			}
		} catch (SQLException e) {
			System.out.println("Guild " + guild.getName() + " prefix error: " + e.getMessage());
		}
		
		return prefix;
	}
	
	/**
	 * Sets a new prefix for the specified guild.
	 * @param guild a {@link net.dv8tion.jda.api.entities.Guild Guild} object to apply setting to.
	 * @param prefix a {@link java.lang.String String} prefix to set.
	 * @return {@code true} if success
	 * @see #getPrefix(Guild)
	 */
	public boolean setPrefix(Guild guild, String prefix) {
		String guildId = guild.getId();
		Query sql = new Query();
		
		if(sql.queryExec("UPDATE guilds SET prefix='" + prefix + "' WHERE guild_id='" + guildId + "';"))
			return true;
		
		return false;
	}
}
