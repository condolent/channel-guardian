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
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnProtectChannel extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		PermissionCheck permissionCheck = new PermissionCheck();
		Query sql = new Query();
		Channels channels = new Channels();
		
		if(!args[0].equalsIgnoreCase(App.prefix + "unprotect")) // Don't listen unless this command is used
			return;
		if(!permissionCheck.isAdmin(e.getMember()) || !permissionCheck.isModerator(e.getMember())) // Author is not allowed to use this
			return;
		if(args.length < 2 || !args[1].contains("#")) { // Not enough arguments or channel not tagged
			e.getChannel().sendMessage(":x: **Incorrect usage!**\nCorrect usage: `" + App.prefix + "unprotect <#channel>`").queue();
			return;
		}
		
		if(!e.getGuild().getSelfMember().hasPermission(App.permissions)) {
			e.getChannel().sendMessage(":x: **Permissions are not correct!**\nMake sure the I have the requested permissions from the invite-link. Else I won't work properly!").queue();
			return;
		}
		
		TextChannel targetChannel = e.getMessage().getMentionedChannels().get(0);
		
		if(!channels.isChannelProtected(targetChannel)) { // Tagged channel is already protected
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

			if(targetChannel.getPermissionOverride(App.getSelfRole(e.getGuild())) == null && targetChannel.getPermissionOverride(e.getGuild().getSelfMember()) == null) { // If no permissions set in channel
				e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
				return;
			}
			
			
			if(targetChannel.getPermissionOverride(e.getGuild().getSelfMember()) == null)  { // Bot role permissions set
				PermissionOverride permOverride = targetChannel.getPermissionOverride(App.getSelfRole(e.getGuild()));
				if(!permOverride.getAllowed().containsAll(App.channelPerms)) {
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			} else if(targetChannel.getPermissionOverride(App.getSelfRole(e.getGuild())) == null)  { // Bot client permissions set
				PermissionOverride permOverride = targetChannel.getPermissionOverride(e.getGuild().getSelfMember());
				if(!permOverride.getAllowed().containsAll(App.channelPerms)) {
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			}
		}
		
		if(!channels.unProtectChannel(targetChannel, accessRole)) {
			e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
			return;
		}

		e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getAsMention() + " is no longer password protected!").queue();
		return;
	}
}
