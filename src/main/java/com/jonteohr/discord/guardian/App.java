package com.jonteohr.discord.guardian;

import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.login.LoginException;

import org.discordbots.api.client.DiscordBotListAPI;

import com.jonteohr.discord.guardian.commands.Password;
import com.jonteohr.discord.guardian.commands.ProtectChannel;
import com.jonteohr.discord.guardian.commands.Settings;
import com.jonteohr.discord.guardian.commands.UnProtectChannel;
import com.jonteohr.discord.guardian.events.GuildLeave;
import com.jonteohr.discord.guardian.events.GuildReady;
import com.jonteohr.discord.guardian.events.OnDirect;
import com.jonteohr.discord.guardian.property.PropertyHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class App {
	public static JDA jda;
	public static DiscordBotListAPI dbl;
	
	public static String prefix = "g!";
	public static int color = 0x3FB8FE;
	public static String image = "http://guardianbot.xyz/guardian_2.png";
	
	public static Collection<Permission> permissions = new ArrayList<Permission>();
	public static Collection<Permission> channelPerms = new ArrayList<Permission>();

	private static Collection<CacheFlag> cacheFlags = new ArrayList<CacheFlag>();
	
	public static void main(String[] args) throws LoginException {
		PropertyHandler prop = new PropertyHandler();
		Collection<GatewayIntent> intents = new ArrayList<GatewayIntent>();
		intents.addAll(GatewayIntent.getIntents(GatewayIntent.DEFAULT));
		
		// Disabled ChacheFlags
		cacheFlags.add(CacheFlag.ACTIVITY);
		cacheFlags.add(CacheFlag.CLIENT_STATUS);
		cacheFlags.add(CacheFlag.EMOTE);
		cacheFlags.add(CacheFlag.VOICE_STATE);
		
		jda = JDABuilder.create(prop.loadProperty("token"), intents)
				.setStatus(OnlineStatus.ONLINE)
				.setMemberCachePolicy(MemberCachePolicy.OWNER)
				.disableCache(cacheFlags)
				.build();
		
		dbl = new DiscordBotListAPI.Builder()
				.token(prop.loadProperty("apiToken"))
				.botId("701037124996169738")
				.build();
		
		// Commands
		jda.addEventListener(new ProtectChannel());
		jda.addEventListener(new UnProtectChannel());
		jda.addEventListener(new Password());
		jda.addEventListener(new Settings());
		
		// Events
		jda.addEventListener(new GuildReady());
		jda.addEventListener(new GuildLeave());
		
		// PM
		jda.addEventListener(new OnDirect());
		
		permissions.add(Permission.MANAGE_SERVER);
		permissions.add(Permission.MANAGE_ROLES);
		permissions.add(Permission.MANAGE_CHANNEL);
		permissions.add(Permission.NICKNAME_CHANGE);
		permissions.add(Permission.VIEW_CHANNEL);
		permissions.add(Permission.MANAGE_PERMISSIONS);
		permissions.add(Permission.MESSAGE_WRITE);
		permissions.add(Permission.MESSAGE_READ);
		permissions.add(Permission.MESSAGE_HISTORY);
		permissions.add(Permission.VOICE_CONNECT);
		
		channelPerms.add(Permission.MESSAGE_READ);
		channelPerms.add(Permission.MANAGE_CHANNEL);
		channelPerms.add(Permission.MANAGE_PERMISSIONS);
	}
	
	/**
	 * Returns the Role that belongs to the bot.
	 * @param guild a {@link net.dv8tion.jda.api.entities.Guild Guild} object to look in.
	 * @return the bots owned {@link net.dv8tion.jda.api.entities.Role Role} or {@code null} if none
	 */
	public static Role getSelfRole(Guild guild) {
		if(guild.getSelfMember().getRoles().stream().filter(Role::isManaged).findFirst().orElse(null) == null) { // If the bot didn't create its own role
			if(guild.getRolesByName(guild.getSelfMember().getUser().getName(), true).size() > 0) // Get a role with name == bot name and make sure we can find something
				return guild.getRolesByName(guild.getSelfMember().getUser().getName(), true).get(0);
			
			return null; // The role doesn't exist
		}
		
		return guild.getSelfMember().getRoles().stream().filter(Role::isManaged).findFirst().orElse(null); // Return the bots' own managed role
	}
}
