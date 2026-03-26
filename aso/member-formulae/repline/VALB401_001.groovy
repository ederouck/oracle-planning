/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB401_001 - Amount to correct on Center : PC99995
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26MAR26
*/

NONEMPTYTUPLE([Center].[PC99995],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Center].[PC99995],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    ABS(
		TRUNCATE(
			([Center].[PC99995],[REPLINE].[TOTAL_REPLINES])
		)
	)	* [EntityLevelwithValidation],
    
    Missing
)