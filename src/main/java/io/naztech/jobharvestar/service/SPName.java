package io.naztech.jobharvestar.service;

/**
 * @author md.kamruzzaman
 */
public enum SPName {

	ACT_job_spec("ACT_job_spec"),
	ACT_run_event_key("ACT_run_event_key"),
	IMP_job_spec("IMP_job_spec"),
	CAL_avg_parsedjob_runtime("CAL_avg_parsedjob_runtime");

	private final String spName;

	private SPName(String spName) {
		this.spName = spName;
	}

	@Override
	public String toString() {
		return spName;
	}

}
