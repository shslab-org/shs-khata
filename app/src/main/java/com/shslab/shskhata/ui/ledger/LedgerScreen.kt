package com.shslab.shskhata.ui.ledger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shslab.shskhata.R
import com.shslab.shskhata.data.db.Txn
import com.shslab.shskhata.R
import com.shslab.shskhata.data.db.TxnType
import com.shslab.shskhata.ui.Routes
import com.shslab.shskhata.ui.util.formatTaka
import com.shslab.shskhata.ui.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    navController: NavHostController,
    customerId: Long,
    viewModel: LedgerViewModel = viewModel(
        factory = LedgerViewModelFactory.fromApp(customerId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val customer = uiState.customer

    var showAddTxn by remember { mutableStateOf(false) }
    var deleteTxnTarget by remember { mutableStateOf<Txn?>(null) }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = customer?.name ?: "Ledger",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        customer?.phone?.let {
                            if (it.isNotBlank()) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddTxn = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add transaction")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BalanceHeader(balance = uiState.balance)

            if (uiState.txns.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_transactions),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.txns, key = { it.id }) { txn ->
                        TxnRow(txn = txn, onDelete = { deleteTxnTarget = txn })
                    }
                }
            }
        }
    }

    if (showAddTxn) {
        AddTransactionSheet(
            onSubmit = { type, amount, note ->
                viewModel.addTransaction(type, amount, note)
                showAddTxn = false
            },
            onDismiss = { showAddTxn = false }
        )
    }

    val target = deleteTxnTarget
    if (target != null) {
        AlertDialog(
            onDismissRequest = { deleteTxnTarget = null },
            title = { Text(stringResource(R.string.confirm_delete_txn_title)) },
            text = {
                Text(
                    "Amount: ${formatTaka(target.amount)}\n" +
                    "Date: ${formatDate(target.createdAt)}"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(target)
                    deleteTxnTarget = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTxnTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BalanceHeader(balance: Double) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.balance_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            val color = when {
                balance > 0.0 -> MaterialTheme.colorScheme.error
                balance < 0.0 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
            Text(
                text = formatTaka(balance),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun TxnRow(txn: Txn, onDelete: () -> Unit) {
    val isGave = txn.type == TxnType.GAVE
    val accentColor = if (isGave) MaterialTheme.colorScheme.error
                      else MaterialTheme.colorScheme.primary
    val containerColor = if (isGave) MaterialTheme.colorScheme.errorContainer
                         else MaterialTheme.colorScheme.primaryContainer
    val onContainer = if (isGave) MaterialTheme.colorScheme.onErrorContainer
                      else MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onDelete),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = containerColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isGave) Icons.Filled.Remove else Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = onContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isGave) stringResource(R.string.you_gave) else stringResource(R.string.you_got),
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatDate(txn.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (txn.note.isNotBlank()) {
                    Text(
                        text = txn.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = formatTaka(txn.amount),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onSubmit: (type: TxnType, amount: Double, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TxnType.GAVE) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.add_transaction),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeToggle(
                    text = stringResource(R.string.you_gave),
                    isSelected = selectedType == TxnType.GAVE,
                    color = MaterialTheme.colorScheme.error,
                    onClick = { selectedType = TxnType.GAVE },
                    modifier = Modifier.weight(1f)
                )
                TypeToggle(
                    text = stringResource(R.string.you_got),
                    isSelected = selectedType == TxnType.GOT,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { selectedType = TxnType.GOT },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                        amountText = it
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text(stringResource(R.string.txn_note)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            androidx.compose.material3.Button(
                enabled = amount != null && amount > 0.0,
                onClick = {
                    amount?.let { onSubmit(selectedType, it, noteText.trim()) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_transaction))
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

@Composable
fun TypeToggle(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f)
                             else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                if (text == "Gave") Icons.Filled.Remove else Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) color
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) color
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/**
 * ViewModel factory that wires the app repositories into LedgerViewModel
 * for a specific customer.
 */
object LedgerViewModelFactory {

    fun fromApp(customerId: Long): androidx.lifecycle.ViewModelProvider.Factory {
        val app = com.shslab.shskhata.SHSKhataApp.instance
        return object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LedgerViewModel::class.java)) {
                    return LedgerViewModel(
                        app.customerRepository,
                        app.txnRepository,
                        customerId
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}
