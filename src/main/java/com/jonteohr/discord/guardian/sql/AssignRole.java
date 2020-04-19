package com.jonteohr.discord.guardian.sql;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class AssignRole {
	
	public void grantUserAccess(User user, Guild guild, Role role) {
		Member member = guild.getMember(user);
		
		guild.addRoleToMember(member, role).complete();
	}
}
