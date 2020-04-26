package com.jonteohr.discord.guardian.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.sql.Query;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {
	public void onGuildLeave(GuildLeaveEvent e) {
		Query sql = new Query();
		
		ResultSet res = sql.queryGet("SELECT * FROM guilds;");
		List<String> guildIds = new ArrayList<String>();
		try {
			while(res.next()) {
				guildIds.add(res.getString(1));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
		
		if(guildIds.contains(e.getGuild().getId())) { // We don't want to track inactive guilds
			sql.queryExec("DELETE FROM guilds WHERE guild_id='" + e.getGuild().getId() + "';");
			sql.queryExec("DELETE FROM channels WHERE guild_id='" + e.getGuild().getId() + "';");
			System.out.println("Removed..");
		}
		
		App.dbl.setStats(App.jda.getGuilds().size()); // Send stats to Top.GG
	}
}
