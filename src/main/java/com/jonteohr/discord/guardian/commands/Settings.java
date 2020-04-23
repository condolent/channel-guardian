package com.jonteohr.discord.guardian.commands;

import com.jonteohr.discord.guardian.App;
import com.jonteohr.discord.guardian.permission.PermissionCheck;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Settings extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\\\s+");
		PermissionCheck perm = new PermissionCheck();
		
		if(!args[0].equalsIgnoreCase(App.prefix + "settings"))
			return;
		if(!perm.isAdmin(e.getMember()))
			return;
		if(args.length < 2) {
			e.getChannel().sendMessage(":x: No setting specified. Use `" + App.prefix + "settings list` to get a list of settings.").queue();
			return;
		}
		
		if(args[1].equalsIgnoreCase("list")) {
			EmbedBuilder msg = new EmbedBuilder();
			msg.setColor(App.color);
			msg.setAuthor("Available settings");
			msg.addField("Command", "`" + App.prefix + "settings name`", true);
			msg.addField("Description", "Toggle short-name for the bot.", true);
			
			e.getChannel().sendMessage(msg.build()).queue();
			return;
		} else if(args[1].equalsIgnoreCase("name")) {
			if(e.getGuild().getSelfMember().getNickname() == null)
				e.getGuild().getSelfMember().modifyNickname("Guardian").complete();
			else
				e.getGuild().getSelfMember().modifyNickname("").complete();
			
			EmbedBuilder msg = new EmbedBuilder();
			msg.setColor(App.color);
			msg.setAuthor("Setting saved");
			msg.setDescription("Toggled shortname.");
			
			e.getChannel().sendMessage(msg.build()).queue();
			return;
		} else { // If none of the applicable settings used
			e.getChannel().sendMessage(":x: Setting not found. Use `" + App.prefix + "settings list` to get a list of settings.").queue();
			return;
		}
	}
}
