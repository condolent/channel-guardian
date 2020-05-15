package com.jonteohr.discord.guardian.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jonteohr.discord.guardian.permission.PermissionErr;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Channels {
	
	/**
	 * Check if a TextChannel is protected or not.
	 * @param channel the {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to check
	 * @return {@code true} if yes
	 * @see #isChannelProtected(VoiceChannel)
	 */
	public boolean isChannelProtected(TextChannel channel) {
		Query sql = new Query();
		String chId = channel.getId();
		
		List<String> result = new ArrayList<String>();
		ResultSet res = sql.queryGet("SELECT channel FROM channels WHERE channel='" + chId + "';");
		try {
			while(res.next()) {
				result.add(res.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		if(result.contains(chId))
			return true;
		
		return false;
	}
	
	/**
	 * Check if a VoiceChannel is protected or not.
	 * @param channel the {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} to check
	 * @return {@code true} if yes
	 * @see #isChannelProtected(TextChannel)
	 */
	public boolean isChannelProtected(VoiceChannel channel) {
		Query sql = new Query();
		String chId = channel.getId();
		
		List<String> result = new ArrayList<String>();
		ResultSet res = sql.queryGet("SELECT channel FROM channels WHERE channel='" + chId + "';");
		try {
			while(res.next()) {
				result.add(res.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		if(result.contains(chId))
			return true;
		
		return false;
	}
	
	/**
	 * Protects a VoiceChannel with the given password and changes channel permissions to hide it.
	 * @param channel a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} to password protect
	 * @param password a {@link java.lang.String String} password
	 * @param role the {@link net.dv8tion.jda.api.entities.Role Role} to grant access to
	 * @return {@code true if success}
	 * @see #protectChannel(TextChannel, String, Role)
	 */
	public boolean protectChannel(VoiceChannel voiceChannel, String password, Role role) {
		String chId = voiceChannel.getId();
		String roleId = role.getId();
		Guild guild = voiceChannel.getGuild();
		Query sql = new Query();
		
		if(!sql.queryExec("INSERT INTO channels(`guild_id`,`channel`,`password`,`role`) VALUES('" + guild.getId() + "','" + chId + "','" + password + "','" + roleId + "');"))
			return false;
		
		// Grants access to the channel for the new role
		voiceChannel.putPermissionOverride(role)
		.setAllow(Permission.VIEW_CHANNEL)
		.queue(
			null,
			failure -> PermissionErr.BotPerm(voiceChannel.getGuild().getDefaultChannel())
		);
		
		// Hides the channel for @everyone
		PermissionOverride permOverride = voiceChannel.getPermissionOverride(voiceChannel.getGuild().getPublicRole());
		if(permOverride == null)
			voiceChannel.putPermissionOverride(voiceChannel.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(voiceChannel.getGuild().getDefaultChannel()));
		else
			permOverride.getManager().deny(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(voiceChannel.getGuild().getDefaultChannel()));
		
		return true;
	}
	
	/**
	 * Protects a TextChannel with the given password and changes channel permissions to hide it.
	 * @param channel a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to password protect
	 * @param password a {@link java.lang.String String} password
	 * @param role the {@link net.dv8tion.jda.api.entities.Role Role} to grant access to
	 * @return {@code true if success}
	 * @see #protectChannel(VoiceChannel, String, Role)
	 */
	public boolean protectChannel(TextChannel channel, String password, Role role) {
		String chId = channel.getId();
		String roleId = role.getId();
		Guild guild = channel.getGuild();
		Query sql = new Query();
		
		if(!sql.queryExec("INSERT INTO channels(`guild_id`,`channel`,`password`,`role`) VALUES('" + guild.getId() + "','" + chId + "','" + password + "','" + roleId + "');"))
			return false;
		
		// Grants access to the channel for the new role
		channel.putPermissionOverride(role)
		.setAllow(Permission.VIEW_CHANNEL)
		.queue(
			null,
			failure -> PermissionErr.BotPerm(channel.getGuild().getDefaultChannel())
		);
		
		// Hides the channel for @everyone
		PermissionOverride permOverride = channel.getPermissionOverride(channel.getGuild().getPublicRole());
		if(permOverride == null)
			channel.putPermissionOverride(channel.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(channel.getGuild().getDefaultChannel()));
		else
			permOverride.getManager().deny(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(channel.getGuild().getDefaultChannel()));
		
		return true;
	}
	
	/**
	 * Removes the protection from a TextChannel.
	 * @param channel the {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} to unlock
	 * @param role the {@link net.dv8tion.jda.api.entities.Role Role} that is connected to it
	 * @return {@code true} if success
	 * @apiNote This will also remove the access role, and reset the permission for the channel.
	 */
	public boolean unProtectChannel(TextChannel channel, Role role) {
		String chId = channel.getId();
		String roleId = role.getId();
		Guild guild = channel.getGuild();
		Query sql = new Query();
		
		if(!sql.queryExec("DELETE FROM channels WHERE guild_id='" + guild.getId() + "' AND channel='" + chId + "' AND role='" + roleId + "';"))
			return false;
		
		// Removes the access role
		role.delete().complete();
		
		// Un-hides the channel for @everyone
		PermissionOverride permOverride = channel.getPermissionOverride(channel.getGuild().getPublicRole());
		if(permOverride == null)
			channel.putPermissionOverride(channel.getGuild().getPublicRole()).clear(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(channel.getGuild().getDefaultChannel()));
		else
			permOverride.getManager().clear(Permission.VIEW_CHANNEL).queue(null, error -> PermissionErr.BotPerm(channel.getGuild().getDefaultChannel()));
		
		return true;
	}
}
