/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB205_001 - Amount to correct on Project: P_AA99999991
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26MAR26
*/

NONEMPTYTUPLE([Project].[P_AA99999991],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Project].[P_AA99999991],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    TRUNCATE(
        ([Project].[P_AA99999991],[REPLINE].[TOTAL_REPLINES])
    ) * [EntityLevelwithValidation],
    
    Missing
)