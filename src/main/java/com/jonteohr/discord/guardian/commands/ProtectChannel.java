package com.jonteohr.discord.guardian.commands;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.permission.PermissionCheck;
import com.jonteohr.discord.guardian.sql.Channels;

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
		
		TextChannel target = e.getMessage().getMentionedChannels().get(0);
		String password = args[2];
		
		if(channels.isChannelProtected(target)) { // Tagged channel is already protected
			e.getChannel().sendMessage(":x: **Channel already protected!**").queue();
			return;
		}
		
		if(password.length() > 10) {
			e.getChannel().sendMessage(":x: **Password too long!**\nPasswords can be maximum of 10 characters.").queue();
			return;
		}
		
		Role accessRole = e.getGuild().createRole()
				.setName(target.getName())
				.complete();
		
		if(!channels.protectChannel(target, password, accessRole)) {
			e.getChannel().sendMessage(":x: **Something went wrong.**").queue();
			return;
		}
		
		e.getChannel().sendMessage(":white_check_mark: Channel " + target.getAsMention() + " is now password protected!").queue();
		return;
	}
}
