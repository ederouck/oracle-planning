/*
================================================================================
  DiagnoseReadOnly — diagnostisch script voor read-only cellen in GLOMIT
  
  Gebruik: lanceer dit script via een action menu of rechtstreeks in
           Calculation Manager op de cel die read-only is voor de gebruiker.
           De gebruiker moet de cel geselecteerd hebben (rechter muisknop).
  
  Output: volledige diagnose in het job-logboek (EPM Cloud job console).
  
  Bart & Partners — 2026
================================================================================
*/

// ── 1. Lees celcoördinaten uit de geselecteerde cel ──────────────────────────
def currentScenario = ""
def currentYear     = ""
def currentPeriod   = ""
def currentEntity   = ""
def currentLoB      = ""
def currentRepLine  = ""
def currentView     = ""
def currentVersion  = ""
def currentAccount  = ""
def currentLocation = ""
def currentCenter   = ""
def currentClient   = ""
def currentProject  = ""
def currentDataSource = ""
def currentIC       = ""
def currentCurrency = ""

operation.grid.dataCellIterator.each { cell ->
    if (cell.isSelected()) {
        currentScenario   = cell.getMemberName("Scenario")   ?: ""
        currentYear       = cell.getMemberName("Years")      ?: ""
        currentPeriod     = cell.getMemberName("Period")     ?: ""
        currentEntity     = cell.getMemberName("Entity")     ?: ""
        currentLoB        = cell.getMemberName("LoB")        ?: ""
        currentRepLine    = cell.getMemberName("RepLine")    ?: ""
        currentView       = cell.getMemberName("View")       ?: ""
        currentVersion    = cell.getMemberName("Version")    ?: ""
        currentAccount    = cell.getMemberName("Account")    ?: ""
        currentCurrency   = cell.getMemberName("Currency")   ?: ""

        // Optioneel aanwezig afhankelijk van formulier
        try { currentLocation   = cell.getMemberName("Location")    ?: "" } catch(e) {}
        try { currentCenter     = cell.getMemberName("Center")      ?: "" } catch(e) {}
        try { currentClient     = cell.getMemberName("Client")      ?: "" } catch(e) {}
        try { currentProject    = cell.getMemberName("Project")     ?: "" } catch(e) {}
        try { currentDataSource = cell.getMemberName("Data Source") ?: "" } catch(e) {}
        try { currentIC         = cell.getMemberName("Intercompany")?: "" } catch(e) {}
    }
}

// ── Helper: separator ────────────────────────────────────────────────────────
def sep = { String title ->
    println ""
    println "=" * 70
    println "  ${title}"
    println "=" * 70
}

// ── Startmelding ─────────────────────────────────────────────────────────────
sep("DIAGNOSE READ-ONLY CEL")
println ""
println "Geselecteerde cel:"
println "  Scenario    : ${currentScenario}"
println "  Year        : ${currentYear}"
println "  Period      : ${currentPeriod}"
println "  Entity      : ${currentEntity}"
println "  LoB         : ${currentLoB}"
println "  RepLine     : ${currentRepLine}"
println "  Account     : ${currentAccount}"
println "  Version     : ${currentVersion}"
println "  View        : ${currentView}"
println "  Currency    : ${currentCurrency}"
if (currentLocation)   println "  Location    : ${currentLocation}"
if (currentCenter)     println "  Center      : ${currentCenter}"
if (currentClient)     println "  Client      : ${currentClient}"
if (currentProject)    println "  Project     : ${currentProject}"
if (currentDataSource) println "  Data Source : ${currentDataSource}"
if (currentIC)         println "  Intercompany: ${currentIC}"

// ── 2. Gebruikersinfo ────────────────────────────────────────────────────────
sep("LAAG 1 — GEBRUIKER")
User currentUser = operation.getUser()
def userName = currentUser.getName()
println "Gebruiker      : ${userName}"
println "Weergavenaam   : ${currentUser.getFirstName()} ${currentUser.getLastName()}"
println ""
println "Groepen:"
currentUser.getGroups().each { g -> println "  - ${g}" }
println ""
println "Rollen:"
currentUser.getRoles().each { r -> println "  - ${r}" }

// ── 3. Formulier read-only attribuut ─────────────────────────────────────────
sep("LAAG 2 — FORMULIER (readOnly attribuut)")
// Dit attribuut is zichtbaar als het formulier readOnly="true" heeft.
// In een Groovy-regel kun je dit niet rechtstreeks opvragen, maar het
// feit dat de regel überhaupt draait wijst erop dat het formulier
// niet volledig read-only is (anders zou de action menu niet beschikbaar zijn).
println "Als dit script draait via een action menu op het formulier,"
println "is het formulier-niveau readOnly=true NIET de oorzaak."
println "(Een volledig read-only formulier toont geen action menus.)"
println ""
println "Controleer in de form XML: readOnly=\"true\" attribuut op <form> tag."

// ── 4. Data Status controle ──────────────────────────────────────────────────
sep("LAAG 3 — DATA STATUS (workflow)")

if (!currentEntity || !currentLoB || !currentRepLine) {
    println "SKIP: Entity, LoB of RepLine niet beschikbaar in celselectie."
} else {
    def strCubeName = "GLOMIT"
    Cube statusCube = operation.application.getCube(strCubeName)
    def statusBuilder = statusCube.flexibleDataGridDefinitionBuilder()
    def rawStatus = 0

    // Bepaal welke RepLine we controleren
    def checkRepLine = currentRepLine
    if (currentRepLine != "Data Status Base Data" && currentRepLine != "Data Status KPI") {
        checkRepLine = "Data Status Base Data"
        println "RepLine '${currentRepLine}' is geen statuslijn → controleer op 'Data Status Base Data'"
    }

    statusBuilder.setPovDimensions(
        "Scenario","Years","Period","Version","View","Account",
        "Entity","Project","Center","Location","Data Source",
        "Intercompany","Client","Spare","Currency")
    statusBuilder.setPov(
        currentScenario, currentYear, currentPeriod,
        "Working","Periodic","No Account",
        currentEntity,
        "No Project","No Center","No Location","No Data Source",
        "No Intercompany","No Client","SP99999","No Currency")
    statusBuilder.setColumnDimensions("RepLine")
    statusBuilder.addColumn(checkRepLine)
    statusBuilder.setRowDimensions("LoB")
    statusBuilder.addRow(currentLoB)

    statusCube.loadGrid(statusBuilder.build(), false).withCloseable { grid ->
        def myCell = grid.dataCellIterator().next()
        rawStatus = myCell.getData()
    }

    def statusLabel
    switch (rawStatus?.toInteger()) {
        case 1:  statusLabel = "Open (1) — NIET de oorzaak"; break
        case 2:  statusLabel = "Submitted (2) — kan schrijven BLOKKEREN voor Accountants"; break
        case 3:  statusLabel = "SubmittedOverride (3) — kan schrijven BLOKKEREN voor Accountants"; break
        case 4:  statusLabel = "LOCKED (4) ← MEEST WAARSCHIJNLIJKE OORZAAK van read-only"; break
        default: statusLabel = "NotStarted / leeg (${rawStatus}) — kan schrijven blokkeren (cel nog niet geïnitialiseerd)"; break
    }

    println "Entity         : ${currentEntity}"
    println "LoB            : ${currentLoB}"
    println "Periode        : ${currentPeriod} / ${currentYear}"
    println "Status RepLine : ${checkRepLine}"
    println "Ruwe waarde    : ${rawStatus}"
    println "Status         : ${statusLabel}"
    println ""
    if (rawStatus?.toInteger() == 4) {
        println ">>> CONCLUSIE: De Entity/LoB-combinatie is VERGRENDELD."
        println "    Alleen een Administrator of BU Controller kan via"
        println "    'Unlock Entity/LoB' de status terugzetten."
    }
}

// ── 5. Member-niveau toegang (ACL) ──────────────────────────────────────────
sep("LAAG 4 — MEMBER SECURITY (ACL op dimensions)")
println "Controleer of de gebruiker Write-toegang heeft op alle members"
println "die in de cel aanwezig zijn. In het bijzonder:"
println ""
println "  Scenario '${currentScenario}':"
println "    → Heeft de gebruiker Write op dit scenario?"
println "    → Controleer via Administration > Manage > Security > Scenario"
println ""
println "  Entity '${currentEntity}':"  
println "    → Heeft de gebruiker Write op deze entiteit?"
println "    → Controleer via Administration > Manage > Security > Entity"
println ""
println "  LoB '${currentLoB}':"
println "    → Heeft de gebruiker Write op deze LoB?"
println "    → Controleer via Administration > Manage > Security > LoB"
println ""
println "Indien Read of geen toegang op één van deze members:"
println "→ Cel is read-only ONGEACHT data status of cell-level security."

// Probeer scenario-toegang te detecteren via de user grants API
try {
    def scenarioAccess = operation.application.getUserGrantedScenarios(currentUser)
    if (scenarioAccess != null) {
        println ""
        println "Scenario-toegang voor ${userName}:"
        scenarioAccess.each { s -> println "  ${s}" }
    }
} catch (Exception ex) {
    println "(Scenario-grants niet opvraagbaar via API in deze context: ${ex.message})"
}

// ── 6. Cell-Level Security evaluatie ────────────────────────────────────────
sep("LAAG 5 — CELL-LEVEL SECURITY")
println "Cell-level security in GLOMIT heeft 81 actieve regels."
println "De meest relevante voor deze cel zijn:"
println ""
println "Controlleer welke CLS-regels van toepassing zijn op:"
println "  Entity  : ${currentEntity}"
println "  LoB     : ${currentLoB}"
println "  Scenario: ${currentScenario}"
println ""
println "Kritieke CLS-regels om te controleren (op basis van configuratie):"
println ""
println "  1. 'Deny Read'-regels op LoB-dimensie:"
println "     → 168 Deny Read-regels aanwezig, verdeeld over LoB en Scenario"
println "     → Als de gebruikersgroep van '${userName}' voorkomt in een"
println "       Deny Read-regel die Entity '${currentEntity}' + LoB '${currentLoB}'"
println "       dekt, ziet de gebruiker de cel als read-only (of zelfs leeg)"
println ""
println "  2. 'Deny Write'-regels:"
println "     → 18 Deny Write-regels aanwezig"  
println "     → Cel is zichtbaar maar niet bewerkbaar"
println ""
println "Hoe te controleren in EPM Cloud:"
println "  Administration → Manage → Cell-Level Security"
println "  Filter op de groepen van gebruiker '${userName}'"
println "  Controleer of Entity/LoB-combinatie gedekt wordt"

// ── 7. ASO leaf-member controle ──────────────────────────────────────────────
sep("LAAG 6 — ASO CUBE: PARENT MEMBERS")
println "De GLOMIT-cube is een ASO-cube."
println "In ASO zijn ALLEEN leaf-members (level 0) van ALLE dimensies schrijfbaar."
println "Als één member in de cel een parent/aggregaat is, is de cel read-only."
println ""
println "Verdachte dimensies voor niet-leaf members:"
println ""

def checkParent = { String dimName, String memberName ->
    if (!memberName || memberName.isEmpty()) return
    try {
        Cube c = operation.application.getCube("GLOMIT")
        def dim = c.getDimension(dimName)
        if (dim) {
            def mbr = dim.getMember(memberName)
            if (mbr) {
                def level = mbr.getLevelNumber()
                def children = mbr.getChildren()?.size() ?: 0
                def leafStatus = (level == 0 && children == 0) ? 
                    "LEAF (schrijfbaar)" : 
                    "PARENT/AGGREGAAT (level=${level}, children=${children}) ← READ-ONLY"
                println "  ${dimName.padRight(15)}: ${memberName.padRight(25)} → ${leafStatus}"
            }
        }
    } catch (Exception ex) {
        println "  ${dimName.padRight(15)}: ${memberName.padRight(25)} → (niet opvraagbaar: ${ex.message})"
    }
}

checkParent("Entity",      currentEntity)
checkParent("LoB",         currentLoB)
checkParent("Account",     currentAccount)
checkParent("Location",    currentLocation)
checkParent("Center",      currentCenter)
checkParent("Client",      currentClient)
checkParent("Project",     currentProject)
checkParent("Data Source", currentDataSource)

// ── 8. Samenvatting ──────────────────────────────────────────────────────────
sep("SAMENVATTING — VOLGORDES VAN ONDERZOEK")
println ""
println "Stap 1  DATA STATUS"
println "        Is de status van Entity '${currentEntity}' / LoB '${currentLoB}'"
println "        gelijk aan LOCKED (4)? → Zie Laag 3 hierboven"
println ""
println "Stap 2  MEMBER ACL"
println "        Heeft '${userName}' Write-toegang op Scenario, Entity én LoB?"
println "        → Zie Laag 4 hierboven"
println ""
println "Stap 3  CELL-LEVEL SECURITY"
println "        Zit '${userName}' in een groep met Deny Read/Write op deze"
println "        Entity/LoB-combinatie?"
println "        → Zie Laag 5 hierboven"
println ""
println "Stap 4  ASO PARENT MEMBER"
println "        Zijn alle members in de cel leaf-members (level 0)?"
println "        → Zie Laag 6 hierboven"
println ""
println "Stap 5  FORMULIER"
println "        Is het formulier zelf readOnly=\"true\"?"
println "        → Controleer form XML"
println ""
println "Gebruiker : ${userName}"
println "Cel       : ${currentEntity} / ${currentLoB} / ${currentRepLine} / ${currentPeriod} ${currentYear}"
println ""
println "Einde diagnose."