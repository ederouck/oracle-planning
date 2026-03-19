/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: Data Status Base Data - Count (counts the number of entity/lob combinations that have a status)
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 19mar2026
*/
NONEMPTYTUPLE([Data Status Base Data])
[Data Status Base Data]/[Data Status Base Data]

CASE 
    WHEN IsAncestor([Repline].[GLOMIT_STATUS], [Repline].CurrentMember) THEN
        
        CASE
            /* L0 Lob + L0 Entity : divide by itself */
            WHEN IsLeaf([Lob].CurrentMember)
             AND IsLeaf([Entity].CurrentMember)
            THEN
                ([Data Status Base Data])/([Data Status Base Data])

            /* L0 Lob + NOT L0 Entity */
            ELSE 
				SUM(
					CROSSJOIN(
						LEAVES([Entity].CURRENTMEMBER),
						LEAVES([LOB].CURRENTMEMBER)
					),
					([Data Status Base Data]) * (([Data Status Base Data]) / ([Data Status Base Data]))
				)
        END
 END