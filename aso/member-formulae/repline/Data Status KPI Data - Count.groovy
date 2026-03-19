/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: Repline
   Member: Data Status KPI Data - Count (counts the number of entity/lob combinations that have a status)
   Source: Oracle EPM Planning (UI copy/paste)
   Updated: 19mar2026
*/
NONEMPTYTUPLE([Data Status KPI])
    
CASE
	/* L0 Lob + L0 Entity */
	WHEN IsLeaf([Lob].CurrentMember) AND IsLeaf([Entity].CurrentMember)
	THEN
		([Data Status KPI])/([Data Status KPI])

	/* all other cases */
	ELSE 
		SUM(
			CROSSJOIN(
				LEAVES([Entity].CURRENTMEMBER),
				LEAVES([LOB].CURRENTMEMBER)
			),
			(([Data Status KPI]) / ([Data Status KPI]))
		)
END