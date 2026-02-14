package me.yashraj.zill.permission.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.yashraj.zill.permission.PermissionType


@Composable
fun PermissionRationaleDialog(
    permissionType: PermissionType,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = getPermissionIcon(permissionType),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(permissionType.title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = stringResource(permissionType.rationaleMessage),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        }
    )
}


/**
 * Get appropriate icon for permission type.
 */
@Composable
private fun getPermissionIcon(permissionType: PermissionType): ImageVector {
    return when (permissionType) {
        is PermissionType.ReadAudio -> Icons.Outlined.MusicNote
        is PermissionType.ReadVideo -> Icons.Outlined.VideoLibrary
        is PermissionType.WriteExternalStorage -> Icons.Outlined.CreateNewFolder
        is PermissionType.Notifications -> Icons.Outlined.Notifications
        is PermissionType.Custom -> Icons.Outlined.Security
    }
}