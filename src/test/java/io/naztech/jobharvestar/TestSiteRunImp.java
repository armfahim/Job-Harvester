package io.naztech.jobharvestar;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.nazdaqTechnologies.core.service.AbstractService;
import com.nazdaqTechnologies.jdbc.JdbcResult;
import com.nazdaqTechnologies.jdbc.StoredProcedure.JdbcStoredProcedure;

import io.naztech.jobharvestar.service.ActionType;
import io.naztech.jobharvestar.service.SPName;
import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSiteRunImp extends AbstractService<Job>{

	@Test
	public void testRunSiteImp() {
		try {
			runSiteImp(1122L, 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void runSiteImp(Long requestKey, int exitStatus) throws Exception {
		Map<String, Object> arg = new HashMap<>();
		arg.put("@id_request_key", requestKey);
		arg.put("@id_exit_status_code", exitStatus);

		action(arg, ActionType.EMPTY.toString());
	}
	
	public JdbcResult action(Map<String, Object> arg, String action) throws Exception {
		
		JdbcResult jdbcResult = new JdbcResult();
		System.out.println("=======================================");
		System.out.println(getJdbcService().getDataSource().getConnection("wscraper_dbo", "wscraper_dbo123"));
		System.out.println("=======================================");
		
		try {
			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure("IMP_job_spec");
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);

			jdbcResult = getJdbcService().executeSP(action, null, "IMP_job_spec", arg, jdbcResult);
		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}
		return jdbcResult;
	}

}
