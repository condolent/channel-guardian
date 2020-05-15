package com.jonteohr.discord.guardian.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jonteohr.discord.guardian.sql.AssignRole;
import com.jonteohr.discord.guardian.sql.Query;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnDirect extends ListenerAdapter {
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		Query sql = new Query();
		AssignRole assignRole = new AssignRole();
		String password = args[0];
		
		if(args.length < 2)
			return;
		if(e.getAuthor().isBot())
			return;
		
		List<String> guilds = new ArrayList<String>();
		List<String> channels = new ArrayList<String>();
		List<String> passwords = new ArrayList<String>();
		List<String> roles = new ArrayList<String>();
		ResultSet res = sql.queryGet("SELECT * FROM channels;");
		try {
			while(res.next()) {
				guilds.add(res.getString(1));
				channels.add(res.getString(2));
				passwords.add(res.getString(3));
				roles.add(res.getString(4));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String guildName = "";
		for(int i = 1; i < args.length; i++) {
			if(i == (args.length - 1)) {
				guildName = guildName + args[i];
			} else {
				guildName = guildName + args[i] + " ";
			}
		}
		
		List<String> guildNames = new ArrayList<String>();
		for(int i = 0; i < guilds.size(); i++)
			guildNames.add(e.getJDA().getGuildById(guilds.get(i)).getName().toLowerCase());
		
		if(!passwords.contains(password) || !guildNames.contains(guildName.toLowerCase())) {
			e.getChannel().sendMessage("The password or server name you entered is incorrect.").queue();
			return;
		}
		
		for(int i = 0; i < guilds.size(); i++) {
			Guild guild = e.getJDA().getGuildById(guilds.get(i));
			TextChannel channel = guild.getTextChannelById(channels.get(i));
			VoiceChannel voiceChannel = guild.getVoiceChannelById(channels.get(i));
			String pw = passwords.get(i);
			Role role = guild.getRoleById(roles.get(i));
			
			if(guild.getName().equalsIgnoreCase(guildName)) {
				if(!guild.getTextChannels().contains(channel) && !guild.getVoiceChannels().contains(voiceChannel)) // if the channel is not in this server
					continue;
				if(!pw.equalsIgnoreCase(password)) // if the wrong pw for the channel/server was given
					continue;
				
				if(!assignRole.grantUserAccess(e.getAuthor(), guild, role)) {
					e.getChannel().sendMessage(guild.getName() + " has an error with the roles. Please contact an server administrator to inform them about this!").queue();
					break;
				}
				e.getChannel().sendMessage("Correct! I have given you a role to access the channel " + (channel != null ? channel.getName() : voiceChannel.getName()) + " inside server " + guild.getName()).queue();
				break;
			}
		}
		
		return;
	}
}
