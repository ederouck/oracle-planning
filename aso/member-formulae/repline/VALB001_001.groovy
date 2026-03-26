/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB001_001 - The total Replines for this month does not match the YTD amount
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26march2026
*/

NONEMPTYTUPLE([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[Data Source].[UPLOAD_PL],[View].[YTD])

IIF(
    TRUNCATE(
        (
            ([TOTAL_REPLINES_IMP],[Account].[GL7999999],[Data Source].[UPLOAD_PL])
            -
            ([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[Data Source].[UPLOAD_PL],[View].[YTD])
        )
    ) <> 0,

    ABS(
		TRUNCATE(
        (
				([TOTAL_REPLINES_IMP],[Account].[GL7999999],[Data Source].[UPLOAD_PL])
				-
				([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[Data Source].[UPLOAD_PL],[View].[YTD])
			)	
		)
	)
    * [EntityLevelwithValidation],

    Missing
)