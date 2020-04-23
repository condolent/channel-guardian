package com.jonteohr.discord.guardian.permission;

import com.jonteohr.discord.guardian.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

public class PermissionErr {
	public static void BotPerm(TextChannel channel) {
		EmbedBuilder msg = new EmbedBuilder();
		
		msg.setColor(0xD52D42);
		msg.setAuthor("Permission error", null, "http://guardianbot.xyz/attention-clipart-warning-triangle-2.png");
		msg.setDescription("I'm missing one or more permissions! Make sure I have the permissions listed down below, if not I will not work.");
		
		String srvPermList = "";
		for(Permission perm : App.permissions) {
			srvPermList = srvPermList + perm.getName() + "\n";
		}
		
		msg.addField("Server Permissions", srvPermList, false);
		
		channel.sendMessage(msg.build()).queue();
	}
}
