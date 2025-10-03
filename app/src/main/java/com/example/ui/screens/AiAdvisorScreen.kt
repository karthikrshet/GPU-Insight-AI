package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.GpuInsightUiState

@Composable
fun AiAdvisorScreen(
    uiState: GpuInsightUiState,
    onAnalyzeError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Gemini AI Debug Advisor", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
                when {
                    uiState.isLoadingAi -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    uiState.aiResponse.isNotEmpty() ->
                        Text(uiState.aiResponse, style = MaterialTheme.typography.bodyMedium)
                    else -> Text("Paste a GPU error/stack trace below to analyze with Gemini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = inputText, onValueChange = { inputText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Paste CUDA/PyTorch error or stack trace...") },
            minLines = 3, maxLines = 6)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { if (inputText.isNotBlank()) onAnalyzeError(inputText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoadingAi && inputText.isNotBlank()
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Analyze with Gemini")
        }
    }
}
