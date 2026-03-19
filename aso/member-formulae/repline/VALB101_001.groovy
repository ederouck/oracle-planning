/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB101_001 - You have a difference on BS to correct
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 02march2026
   Watch out: Input is done on a PL account 
*/

NONEMPTYTUPLE([Repline].[REPLINE_OTHER].[RL999999],[Account].[BALANCE],[Data Source].[UPLOAD_PL],[View].[Periodic])

IIF(
    ROUND(
        (
            ([BALANCE_IMP],[Account].[BALANCE],[Data Source].[UPLOAD_PL],[View].[Periodic])
            -
            ([Repline].[REPLINE_OTHER],[Account].[BALANCE],[Data Source].[UPLOAD_PL],[View].[Periodic])
        ),
        0
    ) <> 0,

    ABS(
		ROUND(
			(
				([BALANCE_IMP],[Account].[BALANCE],[Data Source].[UPLOAD_PL],[View].[Periodic])
				-
				([Repline].[REPLINE_OTHER],[Account].[BALANCE],[Data Source].[UPLOAD_PL],[View].[Periodic])
			),
			0
		)
	)
    * [EntityLevelwithValidation],

    Missing
)