//Set up error messages
def mbUs = messageBundle([
"validation.lockedstatus":"The Entity/LoB intersection is locked.", 
"validation.notstartedstatus":"The Entity/LoB intersection is not started yet.",
"validation.nosource":"The data source for the entity has not been set for this period."
])
def mbl = messageBundleLoader(["en" : mbUs]);

def BF_GetCurrentApplicationName()
{
	//Get the current application name
	def strApplicationName = operation.application.getName().toString()
	println("Current application = " + strApplicationName)
   
    return strApplicationName
}

def BF_GetStatus(currScenario, currYear, currPeriod, currRepLine, currEntity, currLoB)
{
	//Global constants
	def strCubeName = "GLOMIT"	
	
    //Variables
    Cube cube = operation.application.getCube(strCubeName)
    def builder = cube.flexibleDataGridDefinitionBuilder()
	def myStatus = 0
    def strStatus = ""
        
	builder.setPovDimensions("Scenario","Years","Period","Version","View","Account","Entity","Project","Center","Location","Data Source","Intercompany","Client","Spare","Currency")
	builder.setPov(currScenario,currYear,currPeriod,"Working","Periodic","No Account",currEntity,"No Project","No Center","No Location","No Data Source","No Intercompany","No Client","SP99999","No Currency")

	builder.setColumnDimensions("RepLine")
	builder.addColumn(currRepLine)

	builder.setRowDimensions("LoB")
	builder.addRow(currLoB)

	// Load a data grid from the specified grid definition and cube
	cube.loadGrid(builder.build(), false).withCloseable { grid ->
    	def myCell = grid.dataCellIterator().next()
    	myStatus = myCell.getData()
		//println("State value: " + myStatus)
	}

	switch (myStatus)
    {
    	case (1):
        	strStatus = "Open"
            break
    	case (2):
        	strStatus = "Submitted"
            break
    	case (3):
        	strStatus = "SubmittedOverride"
            break
    	case (4):
        	strStatus = "Locked"
            break
        default:
        	strStatus = "NotStarted"
            break
    }
	
    return strStatus
}

def BF_GetDataSource(currScenario, currYear, currPeriod, currEntity)
{
	//Global constants
	def strCubeName = "GLOMIT"	
	
    //Variables
    Cube cube = operation.application.getCube(strCubeName)
    def builder = cube.flexibleDataGridDefinitionBuilder()
	def mySource = 0
    def strSource = ""

	builder.setPovDimensions("Scenario","Years","Period","Version","View","Account","LoB","Project","Center","Location","Data Source","Intercompany","Client","Spare","Currency")
	builder.setPov(currScenario,currYear,currPeriod,"Working","Periodic","No Account","No LoB","No Project","No Center","No Location","No Data Source","No Intercompany","No Client","SP99999","No Currency")

	builder.setColumnDimensions("RepLine")
	builder.addColumn("Current Data Source")

	builder.setRowDimensions("Entity")
	builder.addRow(currEntity)

	// Load a data grid from the specified grid definition and cube
	cube.loadGrid(builder.build(), false).withCloseable { grid ->
    	def myCell = grid.dataCellIterator().next()
    	mySource = myCell.getData()
		println("Source value: " + mySource)
	}

	switch (mySource)
    {
    	case (1):
        	strSource = "Fiora"
            break
    	case (2):
        	strSource = "Navision"
            break
    	case (3):
        	strSource = "Indaver"
            break
    	case (4):
        	strSource = "Other"
            break
    	case (5):
        	strSource = "GLOMIT"
            break
        default:
        	strSource = "No Source"
            break
    }

    return strSource
}

def BF_GetValidationValue(currScenario, currYear, currPeriod, currRepLine, currEntity, currLoB)
{
	def strCubeName = ""	
	if (currScenario == "Actual")
    {
    	strCubeName = "GLOMIT"
    }
	else
    {
    	strCubeName = "Input"
    }
    
	Cube cube = operation.application.getCube(strCubeName)
    def builder = cube.flexibleDataGridDefinitionBuilder()
	def myValue = 0
    
    if (strCubeName == "GLOMIT")
    {
        builder.setPovDimensions("Scenario","Years","Period","Version","View","Account","Entity","Project","Center","Location","Data Source","Intercompany","Client","Spare","Currency")
        builder.setPov(currScenario,currYear,currPeriod,"Working","Periodic","Account",currEntity,"Project","Center","Location","Data Source","Intercompany","Client","Spare","LC")
    }
	else
	{
        builder.setPovDimensions("Scenario","Years","Period","Version","Entity","Project","Center","Location","Client","Spare","Currency")
        builder.setPov(currScenario,currYear,currPeriod,"Working", currEntity,"Project","Center","Location","Client","Spare","LC")
    }

	builder.setColumnDimensions("RepLine")
	builder.addColumn(currRepLine)

	builder.setRowDimensions("LoB")
	builder.addRow(currLoB)

	// Load a data grid from the specified grid definition and cube
	cube.loadGrid(builder.build(), false).withCloseable { grid ->
    	def myCell = grid.dataCellIterator().next()
    	myValue = myCell.getData()
		println("Validation value " + currRepLine + ": " + myValue)
	}

    return myValue.toString()
}

def BF_ClearData_ASO(currScenario, currYear, currPeriod, currRepLine, currEntity, currLoB)
{
    // ***********************************************************************************************
    // Clear data on the ASO cube.
    // This is used in the lock scripts before the import from BSO to ASO takes place.
    // ***********************************************************************************************
    
    println("Clear data")

	//Global constants
	def cube = operation.application.getCube("GLOMIT")

	def strPoV = ""

    //First Crossjoin (1 less than the number of dimension in the PoV)
    strPoV = "Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin("

    //SCENARIO
    strPoV = strPoV + "{[" + currScenario + "]}"
    //PERIOD
    strPoV = strPoV + ",{[" + currPeriod + "]})"
    //SPARE
    strPoV = strPoV + ",{[SP99999]})"
    //VERSION
    strPoV = strPoV + ",{[Working]})"
    //VIEW
    strPoV = strPoV + ",{[Periodic]})"
    //YEARS
    strPoV = strPoV + ",{[" + currYear + "]})"
    //CURRENCY
    strPoV = strPoV + ",{[LC],[EUR_Reporting],[USD_Reporting]})"
    //ACCOUNT
    strPoV = strPoV + ",{Descendants([Account], [Account].dimension.Levels(0))})"
    //CENTER
    strPoV = strPoV + ",{Descendants([Center], [Center].dimension.Levels(0))})"
    //CLIENT
    strPoV = strPoV + ",{Descendants([Client], [Client].dimension.Levels(0))})"
    //strPoV = strPoV + ",{Descendants([TOT_CUST], [TOT_CUST].dimension.Levels(0))})"
    //DATA SOURCE
    strPoV = strPoV + ",{Descendants([TOT_SOURCE], [TOT_SOURCE].dimension.Levels(0))})"
    //ENTITY
    strPoV = strPoV + ",{[" + currEntity + "]})"
    //INTERCOMPANY
    strPoV = strPoV + ",{Descendants([TOT_INTCO], [TOT_INTCO].dimension.Levels(0))})"
    //LOB
    strPoV = strPoV + ",{[" + currLoB + "]})"
    //LOCATION
    strPoV = strPoV + ",{Descendants([WORLD], [WORLD].dimension.Levels(0))})"
    //PROJECT
    strPoV = strPoV + ",{Descendants([Project], [Project].dimension.Levels(0))})"
    //REPLINE
    strPoV = strPoV + ",{" + currRepLine + "})"

    println(strPoV)

    //Perform the partial clear.
    cube.clearPartialData(strPoV,true)

}

def BF_ClearDataFY_ASO(currScenario, currYear, currRepLine, currEntity, currLoB)
{
    // ***********************************************************************************************
    // Clear data on the ASO cube.
    // This is used in export script for Forecast and Budget (BSO to ASO).
    // ***********************************************************************************************
    
    println("Clear data for full year")

	//Global constants
	def cube = operation.application.getCube("GLOMIT")

	def strPoV = ""

    //First Crossjoin (1 less than the number of dimension in the PoV)
    strPoV = "Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin(Crossjoin("

    //SCENARIO
    strPoV = strPoV + "{[" + currScenario + "]}"
    //PERIOD
    strPoV = strPoV + ",{Descendants([YearTotal], [YearTotal].dimension.Levels(0))})"
    //SPARE
    strPoV = strPoV + ",{[SP99999]})"
    //VERSION
    strPoV = strPoV + ",{[Working]})"
    //VIEW
    strPoV = strPoV + ",{[Periodic]})"
    //YEARS
    strPoV = strPoV + ",{[" + currYear + "]})"
    //CURRENCY
    strPoV = strPoV + ",{[LC],[EUR_Reporting]})"
    //ACCOUNT
    strPoV = strPoV + ",{Descendants([Account], [Account].dimension.Levels(0))})"
    //CENTER
    strPoV = strPoV + ",{Descendants([Center], [Center].dimension.Levels(0))})"
    //CLIENT
    strPoV = strPoV + ",{Descendants([TOT_CUST], [TOT_CUST].dimension.Levels(0))})"
    //DATA SOURCE
    strPoV = strPoV + ",{Descendants([TOT_SOURCE], [TOT_SOURCE].dimension.Levels(0))})"
    //ENTITY
    strPoV = strPoV + ",{" + currEntity + "})"
    //INTERCOMPANY
    strPoV = strPoV + ",{Descendants([TOT_INTCO], [TOT_INTCO].dimension.Levels(0))})"
    //LOB
    strPoV = strPoV + ",{" + currLoB + "})"
    //LOCATION
    strPoV = strPoV + ",{Descendants([WORLD], [WORLD].dimension.Levels(0))})"
    //PROJECT
    strPoV = strPoV + ",{Descendants([Project], [Project].dimension.Levels(0))})"
    //REPLINE
    strPoV = strPoV + ",{" + currRepLine + "})"

    println(strPoV)

    //Perform the partial clear.
    cube.clearPartialData(strPoV,true)

}

// Wait for DM job to be completed
def awaitCompletion(HttpResponse<String> jsonResponse, String connectionName) {
    final int IN_PROGRESS = -1
    if(!(200..299).contains(jsonResponse.status))
        throwVetoException("Error occured: $jsonResponse.statusText")

    // Parse the JSON response to get the status of the operation. Keep polling the DM server until the operation completes.
    ReadContext ctx = JsonPath.parse(jsonResponse.body)
    int status = ctx.read('$.status')
    for(long delay = 50; status == IN_PROGRESS; delay = Math.min(1000, delay * 2)) {
        sleep(delay)
        status = getJobStatus(connectionName, (String)ctx.read('$.jobId'))
    }
    return status
}

// Call DM GET REST API to poll the DM server for job status
int getJobStatus(String connectionName, String jobId) {
    HttpResponse<String> pingResponse = operation.application.getConnection(connectionName).get(jobId).asString()
    return JsonPath.parse(pingResponse.body).read('$.status')
}

def BF_WriteResult(currScenario, currYear, currPeriod, currRepLine, currEntity, currLoB, lastAction)
{
  	//************************************************************************************************************************
  	// Write the status to current Entity/LoB
  	//************************************************************************************************************************
	println("Writing end result")

	// Get the User object for the person running the rule
  	User currentUser = operation.getUser()
  	// Get the user's name (login ID)
  	def currentUserName = currentUser.getName()

  	//Get today's date in Essbase format
  	Date currentDate = new Date()
  	def myDateString = currentDate.format("dd MMM yyyy, HH:mm:ss")

	// Get the cube
  	Cube cube = operation.application.getCube("GLOMIT")

  	// Create a DataGridBuilder
  	//DataGridBuilder builder = cube.dataGridBuilder("MM/DD/YYYY")
  	def builder = cube.dataGridBuilder("MM/DD/YYYY", SYSTEM_USER)
  	//def builder = cube.dataGridBuilder("MM/DD/YYYY")

  	// Define the POV
  	builder.addPov(currScenario.toString(),currYear.toString(),currPeriod.toString(),currLoB.toString(),"Working","Periodic","No Account","No Currency","No Project","No Center","No Location","No Data Source","No Intercompany","No Client","SP99999")

  	// Define columns
  	builder.addColumn(currEntity.toString())

  	// Add all current entity to the row, assigning the value 1 = open
  	builder.addRow([currRepLine.toString()],[1])

	println("Last Action: " + lastAction)
	println("Current user: " + currentUserName)
	println("Current date: " + myDateString)
    if (currRepLine == "Data Status KPI")
    {
    	builder.addRow(["Last Action KPI"],[lastAction])
  		builder.addRow(["Last Action By KPI"],[currentUserName])
  		builder.addRow(["Last Action Date KPI"],[myDateString])
    }
  	else
    {
    	builder.addRow(["Last Action Base Data"],[lastAction])
  		builder.addRow(["Last Action By Base Data"],[currentUserName])
  		builder.addRow(["Last Action Date Base Data"],[myDateString])
	}
  	// Build and save the grid
  	DataGridBuilder.Status status = new DataGridBuilder.Status()
  	builder.build(status).withCloseable { grid ->
    	// Save the data to the cube
    	cube.saveGrid(grid)

		// Optional: small retry in case another SYSTEM_USER write is finishing up
        /*
        def attempts = 0
        while (attempts < 10) {
			try {
                println "Save attempt ${attempts}"
				cube.saveGrid(grid)
            } catch (Exception ex) {
                attempts++
                println "Save attempt ${attempts} failed: ${ex.message}"
				sleep(10000) // back-off 300 ms
    	    }
            }
        
        */
	  }
}

def BF_WriteStatus(currScenario, currYear, currPeriod, currRepLine, currEntity, currLoB)
{
  	//************************************************************************************************************************
  	// Write the status to current Entity/LoB
  	//************************************************************************************************************************
	println("Writing status")

	// Get the User object for the person running the rule
  	User currentUser = operation.getUser()
  	// Get the user's name (login ID)
  	def currentUserName = currentUser.getName()

  	//Get today's date in Essbase format
  	Date currentDate = new Date()
  	def myDateString = currentDate.format("dd MMM yyyy, HH:mm:ss")

	// Get the cube
  	Cube cube = operation.application.getCube("GLOMIT")

  	// Create a DataGridBuilder
  	//DataGridBuilder builder = cube.dataGridBuilder("MM/DD/YYYY")
  	def builder = cube.dataGridBuilder("MM/DD/YYYY", SYSTEM_USER)
  	//def builder = cube.dataGridBuilder("MM/DD/YYYY")

  	// Define the POV
  	builder.addPov(currScenario.toString(),currYear.toString(),currPeriod.toString(),currLoB.toString(),"Working","Periodic","No Account","No Currency","No Project","No Center","No Location","No Data Source","No Intercompany","No Client","SP99999")

  	// Define columns
  	builder.addColumn(currEntity.toString())

  	// Add all current entity to the row, assigning the value 1 = open
  	builder.addRow([currRepLine.toString()],[1])

	//println("Last Action: " + lastAction)
	//println("Current user: " + currentUserName)
	//println("Current date: " + myDateString)
    if (currRepLine == "Data Status KPI")
    {
    	builder.addRow(["Last Action KPI"],["Global open"])
  		builder.addRow(["Last Action By KPI"],[currentUserName])
  		builder.addRow(["Last Action Date KPI"],[myDateString])
    }
  	else
    {
    	builder.addRow(["Last Action Base Data"],["Global open"])
  		builder.addRow(["Last Action By Base Data"],[currentUserName])
  		builder.addRow(["Last Action Date Base Data"],[myDateString])
	}
    
  	// Build and save the grid
  	DataGridBuilder.Status status = new DataGridBuilder.Status()
  	builder.build(status).withCloseable { grid ->
    	// Save the data to the cube
    	cube.saveGrid(grid)

		// Optional: small retry in case another SYSTEM_USER write is finishing up
        /*
        def attempts = 0
        while (attempts < 10) {
			try {
                println "Save attempt ${attempts}"
				cube.saveGrid(grid)
            } catch (Exception ex) {
                attempts++
                println "Save attempt ${attempts} failed: ${ex.message}"
				sleep(10000) // back-off 300 ms
    	    }
            }
        
        */
	  }
}

def BF_EncodePath(String path)
{
	path = path.replace("\\", "%5C")
	path = path.replace("/", "%2F")
	path = path.replace(" ", "%20")
	return path
}

def BF_PeriodNumberStringFromString(currPeriod)
{
	def periodString = ""
	
	switch (currPeriod.toString().toUpperCase())
    {
    	case ("JAN"):
        	periodString = "01"
            break
    	case ("FEB"):
        	periodString = "02"
            break
    	case ("MAR"):
        	periodString = "03"
            break
    	case ("APR"):
        	periodString = "04"
            break
    	case ("MAY"):
        	periodString = "05"
            break
    	case ("JUN"):
        	periodString = "06"
            break
    	case ("JUL"):
        	periodString = "07"
            break
    	case ("AUG"):
        	periodString = "08"
            break
    	case ("SEP"):
        	periodString = "09"
            break
    	case ("OCT"):
        	periodString = "10"
            break
    	case ("NOV"):
        	periodString = "11"
            break
    	case ("DEC"):
        	periodString = "12"
            break
        default:
        	periodString = "00"
            break
    }
	
    return periodString
}

def BF_TranslateToEUR(CurrentEntities,CurrentLOBs,CurrentPeriod,CurrentScenario,CurrentVersion,CurrentYear)
{
	//************************************************************************************************************************
    // Translate to EUR
    //************************************************************************************************************************
 
    println("******************************************************************************")
    println("Run BSO translation to EUR")
 
    String planningAppName = "GLOMIT"

    HttpResponse<String> ruleResponse = operation.application.getConnection("RestAPI")
        .post("/HyperionPlanning/rest/v3/applications/${planningAppName}/jobs")
        .header("Content-Type", "application/json")
        .body(json([
            "jobType":"Rules",
            "jobName":"GENERAL_CurrencyConversion_POVEntitiesLOBs_EUR",
            "parameters":[
                "CurrentEntities":"$CurrentEntities",
                "CurrentLOBs":"$CurrentLOBs",
                "CurrentPeriod":"$CurrentPeriod",
                "CurrentScenario":"$CurrentScenario",
                "CurrentVersion":"$CurrentVersion",
                "CurrentYear":"$CurrentYear"
            ]
        ])).asString()
 
    def ruleStatus = awaitCompletionPlanning(ruleResponse, "RestAPI", planningAppName)
    //def ruleStatus = awaitCompletion(ruleResponse, "RestAPI")

	println("Translation rule EUR result: " + ruleStatus)
 
    if (ruleStatus != 0) {
        throwVetoException("Translation to EUR failed.")
    }
}

def BF_TranslateToUSD(CurrentEntities,CurrentPeriod,CurrentScenario,CurrentVersion,CurrentYear)
{
	//************************************************************************************************************************
    // Translate to USD
    //************************************************************************************************************************
 
    println("******************************************************************************")
    println("Run BSO translation to USD")
 
    String planningAppName = "GLOMIT"
 
    HttpResponse<String> ruleResponse = operation.application.getConnection("RestAPI")
        .post("/HyperionPlanning/rest/v3/applications/${planningAppName}/jobs")
        .header("Content-Type", "application/json")
        .body(json([
            "jobType":"Rules",
            "jobName":"GENERAL_CurrencyConversion_UDA TRANS_TO_USD",
            "parameters":[
                "CurrentEntities":"$CurrentEntities",
                "CurrentPeriod":"$CurrentPeriod",
                "CurrentScenario":"$CurrentScenario",
                "CurrentVersion":"$CurrentVersion",
                "CurrentYear":"$CurrentYear"
            ]
        ])).asString()
 
    def ruleStatus = awaitCompletionPlanning(ruleResponse, "RestAPI", planningAppName)
    //def ruleStatus = awaitCompletion(ruleResponse, "RestAPI")
    println("Translation rule USD result: " + ruleStatus)
 
    if (ruleStatus != 0) {
        throwVetoException("Translation to USD failed.")
    }
}

def awaitCompletionPlanning(HttpResponse<String> jsonResponse, String connectionName, String appName) {
    final int IN_PROGRESS = -1
    final int MAX_WAIT_MS = 5 * 60 * 1000   // 5 minutes safety cap (adjust)
    final int MAX_DELAY_MS = 2000           // cap backoff
    long waited = 0

    int httpStatus = jsonResponse.status
    if (!(200..299).contains(httpStatus) && httpStatus != 202) {
        throwVetoException("Error occurred: HTTP $httpStatus - ${jsonResponse.statusText}")
    }

    String body = jsonResponse.body
    String jobId = null
    Integer status = null

    // Try to read jobId and status from body (if any)
    if (body != null && body.trim()) {
        try {
            ReadContext ctx = JsonPath.parse(body)
            // jobId usually present in Planning job response
            def j = ctx.read('$.jobId')
            if (j != null) jobId = String.valueOf(j)
            // status may or may not be present; if missing we’ll poll anyway
            def s = ctx.read('$.status')
            if (s != null) status = (s instanceof Number) ? ((Number) s).intValue() : Integer.valueOf(String.valueOf(s))
        } catch (Exception ignore) {
            // Body may not be JSON or fields absent – we’ll fall back to header/polling
        }
    }

    // If jobId not found in body, try Location header (common with 202 Accepted)
    if (!jobId) {
        String loc = jsonResponse.headers?.getFirst("Location")
        if (loc) {
            // Expect: .../applications/{app}/jobs/{jobId}
            def m = (loc =~ /\/jobs\/(\d+)(?:\b|$)/)
            if (m.find()) jobId = m.group(1)
        }
    }

    if (!jobId) {
        throwVetoException("Could not determine Planning jobId from response body or Location header.")
    }

    // If initial status is not provided, assume IN_PROGRESS to start polling
    if (status == null) status = IN_PROGRESS

    // Exponential backoff from 100ms up to MAX_DELAY_MS
    for (long delay = 100; status == IN_PROGRESS; delay = Math.min(MAX_DELAY_MS, delay * 2)) {
        sleep(delay)
        waited += delay
        status = getPlanningJobStatus(connectionName, appName, jobId)
        if (waited > MAX_WAIT_MS) {
            throwVetoException("Timeout waiting for Planning job $jobId (last status=$status).")
        }
    }

    return status
}

// Helper to poll Planning job status
int getPlanningJobStatus(String connectionName, String appName, String jobId) {
    HttpResponse<String> r = operation.application.getConnection(connectionName)
        .get("/HyperionPlanning/rest/v3/applications/${appName}/jobs/${jobId}")
        .asString()

    if (!(200..299).contains(r.status)) {
        throwVetoException("Failed to retrieve job status for jobId=${jobId}: HTTP ${r.status} ${r.statusText}")
    }

    def body = r.body
    if (!body || !body.trim()) {
        // Unlikely, but be resilient: treat as still in progress
        return -1
    }

    try {
        ReadContext ctx = JsonPath.parse(body)
        def s = ctx.read('$.status')
        return (s instanceof Number) ? ((Number) s).intValue() : Integer.valueOf(String.valueOf(s))
    } catch (Exception e) {
        // If parsing fails, keep waiting instead of crashing
        return -1
    }
}
