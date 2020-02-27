/* __VER_INFO__  :  */
/******************************************************************************
* Author  		: Md.Kamruzzaman
* Date   		: 20190116
* Description 	:
******************************************************************************/
#include <WS_SQL.h>

#define _TABLE_NAME 		{T_JOB_SPEC_AUDIT};
#define _PRIMARY_KEY		{id_job_spec_key};
#define _VERSION			{id_job_spec_ver};

_DROP_TABLE

_CREATE_TABLE(_TABLE_NAME)
(
	  _PRIMARY_KEY						int											NOT NULL
	, _VERSION							int											NOT NULL
	, _TABLE_HEADER
	
	, tx_job_name 						nvarchar (_L_JOB_NAME)						NOT NULL
	, id_org_job_site_key				int											NOT NULL
	, tx_job_ref_id						nvarchar(_L_JOB_REF_ID)						NOT NULL
	, dt_job_posted						date										NOT NULL
	, tx_job_location					nvarchar(_L_JOB_LOCATION)					NOT NULL
	, tx_job_title						nvarchar(_L_JOB_TITLE)						NOT NULL
	, tx_job_spec						nvarchar(max)								NOT NULL
	, tx_job_prerequisite				nvarchar(max)								NOT NULL
	, tx_job_type						nvarchar(_L_JOB_TYPE)						NOT NULL
	, tx_job_category					nvarchar(_L_JOB_CATEGORY)					NOT NULL
	, tx_job_url						nvarchar(_L_JOB_URL)							NOT NULL
	, dtt_job_added						datetime									NOT NULL
	, dtt_last_update					datetime									NOT NULL
	, tx_application_url				nvarchar(_L_APPLICATION_URL)					NOT NULL
	, tx_apply_email					nvarchar(_L_APPLY_EMAIL)						NOT NULL
	, tx_comment						nvarchar(_L_COMMENT)							NOT NULL

	, CONSTRAINT pk_job_spec_audit_key	PRIMARY KEY CLUSTERED (_PRIMARY_KEY, _VERSION)
)
go


go
_GRANT_PERM_TBL