package com.jonteohr.discord.guardian.commands;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.permission.PermissionCheck;
import com.jonteohr.discord.guardian.sql.Channels;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ProtectChannel extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split(" ");
		PermissionCheck permissionCheck = new PermissionCheck();
		Channels channels = new Channels();
		
		if(!args[0].equalsIgnoreCase(App.prefix + "protect")) // Don't listen unless this command is used
			return;
		if(!permissionCheck.isAdmin(e.getMember()) || !permissionCheck.isModerator(e.getMember())) // Author is not allowed to use this
			return;
		if(args.length < 3) { // Not enough arguments
			e.getChannel().sendMessage(":x: **Incorrect usage!**\nCorrect usage: `" + App.prefix + "protect <#channel> <password>`").queue();
			return;
		}
		if(!args[1].contains("#")) { // Channel not tagged
			e.getChannel().sendMessage(":x: **Incorrect usage!**\nCorrect usage: `" + App.prefix + "protect <#channel> <password>`").queue();
			return;
		}
		
		if(!e.getGuild().getSelfMember().hasPermission(App.permissions)) {
			e.getChannel().sendMessage(":x: **Permissions are not correct!**\nMake sure the I have the requested permissions from the invite-link. Else I won't work properly!").queue();
			return;
		}
		
		TextChannel targetChannel = e.getMessage().getMentionedChannels().get(0);
		String password = args[2];
		
		if(channels.isChannelProtected(targetChannel)) { // Tagged channel is already protected
			e.getChannel().sendMessage(":x: **Channel already protected!**").queue();
			return;
		}
		
		if(password.length() > 10) {
			e.getChannel().sendMessage(":x: **Password too long!**\nPasswords can be maximum of 10 characters.").queue();
			return;
		}
		
		if(!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) {
			PermissionOverride permOverride = targetChannel.getPermissionOverride(App.getSelfRole(e.getGuild()));
			if(!permOverride.getAllowed().containsAll(App.channelPerms)) {
				String perms = "";
				for(Permission perm : App.channelPerms) {
					perms = perms + "`" + perm.getName() + "`\n";
				}
				e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
				return;
			}
		}
		
		// Create a role for the channel and remember it's ID!
		Role accessRole = e.getGuild().createRole()
				.setName(targetChannel.getName())
				.complete();
		
		if(!channels.protectChannel(targetChannel, password, accessRole)) { // Something with the Query probably went wrong
			e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
			return;
		}
		
		e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getAsMention() + " is now password protected!").queue();
		return;
	}
}
