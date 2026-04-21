/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: VALB104_001 - Your Primary Ledger is different from your Analytical Ledger (DRILL HERE)
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 09April2026
*/

NONEMPTYTUPLE([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[TOT_BU],[CENTER].[Profit],[YTD])

IIF(
    TRUNCATE(
        ([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[TOT_BU],[CENTER].[Profit],[YTD])
        -
        ([Repline].[TOTAL_REPLINES_IMP],[Account],[TOT_BU],[CENTER].[TOT_Center],[Periodic])
    ) <> 0,

    (
        ([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[TOT_BU],[CENTER].[Profit],[YTD]) * 
			( ([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[CENTER].[Profit],[Periodic]) /
			  ([Repline].[TOTAL_REPLINES],[Account].[P_AND_L],[CENTER].[Profit],[Periodic])
			) 
        -
        ([Repline].[TOTAL_REPLINES_IMP],[Account],[TOT_BU],[CENTER].[TOT_Center],[Periodic]) * 
			( 	([Repline].[TOTAL_REPLINES_IMP],[Account],[CENTER].[TOT_Center],[Periodic]) /
				([Repline].[TOTAL_REPLINES_IMP],[Account],[CENTER].[TOT_Center],[Periodic])
			)
    )


	* [EntityLevelwithValidation],

    Missing
)