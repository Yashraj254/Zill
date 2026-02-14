package me.yashraj.zill.permission

import android.Manifest
import android.os.Build
import androidx.annotation.StringRes
import me.yashraj.zill.R

sealed class PermissionType(
    val permissions: List<String>,
    @StringRes val title: Int,
    @StringRes val rationaleMessage: Int,
    @StringRes val deniedMessage: Int,
    @StringRes val settingsInstructions: Int
) {
    /**
     * Audio read permission (Android 8+)
     */
    object ReadAudio : PermissionType(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        },
        title = R.string.permission_read_audio_title,
        rationaleMessage = R.string.permission_read_audio_rationale,
        deniedMessage = R.string.permission_read_audio_denied,
        settingsInstructions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.string.permission_read_audio_settings_instructions
        } else {
            R.string.permission_storage_settings_instructions
        }
    )

    /**
     * Video read permission (Android 8+)
     */
    object ReadVideo : PermissionType(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        },
        title = R.string.permission_read_video_title,
        rationaleMessage = R.string.permission_read_video_rationale,
        deniedMessage = R.string.permission_read_video_denied,
        settingsInstructions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            R.string.permission_read_video_settings_instructions
        } else {
            R.string.permission_storage_settings_instructions
        }
    )

    /**
     * Write/Modify permission for Android 10-12
     * For Android 13+, use MediaStore APIs with user consent
     */
    object WriteExternalStorage : PermissionType(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ requires special permission via Settings
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        },
        title = R.string.permission_write_external_storage_title,
        rationaleMessage = R.string.permission_write_external_storage_rationale,
        deniedMessage = R.string.permission_write_external_storage_denied,
        settingsInstructions = R.string.permission_storage_settings_instructions
    )

    /**
     * Notification permission (Android 13+)
     */
    object Notifications : PermissionType(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        },
        title = R.string.permission_notifications_title,
        rationaleMessage = R.string.permission_notifications_rationale,
        deniedMessage = R.string.permission_notifications_denied,
        settingsInstructions = R.string.permission_notifications_settings_instructions
    )

    /**
     * Custom permission type for flexibility
     */
    class Custom(
        permissions: List<String>,
        @StringRes title: Int,
        @StringRes rationaleMessage: Int,
        @StringRes deniedMessage: Int,
        @StringRes settingsInstructions: Int
    ) : PermissionType(
        permissions = permissions,
        title = title,
        rationaleMessage = rationaleMessage,
        deniedMessage = deniedMessage,
        settingsInstructions = settingsInstructions
    )
}