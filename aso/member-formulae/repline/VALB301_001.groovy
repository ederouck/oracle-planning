/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB301_001 - Amount to correct on Client: C_99999997
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26mar26
*/
NONEMPTYTUPLE([Client].[C_99999997],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Client].[C_99999997],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    ABS(
		TRUNCATE(
			([Client].[C_99999997],[REPLINE].[TOTAL_REPLINES])
		)
	) * [EntityLevelwithValidation],
    
    Missing
)