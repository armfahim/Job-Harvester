package io.naztech.jobharvestar.service;

public enum RsType {
	RS_TYPE_JOB_SPEC("RS_TYPE_JOB_SPEC");

	private final String rsType;

	private RsType(String rsType) {
		this.rsType = rsType;
	}

	@Override
	public String toString() {
		return rsType;
	}

}
