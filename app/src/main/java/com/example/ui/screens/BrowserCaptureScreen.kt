package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Workspace
import com.example.ui.viewmodel.ResearchViewModel

@Composable
fun BrowserCaptureScreen(viewModel: ResearchViewModel, modifier: Modifier = Modifier) {
    val workspaces by viewModel.allWorkspaces.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var activeDbIndex by remember { mutableStateOf(0) } // 0=Google Scholar, 1=PubMed, 2=arXiv
    var currentUrl by remember { mutableStateOf("https://scholar.google.com/search?q=machine+learning+cancer") }
    var searchQuery by remember { mutableStateOf("machine learning genomic analysis") }
    var selectedWorkspaceInExt by remember { mutableStateOf(1L) } // default Personal Vault
    var expandedWorkspaces by remember { mutableStateOf(false) }

    // Mock Academic Papers Database for Extension
    val mockAcademicDb = remember {
        listOf(
            MockAcademicPaper(
                title = "Deep Neural Processing of Genomic Splice Sites",
                authors = "Gomez, P., & Thornton, M.",
                journal = "Journal of Computational Biology",
                year = "2024",
                doi = "doi:10.1089/jcb.2024.112",
                sourceDb = "PubMed",
                url = "https://pubmed.ncbi.nlm.nih.gov/8493122/",
                abstract = "We introduce SpliceNet, a deep convolutional neural structure developed to predict RNA splicing junctions across multiple oncology pathways. SpliceNet resolves localized splicing changes under low-count RNA assays."
            ),
            MockAcademicPaper(
                title = "Generative Reasoning in Theorem proving with Chain-of-Thought",
                authors = "Rostova, E., & Keller, J.",
                journal = "IEEE Transactions on Neural Networks",
                year = "2025",
                doi = "doi:10.1109/tnnls.2025.10948",
                sourceDb = "Google Scholar",
                url = "https://scholar.google.com/citations?paper=9403810/",
                abstract = "Theorem proving represents a fundamental benchmark for automated math logical systems. We explore how enforcing semantic chain-of-thought protocols stabilizes inference paths on complex logical deduction trials."
            ),
            MockAcademicPaper(
                title = "Cryptographic Security in Decentralized Document Sync Networks",
                authors = "Rivera, A., & Glenn, B.",
                journal = "arXiv preprint",
                year = "2026",
                doi = "doi:10.48550/arXiv.2604.0987",
                sourceDb = "arXiv",
                url = "https://arxiv.org/abs/2604.0987",
                abstract = "We propose an end-to-end encrypted SQLite database sync layer utilizing AES-256 local key rings. This system validates secure research and bibliographies replication across insecure multi-device channels."
            ),
            MockAcademicPaper(
                title = "Targeted Therapeutics for SF3B1 Mutant Myeloid Leukemia",
                authors = "Lewis, S., & Yoshimi, A.",
                journal = "Cancer Discovery Series",
                year = "2025",
                doi = "doi:10.1158/2159-8290.CD-25-0105",
                sourceDb = "PubMed",
                url = "https://pubmed.ncbi.nlm.nih.gov/3829103/",
                abstract = "Oncogenic mutations in transcription factor SF3B1 induce aberrant 3' splice site validation. We demonstrate how a novel small-molecule modulator selective inhibits leukemia cells in vivo."
            ),
            MockAcademicPaper(
                title = "A Baseline Study of Quantum Logical Decoherence Metrics",
                authors = "Thornton, M., & Zhao, L.",
                journal = "Physical Review Letters",
                year = "2024",
                doi = "doi:10.1103/PhysRevLett.132.09102",
                sourceDb = "Google Scholar",
                url = "https://journals.aps.org/prl/abstract/10.1103/PhysRevLett.132.09102",
                abstract = "We investigate quantum gate logic failures over high noise environments. Applying predictive algorithms, we bound the error cascade threshold to inform decoherence mitigation frameworks."
            )
        )
    }

    val selectedDbName = when (activeDbIndex) {
        0 -> "Google Scholar"
        1 -> "PubMed"
        2 -> "arXiv"
        else -> "Google Scholar"
    }

    val filteredMockList = remember(activeDbIndex, searchQuery) {
        mockAcademicDb.filter {
            it.sourceDb == selectedDbName &&
            (searchQuery.isBlank() || 
             it.title.contains(searchQuery, ignoreCase = true) || 
             it.abstract.contains(searchQuery, ignoreCase = true))
        }
    }

    LaunchedEffect(activeDbIndex, searchQuery) {
        currentUrl = when (activeDbIndex) {
            0 -> "https://scholar.google.com/scholar?q=${searchQuery.replace(" ", "+")}"
            1 -> "https://pubmed.ncbi.nlm.nih.gov/?term=${searchQuery.replace(" ", "+")}"
            2 -> "https://arxiv.org/search/?query=${searchQuery.replace(" ", "+")}&searchtype=all"
            else -> "https://scholar.google.com/scholar"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Core Browser Container Outer Header Matching Bold Typography Subheadings
        Text(
            text = "EXTERNAL DATABASE WEB COMPANION",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Simulates instant peer bibliography acquisition from direct web indexes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated Chrome URL Bar with Premium Card styling
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Address Bar Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Back", modifier = Modifier.size(18.dp), tint = Color.Gray)
                        Icon(Icons.Filled.ArrowForward, "Forward", modifier = Modifier.size(18.dp), tint = Color.Gray)
                        Icon(Icons.Filled.Refresh, "Reload", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Simulated URL Input Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Lock, "Secure Connection", tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUrl,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // extension trigger icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Extension,
                            contentDescription = "Research AI Extension Panel Active",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Database Selector Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Google Scholar", "PubMed", "arXiv").forEachIndexed { i, db ->
                        val active = activeDbIndex == i
                        AssistChip(
                            onClick = { activeDbIndex = i },
                            label = { Text(db) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
                                labelColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            ),
                            border = if (active) null else BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Extension target configuration bar
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.ChromeReaderMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Save direct captures into: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Box(modifier = Modifier.wrapContentSize()) {
                    val activeWorkspaceName = workspaces.find { it.id == selectedWorkspaceInExt }?.name ?: "Personal Vault"
                    Text(
                        text = "$activeWorkspaceName ▾",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { expandedWorkspaces = !expandedWorkspaces }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )

                    DropdownMenu(
                        expanded = expandedWorkspaces,
                        onDismissRequest = { expandedWorkspaces = false }
                    ) {
                        workspaces.forEach { space ->
                            DropdownMenuItem(
                                text = { Text(space.name) },
                                onClick = {
                                        selectedWorkspaceInExt = space.id
                                        expandedWorkspaces = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search engine filter input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter database paper records...") },
            trailingIcon = { Icon(Icons.Filled.FilterList, "Filter") },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Results Stream
        if (filteredMockList.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "No indexed papers matched search criteria inside $selectedDbName.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("academic_results_list")
            ) {
                items(filteredMockList) { mockPaper ->
                    MockPaperResultCard(
                        mockPaper = mockPaper,
                        onCapture = {
                            viewModel.captureFromExtension(
                                title = mockPaper.title,
                                authors = mockPaper.authors,
                                journal = mockPaper.journal,
                                year = mockPaper.year,
                                textContent = mockPaper.abstract,
                                originDoi = mockPaper.doi,
                                originUrl = mockPaper.url,
                                targetWorkspaceId = selectedWorkspaceInExt
                            )
                        },
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

data class MockAcademicPaper(
    val title: String,
    val authors: String,
    val journal: String,
    val year: String,
    val doi: String,
    val sourceDb: String,
    val url: String,
    val abstract: String
)

@Composable
fun MockPaperResultCard(
    mockPaper: MockAcademicPaper,
    onCapture: () -> Unit,
    isLoading: Boolean
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mockPaper.doi,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Verified Document",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mockPaper.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${mockPaper.authors} • ${mockPaper.journal} (${mockPaper.year})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mockPaper.abstract,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Browser Extension Button
            Button(
                onClick = onCapture,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B5E20), // deep safe emerald
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("extension_capture_btn_${mockPaper.title.take(12)}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Extension,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Capture Reference to Research AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
