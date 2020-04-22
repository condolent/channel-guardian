package com.jonteohr.discord.guardian.sql;

import com.jonteohr.discord.guardian.App;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class AssignRole {
	
	/**
	 * Grants a {@link net.dv8tion.jda.api.entities.User User} a custom role to access the protected channel.
	 * @param user a {@link net.dv8tion.jda.api.entities.User User} to grant access to.
	 * @param guild the {@link net.dv8tion.jda.api.entities.Guild Guild} to work inside.
	 * @param role the {@link net.dv8tion.jda.api.entities.Role Role} to assign.
	 */
	public boolean grantUserAccess(User user, Guild guild, Role role) {
		Member member = guild.getMember(user);
		
		if(role.getPosition() < App.getSelfRole(guild).getPosition()) { // Make sure hierarchy is correct for the guild
			guild.addRoleToMember(member, role).complete();
			return true;
		}
		
		return false;
	}
}
