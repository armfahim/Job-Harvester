CREATE ROLE [app_ro]
GO

CREATE ROLE [app_rw]
GO


CREATE ROLE [app_so]
GO




CREATE PROC [dbo].[DROP_db_object]
(
	  @tx_db_object_type	varchar (32)
	, @tx_db_object_name	varchar (512)
	, @is_print_sql			int		= 0
)
AS
BEGIN
	SET NOCOUNT ON

	DECLARE	  @l_tx_schema_name		varchar(64)
			, @l_tx_name			varchar(128)
			, @l_tx_db_type_name 	varchar (32)
			, @l_tx_dyna_sql 		varchar (512)
			, @l_dot_index			int


	SELECT @l_dot_index = CHARINDEX('.', @tx_db_object_name)

	IF (@l_dot_index = 0)
	BEGIN
		SELECT @l_tx_schema_name = 'dbo'
	END
	ELSE
	BEGIN
		SELECT @l_tx_schema_name = SUBSTRING(@tx_db_object_name, 0, @l_dot_index )
	END

	SELECT	@l_tx_name = SUBSTRING(@tx_db_object_name,  @l_dot_index + 1, LEN(@tx_db_object_name) )


	IF (@tx_db_object_type NOT IN ('U', 'V', 'P', 'FN', 'TR') )
	BEGIN
		RAISERROR ('Invalid object type %s', 0, 1, @tx_db_object_type) WITH NOWAIT
		RETURN -1
	END

	RAISERROR ('%s : [%s].[%s]', 0, 1, @tx_db_object_type, @l_tx_schema_name, @l_tx_name) WITH NOWAIT

	IF EXISTS  	(	SELECT 	1
					FROM	sys.objects O
					JOIN	sys.schemas	S ON S.schema_id = O.schema_id
					WHERE	O.type 	= @tx_db_object_type
					AND		S.name	= @l_tx_schema_name
					AND		O.name	= @l_tx_name
				)
	BEGIN

		SELECT @l_tx_db_type_name = CASE
										WHEN @tx_db_object_type = 'U' 	THEN 'TABLE'
										WHEN @tx_db_object_type = 'V' 	THEN 'VIEW'
										WHEN @tx_db_object_type = 'TR' 	THEN 'TRIGGER'
										WHEN @tx_db_object_type = 'P' 	THEN 'PROC'
										WHEN @tx_db_object_type = 'FN' 	THEN 'FUNCTION'
										ELSE '?'
									END

		SELECT 	@l_tx_dyna_sql = 'DROP ' +  @l_tx_db_type_name + ' ' + S.name + '.' + O.name
		FROM	sys.objects O
		JOIN	sys.schemas	S ON S.schema_id = O.schema_id
		WHERE	O.type 	= @tx_db_object_type
		AND		S.name	= @l_tx_schema_name
		AND		O.name	= @l_tx_name

		RAISERROR ('%s', 0, 1, @l_tx_dyna_sql ) WITH NOWAIT

		EXEC (@l_tx_dyna_sql)
	END

	SET NOCOUNT OFF

	RETURN 0

END

go