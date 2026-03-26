/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB203_001 - Amount to correct on Center: PC99991
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26mar2023
*/

NONEMPTYTUPLE([Center].[PC99991],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Center].[PC99991],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    ABS(
      TRUNCATE(
          ([Center].[PC99991],[REPLINE].[TOTAL_REPLINES])
      )
    ) * [EntityLevelwithValidation],
    
    Missing
)