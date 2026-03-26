/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB204_001 - Amount to correct on Account: GL9999991
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26feb2023
*/

NONEMPTYTUPLE([Account].[GL9999991],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Account].[GL9999991],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    TRUNCATE(
        ([Account].[GL9999991],[REPLINE].[TOTAL_REPLINES])
    ) * [EntityLevelwithValidation],
    
    Missing
)