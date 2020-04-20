package com.jonteohr.discord.guardian.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.sql.Query;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReady extends ListenerAdapter {
	public void onGuildReady(GuildReadyEvent e) {
		joinedGuild(e.getGuild(), e.getJDA());
	}
	
	public void onGuildJoin(GuildJoinEvent e) {
		joinedGuild(e.getGuild(), e.getJDA());
	}
	
	private void joinedGuild(Guild guild, JDA jda) {
		Query sql = new Query();
		
		ResultSet res = sql.queryGet("SELECT * FROM guilds;");
		List<String> guildIds = new ArrayList<String>();
		try {
			while(res.next()) {
				guildIds.add(res.getString(1));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(!guildIds.contains(guild.getId())) {
			sql.queryExec("INSERT INTO guilds(`guild_id`) VALUES('" + guild.getId() + "');");
			newGuild(guild);
		}
		
		jda.getPresence().setActivity(Activity.watching("g!password"));
	}
	
	private void newGuild(Guild guild) {
		TextChannel channel = guild.getDefaultChannel();
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setTitle("Hello, " + guild.getName() + "!");
		msg.setColor(App.color);
		msg.setThumbnail(App.image);
		
		msg.setDescription("I'm very excited to join you!\n"
				+ "My setup is really easy. There is none.\n\n"
				+ "To password protect a channel, simply use the command `" + App.prefix + "protect <#channel> <password>`.\n"
				+ "Note that you can only set one password per channel. Also make sure you actually tag the channel with the `#` character.\n\n"
				+ "If you wish to un-protect a channel, then simply do `" + App.prefix + "unprotect <#channel>`.\n\n"
				+ "Only members with the administrator permission, or the manage channels permission can use the above commands.\n\n"
				+ "For the regular users it's very easy to use. All they have to do is type `" + App.prefix + "password` anywhere in the server and I will send them a direct message containing more information.\n"
				+ "But they will need the password to gain access to a protected channel.\n\n"
				+ "That was all for me now! Good luck!");
		
		channel.sendMessage(msg.build()).queue();
	}
}
