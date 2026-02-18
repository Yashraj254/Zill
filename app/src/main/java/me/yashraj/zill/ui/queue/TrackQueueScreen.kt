package me.yashraj.zill.ui.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.ui.core.DraggableItem
import me.yashraj.zill.ui.core.dragContainer
import me.yashraj.zill.ui.core.rememberDragDropState
import me.yashraj.zill.ui.player.PlayerViewModel
import me.yashraj.zill.ui.theme.IcyBgBottom
import me.yashraj.zill.ui.theme.IcyPrimary
import me.yashraj.zill.ui.theme.IcySecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackQueueScreen(
    queue: List<Track>,
    currentTrack: Track?,
    onDismiss: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        playerViewModel.onReorder(fromIndex, toIndex) // notify ViewModel of new order
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = IcyBgBottom,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(IcySecondary.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Queue",
                color = IcyPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .dragContainer(dragDropState),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(items = queue, key = { _, track -> track.id }) { index, track ->
                    DraggableItem(dragDropState = dragDropState, index = index) { isDragging, startDrag ->
                        val isCurrent = track.id == currentTrack?.id
                        QueueTrackItem(
                            track = track,
                            isCurrentTrack = isCurrent,
                            onClick = { playerViewModel.onPlayFromPlaylist(queue, index) },
                            onDragHandleTouch = { startDrag() },
                            onDragOffset = { offset -> dragDropState.onDrag(offset) },
                            onDragEnd = { dragDropState.onDragInterrupted() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
