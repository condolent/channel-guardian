package com.jonteohr.discord.guardian.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Channels {
	
	/**
	 * 
	 * @param channel
	 * @return
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
	 * Protects a channel with the given password and changes channel permissions to hide it.
	 * @param channel
	 * @param password
	 * @param role
	 * @return
	 */
	public boolean protectChannel(TextChannel channel, String password, Role role) {
		String chId = channel.getId();
		String roleId = role.getId();
		Guild guild = channel.getGuild();
		Query sql = new Query();
		
		if(!sql.queryExec("INSERT INTO channels(`guild_id`,`channel`,`password`,`role`) VALUES('" + guild.getId() + "','" + chId + "','" + password + "','" + roleId + "');"))
			return false;
		
		// Hides the channel for @everyone
		PermissionOverride permOverride = channel.getPermissionOverride(channel.getGuild().getPublicRole());
		permOverride.getManager().deny(Permission.VIEW_CHANNEL).queue();
		
		// Grants access to the channel for the new role
		channel.createPermissionOverride(role)
		.setAllow(Permission.VIEW_CHANNEL)
		.queue();
		
		return true;
	}
	
	/**
	 * 
	 * @param channel
	 * @param password
	 * @param role
	 * @return
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
		permOverride.getManager().clear(Permission.VIEW_CHANNEL).queue();
		
		return true;
	}
}
