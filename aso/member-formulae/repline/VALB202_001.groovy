/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB202_001 - Amount to correct on Location: AA99991
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26feb2023
*/

NONEMPTYTUPLE([Location].[AA99991],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Location].[AA99991],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    ABS(
      TRUNCATE(
          ([Location].[AA99991],[REPLINE].[TOTAL_REPLINES])
      )
    ) * [EntityLevelwithValidation],
    
    Missing
)