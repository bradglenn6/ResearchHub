package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Paper
import com.example.ui.viewmodel.ResearchViewModel
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.AmberWarn
import com.example.ui.theme.CoralAlert

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: ResearchViewModel, modifier: Modifier = Modifier) {
    val papers by viewModel.allPapers.collectAsStateWithLifecycle()
    val workspaces by viewModel.allWorkspaces.collectAsStateWithLifecycle()
    
    // Customization states
    val showPinned by viewModel.showPinned.collectAsStateWithLifecycle()
    val showRecent by viewModel.showRecent.collectAsStateWithLifecycle()
    val showTelemetry by viewModel.showTelemetry.collectAsStateWithLifecycle()
    val showLab by viewModel.showLab.collectAsStateWithLifecycle()
    
    // Goals
    val targetCount by viewModel.targetPaperCount.collectAsStateWithLifecycle()
    val researchGoal by viewModel.currentResearchGoal.collectAsStateWithLifecycle()
    
    // Lab States
    val selectedSubject by viewModel.selectedLabSubject.collectAsStateWithLifecycle()
    
    // Biomedical
    val centrifugeRpm by viewModel.centrifugeRpm.collectAsStateWithLifecycle()
    val cryoTemp by viewModel.cryoTemp.collectAsStateWithLifecycle()
    
    // CS
    val gpuPowerLimit by viewModel.gpuPowerLimit.collectAsStateWithLifecycle()
    val contextLength by viewModel.contextLength.collectAsStateWithLifecycle()
    
    // Chemistry
    val titrationPh by viewModel.titrationPh.collectAsStateWithLifecycle()
    val wavelengthNm by viewModel.wavelengthNm.collectAsStateWithLifecycle()
    
    // Quantum
    val interferometerAlign by viewModel.interferometerAlign.collectAsStateWithLifecycle()
    val laserPowerMw by viewModel.laserPowerMw.collectAsStateWithLifecycle()
    
    // Lab Diagnostics
    val labProgress by viewModel.labAnalysisProgress.collectAsStateWithLifecycle()
    val labReport by viewModel.labAnalysisReport.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isLabAnalyzing.collectAsStateWithLifecycle()
    
    var isConfiguratiorExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .verticalScroll(scrollState)
    ) {
        // Upper Title Header Matching Bold Typography Subheadings
        Text(
            text = "RESEARCH INTEL HUB",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customizable telemetry metrics and subject laboratory instruments.",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            IconButton(
                onClick = { isConfiguratiorExpanded = !isConfiguratiorExpanded },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isConfiguratiorExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("dashboard_config_toggle")
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Customize Dashboard Widgets",
                    tint = if (isConfiguratiorExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Customizable widget configuration block
        AnimatedVisibility(
            visible = isConfiguratiorExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.SettingsSuggest, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "DASHBOARD CONFIGURATOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Switches for showing elements
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConfigOptionChip(
                            label = "Pinned Papers",
                            selected = showPinned,
                            onToggle = { viewModel.setPinnedVisible(!showPinned) },
                            tag = "toggle_show_pinned"
                        )
                        ConfigOptionChip(
                            label = "Recent Feed",
                            selected = showRecent,
                            onToggle = { viewModel.setRecentVisible(!showRecent) },
                            tag = "toggle_show_recent"
                        )
                        ConfigOptionChip(
                            label = "Project Goals",
                            selected = showTelemetry,
                            onToggle = { viewModel.setTelemetryVisible(!showTelemetry) },
                            tag = "toggle_show_goals"
                        )
                        ConfigOptionChip(
                            label = "Mobile Subject Lab",
                            selected = showLab,
                            onToggle = { viewModel.setLabVisible(!showLab) },
                            tag = "toggle_show_lab"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Customize Study Campaign Goals:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = researchGoal,
                        onValueChange = { viewModel.updateResearchGoal(it) },
                        label = { Text("Active Research Objective") },
                        placeholder = { Text("e.g., Publish Cancer Splicing Therapeutics") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_dashboard_goal")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Target Dossiers: $targetCount",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Slider(
                            value = targetCount.toFloat(),
                            onValueChange = { viewModel.updateTargetCount(it.toInt()) },
                            valueRange = 1f..15f,
                            steps = 14,
                            modifier = Modifier
                                .weight(2f)
                                .testTag("slider_target_count")
                        )
                    }
                }
            }
        }

        // Section 1: Project Goals & Telemetry
        AnimatedVisibility(visible = showTelemetry) {
            Column {
                Text(
                    text = "ACTIVE STUDY OBJECTIVES & TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.25.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                CircularProgressIndicator(
                                    progress = if (targetCount > 0) (papers.size.toFloat() / targetCount.toFloat()).coerceAtMost(1f) else 0f,
                                    strokeWidth = 5.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "${papers.size}/${targetCount}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = researchGoal.ifBlank { "Ongoing Inquiry Dossier" },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Validating database indexing & dossier milestones.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Milestones Checklist
                        MilestoneItem(
                            checked = papers.isNotEmpty(),
                            title = "Compile bibliography cache indices",
                            description = "Add at least one validated peer-reviewed analysis."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MilestoneItem(
                            checked = workspaces.size > 1,
                            title = "Establish shared peer repositories",
                            description = "Set up minimum of two collaborative labs or team folders."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MilestoneItem(
                            checked = papers.size >= targetCount,
                            title = "Meet research dossier target ($targetCount papers)",
                            description = "Collect and register all necessary references for active goal."
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Section 2: Pinned Documents
        AnimatedVisibility(visible = showPinned) {
            val pinnedPapers = remember(papers) { papers.filter { it.isPinned } }
            Column {
                Text(
                    text = "PINNED FREQUENTLY ACCESSED INDEXES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.25.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (pinnedPapers.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = "No pin",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No pinned research indices",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bookmark your core papers by hitting the pin icon in the general Catalog list.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pinnedPapers.forEach { paper ->
                            DashboardPaperPinCard(
                                paper = paper,
                                onUnpin = { viewModel.togglePinPaper(paper) },
                                onClick = {
                                    viewModel.selectPaper(paper.id)
                                    viewModel.selectTab(1) // Jump to general catalog library screen (tab 1)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Section 3: Recent Activity / Sync Feed
        AnimatedVisibility(visible = showRecent) {
            Column {
                Text(
                    text = "RECENT DOSSIER FEEDS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.25.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (papers.isEmpty()) {
                            Text(
                                text = "Log database stream empty.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            val recentFeed = papers.take(3)
                            recentFeed.forEachIndexed { idx, paper ->
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(if (paper.isPinned) EmeraldSafe else MaterialTheme.colorScheme.primary)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = paper.title,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Category: ${paper.category} | Created recently",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Filled.History,
                                            contentDescription = "Recent Log",
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (idx < recentFeed.size - 1) {
                                        Divider(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Section 4: Real-time Mobile Lab Hub
        AnimatedVisibility(visible = showLab) {
            Column {
                Text(
                    text = "REAL-TIME MOBILE RESEARCH LAB",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.25.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Biotech,
                                "Laboratory",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "LAB INSTRUMENTATION DECK",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Text(
                            text = "Spin micro-centrifuges, adjust deep neural weights, spectroscopic pH parameters, or align interferometers based on subject domain.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Subject Selector Row
                        val subjects = listOf("Biomedicine", "Computer Science", "Chemistry", "Quantum Physics")
                        ScrollableTabRow(
                            selectedTabIndex = subjects.indexOf(selectedSubject).coerceAtLeast(0),
                            edgePadding = 0.dp,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            subjects.forEach { subject ->
                                Tab(
                                    selected = selectedSubject == subject,
                                    onClick = { viewModel.selectLabSubject(subject) },
                                    modifier = Modifier.testTag("lab_subject_${subject.replace(" ", "_")}")
                                ) {
                                    Text(
                                        text = subject.uppercase(),
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = if (selectedSubject == subject) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Subject specific sliders / configurations
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            when (selectedSubject) {
                                "Biomedicine" -> {
                                    Text(
                                        text = "NECESSARY BIOMEDICAL INSTRUMENTS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Instrument Slider 1
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Micro-centrifuge Spindle Speed", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${centrifugeRpm.toInt()} RPM", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = centrifugeRpm,
                                            onValueChange = { viewModel.updateCentrifugeRpm(it) },
                                            valueRange = 4000f..14000f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_centrifuge_rpm")
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Instrument Slider 2
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Liquid N2 Cryo Temperature", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${String.format("%.1f", cryoTemp)}°C", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = cryoTemp,
                                            onValueChange = { viewModel.updateCryoTemp(it) },
                                            valueRange = -196f..-150f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_cryo_temp")
                                        )
                                    }
                                }
                                "Computer Science" -> {
                                    Text(
                                        text = "CUDA DL NODE ACCELERATION DIALS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // CS dial 1
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("GPU Active Wattage Cap", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${gpuPowerLimit}W", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = gpuPowerLimit.toFloat(),
                                            onValueChange = { viewModel.updateGpuPowerLimit(it.toInt()) },
                                            valueRange = 100f..450f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_gpu_power")
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // CS dial 2
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Logical Core Context Step Size", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${contextLength} Tokens", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = contextLength.toFloat(),
                                            onValueChange = { viewModel.updateContextLength(it.toInt()) },
                                            valueRange = 8192f..512000f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_context_length")
                                        )
                                    }
                                }
                                "Chemistry" -> {
                                    Text(
                                        text = "UV-VIS ABSORBANCE SPECTROMETER LOGS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Chem dial 1
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Buffer Titration pH target", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text(String.format("%.2f pH", titrationPh), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = titrationPh,
                                            onValueChange = { viewModel.updateTitrationPh(it) },
                                            valueRange = 0.0f..14.0f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_titration_ph")
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Chem dial 2
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Excitation Spectrum wavelength", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${wavelengthNm} nm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = wavelengthNm.toFloat(),
                                            onValueChange = { viewModel.updateWavelengthNm(it.toInt()) },
                                            valueRange = 190f..850f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_wavelength")
                                        )
                                    }
                                }
                                "Quantum Physics" -> {
                                    Text(
                                        text = "SUPERHEATED LASER INTERFEROMETER COUNTERS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Quantum dial 1
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Optical Plane Alignment Ratio", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${String.format("%.1f", interferometerAlign)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = interferometerAlign,
                                            onValueChange = { viewModel.updateInterferometerAlign(it) },
                                            valueRange = 85.0f..100.0f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_interferometer_alignment")
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Quantum dial 2
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Coherent Laser Power limit", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                            Text("${laserPowerMw} mW", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = laserPowerMw.toFloat(),
                                            onValueChange = { viewModel.updateLaserPowerMw(it.toInt()) },
                                            valueRange = 10f..1000f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("slider_laser_power")
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Diagnostics Trigger & Logs
                        Button(
                            onClick = { viewModel.runLabAnalysis() },
                            enabled = !isAnalyzing,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_lab_analysis_btn")
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("COMPUTING SIMULATION... ${(labProgress * 100).toInt()}%", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Filled.NetworkCheck, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("RUN SUBJECT SPECIFIC LAB ANALYSIS", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = isAnalyzing || labReport != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                if (isAnalyzing) {
                                    LinearProgressIndicator(
                                        progress = labProgress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(50))
                                    )
                                }
                                
                                labReport?.let { report ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    ) {
                                        Text(
                                            text = report,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigOptionChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    FilterChip(
        selected = selected,
        onClick = onToggle,
        label = { Text(label, fontWeight = FontWeight.Bold) },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        leadingIcon = {
            if (selected) {
                Icon(Icons.Filled.Check, null, modifier = Modifier.size(14.dp))
            } else {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
            }
        },
        modifier = Modifier.testTag(tag)
    )
}

@Composable
fun MilestoneItem(
    checked: Boolean,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = if (checked) "Done" else "Todo",
            tint = if (checked) EmeraldSafe else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DashboardPaperPinCard(
    paper: Paper,
    onUnpin: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = paper.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onUnpin,
                    modifier = Modifier.size(32.dp).testTag("unpin_btn_${paper.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Unpin Paper",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = paper.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${paper.journal} • ${paper.year} • ${paper.authors}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
