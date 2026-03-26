/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB302_001 - Amount to correct on Location: AA99997
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26feb2023
*/

NONEMPTYTUPLE([Location].[AA99997],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Location].[AA99997],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    TRUNCATE(
        ([Location].[AA99997],[REPLINE].[TOTAL_REPLINES])
    ) * [EntityLevelwithValidation],
    
    Missing
)