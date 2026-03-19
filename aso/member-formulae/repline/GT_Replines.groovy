/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: GT_Replines - Summing the Total Replines and the Replines Other
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 13mar2026
*/

IIF(
    ROUND(
        ([TOTAL_REPLINES]) - ([REPLINE_OTHER])
        ,0
    ) <> 0,
    ROUND(
        ([TOTAL_REPLINES]) - ([REPLINE_OTHER])
        ,0
    ),
    
    Missing
)