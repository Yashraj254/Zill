package me.yashraj.zill.ui.permission.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.yashraj.zill.ui.permission.PermissionType

@Composable
fun RequestPermission(
    permissionType: PermissionType,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current

    // Skip if no permissions needed
    if (permissionType.permissions.isEmpty()) {
        LaunchedEffect(Unit) { onPermissionGranted() }
        return
    }

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }

        if (allGranted) {
            onPermissionGranted()
        } else {
            val shouldShowRationale = permissionType.permissions.any { permission ->
                permissionsMap[permission] == false &&
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as androidx.activity.ComponentActivity,
                            permission
                        )
            }

            if (shouldShowRationale) {
                showRationaleDialog = true
            } else {
                showPermanentlyDeniedDialog = true
            }

            onPermissionDenied()
        }
    }

    // Check permissions on first composition
    LaunchedEffect(Unit) {
        val allGranted = permissionType.permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            onPermissionGranted()
        } else {
            permissionLauncher.launch(permissionType.permissions.toTypedArray())
        }
    }

    // Rationale Dialog
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            permissionType = permissionType,
            onDismiss = { showRationaleDialog = false },
            onConfirm = {
                showRationaleDialog = false
                permissionLauncher.launch(permissionType.permissions.toTypedArray())
            }
        )
    }

    // Permanently Denied Dialog
    if (showPermanentlyDeniedDialog) {
        PermissionPermanentlyDeniedDialog(
            permissionType = permissionType,
            onDismiss = { showPermanentlyDeniedDialog = false },
            onOpenSettings = {
                showPermanentlyDeniedDialog = false
                openAppSettings(context)
            }
        )
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun rememberPermissionState(permissionType: PermissionType): Boolean {
    val context = LocalContext.current

    return remember(permissionType) {
        if (permissionType.permissions.isEmpty()) {
            true
        } else {
            permissionType.permissions.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        }
    }
}

