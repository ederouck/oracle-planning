/* 
   ASO Member formula
   Application: Glomit
   Cube: Glomit
   Dimension: View
   Member: ERROR_fiora
   Explanation: in combination with replines in the validation section
   Source: Oracle EPM Planning
*/
/*NONEMPTYTUPLE([View].[Periodic],[LC],[Client],[Location],[Project],[Spare],[Center],[Intercompany],[Data Source])*/
IIF(
	IsAncestor([Repline].[GLOMIT_VAL], [Repline].CurrentMember) , /*only validation accounts*/
    IIF( /* sum of lob and fiora entities is zero */
			NonEmptyCount(
				CrossJoin(
					CrossJoin(
						{
							Filter(
								Leaves([Entity].CurrentMember),
								(
									[Current Data Source],
									[Actual],
									[Working],
									[No Currency],
									[No Client],
									[No LoB],
									[No Location],
									[No Project],
									[SP99999],
									[No Center],
									[No Account],
									[No Intercompany],
									[No Data Source],
									[Periodic]
								) = 1
							)
						},
						{Leaves([Lob].CurrentMember)}
					),
					{[Repline].CurrentMember}
				)
			) = 0,
        Missing,
		NonEmptyCount(
			CrossJoin(
				CrossJoin(
					{
						Filter(
							Leaves([Entity].CurrentMember),
							(
								[Current Data Source],
								[Actual],
								[Working],
								[No Currency],
								[No Client],
								[No LoB],
								[No Location],
								[No Project],
								[SP99999],
								[No Center],
								[No Account],
								[No Intercompany],
								[No Data Source],
								[Periodic]
							) = 1
						)
					},
					{Leaves([Lob].CurrentMember)}
				),
				{[Repline].CurrentMember}
			)
		)
	)
	),
    Missing
)