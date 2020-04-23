package com.jonteohr.discord.guardian.commands;

import java.util.Random;

import com.jonteohr.discord.guardian.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Password extends ListenerAdapter {
	private String[] titles = {
			"Hello there",
			"Top O' The Morning To Ya",
			"What’s kickin’ little chicken",
			"Peek-a-boo",
			"Hiya",
			"Howdy"
	};
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "password"))
			return;
		
		Random rand = new Random();
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setTitle(titles[rand.nextInt(titles.length)] + ", " + e.getAuthor().getName() + "!");
		msg.setColor(App.color);
		msg.setThumbnail(App.image);
		msg.setDescription("I'm the Guardian.\n"
				+ "I will let you join a channel, **if** you have a password to provide me with!\n\n"
				+ "Enter your password and server like the example below and I will grant you permission to join the channel you wish:\n"
				+ "`mypassword servername`");
		
		e.getAuthor().openPrivateChannel().complete().sendMessage(msg.build()).queue();
	}
}
