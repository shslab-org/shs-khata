package com.shslab.shskhata.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shslab.shskhata.ui.customers.CustomerListScreen
import com.shslab.shskhata.ui.ledger.LedgerScreen

object Routes {
    const val CUSTOMERS = "customers"
    const val LEDGER = "ledger/{customerId}"

    fun ledger(customerId: Long) = "ledger/$customerId"

    @Composable
    fun NavHostController.openLedger(customerId: Long) {
        navigate(Routes.ledger(customerId))
    }
}

@Composable
fun SHSKhataRoot(navController: NavHostController) {
    MaterialTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = Routes.CUSTOMERS,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.CUSTOMERS) {
                    CustomerListScreen(navController = navController)
                }
                composable(
                    route = Routes.LEDGER,
                    arguments = listOf(
                        androidx.navigation.navArgument("customerId") {
                            type = androidx.navigation.NavType.LongType
                            nullable = false
                        }
                    )
                ) { entry ->
                    val id = entry.arguments?.getLong("customerId") ?: return@composable
                    LedgerScreen(
                        navController = navController,
                        customerId = id
                    )
                }
            }
        }
    }
}
