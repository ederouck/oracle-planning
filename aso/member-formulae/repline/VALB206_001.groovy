/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB206_001 - Amount to correct on IC: I_991
   Updated: 12march2026
*/

NONEMPTYTUPLE([Intercompany].[I_991],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Intercompany].[I_991],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    TRUNCATE(
        ([Intercompany].[I_991],[REPLINE].[TOTAL_REPLINES])
    ) * [EntityLevelwithValidation],
    
    Missing
)