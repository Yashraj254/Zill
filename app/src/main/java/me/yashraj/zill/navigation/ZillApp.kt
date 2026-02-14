package me.yashraj.zill.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.rememberNavBackStack

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ZillApp() {
    val backStack = rememberNavBackStack(Screen.Music)
    val currentKey = backStack.last()

    val appBarController = remember { AppBarController() }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    val isBottomNavScreen = BottomDestination.entries.any {
        it.key::class == currentKey::class
    }
    CompositionLocalProvider(
        LocalAppBarController provides appBarController,
        LocalNavBackStack provides backStack,
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                BottomDestination.entries.forEach { destination ->

                    val isSelected = currentKey::class == destination.key::class

                    item(
                        icon = {
                            Icon(
                                destination.icon, contentDescription = stringResource(destination.label)
                            )
                        },
                        label = { Text(stringResource(destination.label)) },
                        selected = isSelected,
                        onClick = {
                            backStack.clear()
                            backStack.add(destination.key)
                        }
                    )
                }
            },
            layoutType = if (isBottomNavScreen) {
                layoutType
            } else {
                // Forces NavigationSuiteScaffold to render nothing
                NavigationSuiteType.None
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(appBarController.state.title) },
                        navigationIcon = {
                            if (appBarController.state.showBack) {
                                IconButton(onClick = { backStack.removeLastOrNull() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                ZillNavDisplay(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

