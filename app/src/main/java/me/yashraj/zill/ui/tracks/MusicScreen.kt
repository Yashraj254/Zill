package me.yashraj.zill.ui.tracks

import androidx.compose.runtime.Composable
import me.yashraj.zill.ui.permission.PermissionType
import me.yashraj.zill.ui.permission.components.RequestPermission

@Composable
fun MusicScreen() {

    RequestPermission(
        permissionType = PermissionType.ReadAudio,
        onPermissionGranted = { }
    )

}