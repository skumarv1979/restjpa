package com.omrtb.restjpa.utils;

import java.util.HashMap;
import java.util.Map;

import com.omrtb.restjpa.entity.model.Role;
import com.omrtb.restjpa.repository.RoleRepository;

public class RoleSingleton {

	private static RoleSingleton instance = null;
	
	private static final Map<String, Role> map = new HashMap<String, Role>();
	
	private RoleSingleton() {
	}
	
    public static RoleSingleton getInstance(RoleRepository roleRepository) { 
        if (instance == null) {
        	synchronized (RoleSingleton.class) {
        		if(instance==null) {
        			instance = new RoleSingleton();
        			Iterable<Role> rolesItr = roleRepository.findAll();
        			if(rolesItr!=null) {
	        			for (Role role : rolesItr) {
	        				map.put(role.getName(), role);
						}
        			}
        		}
        	}
        }
        return instance;
    }
    
    public Role getRole(String name) {
		return map.get(name);
	}
}
