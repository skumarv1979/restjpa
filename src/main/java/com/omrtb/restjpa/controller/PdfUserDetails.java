package com.omrtb.restjpa.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.omrtb.restjpa.entity.model.Role;
import com.omrtb.restjpa.entity.model.User;

public class PdfUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PdfUserDetails(User user) {
        this.user = user;
    }
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		List<GrantedAuthority> lst = new ArrayList<GrantedAuthority>();
		if(roles==null || roles.isEmpty()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
			lst.add(grantedAuthority);
		}
		else {
			Iterator<Role> itr = roles.iterator();
			while(itr.hasNext()) {
				Role role = itr.next();
				GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+role.getName());
				lst.add(grantedAuthority);
			}
		}
		return lst;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
    public User getUserDetails() {
        return user;
    }

}
