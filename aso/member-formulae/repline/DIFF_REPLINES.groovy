/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: DIFF_REPLINES - You have an unbalance on your entity data to correct
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 24march2025
*/

/* NONEMPTYTUPLE([TOTAL_REPLINES_IMP],[Account].[GL7999999],[Repline].[TOTAL_REPLINES],[Account].[P_AND_L])*/

        ([Repline].[TOTAL_REPLINES],[TOT_BU])
        -
        (
            ([Repline].[REPLINE_OTHER],[TOT_BU])
            -
            IIF(
                [Period].CurrentMember IS [Period].[Jan],
                /* Jan: geen vorige periode aftrekken */
                Missing,
                /* Andere maanden: wel vorige periode */
                ([Repline].[REPLINE_OTHER],[TOT_BU],[Period].CurrentMember.PrevMember)
            )
        )