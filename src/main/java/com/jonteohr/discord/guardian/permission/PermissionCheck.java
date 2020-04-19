package com.jonteohr.discord.guardian.permission;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class PermissionCheck {
	
	/**
	 * If the given member has the ADMINISTRATOR {@link net.dv8tion.jda.api.Permission Permission} on the guild.
	 * @param member a {@link net.dv8tion.jda.api.entities.Member Member} object.
	 * @return {@code true} if yes
	 * @see #isModerator(Member)
	 */
	public boolean isAdmin(Member member) {
		if(member.hasPermission(Permission.ADMINISTRATOR))
			return true;
		return false;
	}
	
	/**
	 * If the given {@link net.dv8tion.jda.api.entities.Member Member} has the MANAGE_CHANNEL {@link net.dv8tion.jda.api.Permission Permission} on the current guild.
	 * @param member a {@link net.dv8tion.jda.api.entities.Member Member} object of which member to look for.
	 * @return {@code true} if yes
	 * @see #isAdmin(Member)
	 */
	public boolean isModerator(Member member) {
		if(member.hasPermission(Permission.MANAGE_CHANNEL))
			return true;
		return false;
	}
}
