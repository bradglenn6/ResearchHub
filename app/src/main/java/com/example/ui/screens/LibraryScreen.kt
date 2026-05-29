package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Paper
import com.example.data.model.Workspace
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.AmberWarn
import com.example.ui.theme.CoralAlert
import com.example.ui.viewmodel.ResearchViewModel

@Composable
fun LibraryScreen(viewModel: ResearchViewModel, modifier: Modifier = Modifier) {
    val papers by viewModel.filteredPapers.collectAsStateWithLifecycle()
    val workspaces by viewModel.allWorkspaces.collectAsStateWithLifecycle()
    val selectedWorkId by viewModel.selectedWorkspaceId.collectAsStateWithLifecycle()
    val selectedPaper by viewModel.selectedPaper.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val syncing by viewModel.syncing.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isAddPaperOpen by remember { mutableStateOf(false) }

    val filteredList = remember(papers, searchQuery) {
        if (searchQuery.isBlank()) {
            papers
        } else {
            papers.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.authors.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isAddPaperOpen = true },
                icon = { Icon(Icons.Filled.Add, "Add Draft") },
                text = { Text("Validate Paper", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("validate_paper_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Search Bar with Bold Aesthetic Rounding (24dp)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search catalog indexes...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Filled.Search, "Search icon", tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, "Clear search")
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Workspace Horizon selector
            Text(
                text = "ACTIVE WORKSPACES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            WorkspaceChipSelector(
                workspaces = workspaces,
                selectedId = selectedWorkId,
                onSelected = { viewModel.selectWorkspace(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Workspace Highlights Block - styled exactly like the Design HTML!
            val activeWorkspace = workspaces.find { it.id == selectedWorkId }
            if (activeWorkspace != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Workspace Node Active".uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 9.sp
                                )
                            }
                            Icon(
                                imageVector = if (activeWorkspace.id == 1L) Icons.Filled.Lock else Icons.Filled.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = activeWorkspace.name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${filteredList.size} papers categorized in current indexes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.triggerCloudSync() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sync & Catalog", fontWeight = FontWeight.ExtraBold)
                            }
                            
                            IconButton(
                                onClick = { viewModel.showToast("Copied secure workspace key to clipboard.") },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Filled.Share, "Share node", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }

            // Paper Feed Subhead
            Text(
                text = "INDEXED DOCUMENT FEED",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            if (filteredList.isEmpty()) {
                EmptyStateCard(searchQuery = searchQuery)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("papers_list")
                ) {
                    items(filteredList, key = { it.id }) { paper ->
                        PaperCard(
                            paper = paper,
                            onClick = { viewModel.selectPaper(paper.id) },
                            onDelete = { viewModel.deletePaper(paper) },
                            onPin = { viewModel.togglePinPaper(paper) }
                        )
                    }
                }
            }
        }

        // Add Paper Ingestion Dialog
        if (isAddPaperOpen) {
            AddPaperDialog(
                workspaces = workspaces,
                selectedWorkspaceId = if (selectedWorkId == 0L) 1L else selectedWorkId,
                onDismiss = { isAddPaperOpen = false },
                onIngest = { title, authors, journal, year, text, workspaceId, category, url, doi ->
                    viewModel.registerNewPaper(
                        title = title,
                        authors = authors,
                        journal = journal,
                        year = year,
                        textContent = text,
                        workspaceId = workspaceId,
                        category = category,
                        url = url,
                        doi = doi
                    )
                    isAddPaperOpen = false
                }
            )
        }

        // Paper Details Full Bottom Panel
        if (selectedPaper != null) {
            PaperDetailsDrawer(
                paper = selectedPaper!!,
                onDismiss = { viewModel.selectPaper(null) },
                onDelete = {
                    viewModel.deletePaper(selectedPaper!!)
                    viewModel.selectPaper(null)
                }
            )
        }

        // Full Screen Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI Peer Validation Active",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Extracting abstract semantics, checking plagiarism registers, and formulating styles...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SyncIndicatorHeader(
    syncStatus: String,
    syncing: Boolean,
    onForceSync: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = if (syncing) Icons.Filled.Refresh else Icons.Filled.CloudDone,
                contentDescription = "Sync State Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (syncing) "Synchronizing workspaces..." else "Secure Workspace Sync",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = syncStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onForceSync,
                enabled = !syncing,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("force_sync_button")
            ) {
                if (syncing) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WorkspaceChipSelector(
    workspaces: List<Workspace>,
    selectedId: Long,
    onSelected: (Long) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilterChip(
                selected = selectedId == 0L,
                onClick = { onSelected(0L) },
                label = { Text("All Libraries") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.FolderCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("workspace_chip_all")
            )
        }

        items(workspaces) { space ->
            FilterChip(
                selected = selectedId == space.id,
                onClick = { onSelected(space.id) },
                label = { Text(space.name) },
                leadingIcon = {
                    Icon(
                        imageVector = if (space.id == 1L) Icons.Outlined.Lock else Icons.Outlined.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("workspace_chip_${space.id}")
            )
        }
    }
}

@Composable
fun PaperCard(
    paper: Paper,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .testTag("paper_card_${paper.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Topic tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = paper.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Plagiarism risk flag
                if (paper.plagiarismScore != null) {
                    val score = paper.plagiarismScore
                    val (color, label) = when {
                        score < 10 -> Pair(EmeraldSafe, "Pristine Integrity ($score%)")
                        score <= 20 -> Pair(AmberWarn, "Safe Citation ($score%)")
                        else -> Pair(CoralAlert, "High Sim Overlap ($score%)")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = paper.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = paper.authors,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${paper.journal} • ${paper.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = { onPin() },
                        modifier = Modifier.size(24.dp).testTag("pin_btn_${paper.id}")
                    ) {
                        Icon(
                            imageVector = if (paper.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Toggle Pin",
                            tint = if (paper.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { onClick() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Inspect analysis",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete paper",
                            tint = Color.Red.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(searchQuery: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = if (searchQuery.isNotEmpty()) Icons.Outlined.SearchOff else Icons.Outlined.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isNotEmpty()) "No results fit '$searchQuery'" else "Library is Empty",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (searchQuery.isNotEmpty()) "Try revising your keywords or filters." else "Click 'Validate Paper' at the bottom right to upload a new draft and unlock AI analyses.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp),
                maxLines = 4
            )
        }
    }
}

@Composable
fun AddPaperDialog(
    workspaces: List<Workspace>,
    selectedWorkspaceId: Long,
    onDismiss: () -> Unit,
    onIngest: (String, String, String, String, String, Long, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }
    var journal by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2026") }
    var textContent by remember { mutableStateOf("") }
    var doi by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var expandedWorkspaces by remember { mutableStateOf(false) }

    var workspaceIdSelected by remember { mutableStateOf(selectedWorkspaceId) }
    var categoryText by remember { mutableStateOf("Computer Science") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ingest Draft with E2E Validation")
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                item {
                    Text(
                        text = "Your document is fully encrypted locally in SQLite under AES-256 before analysis triggers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Paper Title *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_title_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = authors,
                        onValueChange = { authors = it },
                        label = { Text("Authors (separated by commas)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = journal,
                            onValueChange = { journal = it },
                            label = { Text("Journal/Proceeding") },
                            singleLine = true,
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Year") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = doi,
                            onValueChange = { doi = it },
                            label = { Text("DOI (Optional)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            label = { Text("URL Link (Optional)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Category Input
                    OutlinedTextField(
                        value = categoryText,
                        onValueChange = { categoryText = it },
                        label = { Text("General Topic / Subject Area") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Target Workspace Selector
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val activeWorkspaceName = workspaces.find { it.id == workspaceIdSelected }?.name ?: "Personal Vault"
                        OutlinedButton(
                            onClick = { expandedWorkspaces = !expandedWorkspaces },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save to Workspace: $activeWorkspaceName")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }

                        DropdownMenu(
                            expanded = expandedWorkspaces,
                            onDismissRequest = { expandedWorkspaces = false }
                        ) {
                            workspaces.forEach { space ->
                                DropdownMenuItem(
                                    text = { Text(space.name) },
                                    onClick = {
                                        workspaceIdSelected = space.id
                                        expandedWorkspaces = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        label = { Text("Paper Abstract / Raw Draft Content *") },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_content_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onIngest(title, authors, journal, year, textContent, workspaceIdSelected, categoryText, url, doi)
                },
                enabled = title.isNotBlank() && textContent.isNotBlank(),
                modifier = Modifier.testTag("dialog_submit_button")
            ) {
                Text("Validate & Ingest")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PaperDetailsDrawer(
    paper: Paper,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0=Summary, 1=Plagiarism, 2=Citations, 3=Peer suggestions
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null, // Custom Header in content to maximize reading space
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 550.dp)
            ) {
                // Header Pane
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = paper.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = paper.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = paper.authors,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, "Dismiss")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Sub Tab chips
                ScrollableTabRow(
                    selectedTabIndex = activeSubTab,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeSubTab == 0,
                        onClick = { activeSubTab = 0 },
                        text = { Text("Summary", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeSubTab == 1,
                        onClick = { activeSubTab = 1 },
                        text = { Text("Integrity Check", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeSubTab == 2,
                        onClick = { activeSubTab = 2 },
                        text = { Text("Bibliographies", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = activeSubTab == 3,
                        onClick = { activeSubTab = 3 },
                        text = { Text("AI peer reviews", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail display pane based on active sub tab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (activeSubTab) {
                            0 -> { // Summary Tab
                                item {
                                    Text(
                                        text = "Automated Paper Summary",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = paper.summary ?: "Summary currently empty.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 22.sp
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Executive Breakthroughs & Key Findings",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = paper.keyFindings ?: "• No explicit breakthrough findings isolated.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 20.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                if (paper.textContent.isNotEmpty()) {
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Ingested Abstract Content",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = paper.textContent,
                                            style = MaterialTheme.typography.bodySmall,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                            1 -> { // Plagiarism Tab
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(36.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "${paper.plagiarismScore ?: 0}%",
                                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                                    color = if ((paper.plagiarismScore ?: 0) < 15) EmeraldSafe else CoralAlert
                                                )
                                                Text("Similarity", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = "System Plagiarism Register",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Scanned against preprints, open archives & IEEE Index.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "AI Peer Verification Log",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = paper.plagiarismReport ?: "Checking logs...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(12.dp),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.VerifiedUser, "Shield Verified", tint = EmeraldSafe, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Intellectual Privacy Lock: On Device AES key active",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = EmeraldSafe
                                        )
                                    }
                                }
                            }
                            2 -> { // Citations Tab
                                item {
                                    Text(
                                        text = "Automated Citations & Styles",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Ready to copy into your bibliographies and manuscript.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }

                                item {
                                    CitationBox(
                                        title = "APA (7th edition)",
                                        citation = paper.citationApa ?: "Generating citation...",
                                        onCopy = { copyToClipboard(context, paper.citationApa ?: "") }
                                    )
                                }

                                item {
                                    CitationBox(
                                        title = "MLA (9th edition)",
                                        citation = paper.citationMla ?: "Generating citation...",
                                        onCopy = { copyToClipboard(context, paper.citationMla ?: "") }
                                    )
                                }

                                item {
                                    CitationBox(
                                        title = "Chicago (Author-Date)",
                                        citation = paper.citationChicago ?: "Generating citation...",
                                        onCopy = { copyToClipboard(context, paper.citationChicago ?: "") }
                                    )
                                }
                            }
                            3 -> { // Suggestions Tab
                                item {
                                    Text(
                                        text = "AI Suggested Peer-Reviewed Sources",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Relevant peer work recommended for citation to strengthen your manuscript.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            val sourceTexts = paper.suggestedSources?.split("\n•") ?: emptyList()
                                            if (sourceTexts.isEmpty()) {
                                                Text("Finding references...", style = MaterialTheme.typography.bodyMedium)
                                            } else {
                                                sourceTexts.forEach { sourceText ->
                                                    val cleanStr = sourceText.replace("•", "").trim()
                                                    if (cleanStr.isNotEmpty()) {
                                                        Text(
                                                            text = "• $cleanStr",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            lineHeight = 20.sp,
                                                            modifier = Modifier.padding(vertical = 4.dp)
                                                        )
                                                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
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
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close Inspector")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.8f))
            ) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Erase Archive")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("paper_details_dialog")
    )
}

@Composable
fun CitationBox(
    title: String,
    citation: String,
    onCopy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy Citation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = citation,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Academic Citation", text)
    clipboard.setPrimaryClip(clip)
}
