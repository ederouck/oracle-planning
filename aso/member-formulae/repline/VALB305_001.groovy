/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB305_001 - Amount to correct on Intercompany: I_997
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 26feb2023
*/

NONEMPTYTUPLE([Intercompany].[I_997],[REPLINE].[TOTAL_REPLINES])

IIF(
    TRUNCATE(
        ([Intercompany].[I_997],[REPLINE].[TOTAL_REPLINES])
    ) <> 0,
    
    TRUNCATE(
        ([Intercompany].[I_997],[REPLINE].[TOTAL_REPLINES])
    ) * [EntityLevelwithValidation],
    
    Missing
)