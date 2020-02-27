--DROP T_JOB_SPEC;

CREATE TABLE T_JOB_SPEC(
	  id_job_spec_key		int	Identity	NOT NULL													PRIMARY KEY
	, id_job_spec_ver		int			NOT NULL													--FOREIGN KEY (id_job_spec_ver) REFERENCES T_JOB_SPEC_AUDIT(id_job_spec_ver)
	, id_org_job_site_key		int			NOT NULL													--FOREIGN KEY (id_org_job_site_key) REFERENCES T_ORG_JOB_SITE(id_org_job_site_key)
	, tx_job_ref_id			varchar(256)		NOT NULL			DEFAULT 'Requisition ID not provided'	
	, tx_job_name			varchar(256)		NOT NULL			DEFAULT 'Job Name'
	, dt_job_posted			date			NOT NULL			DEFAULT '2019-01-20'
	, tx_job_location		varchar(512)		NOT NULL			DEFAULT	'Location not provided'
	, tx_job_title			varchar(256)		NOT NULL			DEFAULT 'Job Title'
	, tx_job_spec			varchar(max)		NOT NULL			DEFAULT 'Description' 
	, tx_job_prerequisite		varchar(max)		NOT NULL			DEFAULT 'Prerequisites not provided'
	, tx_job_type			varchar(128)		NOT NULL			DEFAULT 'Job Type'
	, tx_job_category		varchar(256)		NOT NULL			DEFAULT 'Unscpecified'
	, id_job_tag_count		smallint		NOT NULL			DEFAULT	-32768
	, tx_comment			varchar(512)		NOT NULL			DEFAULT 'No Comments'
	, tx_job_url			varchar(512)		NOT NULL	UNIQUE	DEFAULT 'job URL' 
	, is_active			int			NOT NULL			DEFAULT 1
	, dtt_job_added			datetime		NOT NULL			DEFAULT '2019-20-01 00:00:00'
	, tx_application_url		varchar(512)		NOT NULL			DEFAULT 'Please check for Email Address'
	, tx_apply_email		varchar(256)		NOT NULL			DEFAULT 'Please check for Application URL'
	, id_env_key			int			NOT NULL			DEFAULT 0
	, id_user_mod_key		int			NOT NULL			DEFAULT 0
	, dtt_mod			datetime		NOT NULL			DEFAULT '2019-20-01 00:00:00'
	, id_state_key			int			NOT NULL			DEFAULT 0
	, id_action_key			int			NOT NULL			DEFAULT 0
	, id_event_key			int			NOT NULL			DEFAULT 0
);

--DROP T_ORG_JOB_SITE;
-- TODO alter table "dbo"."T_ORG_JOB_SITE" add id_archived bit DEFAULT 0;
CREATE TABLE T_ORG_JOB_SITE(
	  id_org_job_site_key		int	Identity	NOT NULL					PRIMARY KEY
	, id_org_job_site_ver		int			NOT NULL	DEFAULT 0		--FOREIGN KEY (id_org_job_site_ver) REFERENCES T_ORG_JOB_SITE_AUDIT(id_org_job_site_ver)
	, id_org_key			int 			NOT NULL	DEFAULT 0		--FOREIGN KEY (id_org_key) REFERENCES T_ORG(id_org_key)
	, tx_org_job_site_url		varchar(256)		NOT NULL	UNIQUE
	, tx_org_parser_func		varchar(256)		NOT NULL	DEFAULT 'parseSite'
	, int_freq			int			NOT NULL	DEFAULT 0
	, tx_freq_unit			varchar(128)		NOT NULL	DEFAULT 'DAILY'
	, dtt_last_run			datetime		NOT NULL	DEFAULT '0000-00-00 00:00:00'
	, tx_last_run_status		varchar(256)		NOT NULL	DEFAULT 'Not Running'
	, dtt_last_run_start		datetime		NOT NULL	DEFAULT '0000-00-00 00:00:00'	
	, dtt_last_run_end		datetime		NOT NULL	DEFAULT '0000-00-00 00:00:00'
	, ct_new			int			NOT NULL	DEFAULT 0
	, ct_update			int			NOT NULL	DEFAULT 0
	, ct_del			int			NOT NULL	DEFAULT 0
	, ct_err			int			NOT NULL	DEFAULT 0
	, tx_comment			varchar(256)		NOT NULL	DEFAULT 'No Comments'
	, tx_config			varchar(max)		NOT NULL	DEFAULT 'Config'
	, id_env_key			int			NOT NULL	DEFAULT 0
	, id_user_mod_key		int			NOT NULL	DEFAULT 0
	, dtt_mod			datetime		NOT NULL	DEFAULT '0000-00-00 00:00:00'
	, id_event_key			int			NOT NULL	DEFAULT 0
	, id_state_key			int			NOT NULL	DEFAULT 0
	, id_action_key			int			NOT NULL	DEFAULT 0
);

--DROP TABLE T_ORG;

CREATE TABLE T_ORG(
	  id_org_key			int	Identity	NOT NULL											PRIMARY KEY
	, id_org_ver			int			NOT NULL											--FOREIGN KEY (id_org_ver) REFERENCES T_ORG_AUDIT(id_org_ver)
	, tx_org_name			varchar(256)		NOT NULL	UNIQUE	DEFAULT 'Organization name'
	, tx_org_short_name		varchar(32)		NOT NULL	UNIQUE	DEFAULT 'ORG'
	, tx_org_address		varchar(512)		NOT NULL			DEFAULT 'Organization Address'
	, tx_org_url			varchar(256)		NOT NULL			DEFAULT 'Organization URL'
	, tx_org_parser_func		varchar(256)		NOT NULL			DEFAULT 'parseSite'
	, tx_wave			varchar(8)		NOT NULL			DEFAULT 'WAVE'
	, tx_comment			varchar(256)		NOT NULL			DEFAULT 'No Comment'
	, id_env_key			int			NOT NULL			DEFAULT 0
	, id_user_mod_key		int			NOT NULL			DEFAULT 0
	, dtt_mod			datetime		NOT NULL			DEFAULT '0000-00-00 00:00:00'
	, id_event_key			int			NOT NULL			DEFAULT 0
	, id_state_key			int			NOT NULL			DEFAULT 0
	, id_action_key			int			NOT NULL			DEFAULT 0
);
