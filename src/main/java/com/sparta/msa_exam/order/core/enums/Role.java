package com.sparta.msa_exam.order.core.enums;

public enum Role {
	ADMIN("ADMIN"), MANAGER("MANAGER"), MEMBER("MEMBER"), ANONYMOUS("ANONYMOUS");

	private String role;

	Role(String role) {
		this.role = role;
	}

	public String getRoleByName() {
		return role;
	}

	public boolean isManager() {
		return this == MANAGER;
	}

	public boolean isMember() {
		return this == MEMBER;
	}

	public static Role getRoleByName(String roleName) {
		return Role.valueOf(roleName); // 문자열을 Role 타입으로 변환
	}
}