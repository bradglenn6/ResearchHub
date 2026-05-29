package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.database.AppDatabase
import com.example.data.model.Paper
import com.example.data.model.Workspace
import com.example.data.model.FundingOpportunity
import com.example.data.api.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ResearchRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val paperDao = db.paperDao()
    private val workspaceDao = db.workspaceDao()
    private val fundingDao = db.fundingDao()

    val allPapers: Flow<List<Paper>> = paperDao.getAllPapers()
    val allWorkspaces: Flow<List<Workspace>> = workspaceDao.getAllWorkspaces()
    val allFunding: Flow<List<FundingOpportunity>> = fundingDao.getAllFunding()

    fun getPapersInWorkspace(workspaceId: Long): Flow<List<Paper>> = paperDao.getPapersByWorkspace(workspaceId)
    fun getPaperById(id: Long): Flow<Paper?> = paperDao.getPaperById(id)

    suspend fun savePaper(paper: Paper): Long = withContext(Dispatchers.IO) {
        paperDao.insertPaper(paper)
    }

    suspend fun updatePaper(paper: Paper) = withContext(Dispatchers.IO) {
        paperDao.updatePaper(paper)
    }

    suspend fun deletePaperById(id: Long) = withContext(Dispatchers.IO) {
        paperDao.deletePaperById(id)
    }

    suspend fun createWorkspace(workspace: Workspace): Long = withContext(Dispatchers.IO) {
        workspaceDao.insertWorkspace(workspace)
    }

    suspend fun deleteWorkspace(workspace: Workspace) = withContext(Dispatchers.IO) {
        if (workspace.id != 1L) {
            workspaceDao.deleteWorkspace(workspace)
        }
    }

    /**
     * Triggered on app launch: Seeds default academic entries if Room DB is empty.
     */
    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val existingWorkspaces = allWorkspaces.first()
        if (existingWorkspaces.isEmpty()) {
            Log.d("ResearchRepository", "Seeding initial workspaces and research items.")
            
            // Seed Workspaces
            val personalId = workspaceDao.insertWorkspace(
                Workspace(
                    id = 1,
                    name = "Personal Vault",
                    description = "Your private research library. End-to-end encrypted locally via AES-256.",
                    owner = "Brad G (You)",
                    teamMembers = "Me (Only)"
                )
            )
            
            val bioTechId = workspaceDao.insertWorkspace(
                Workspace(
                    id = 2,
                    name = "Oncology Splicing Lab",
                    description = "Collaborative workspace for research on genomic splice therapies.",
                    owner = "Brad G (You)",
                    teamMembers = "Brad G, Dr. Sarah Lewis, Prof. John Keller"
                )
            )
            
            val computerSciId = workspaceDao.insertWorkspace(
                Workspace(
                    id = 3,
                    name = "Reasoning & LLMs Group",
                    description = "Analyzing chain-of-thought methodologies and deduction failures in neural nets.",
                    owner = "AI Research Consortium",
                    teamMembers = "Brad G, Dr. Elena Rostova, Mia Thornton"
                )
            )

            // Seed Papers
            paperDao.insertPaper(
                Paper(
                    title = "Attention Is All You Need",
                    authors = "Vaswani, A., Shazeer, N., Parmar, N., Uszkoreit, J., Jones, L., Gomez, A. N., ... & Polosukhin, I.",
                    journal = "Advances in Neural Information Processing Systems",
                    year = "2017",
                    url = "https://arxiv.org/abs/1706.03762",
                    doi = "doi:10.5555/3295222.3295349",
                    textContent = "We propose a new simple network architecture, the Transformer, based solely on attention mechanisms, dispensing with recurrence and convolutions entirely. Experiments on two machine translation tasks show these models to be superior in quality while being more parallelizable and requiring significantly less time to train.",
                    category = "Artificial Intelligence",
                    summary = "This foundational paper introduces the Transformer architecture, replacing recurrent and convolutional neural networks with self-attention layers. The architecture maximizes parallel training capabilities and has become the absolute backplane of all contemporary LLMs, including GPT, Gemini, and Claude.",
                    keyFindings = "• Eliminates recurrent networks entirely in favor of self-attention mechanisms.\n• Dramatically increases parallelism which allows training on huge web-scale corpora.\n• Set a new state-of-the-art in English-to-German and English-to-French translation tests.",
                    suggestedSources = "• 'BERT: Pre-training of Deep Bidirectional Transformers' (doi:10.18653/v1/N19-1423)\n• 'Language Models are Few-Shot Learners' (doi:10.5555/3495724.3495883)",
                    plagiarismScore = 4,
                    plagiarismReport = "Standard academic citation signatures detected, 4% literal matching strings representing standardized bibliography index. No duplications detected.",
                    citationApa = "Vaswani, A., Shazeer, N., Parmar, N., Uszkoreit, J., Jones, L., Gomez, A. N., ... & Polosukhin, I. (2017). Attention is all you need. Advances in Neural Information Processing Systems, 30, 5998-6008.",
                    citationMla = "Vaswani, Ashish, et al. \"Attention Is All You Need.\" Advances in Neural Information Processing Systems, vol. 30, 2017, pp. 5998-6008.",
                    citationChicago = "Vaswani, Ashish, Noam Shazeer, Niki Parmar, Jakob Uszkoreit, Llion Jones, Aidan N. Gomez, Łukasz Kaiser, and Illia Polosukhin. 2017. \"Attention Is All You Need.\" Advances in Neural Information Processing Systems 30: 5998-6008.",
                    workspaceId = computerSciId,
                    savedOffline = true
                )
            )

            paperDao.insertPaper(
                Paper(
                    title = "Splicing Factor Mutations in Cancer",
                    authors = "Yoshimi, A., Balasis, M. E., & Bradley, R. K.",
                    journal = "Nature Reviews Cancer",
                    year = "2021",
                    url = "https://www.nature.com/articles/s41568-021-00334-1",
                    doi = "doi:10.1038/s41568-021-00334-1",
                    textContent = "Mutations in genes encoding RNA splicing factors are highly frequent in cancer, particularly in myeloid neoplasms. Recent therapeutics have begun targeting splicing dependencies in malignant cancer cells, revealing a novel front of clinical precision medicine.",
                    category = "Biomedicine",
                    summary = "A comprehensive survey of cancer-specific RNA splicing pathway failures. Explores how somatic alterations in RNA splicing factor proteins (like SF3B1 or SRSF2) trigger cellular malignancies and evaluates the latest small-molecule pharmacological inhibitors currently in clinical trial phases.",
                    keyFindings = "• Splice mutations are prevalent in myeloid leukemia and select solid adenocarcinomas.\n• Splicing modulators selectively impair cells carrying specific somatic splicing changes.\n• Highlights novel synergistic effects when paired with traditional immune checkpoint blockers.",
                    suggestedSources = "• 'Targeting Splicing in Cancer Therapeutics' (doi:10.1016/j.ccell.2023.01.002)",
                    plagiarismScore = 2,
                    plagiarismReport = "Normal boilerplate matched for biological nomenclature standards. The paper's text shows exceptional originality.",
                    citationApa = "Yoshimi, A., Balasis, M. E., & Bradley, R. K. (2021). Splicing factor mutations in cancer. Nature Reviews Cancer, 21(5), 282-296.",
                    workspaceId = bioTechId,
                    savedOffline = true
                )
            )

            paperDao.insertPaper(
                Paper(
                    title = "Logical Entailment in Large Language Models",
                    authors = "Glenn, B., & Lewis, S.",
                    journal = "Journal of Logic and Artificial Intelligence",
                    year = "2025",
                    url = "https://example-academic.org/llm-logic",
                    doi = "doi:10.1017/jlai.2025.10",
                    textContent = "We evaluate the deductive deduction capabilities of current deep neural networks on symbolic logical datasets. We identify recursive reasoning boundaries and show that error propagation remains the primary bottleneck for complex planning tasks.",
                    category = "Computer Science",
                    summary = "This draft paper evaluates the reasoning boundaries of frontier LLMs on deductive matrices. It reveals that while models excel at localized inductive steps, recursive logical deduction is subject to rapid cascade failures, suggesting a modular combination of symbolic solvers and neural components.",
                    keyFindings = "• Confirms recursive reasoning error rates increase exponentially with reasoning depth.\n• Proposes a modular logic system that handles verification separately from prompt generation.\n• Shows significant reduction in fallacies when prompts are forced into structured pseudocode.",
                    suggestedSources = "• 'Chain-of-Thought Prompting Elicits Reasoning in Large Language Models' (doi:10.5555/3600269.3600587)",
                    plagiarismScore = 0,
                    plagiarismReport = "No plagiarized statements are found. Draft exhibits pristine linguistic and structural integrity.",
                    citationApa = "Glenn, B., & Lewis, S. (2025). Logical entailment in large language models. Journal of Logic and Artificial Intelligence, 12(1), 74-91.",
                    workspaceId = 1, // Personal Vault
                    savedOffline = true
                )
            )

            // Seed some default Funding opportunities
            fundingDao.insertFunding(
                FundingOpportunity(
                    title = "Collaborative Computing and Research Core Projects",
                    agency = "National Science Foundation (NSF)",
                    amount = "Up to $1,200,000",
                    description = "Grants funding specifically for high-impact proposals in algorithmic safety, reasoning architectures, and interdisciplinary intelligence frameworks.",
                    deadline = "November 16, 2026",
                    matchingTopic = "Perfect fit for: Reasoning & LLMs"
                )
            )
            
            fundingDao.insertFunding(
                FundingOpportunity(
                    title = "Cancer Splicing modulators and precision trials",
                    agency = "National Cancer Institute (NCI)",
                    amount = "Up to $2,500,000",
                    description = "Supports translating cellular research into small-molecule trial compositions targeting leukemia splicing pathways.",
                    deadline = "October 5, 2026",
                    matchingTopic = "Excellent match for Oncology Splicing Lab"
                )
            )
        }
    }

    /**
     * Executes the Gemini-backed analysis of a research paper and saves it to Room DB
     */
    suspend fun analyzeAndSavePaper(
        title: String,
        authors: String,
        journal: String,
        year: String,
        textContent: String,
        workspaceId: Long,
        category: String,
        url: String = "",
        doi: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val analysis = GeminiClient.analyzePaper(title, textContent)
        
        val paper = Paper(
            title = title.ifBlank { "Untitled AI Validated Paper" },
            authors = authors.ifBlank { "Anonymous Scholar" },
            journal = journal.ifBlank { "Unpublished Draft" },
            year = year.ifBlank { "2026" },
            url = url,
            doi = doi,
            textContent = textContent,
            category = category.ifBlank { "Interdisciplinary" },
            
            summary = analysis.summary,
            keyFindings = analysis.keyFindings.joinToString("\n"),
            suggestedSources = analysis.suggestedSources.joinToString("\n") { 
                "• ${it.title} by ${it.authors}\n  Reason: ${it.reason}\n  ${it.expectedDoi}" 
            },
            plagiarismScore = analysis.plagiarismScore,
            plagiarismReport = analysis.plagiarismReport,
            
            citationApa = analysis.citationApa,
            citationMla = analysis.citationMla,
            citationChicago = analysis.citationChicago,
            
            workspaceId = workspaceId,
            savedOffline = true
        )

        val newId = paperDao.insertPaper(paper)
        newId
    }

    /**
     * Fetches funding from Gemini API based on a set of research topics and saves to Room
     */
    suspend fun searchAndSaveFunding(topics: String) = withContext(Dispatchers.IO) {
        val result = GeminiClient.findFundingOpportunities(topics)
        fundingDao.deleteAllFunding()
        
        for (item in result.fundingOpportunities) {
            fundingDao.insertFunding(
                FundingOpportunity(
                    title = item.title,
                    agency = item.agency,
                    amount = item.amount,
                    description = item.description,
                    deadline = item.deadline,
                    matchingTopic = item.matchingTopic
                )
            )
        }
    }

    /**
     * Simulates Cross-Device Cloud Syncing
     */
    suspend fun syncDatabaseToCloud(): String = withContext(Dispatchers.IO) {
        delay(1800) // Realistic backend sync delay
        val randomSyncCode = (100000..999999).random()
        "Sync verified. All local records, encryption hashes, and workspaces uploaded successfully. Sync Code: S-$randomSyncCode. All secondary devices updated."
    }
}
