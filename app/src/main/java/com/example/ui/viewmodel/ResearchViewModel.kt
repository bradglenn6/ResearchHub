package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Paper
import com.example.data.model.Workspace
import com.example.data.model.FundingOpportunity
import com.example.data.repository.ResearchRepository
import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID

class ResearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ResearchRepository(application)

    // Core Flows from Room Database
    val allPapers: StateFlow<List<Paper>> = repository.allPapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkspaces: StateFlow<List<Workspace>> = repository.allWorkspaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFunding: StateFlow<List<FundingOpportunity>> = repository.allFunding
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Workspace filter (0 means "All Workspaces")
    private val _selectedWorkspaceId = MutableStateFlow<Long>(0)
    val selectedWorkspaceId: StateFlow<Long> = _selectedWorkspaceId.asStateFlow()

    // Combined Flow: Active Paper List based on selected Workspace filter
    val filteredPapers: StateFlow<List<Paper>> = combine(allPapers, _selectedWorkspaceId) { papers, workspaceId ->
        if (workspaceId == 0L) {
            papers
        } else {
            papers.filter { it.workspaceId == workspaceId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Details / Inspector state
    private val _selectedPaperId = MutableStateFlow<Long?>(null)
    val selectedPaperId: StateFlow<Long?> = _selectedPaperId.asStateFlow()

    val selectedPaper: StateFlow<Paper?> = combine(allPapers, _selectedPaperId) { papers, id ->
        if (id == null) null else papers.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Feedback flags
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    private val _syncStatus = MutableStateFlow("Synced locally. Offline mode active.")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _apiKeyConfigured = MutableStateFlow(GeminiClient.hasValidApiKey())
    val apiKeyConfigured: StateFlow<Boolean> = _apiKeyConfigured.asStateFlow()

    // Dashboard customization preferences
    private val _showPinned = MutableStateFlow(true)
    val showPinned: StateFlow<Boolean> = _showPinned.asStateFlow()

    private val _showRecent = MutableStateFlow(true)
    val showRecent: StateFlow<Boolean> = _showRecent.asStateFlow()

    private val _showTelemetry = MutableStateFlow(true)
    val showTelemetry: StateFlow<Boolean> = _showTelemetry.asStateFlow()

    private val _showLab = MutableStateFlow(true)
    val showLab: StateFlow<Boolean> = _showLab.asStateFlow()

    // Research target goals
    private val _targetPaperCount = MutableStateFlow(5)
    val targetPaperCount: StateFlow<Int> = _targetPaperCount.asStateFlow()

    private val _currentResearchGoal = MutableStateFlow("Publish Splicing Factor AI Analysis")
    val currentResearchGoal: StateFlow<String> = _currentResearchGoal.asStateFlow()

    // Mobile Lab Interactive States
    private val _selectedLabSubject = MutableStateFlow("Biomedicine")
    val selectedLabSubject: StateFlow<String> = _selectedLabSubject.asStateFlow()

    // Lab Sliders/Parameters
    // Biomedical
    private val _centrifugeRpm = MutableStateFlow(8000f)
    val centrifugeRpm: StateFlow<Float> = _centrifugeRpm.asStateFlow()
    private val _cryoTemp = MutableStateFlow(-196.2f)
    val cryoTemp: StateFlow<Float> = _cryoTemp.asStateFlow()
    
    // Computer Science
    private val _gpuPowerLimit = MutableStateFlow(320)
    val gpuPowerLimit: StateFlow<Int> = _gpuPowerLimit.asStateFlow()
    private val _contextLength = MutableStateFlow(128000)
    val contextLength: StateFlow<Int> = _contextLength.asStateFlow()

    // Chemistry
    private val _titrationPh = MutableStateFlow(7.0f)
    val titrationPh: StateFlow<Float> = _titrationPh.asStateFlow()
    private val _wavelengthNm = MutableStateFlow(520)
    val wavelengthNm: StateFlow<Int> = _wavelengthNm.asStateFlow()

    // Quantum Physics
    private val _interferometerAlign = MutableStateFlow(98.5f)
    val interferometerAlign: StateFlow<Float> = _interferometerAlign.asStateFlow()
    private val _laserPowerMw = MutableStateFlow(500)
    val laserPowerMw: StateFlow<Int> = _laserPowerMw.asStateFlow()

    // Lab Analysis Status
    private val _labAnalysisProgress = MutableStateFlow(0f)
    val labAnalysisProgress: StateFlow<Float> = _labAnalysisProgress.asStateFlow()

    private val _labAnalysisReport = MutableStateFlow<String?>(null)
    val labAnalysisReport: StateFlow<String?> = _labAnalysisReport.asStateFlow()

    private val _isLabAnalyzing = MutableStateFlow(false)
    val isLabAnalyzing: StateFlow<Boolean> = _isLabAnalyzing.asStateFlow()

    // Active Tab in the main UI Layout (0=Dashboard, 1=Library, 2=Browser Capture, 3=Workspaces, 4=AI Grants, 5=Security Settings)
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    init {
        // Trigger seeding if database is empty on launch
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            _apiKeyConfigured.value = GeminiClient.hasValidApiKey()
        }
    }

    fun selectTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun selectWorkspace(workspaceId: Long) {
        _selectedWorkspaceId.value = workspaceId
        _selectedPaperId.value = null // reset selection when category changes
    }

    fun selectPaper(paperId: Long?) {
        _selectedPaperId.value = paperId
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    /**
     * Standard local manual research paper registry creation (triggers AI validation)
     */
    fun registerNewPaper(
        title: String,
        authors: String,
        journal: String,
        year: String,
        textContent: String,
        workspaceId: Long,
        category: String,
        url: String = "",
        doi: String = ""
    ) {
        if (title.isBlank() || textContent.isBlank()) {
            _toastMessage.value = "Required: Title and Text Content"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _toastMessage.value = "Invoking Gemini AI for Peer Validation and Citations..."
                val newId = repository.analyzeAndSavePaper(
                    title = title,
                    authors = authors,
                    journal = journal,
                    year = year,
                    textContent = textContent,
                    workspaceId = workspaceId,
                    category = category,
                    url = url,
                    doi = doi
                )
                _toastMessage.value = "Succesfully validated & saved paper."
                _selectedPaperId.value = newId // Auto load detail view
                _activeTab.value = 0 // Switch to library
            } catch (e: Exception) {
                Log.e("ViewModel", "Paper register failed", e)
                _toastMessage.value = "Error during AI generation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Capture / Simulates a browser extension scrap target
     */
    fun captureFromExtension(
        title: String,
        authors: String,
        journal: String,
        year: String,
        textContent: String,
        originDoi: String,
        originUrl: String,
        targetWorkspaceId: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _toastMessage.value = "Browser Companion: Capturing reference metadata..."
                // Synthesize category
                val category = when {
                    title.contains("Bio", true) || title.contains("Cancer", true) -> "Biomedicine"
                    title.contains("Neural", true) || title.contains("Language", true) || title.contains("Attention", true) -> "Computer Science"
                    else -> "Interdisciplinary"
                }

                repository.analyzeAndSavePaper(
                    title = title,
                    authors = authors,
                    journal = journal,
                    year = year,
                    textContent = textContent,
                    workspaceId = targetWorkspaceId,
                    category = category,
                    url = originUrl,
                    doi = originDoi
                )
                _toastMessage.value = "Reference parsed into Workspace!"
                _activeTab.value = 0 // Transition back to view
            } catch (e: Exception) {
                _toastMessage.value = "Extension import failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Creates a new collaborative workspace
     */
    fun createWorkspace(name: String, description: String, teamMembersSeparated: String) {
        if (name.isBlank()) {
            _toastMessage.value = "Workspace name cannot be blank."
            return
        }
        viewModelScope.launch {
            val workspace = Workspace(
                name = name,
                description = description,
                owner = "Brad G (You)",
                teamMembers = "Me, $teamMembersSeparated"
            )
            repository.createWorkspace(workspace)
            _toastMessage.value = "Shared workspace '$name' created successfully."
        }
    }

    /**
     * Erases a shared workspace
     */
    fun deleteWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            repository.deleteWorkspace(workspace)
            _toastMessage.value = "Workspace folder '${workspace.name}' deleted."
        }
    }

    /**
     * Erases a paper
     */
    fun deletePaper(paper: Paper) {
        viewModelScope.launch {
            repository.deletePaperById(paper.id)
            _selectedPaperId.value = null
            _toastMessage.value = "Document deleted."
        }
    }

    /**
     * Toggles a paper's pinned state in the database
     */
    fun togglePinPaper(paper: Paper) {
        viewModelScope.launch {
            val updated = paper.copy(isPinned = !paper.isPinned)
            repository.updatePaper(updated)
            if (updated.isPinned) {
                _toastMessage.value = "Paper pinned to your Dashboard."
            } else {
                _toastMessage.value = "Paper removed from Dashboard pins."
            }
        }
    }

    fun setPinnedVisible(visible: Boolean) { _showPinned.value = visible }
    fun setRecentVisible(visible: Boolean) { _showRecent.value = visible }
    fun setTelemetryVisible(visible: Boolean) { _showTelemetry.value = visible }
    fun setLabVisible(visible: Boolean) { _showLab.value = visible }
    
    fun updateTargetCount(count: Int) { _targetPaperCount.value = count }
    fun updateResearchGoal(goal: String) { _currentResearchGoal.value = goal }
    
    fun selectLabSubject(subject: String) {
        _selectedLabSubject.value = subject
        _labAnalysisReport.value = null
        _labAnalysisProgress.value = 0f
    }
    
    fun updateCentrifugeRpm(rpm: Float) { _centrifugeRpm.value = rpm }
    fun updateCryoTemp(temp: Float) { _cryoTemp.value = temp }

    fun updateGpuPowerLimit(power: Int) { _gpuPowerLimit.value = power }
    fun updateContextLength(tokens: Int) { _contextLength.value = tokens }

    fun updateTitrationPh(ph: Float) { _titrationPh.value = ph }
    fun updateWavelengthNm(nm: Int) { _wavelengthNm.value = nm }

    fun updateInterferometerAlign(align: Float) { _interferometerAlign.value = align }
    fun updateLaserPowerMw(power: Int) { _laserPowerMw.value = power }

    fun runLabAnalysis() {
        if (_isLabAnalyzing.value) return
        viewModelScope.launch {
            _isLabAnalyzing.value = true
            _labAnalysisReport.value = null
            _labAnalysisProgress.value = 0f
            
            for (i in 1..10) {
                delay(120)
                _labAnalysisProgress.value = i / 10f
            }
            
            val subject = _selectedLabSubject.value
            val report = when (subject) {
                "Biomedicine" -> """
                    --- BIOMEDICAL ANALYSIS SECURE LOG ---
                    [STATUS] DNA/RNA Sequencing Complete
                    [EQUIPMENT] Centrifuge: ${_centrifugeRpm.value.toInt()} RPM | Sub-Zero Cryo: ${_cryoTemp.value}°C
                    [RESULTS] Resolved 98.4% sequence clarity. Separated genomic oncology profiles with perfect visual bands. RNA Splicing isolated a target anomaly matching somatic SF3B1 mutant signatures.
                    [CONCLUSION] High target feasibility of small-molecule spliceosome therapeutics.
                """.trimIndent()
                
                "Computer Science" -> """
                    --- DEEP LEARNING COMPUTE DIAGNOSTICS ---
                    [STATUS] CUDA Fine-tuning Validation Solved
                    [EQUIPMENT] GPU Power: ${_gpuPowerLimit.value}W | Prompt Budget: ${_contextLength.value} context
                    [RESULTS] Multi-head self attention weights analyzed. Active context window simulated to ${_contextLength.value} steps. Cascade logic error deferred beyond step 15. Throughput: 182.5 tokens/second.
                    [CONCLUSION] Local training logs indicate reasoning stability under pseudocode constraints.
                """.trimIndent()
                
                "Chemistry" -> """
                    --- MOLECULAR TRANS-ABSORBANCE SCAN ---
                    [STATUS] UV-Vis Resonance Resolved
                    [EQUIPMENT] pH Meter: ${String.format("%.2f", _titrationPh.value)} | Spectrometer: ${_wavelengthNm.value} nm
                    [RESULTS] Captured maximum absorbance resonance at spectrum ${_wavelengthNm.value} nm. Concentration pH level is state-verified as ${if (_titrationPh.value < 6.5f) "Acidic Solution" else if (_titrationPh.value > 7.5f) "Alkaline Buffer" else "Neutral Buffer"}. Mass-spec noise variance is optimized below 0.02%.
                    [CONCLUSION] Molecular synthesis and purity validated. Buffer complies with trial standards.
                """.trimIndent()
                
                "Quantum Physics" -> """
                    --- SCHRODINGER INTERFEROMETRY REPORT ---
                    [STATUS] Quantum Coherence Check Perfect
                    [EQUIPMENT] Optical Alignment: ${String.format("%.1f", _interferometerAlign.value)}% | Laser Power: ${_laserPowerMw.value} mW
                    [RESULTS] Maintained solid quantum superposition wave state for ${String.format("%.2f", _interferometerAlign.value * 1.5)} microseconds. Cryostat superconductor coils holding at 1.45 Teslas. Interference fringes tracked with 99.8% geometric clarity.
                    [CONCLUSION] Interferometer path length calibrated. Hyper-entangled state generated successfully.
                """.trimIndent()
                else -> "Custom subject log completed successfully."
            }
            
            _labAnalysisReport.value = report
            _isLabAnalyzing.value = false
            _toastMessage.value = "$subject Lab Diagnostics Completed successfully."
        }
    }

    /**
     * Executes cloud sync simulation
     */
    fun triggerCloudSync() {
        if (_syncing.value) return
        viewModelScope.launch {
            _syncing.value = true
            _syncStatus.value = "Initiating multi-device synchronization..."
            try {
                val report = repository.syncDatabaseToCloud()
                _syncStatus.value = "Last synced: Just Now (Cloud Safe)"
                _toastMessage.value = "Cross-device sync completed successfully."
            } catch (e: Exception) {
                _syncStatus.value = "Sync fail. Working in Offline Mode."
            } finally {
                _syncing.value = false
            }
        }
    }

    /**
     * Finding grants based on user project topics
     */
    fun searchFundingOpportunities(keywords: String) {
        if (keywords.isBlank()) {
            _toastMessage.value = "Please insert search topics or keywords"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _toastMessage.value = "Dean AI: Inspecting grant registers for '$keywords'..."
            try {
                repository.searchAndSaveFunding(keywords)
                _toastMessage.value = "Found new matching opportunities!"
            } catch (e: Exception) {
                _toastMessage.value = "Failed fetching opportunities: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
