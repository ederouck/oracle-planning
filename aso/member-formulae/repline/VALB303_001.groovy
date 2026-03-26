/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB303_001 - Amount to correct on Repline: RL999997
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26mar26
*/

NONEMPTYTUPLE([RepLine].[RL999997])

IIF(
    TRUNCATE(
        [RepLine].[RL999997]
    ) <> 0,
    
    ABS(
		TRUNCATE(
			[RepLine].[RL999997]
		)
	)	* [EntityLevelwithValidation],
    
    Missing
)