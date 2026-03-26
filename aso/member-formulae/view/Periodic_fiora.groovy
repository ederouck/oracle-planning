/*NONEMPTYTUPLE([Current Data Source],[Actual],[Working],[No Currency],[No Client],[No LoB],[No Location],[No Project],[SP99999],[No Center],[No Account],[No Intercompany],[No Data Source],[Periodic])*/
IIF(
		(
			([Current Data Source],[Actual],[Working],[No Currency],[No Client],[No LoB],[No Location],[No Project],[SP99999],[No Center],[No Account],[No Intercompany],[No Data Source],[Periodic]) = 1
		),
		([Periodic]),
		Missing
	)