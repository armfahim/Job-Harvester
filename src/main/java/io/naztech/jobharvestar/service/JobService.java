package io.naztech.jobharvestar.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nazdaqTechnologies.core.service.AbstractService;
import com.nazdaqTechnologies.jdbc.JdbcResult;
import com.nazdaqTechnologies.jdbc.JdbcService;
import com.nazdaqTechnologies.jdbc.StoredProcedure.JdbcStoredProcedure;
import com.nazdaqTechnologies.jdbc.util.JdbcUtils;

import io.naztech.talent.model.Job;

/**
 * @author md.kamruzzaman
 */
public class JobService extends AbstractService<Job> {
	private static Logger log = LoggerFactory.getLogger(JobService.class);

	public Job insert(Job job) {
		try {
			return action(job, ActionType.NEW.toString());
		} catch (Exception e) {
			log.error("Exception inserting job {}", e);
		}
		return job;
	}

	public Job update(Job job) {
		try {
			return action(job, ActionType.UPDATE.toString());
		} catch (Exception e) {
			log.error("Exception updating job {}", e);
		}
		return job;
	}

	public List<Job> select(Job job) {

		List<Job> jobList = null;
		try {
			Map<String, Object> spArgsMap = JdbcService.createSqlMap(job, Job.getSql2BeanMap());
			JdbcResult jdbcResult = new JdbcResult();
			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure(SPName.ACT_job_spec.toString());
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);

			jdbcResult = getJdbcService().executeSP(ActionType.SELECT.toString(), null, SPName.ACT_job_spec.toString(), spArgsMap, jdbcResult);
			jobList = JdbcUtils.mapRows(Job.class, Job.getRs2BeanMap(), jdbcResult.getRsTypeMap(RsType.RS_TYPE_JOB_SPEC.toString()));
		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
		}
		return jobList;
	}

	public Job delete(Job job) {
		try {
			return action(job, ActionType.DELETE.toString());
		} catch (Exception e) {
			log.error("Exception inserting job {}", e);
		}
		return job;
	}

	public List<Job> insert(List<Job> jobList) {
		if (null == jobList) {
			return Collections.emptyList();
		}
		jobList.parallelStream().forEach(job -> {
			// insert Job2 here
			try {
				insert(job);
			} catch (Exception e) {
				log.error("Exception inserting Job2 {}", e);
			}

		});
		return jobList;
	}

	private Job action(Job job, String action) throws Exception {

		try {
			Map<String, Object> spArgsMap = JdbcService.createSqlMap(job, Job.getSql2BeanMap());
			JdbcResult jdbcResult = new JdbcResult();

			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure(SPName.ACT_job_spec.toString());

			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);
			jdbcResult = getJdbcService().executeSP(action, null, SPName.ACT_job_spec.toString(), spArgsMap, jdbcResult);
		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}
		return job;
	}
	
	
	public JdbcResult action(Map<String, Object> arg, String action) throws Exception {
		JdbcResult jdbcResult = null;
		try {
			jdbcResult = new JdbcResult();
			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure(SPName.IMP_job_spec.toString());
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);

			jdbcResult = getJdbcService().executeSP(action, null, SPName.IMP_job_spec.toString(), arg, jdbcResult);

		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}
		return jdbcResult;
	}
	
	public void actionCalAvg(Map<String, Object> arg, String action) throws Exception {
		JdbcResult jdbcResult = null;
		try {
			jdbcResult = new JdbcResult();
			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure(SPName.CAL_avg_parsedjob_runtime.toString());
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);

			getJdbcService().executeSP(action, null, SPName.CAL_avg_parsedjob_runtime.toString(), arg, jdbcResult);

		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}
	}
	
	public int getRunEventKey(String orgShortName) throws Exception{
		Integer eventKey = null;
		try {
			Map<String, Object> spArgsMap = new HashMap<>();
			JdbcResult jdbcResult = new JdbcResult();
			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService().getJdbcStoredProcedure(SPName.ACT_run_event_key.toString());
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);
	
			jdbcResult = getJdbcService().executeSP(null, null, SPName.ACT_run_event_key.toString(), spArgsMap, jdbcResult);
			Map<String, Object> outputMap = jdbcResult.getOutputParamValueMap();
			
			if(outputMap.containsKey("@id_run_event_key")) {
				eventKey = (Integer)outputMap.get("@id_run_event_key");
			}
			else {
				throw new Exception("Error generating run event key for " + orgShortName);
			}
		}
		catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}
		return eventKey;
	}

}
