package com.jonteohr.discord.guardian.commands;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.permission.PermissionCheck;
import com.jonteohr.discord.guardian.sql.Channels;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ProtectChannel extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		PermissionCheck permissionCheck = new PermissionCheck();
		Channels channels = new Channels();
		
		// Don't listen unless this command is used
		if(!args[0].equalsIgnoreCase(App.prefix + "protect"))
			return;
		// Author is not allowed to use this
		if(!permissionCheck.isAdmin(e.getMember()) || !permissionCheck.isModerator(e.getMember()))
			return;
		// Not enough arguments
		if(args.length < 3) {
			e.getChannel().sendMessage(":x: **Incorrect usage!**\nCorrect usage: `" + App.prefix + "protect <#channel> <password>`").queue();
			return;
		}
		
		// Bot does not have correct server permissions
		if(!e.getGuild().getSelfMember().hasPermission(App.permissions)) {
			String perms = "";
			for(Permission perm : App.permissions) {
				perms = perms + "`" + perm.getName() + "`\n";
			}
			e.getChannel().sendMessage(":x: **Permissions are not correct!**\nMake sure the I have the following server permissions:\n" + perms).queue();
			return;
		}
		
		String password = args[1];
		
		// Create the channel name
		String name = "";
		for(int i = 2; i < args.length; i++) {
			name = name + args[i];
		}
		
		// No channel mentioned and not a voice channel
		if(e.getMessage().getMentionedChannels().size() < 1 && e.getGuild().getVoiceChannelsByName(name, true).size() < 1) {
			e.getChannel().sendMessage(":x: No text channel was mentioned, and could not find a voice channel named ").queue(); //name).queue();
			return;
		}
		
		// Password is too long
		if(password.length() > 10) {
			e.getChannel().sendMessage(":x: **Password too long!**\nPasswords can be maximum of 10 characters.").queue();
			return;
		}
		
		// it's a text channel!
		if(e.getMessage().getMentionedChannels().size() > 0) {
			TextChannel targetChannel = e.getMessage().getMentionedChannels().get(0);
			
			// Tagged channel is already protected
			if(channels.isChannelProtected(targetChannel)) {
				e.getChannel().sendMessage(":x: **Channel already protected!**").queue();
				return;
			}
			
			// If bot doesn't have admin permissions, we gotta dig deeper.
			if(!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) {
				String perms = "";
				for(Permission perm : App.channelPerms) {
					perms = perms + "`" + perm.getName() + "`\n";
				}
				
				// Bot does not have the channel permissions
				if(!e.getGuild().getSelfMember().hasPermission(targetChannel, App.channelPerms)) {
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			}
			
			// Create a role for the channel and remember it's ID!
			Role accessRole = e.getGuild().createRole()
					.setName(targetChannel.getName())
					.complete();
			
			// Something with the Query probably went wrong
			if(!channels.protectChannel(targetChannel, password, accessRole)) {
				e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
				return;
			}
			
			e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getAsMention() + " is now password protected!").queue();
			return;
		}
		
		// it's a voice channel!
		else if(e.getGuild().getVoiceChannelsByName(name, true).size() > 0) {
			VoiceChannel targetChannel = e.getGuild().getVoiceChannelsByName(name, true).get(0);
			
			// Tagged channel is already protected
			if(channels.isChannelProtected(targetChannel)) {
				e.getChannel().sendMessage(":x: **Channel already protected!**").queue();
				return;
			}
			
			// If bot doesn't have admin permissions, we gotta dig deeper.
			if(!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) {
				String perms = "";
				for(Permission perm : App.channelPerms) {
					perms = perms + "`" + perm.getName() + "`\n";
				}
				
				// Bot does not have the channel permissions
				if(!e.getGuild().getSelfMember().hasPermission(targetChannel, App.channelPerms)) {
					e.getChannel().sendMessage(":x: **Channel permissions insufficient!**\nI need these permissions in the channel:\n" + perms).queue();
					return;
				}
			}
			
			// Create a role for the channel and remember it's ID!
			Role accessRole = e.getGuild().createRole()
					.setName(targetChannel.getName() + " (v)")
					.complete();
			
			// Something with the Query probably went wrong
			if(!channels.protectChannel(targetChannel, password, accessRole)) {
				e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
				return;
			}
			
			e.getChannel().sendMessage(":white_check_mark: Channel " + targetChannel.getName() + " is now password protected!").queue();
			return;
		}
	}
}
