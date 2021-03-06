package com.jonteohr.discord.guardian.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.permission.PermissionCheck;
import com.jonteohr.discord.guardian.sql.Channels;
import com.jonteohr.discord.guardian.sql.Query;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnProtectChannel extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		PermissionCheck permissionCheck = new PermissionCheck();
		Query sql = new Query();
		Channels channels = new Channels();
		
		// Don't listen unless this command is used
		if(!args[0].equalsIgnoreCase(App.prefix + "unprotect"))
			return;
		// Author is not allowed to use this
		if(!permissionCheck.isAdmin(e.getMember()) || !permissionCheck.isModerator(e.getMember()))
			return;
		// Not enough arguments
		if(args.length < 2) {
			e.getChannel().sendMessage(":x: **Incorrect usage!**\nCorrect usage: `" + App.prefix + "unprotect <#channel>`").queue();
			return;
		}
		
		// Bot does not have correct server permissions
		if(!e.getGuild().getSelfMember().hasPermission(App.permissions)) {
			e.getChannel().sendMessage(":x: **Permissions are not correct!**\nMake sure the I have the requested permissions from the invite-link. Else I won't work properly!").queue();
			return;
		}
		
		// Create the channel name
		String name = "";
		for(int i = 1; i < args.length; i++) {
			if(i == args.length - 1) {
				name = name + args[i];
				break;
			}
			name = name + args[i] + " ";
		}
		
		// No channel mentioned and not a voice channel
		if(e.getMessage().getMentionedChannels().size() < 1 && e.getGuild().getVoiceChannelsByName(name, true).size() < 1) {
			e.getChannel().sendMessage(":x: No text channel was mentioned, and could not find a voice channel named " + name).queue(); //name).queue();
			return;
		}
		
		// It's a text channel!
		if(e.getMessage().getMentionedChannels().size() > 0) {
			TextChannel targetChannel = e.getMessage().getMentionedChannels().get(0);
			
			// Tagged channel is already protected
			if(!channels.isChannelProtected(targetChannel)) {
				e.getChannel().sendMessage(":x: **Channel is not protected!**").queue();
				return;
			}
			
			List<String> roleList = new ArrayList<String>();
			ResultSet res = sql.queryGet("SELECT role FROM channels WHERE channel='" + targetChannel.getId() + "' AND guild_id='" + e.getGuild().getId() + "';");
			try {
				while(res.next()) {
					roleList.add(res.getString(1));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				return;
			}
			
			Role accessRole = e.getGuild().getRoleById(roleList.get(0));
			
			if(!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) { // If bot doesn't have admin permissions, we gotta dig deeper.
				String perms = "";
				for(Permission perm : App.channelPerms) {
					perms = perms + "`" + perm.getName() + "`\n";
				}

				if(!e.getGuild().getSelfMember().hasPermission(targetChannel, App.channelPerms)) { // Bot does not have the channel permissions
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			}
			
			if(!channels.unProtectChannel(targetChannel, accessRole)) {
				e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
				return;
			}

			e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getAsMention() + " is no longer password protected!").queue();
			return;
		}
		
		// it's a voice channel
		else if(e.getGuild().getVoiceChannelsByName(name, true).size() > 0) {
			VoiceChannel targetChannel = e.getGuild().getVoiceChannelsByName(name, true).get(0);
			
			// Tagged channel is already protected
			if(!channels.isChannelProtected(targetChannel)) {
				e.getChannel().sendMessage(":x: **Channel is not protected!**").queue();
				return;
			}
			
			List<String> roleList = new ArrayList<String>();
			ResultSet res = sql.queryGet("SELECT role FROM channels WHERE channel='" + targetChannel.getId() + "' AND guild_id='" + e.getGuild().getId() + "';");
			try {
				while(res.next()) {
					roleList.add(res.getString(1));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				return;
			}
			
			Role accessRole = e.getGuild().getRoleById(roleList.get(0));
			
			if(!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) { // If bot doesn't have admin permissions, we gotta dig deeper.
				String perms = "";
				for(Permission perm : App.channelPerms) {
					perms = perms + "`" + perm.getName() + "`\n";
				}

				if(!e.getGuild().getSelfMember().hasPermission(targetChannel, App.channelPerms)) { // Bot does not have the channel permissions
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			}
			
			if(!channels.unProtectChannel(targetChannel, accessRole)) {
				e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
				return;
			}

			e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getName() + " is no longer password protected!").queue();
			return;
		}
	}
}
