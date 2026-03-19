/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: View
   Member: STATUS_Lob_by_Entity2
   Explanation: rolls up the status of a bu for all entities below
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 19march26
*/
CASE 
    WHEN IsAncestor([Repline].[GLOMIT_STATUS], [Repline].CurrentMember) THEN
        
        CASE
            /* L0 Lob + L0 Entity -> neem gewoon Periodic over */
            WHEN IsLeaf([Lob].CurrentMember)
             AND IsLeaf([Entity].CurrentMember)
            THEN
                ([View].[Periodic])

            /* L0 Lob + NOT L0 Entity */
            WHEN IsLeaf([Lob].CurrentMember)
             AND NOT IsLeaf([Entity].CurrentMember)
            THEN
                MIN(
                    DESCENDANTS([Entity].[Tot_Entity], [Entity].Levels(0)),
                    ([View].[Periodic]) * (([View].[Periodic]) / ([View].[Periodic]))
                )

            /* > L0 Lob */
            WHEN NOT IsLeaf([Lob].CurrentMember)
            THEN
                MIN(
                    CHILDREN([Lob].CurrentMember),
                    ([View].[STATUS_Lob_by_Entity2]) * (([View].[STATUS_Lob_by_Entity2]) / ([View].[STATUS_Lob_by_Entity2]))
                )
        END

// END