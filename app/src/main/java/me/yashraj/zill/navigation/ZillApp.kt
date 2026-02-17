package me.yashraj.zill.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.coroutines.launch


private val NAV_BAR_HEIGHT = 80.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ZillApp() {
    val backStack = rememberNavBackStack(Screen.Music)
    val currentKey = backStack.last()
    val appBarController = remember { AppBarController() }
    val sheetController = remember { PlayerSheetController() }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    val startDestination = Screen.Music

    val isBottomNavScreen = BottomDestination.entries.any {
        it.key::class == currentKey::class
    }

    // Use custom bottom bar only for compact layouts on top-level screens
    val useCustomBottomBar =
        isBottomNavScreen && layoutType == NavigationSuiteType.NavigationBar

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Player sheet drag state
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = PlayerSheetState.MINI,
            positionalThreshold = { it * 0.4f },
            velocityThreshold = { with(density) { 600.dp.toPx() } },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            decayAnimationSpec = exponentialDecay(),
        )
    }

    // 0f = MINI, 1f = EXPANDED
    val expandProgress by remember {
        derivedStateOf {
            val anchors = draggableState.anchors
            val mini = anchors.positionOf(PlayerSheetState.MINI)
            val expanded = anchors.positionOf(PlayerSheetState.EXPANDED)

            if (mini == expanded || mini.isNaN() || expanded.isNaN()) 0f
            else {
                val raw =
                    1f - (draggableState.offset - expanded) / (mini - expanded)
                raw.coerceIn(0f, 1f)
            }
        }
    }

    // Move player sheet to MINI on back press if currently EXPANDED
    BackHandler(
        enabled = sheetController.isVisible &&
                draggableState.currentValue == PlayerSheetState.EXPANDED
    ) {
        scope.launch {
            draggableState.animateTo(PlayerSheetState.MINI)
        }
    }

    // Provide appBar, controllers and back stack to the composition
    CompositionLocalProvider(
        LocalAppBarController provides appBarController,
        LocalNavBackStack provides backStack,
        LocalPlayerSheetController provides sheetController,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeightPx = with(density) { maxHeight.toPx() }
            val miniHeightPx = with(density) { MINI_PLAYER_HEIGHT.toPx() }
            val navBarHeightPx = with(density) { NAV_BAR_HEIGHT.toPx() }

            val bottomOffsetPx =
                if (useCustomBottomBar) navBarHeightPx else 0f

            // Update drag anchors once dimensions are known
            LaunchedEffect(screenHeightPx, bottomOffsetPx) {
                draggableState.updateAnchors(
                    DraggableAnchors {
                        PlayerSheetState.EXPANDED at 0f
                        PlayerSheetState.MINI at
                                (screenHeightPx - miniHeightPx - bottomOffsetPx)
                    }
                )
            }

            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    if (!useCustomBottomBar && isBottomNavScreen) {
                        BottomDestination.entries.forEach { destination ->
                            val isSelected =
                                currentKey::class == destination.key::class

                            item(
                                icon = {
                                    Icon(
                                        destination.icon,
                                        stringResource(destination.label)
                                    )
                                },
                                label = {
                                    Text(stringResource(destination.label))
                                },
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        backStack.clear()
                                        backStack.add(Screen.Music)
                                        if (destination.key::class != Screen.Music::class) {
                                            backStack.add(destination.key)
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                layoutType = if (layoutType == NavigationSuiteType.NavigationBar)
                    NavigationSuiteType.None else layoutType,
            ) {
                val contentPaddingModifier =
                    if (useCustomBottomBar) Modifier.padding(bottom = NAV_BAR_HEIGHT)
                    else Modifier

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(appBarController.state.title) },
                            navigationIcon = {
                                if (appBarController.state.showBack) {
                                    IconButton(
                                        onClick = { backStack.removeLastOrNull() }
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                        )
                                    }
                                }
                            },
                        )
                    },
                ) { innerPadding ->
                    ZillNavDisplay(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .then(contentPaddingModifier),
                        startDestination = startDestination,
                        currentKey = currentKey
                    )
                }
            }

            // Player sheet (slides behind bottom bar)
            if (sheetController.isVisible) {
                PlayerDraggableSheet(
                    draggableState = draggableState,
                    expandProgress = expandProgress,
                    onExpand = {
                        scope.launch {
                            draggableState.animateTo(PlayerSheetState.EXPANDED)
                        }
                    },
                    onCollapse = {
                        scope.launch {
                            draggableState.animateTo(PlayerSheetState.MINI)
                        }
                    },
                )
            }

            // Custom bottom navigation
            if (useCustomBottomBar) {
                val navTranslationY by remember {
                    derivedStateOf { navBarHeightPx * expandProgress }
                }
                val navAlpha by remember {
                    derivedStateOf { 1f - expandProgress }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(NAV_BAR_HEIGHT)
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = navTranslationY
                            alpha = navAlpha
                        }
                ) {
                    NavigationBar {
                        BottomDestination.entries.forEach { destination ->
                            val isSelected =
                                currentKey::class == destination.key::class

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        destination.icon,
                                        stringResource(destination.label)
                                    )
                                },
                                label = {
                                    Text(stringResource(destination.label))
                                },
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        backStack.clear()
                                        backStack.add(Screen.Music)
                                        if (destination.key::class != Screen.Music::class) {
                                            backStack.add(destination.key)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
