/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB207_001 - Amount to correct on Repline: I_991
   Updated: 26MAR26
*/

NONEMPTYTUPLE([REPLINE].[RL999991])

IIF(
    TRUNCATE(
        ([REPLINE].[RL999991])
    ) <> 0,
    
    TRUNCATE(
        ([REPLINE].[RL999991])
    ) * [EntityLevelwithValidation],
    
    Missing
)