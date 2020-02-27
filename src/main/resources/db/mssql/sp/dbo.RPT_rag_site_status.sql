USE [WSCRAPER_PROD]
GO

/****** Object:  StoredProcedure [dbo].[RPT_rag_site_status]    Script Date: 7/25/2019 7:21:48 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

DROP PROCEDURE IF EXISTS dbo.RPT_rag_site_status;
GO

CREATE PROC [dbo].[RPT_rag_site_status]
  @tx_action_name		varchar(32)				= NULL	OUTPUT	

, @top_count			int						= 1000
, @tx_site_state		varchar(32)				= NULL
, @tx_job_status 		varchar(64)				= NULL
, @tx_org_short_name	varchar(32)				= NULL

AS
BEGIN
	-- GLOBAL VARS --	

	IF(@tx_action_name = 'RAG_SITE_STATUS_SUCCESSFUL')
	BEGIN
		SELECT tx_organization	= ORG.tx_org_name
		, tx_wave						= ORG.tx_wave
		, tx_site_state					= SITE.tx_site_state
		, ct_job_expected				= SITE.id_job_count_expected
		, ct_job_parsed					= SITE.id_job_count_parsed
		, avg_parsed_job				= ISNULL(ct_avg_job_parsed, 0)
		, tx_parsed_diff				= ((SITE.id_job_count_parsed - ISNULL(CAST(ct_avg_job_parsed AS FLOAT), 0)) * 100) / SITE.id_job_count_parsed	
		, dtt_start						= SITE.dtt_last_run_start
		, dtt_end						= SITE.dtt_last_run_end
		, runtime						= SITE.id_runtime / 60.0
		, avg_runtime					= ISNULL(CAST(tm_avg_run_second AS FLOAT), 0) / 60.0
		, tx_runtime_diff				= (((SITE.id_runtime / 60.0) - (ISNULL(CAST(tm_avg_run_second AS FLOAT), 0) / 60.0) ) * 100) / (SITE.id_runtime / 60.0)
		, tx_org_job_site_url			= SITE.tx_org_job_site_url
		INTO #T_TMP_RAG FROM T_ORG ORG
		JOIN T_ORG_JOB_SITE SITE ON ORG.tx_org_short_name = SITE.tx_org_short_name
		WHERE		SITE.dtt_last_run_start 	IS NOT NULL
		AND			SITE.id_job_count_expected 	> 0 
		AND			SITE.id_job_count_parsed 	> 0
		AND			SITE.id_runtime				> 0
		AND			SITE.tx_site_state 			= 'SUCCESSFUL'
		ORDER BY	SITE.dtt_last_run_start DESC

		SELECT 
		  Organization 		= tx_organization
		, RAG 				= CASE	WHEN CAST(tx_parsed_diff AS FLOAT) > 10 OR CAST(tx_runtime_diff AS FLOAT) > 10 THEN '<div class="bg-danger p-10 text-center">'
									WHEN (CAST(tx_parsed_diff AS FLOAT) >= 5 AND CAST(tx_parsed_diff AS FLOAT) <= 10) OR (CAST(tx_runtime_diff AS FLOAT) >= 5 AND CAST(tx_runtime_diff AS FLOAT) <= 10) THEN '<div class="bg-warning p-10 text-center">'
									ELSE '<div class="bg-success  p-10 text-center">' END
		, Wave 				= tx_wave
		, Status 			= tx_site_state
		, Expected 			= ct_job_expected
		, Parsed 			= ct_job_parsed
		, Parsed_average 	= ROUND(ISNULL(avg_parsed_job, 0), 2)
		, parsed_diff 		= CASE WHEN SIGN(ROUND(ISNULL(tx_parsed_diff, 0), 2)) = -1 THEN '(' + CAST(ABS(ROUND(ISNULL(tx_parsed_diff, 0), 2)) AS VARCHAR(32)) + ')' ELSE CAST(ABS(ROUND(ISNULL(tx_parsed_diff, 0), 2)) AS VARCHAR(32)) END
		, Started 			= dtt_start
		, Ended 			= dtt_end
		, Duration 			= ROUND(CAST(runtime AS varchar(16)), 2)
		, Runtime_average 	= ROUND(ISNULL(avg_runtime, 0), 2)
		, Runtime_diff 		= CASE WHEN SIGN(ROUND(ISNULL(tx_runtime_diff, 0), 2)) = -1 THEN '(' + CAST(ABS(ROUND(ISNULL(tx_runtime_diff, 0), 2)) AS VARCHAR(32)) + ')' ELSE CAST(ABS(ROUND(ISNULL(tx_runtime_diff, 0), 2)) AS VARCHAR(32)) END
		, OrgURL 			= tx_org_job_site_url
		FROM #T_TMP_RAG
		ORDER BY tx_wave ASC
		DROP TABLE #T_TMP_RAG
	END	

	IF(@tx_action_name = 'RAG_SITE_STATUS_OTHER')
	BEGIN
		
		IF(@tx_site_state = 'FAILED')
		BEGIN
			SET @tx_site_state_2 =  'BLIND'
		END

		SELECT tx_organization	= ORG.tx_org_name
		, tx_wave						= ORG.tx_wave
		, tx_site_state					= SITE.tx_site_state
		, ct_job_expected				= SITE.id_job_count_expected
		, ct_job_parsed					= SITE.id_job_count_parsed
		, avg_parsed_job				= ISNULL(ct_avg_job_parsed, 0)
		, tx_parsed_diff				= ((SITE.id_job_count_parsed - ISNULL(CAST(ct_avg_job_parsed AS FLOAT), 0)) * 100) / SITE.id_job_count_parsed	
		, dtt_start						= SITE.dtt_last_run_start
		, dtt_end						= SITE.dtt_last_run_end
		, runtime						= SITE.id_runtime / 60.0
		, avg_runtime					= ISNULL(CAST(tm_avg_run_second AS FLOAT), 0) / 60.0
		, tx_runtime_diff				= (((SITE.id_runtime / 60.0) - (ISNULL(CAST(tm_avg_run_second AS FLOAT), 0) / 60.0) ) * 100) / (SITE.id_runtime / 60.0)
		, tx_org_job_site_url			= SITE.tx_org_job_site_url
		INTO #T_TMP_OT_RAG FROM T_ORG ORG
		JOIN T_ORG_JOB_SITE SITE ON ORG.tx_org_short_name = SITE.tx_org_short_name
		WHERE		SITE.dtt_last_run_start 	IS NOT NULL
		AND			SITE.id_job_count_expected 	> 0 
		AND			SITE.id_job_count_parsed 	> 0
		AND			SITE.id_runtime				> 0
		AND			SITE.tx_site_state 			!= '?'
		AND			SITE.tx_site_state 			= CASE 	WHEN @tx_site_state = 'ALL' THEN SITE.tx_site_state														
		                                				ELSE @tx_site_state END
		AND			SITE.tx_site_state			= ISNULL(@tx_site_state_2, SITE.tx_site_state)
		ORDER BY	SITE.dtt_last_run_start DESC

		SELECT 
		  Organization 		= tx_organization
		, Wave 				= tx_wave
		, Status 			= tx_site_state
		, Expected 			= ct_job_expected
		, Parsed 			= ct_job_parsed
		, parsed_average 	= ROUND(ISNULL(avg_parsed_job, 0), 2)
		, Started 			= dtt_start
		, Ended 			= dtt_end
		, Duration 			= ROUND(CAST(runtime AS varchar(16)), 2)
		, Runtime_average 	= ROUND(ISNULL(avg_runtime, 0), 2)
		, Runtime_diff 		= CASE WHEN SIGN(ROUND(ISNULL(tx_runtime_diff, 0), 2)) = -1 THEN '(' + CAST(ABS(ROUND(ISNULL(tx_runtime_diff, 0), 2)) AS VARCHAR(32)) + ')' ELSE CAST(ABS(ROUND(ISNULL(tx_runtime_diff, 0), 2)) AS VARCHAR(32)) END
		, OrgURL 			= tx_org_job_site_url
		FROM #T_TMP_OT_RAG
		ORDER BY tx_wave ASC
		DROP TABLE #T_TMP_OT_RAG
	END

	IF(@tx_action_name = 'JOB_DETAIL_BY_ENTITY_STATUS')
	BEGIN
		
		SELECT TOP (@top_count)
		  tx_job_name
		, tx_job_url
		, tx_job_status
		, tx_job_location
		, dtt_job_added
		, dt_job_posted
		, tx_org_name 		= O.tx_org_name
		, tx_job_category 	= CASE WHEN tx_job_category = '?' THEN 'N/A' ELSE tx_job_category END
		FROM T_JOB_SPEC S WITH(NOLOCK)
		JOIN T_ORG O ON O.tx_org_short_name = S.tx_org_short_name
		WHERE tx_job_status = ISNULL(@tx_job_status, tx_job_status) --'{{status}}'
		AND S.tx_org_short_name = ISNULL(@tx_org_short_name, S.tx_org_short_name) --'{{org_name}}'
	END
END

GO
