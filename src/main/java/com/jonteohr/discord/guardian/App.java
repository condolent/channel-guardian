package com.jonteohr.discord.guardian;

import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.login.LoginException;

import com.jonteohr.discord.guardian.commands.Password;
import com.jonteohr.discord.guardian.commands.ProtectChannel;
import com.jonteohr.discord.guardian.commands.UnProtectChannel;
import com.jonteohr.discord.guardian.events.GuildLeave;
import com.jonteohr.discord.guardian.events.GuildReady;
import com.jonteohr.discord.guardian.events.OnDirect;
import com.jonteohr.discord.guardian.property.PropertyHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App {
	public static JDA jda;
	
	public static String prefix = "g!";
	public static int color = 0x3FB8FE;
	public static String image = "https://netcube.xyz/guardian.png";
	
	public static void main(String[] args) throws LoginException {
		PropertyHandler prop = new PropertyHandler();
		Collection<GatewayIntent> intents = new ArrayList<GatewayIntent>();
		intents.addAll(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
		
		jda = JDABuilder.create(prop.loadProperty("token"), intents)
				.setStatus(OnlineStatus.ONLINE)
				.build();
		
		// Commands
		jda.addEventListener(new ProtectChannel());
		jda.addEventListener(new UnProtectChannel());
		jda.addEventListener(new Password());
		
		// Events
		jda.addEventListener(new GuildReady());
		jda.addEventListener(new GuildLeave());
		
		// PM
		jda.addEventListener(new OnDirect());
		
	}
}
