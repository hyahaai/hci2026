package com.example.emotiongallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotiongallery.data.GalleryDatabase
import com.example.emotiongallery.data.GalleryPhotoEntity
import com.example.emotiongallery.data.PhotoRecordEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class GalleryPhoto(
    val id: Int,
    val colors: List<Color>,
    val imageRes: Int? = null
)

data class PhotoRecord(
    val emotion: String = "",
    val memo: String = ""
)

enum class PresentationMode {
    V1_QUICK_BUTTON,
    V2_RADIAL_PALETTE,
    V3_MULTI_SELECT
}

data class RadialMenuInfo(
    val photoId: Int,
    val initialTouchPos: Offset,
    val currentTouchPos: Offset
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmotionGalleryApp()
        }
    }
}

@Composable
fun EmotionGalleryApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF6F7FA)
    ) {
        GalleryRootScreen()
    }
}

@Composable
fun GalleryRootScreen() {
    val context = LocalContext.current
    val database = remember { GalleryDatabase.getDatabase(context) }
    val dao = database.galleryDao()
    val scope = rememberCoroutineScope()

    val photos by dao.getAllPhotos().collectAsState(initial = emptyList())
    val recordsFromDb by dao.getAllRecords().collectAsState(initial = emptyList())

    val records = remember(recordsFromDb) {
        mutableStateMapOf<Int, PhotoRecord>().apply {
            recordsFromDb.forEach { entity ->
                put(entity.photoId, PhotoRecord(entity.emotion, entity.memo))
            }
        }
    }

    LaunchedEffect(photos) {
        if (photos.isEmpty()) {
            val initialPhotos = listOf(
                GalleryPhotoEntity(id = 1, colorStart = Color(0xFF8D6E63).toArgb(), colorEnd = Color(0xFFD7CCC8).toArgb(), imageRes = R.drawable.p_043878c4f08669b06b83ce0e3f119e41),
                GalleryPhotoEntity(id = 2, colorStart = Color(0xFF64B5F6).toArgb(), colorEnd = Color(0xFFFFF59D).toArgb(), imageRes = R.drawable.p_139c0466879591993e40b0317e330199),
                GalleryPhotoEntity(id = 3, colorStart = Color(0xFFFFB74D).toArgb(), colorEnd = Color(0xFFFFE0B2).toArgb(), imageRes = R.drawable.p_3bcd7f51d613f55dc7d667933fd925f6),
                GalleryPhotoEntity(id = 4, colorStart = Color(0xFF4FC3F7).toArgb(), colorEnd = Color(0xFFC8E6C9).toArgb(), imageRes = R.drawable.p_412ef09c4b09415e6546e596698e1d18),
                GalleryPhotoEntity(id = 5, colorStart = Color(0xFFFFCC80).toArgb(), colorEnd = Color(0xFFC5E1A5).toArgb(), imageRes = R.drawable.p_4e178a8c4a51abde1debc71d589e3f41),
                GalleryPhotoEntity(id = 6, colorStart = Color(0xFFA1887F).toArgb(), colorEnd = Color(0xFFFFECB3).toArgb(), imageRes = R.drawable.p_4ef476358b30a7d7ef43a88935067186),
                GalleryPhotoEntity(id = 7, colorStart = Color(0xFF90CAF9).toArgb(), colorEnd = Color(0xFFE3F2FD).toArgb(), imageRes = R.drawable.p_6771bec00b3f69cf5395c3024e56cc77),
                GalleryPhotoEntity(id = 8, colorStart = Color(0xFFBCAAA4).toArgb(), colorEnd = Color(0xFFD7CCC8).toArgb(), imageRes = R.drawable.p_6ce838ef96fa9976aa15154e8e3cbde0),
                GalleryPhotoEntity(id = 9, colorStart = Color(0xFFAED581).toArgb(), colorEnd = Color(0xFFFFF9C4).toArgb(), imageRes = R.drawable.p_76433c00c62ae83e9e2006b03a0b4fc4),
                GalleryPhotoEntity(id = 10, colorStart = Color(0xFFFFB74D).toArgb(), colorEnd = Color(0xFFFFF3E0).toArgb(), imageRes = R.drawable.p_99ae8324c99a3101e670383bd2d7d517),
                GalleryPhotoEntity(id = 11, colorStart = Color(0xFFFFA726).toArgb(), colorEnd = Color(0xFFE1F5FE).toArgb(), imageRes = R.drawable.df3c25d0b12acae8ced735dae44bc7e1),
                GalleryPhotoEntity(id = 12, colorStart = Color(0xFFB0BEC5).toArgb(), colorEnd = Color(0xFFECEFF1).toArgb(), imageRes = R.drawable.c1eea27b01a083f771d8f9ad1b649b48),
                GalleryPhotoEntity(id = 13, colorStart = Color(0xFFFFAB91).toArgb(), colorEnd = Color(0xFFFBE9E7).toArgb(), imageRes = R.drawable.e29bdece8b6d9d69bc9011bae832c402)
            )
            dao.insertPhotos(initialPhotos)
        }
    }

    val galleryPhotos = remember(photos) {
        photos.map { entity ->
            GalleryPhoto(
                id = entity.id,
                colors = listOf(Color(entity.colorStart), Color(entity.colorEnd)),
                imageRes = entity.imageRes
            )
        }
    }

    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedEmotionFilter by remember { mutableStateOf<String?>(null) }
    var presentationMode by remember { mutableStateOf(PresentationMode.V2_RADIAL_PALETTE) }

    val onToggleMode = {
        presentationMode = when (presentationMode) {
            PresentationMode.V1_QUICK_BUTTON -> PresentationMode.V2_RADIAL_PALETTE
            PresentationMode.V2_RADIAL_PALETTE -> PresentationMode.V3_MULTI_SELECT
            PresentationMode.V3_MULTI_SELECT -> PresentationMode.V1_QUICK_BUTTON
        }
    }

    if (selectedPhotoIndex == null) {
        MainGalleryScreen(
            photos = galleryPhotos,
            records = records,
            searchText = searchText,
            onSearchChange = { searchText = it },
            selectedEmotionFilter = selectedEmotionFilter,
            onEmotionFilterChange = { selectedEmotionFilter = it },
            presentationMode = presentationMode,
            onToggleMode = onToggleMode,
            onPhotoClick = { photo ->
                selectedPhotoIndex = galleryPhotos.indexOf(photo)
            },
            onQuickSaveEmotion = { photoId, emotion ->
                scope.launch {
                    val oldRecord = records[photoId] ?: PhotoRecord()
                    val newEmotion = if (oldRecord.emotion == emotion) "" else emotion
                    val newRecord = oldRecord.copy(emotion = newEmotion)
                    dao.insertRecord(PhotoRecordEntity(photoId, newRecord.emotion, newRecord.memo))
                }
            },
            onResetAll = {
                scope.launch {
                    dao.deleteAllRecords()
                }
            }
        )
    } else {
        val currentIndex = selectedPhotoIndex ?: 0
        if (currentIndex >= galleryPhotos.size) {
            selectedPhotoIndex = null
            return
        }
        val photo = galleryPhotos[currentIndex]

        DetailPhotoScreen(
            photo = photo,
            record = records[photo.id] ?: PhotoRecord(),
            presentationMode = presentationMode,
            onToggleMode = onToggleMode,
            onResetAll = {
                scope.launch {
                    dao.deleteAllRecords()
                }
            },
            onBack = { selectedPhotoIndex = null },
            onPrevious = {
                selectedPhotoIndex = if (currentIndex == 0) galleryPhotos.lastIndex else currentIndex - 1
            },
            onNext = {
                selectedPhotoIndex = if (currentIndex == galleryPhotos.lastIndex) 0 else currentIndex + 1
            },
            onSaveMemo = { memo ->
                scope.launch {
                    val oldRecord = records[photo.id] ?: PhotoRecord()
                    val newRecord = oldRecord.copy(memo = memo)
                    dao.insertRecord(PhotoRecordEntity(photo.id, newRecord.emotion, newRecord.memo))
                }
            },
            onSaveEmotion = { emotion ->
                scope.launch {
                    val oldRecord = records[photo.id] ?: PhotoRecord()
                    val newRecord = oldRecord.copy(emotion = emotion)
                    dao.insertRecord(PhotoRecordEntity(photo.id, newRecord.emotion, newRecord.memo))
                }
            }
        )
    }
}

@Composable
fun MainGalleryScreen(
    photos: List<GalleryPhoto>,
    records: Map<Int, PhotoRecord>,
    searchText: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit,
    selectedEmotionFilter: String?,
    onEmotionFilterChange: (String?) -> Unit,
    presentationMode: PresentationMode,
    onToggleMode: () -> Unit,
    onQuickSaveEmotion: (Int, String) -> Unit,
    onResetAll: () -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit
) {
    val filteredPhotos = photos.filter { photo ->
        val record = records[photo.id]
        if (selectedEmotionFilter != null && record?.emotion != selectedEmotionFilter) return@filter false
        val searchableText = listOf(record?.memo.orEmpty(), record?.emotion.orEmpty()).joinToString(" ")
        val query = searchText.text.trim()
        val queryWords = query.split("\\s+".toRegex()).filter { it.isNotBlank() }
        query.isBlank() || queryWords.all { word -> searchableText.contains(word, ignoreCase = true) }
    }

    val emotionOrder = listOf("기쁨", "평온", "슬픔", "화남", "")
    val groupedPhotos = filteredPhotos.groupBy { records[it.id]?.emotion.orEmpty() }
    val sortedEmotions = emotionOrder.filter { it in groupedPhotos.keys || (it == "" && groupedPhotos.containsKey("")) }

    // 시각적 순서를 반영한 통합 리스트 (범위 선택용)
    val visualOrderPhotos = remember(groupedPhotos, sortedEmotions) {
        sortedEmotions.flatMap { groupedPhotos[it].orEmpty() }
    }

    var radialMenuState by remember { mutableStateOf<RadialMenuInfo?>(null) }
    var v1LongPressedPhotoId by remember { mutableStateOf<Int?>(null) }
    
    // V3 State
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateMapOf<Int, Boolean>() }
    var draggedStackOffset by remember { mutableStateOf<Offset?>(null) }
    
    // Position tracking for swipe-to-select and auto-scroll
    val photoBoundsMap = remember { mutableStateOf(mutableMapOf<Int, Rect>()) }.value
    var isSwipeSelecting by remember { mutableStateOf(false) }
    var swipeSelectionStartIndex by remember { mutableStateOf<Int?>(null) }
    val initialSelectedIdsAtGesture = remember { mutableStateMapOf<Int, Boolean>() }
    var currentTouchPosForScroll by remember { mutableStateOf<Offset?>(null) }
    
    val gridState = rememberLazyGridState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val density = LocalDensity.current
        val dropZoneHeightPx = with(density) { 180.dp.toPx() }

        // Fixed Auto-scroll logic
        LaunchedEffect(isSwipeSelecting, draggedStackOffset != null) {
            if (isSwipeSelecting || draggedStackOffset != null) {
                while (true) {
                    val pos = draggedStackOffset ?: currentTouchPosForScroll
                    if (pos == null) break
                    
                    val scrollThreshold = with(density) { 100.dp.toPx() }
                    var scrolled = false
                    
                    if (pos.y < scrollThreshold) {
                        gridState.scrollBy(-30f)
                        scrolled = true
                    } else if (pos.y > screenHeight - scrollThreshold) {
                        gridState.scrollBy(30f)
                        scrolled = true
                    }
                    
                    if (!scrolled) break
                    delay(16) 
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (presentationMode == PresentationMode.V3_MULTI_SELECT && isSelectionMode) {
                SelectionHeader(
                    selectedCount = selectedIds.count { it.value },
                    onClose = {
                        isSelectionMode = false
                        selectedIds.clear()
                    }
                )
            } else {
                HeaderBar(
                    presentationMode = presentationMode,
                    onToggleMode = onToggleMode,
                    onResetAll = onResetAll
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
            Text(text = "어떤 사진을 검색할까요?", fontSize = 26.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(start = 36.dp))
            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.padding(horizontal = 22.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SearchBox(value = searchText, onValueChange = onSearchChange, placeholder = "메모 검색...", modifier = Modifier.weight(1f))
                if (selectedEmotionFilter != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(28.dp)).background(Color(0xFF7CB342)).clickable { onEmotionFilterChange(null) }, contentAlignment = Alignment.Center) {
                        val emoji = when(selectedEmotionFilter) { "기쁨" -> "😃"; "평온" -> "😐"; "슬픔" -> "😭"; "화남" -> "😡"; else -> "✓" }
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.padding(horizontal = 22.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("기쁨", "평온", "슬픔", "화남").forEach { emotion ->
                    val isSelected = selectedEmotionFilter == emotion
                    val emoji = when(emotion) { "기쁨" -> "😃"; "평온" -> "😐"; "슬픔" -> "😭"; "화남" -> "😡"; else -> "" }
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSelected) Color(0xFF7CB342) else Color(0xFFEDEFF8)).clickable { onEmotionFilterChange(if (isSelected) null else emotion) }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(text = "$emoji $emotion", fontSize = 13.sp, color = if (isSelected) Color.White else Color(0xFF737783), fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3), 
                horizontalArrangement = Arrangement.spacedBy(2.dp), 
                verticalArrangement = Arrangement.spacedBy(2.dp), 
                modifier = Modifier.fillMaxSize()
            ) {
                sortedEmotions.forEach { emotion ->
                    val photosInGroup = groupedPhotos[emotion].orEmpty()
                    if (photosInGroup.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(text = if (emotion.isEmpty()) "분류되지 않음" else emotion, modifier = Modifier.padding(start = 22.dp, top = 16.dp, bottom = 8.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
                        }
                        items(photosInGroup) { photo ->
                            val isSelected = selectedIds[photo.id] == true
                            PhotoThumbnail(
                                photo = photo,
                                record = records[photo.id],
                                isSelected = isSelected,
                                showSelectionOverlay = presentationMode == PresentationMode.V3_MULTI_SELECT && isSelectionMode,
                                onBoundsReported = { bounds ->
                                    photoBoundsMap[photo.id] = bounds
                                },
                                onClick = {
                                    if (presentationMode == PresentationMode.V3_MULTI_SELECT && isSelectionMode) {
                                        selectedIds[photo.id] = !isSelected
                                    } else {
                                        onPhotoClick(photo)
                                    }
                                },
                                onRadialMenuStart = { touchPos ->
                                    if (presentationMode == PresentationMode.V2_RADIAL_PALETTE) {
                                        radialMenuState = RadialMenuInfo(photo.id, touchPos, touchPos)
                                    } else if (presentationMode == PresentationMode.V1_QUICK_BUTTON) {
                                        v1LongPressedPhotoId = photo.id
                                    } else if (presentationMode == PresentationMode.V3_MULTI_SELECT) {
                                        if (!isSelectionMode) {
                                            isSelectionMode = true
                                            selectedIds[photo.id] = true
                                            isSwipeSelecting = true
                                            swipeSelectionStartIndex = visualOrderPhotos.indexOf(photo)
                                            initialSelectedIdsAtGesture.clear()
                                            selectedIds.forEach { (id, sel) -> initialSelectedIdsAtGesture[id] = sel }
                                        } else if (selectedIds[photo.id] == true) {
                                            draggedStackOffset = touchPos
                                            isSwipeSelecting = false
                                        } else {
                                            selectedIds[photo.id] = true
                                            isSwipeSelecting = true
                                            swipeSelectionStartIndex = visualOrderPhotos.indexOf(photo)
                                            initialSelectedIdsAtGesture.clear()
                                            selectedIds.forEach { (id, sel) -> initialSelectedIdsAtGesture[id] = sel }
                                        }
                                    }
                                },
                                onRadialMenuMove = { current ->
                                    currentTouchPosForScroll = current
                                    if (presentationMode == PresentationMode.V2_RADIAL_PALETTE) {
                                        radialMenuState = radialMenuState?.copy(currentTouchPos = current)
                                    } else if (presentationMode == PresentationMode.V3_MULTI_SELECT) {
                                        if (draggedStackOffset != null) {
                                            draggedStackOffset = current
                                        } else if (isSwipeSelecting) {
                                            // 시각적 순서 기반 범위 선택
                                            val currentHoveredId = photoBoundsMap.entries.find { it.value.contains(current) }?.key
                                            val currentIndex = visualOrderPhotos.indexOfFirst { it.id == currentHoveredId }
                                            
                                            if (swipeSelectionStartIndex != null && currentIndex != -1) {
                                                val start = swipeSelectionStartIndex!!
                                                val range = if (start < currentIndex) start..currentIndex else currentIndex..start
                                                
                                                // 현재 제스처 범위 외에는 초기 상태로 복구 (Deselection 지원)
                                                visualOrderPhotos.forEach { photoItem ->
                                                    selectedIds[photoItem.id] = initialSelectedIdsAtGesture[photoItem.id] ?: false
                                                }
                                                // 현재 범위 내 아이템들 선택
                                                range.forEach { idx ->
                                                    selectedIds[visualOrderPhotos[idx].id] = true
                                                }
                                            }
                                        }
                                    }
                                },
                                onRadialMenuEnd = {
                                    currentTouchPosForScroll = null
                                    if (presentationMode == PresentationMode.V3_MULTI_SELECT && draggedStackOffset != null) {
                                        val offset = draggedStackOffset!!
                                        if (offset.y > screenHeight - dropZoneHeightPx) {
                                            val zoneWidth = screenWidth / 4
                                            val index = (offset.x / zoneWidth).toInt().coerceIn(0, 3)
                                            val labels = listOf("기쁨", "평온", "슬픔", "화남")
                                            val selectedLabel = labels[index]
                                            
                                            selectedIds.filter { it.value }.keys.forEach { id ->
                                                onQuickSaveEmotion(id, selectedLabel)
                                            }
                                            isSelectionMode = false
                                            selectedIds.clear()
                                        }
                                    }
                                    draggedStackOffset = null
                                    isSwipeSelecting = false
                                    swipeSelectionStartIndex = null
                                }
                            )
                        }
                    }
                }
            }
        }

        if (presentationMode == PresentationMode.V2_RADIAL_PALETTE) {
            radialMenuState?.let { info ->
                RadialEmotionPicker(info = info, onConfirm = { onQuickSaveEmotion(info.photoId, it) }, onDismiss = { radialMenuState = null })
            }
        } else if (v1LongPressedPhotoId != null) {
            val currentPhoto = photos.find { it.id == v1LongPressedPhotoId }
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { v1LongPressedPhotoId = null }, contentAlignment = Alignment.Center) {
                Card(modifier = Modifier.padding(32.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    val currentEmotion = records[v1LongPressedPhotoId]?.emotion.orEmpty()
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "감정 선택 (V1)", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("기쁨" to "😃", "평온" to "😐", "슬픔" to "😭", "화남" to "😡").forEach { (label, emoji) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onQuickSaveEmotion(v1LongPressedPhotoId!!, label); v1LongPressedPhotoId = null }) {
                                    val isSelected = currentEmotion == label
                                    Text(text = emoji, fontSize = 36.sp); Text(text = label, fontSize = 12.sp, color = if (isSelected) Color(0xFF4CAF50) else Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (presentationMode == PresentationMode.V3_MULTI_SELECT && draggedStackOffset != null) {
            val count = selectedIds.count { it.value }
            
            // Drop Zones Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                        )
                    )
                    .padding(bottom = 30.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf("기쁨" to "😃", "평온" to "😐", "슬픔" to "😭", "화남" to "😡").forEachIndexed { index, (label, emoji) ->
                        val zoneWidth = screenWidth / 4
                        val isHovered = draggedStackOffset!!.y > screenHeight - dropZoneHeightPx && 
                                        draggedStackOffset!!.x > index * zoneWidth && 
                                        draggedStackOffset!!.x < (index + 1) * zoneWidth
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .size(if (isHovered) 90.dp else 75.dp)
                                .scale(if (isHovered) 1.1f else 1.0f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isHovered) Color(0xFF7CB342) else Color.White.copy(alpha = 0.9f))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = emoji, fontSize = if (isHovered) 36.sp else 30.sp)
                            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isHovered) Color.White else Color.Black)
                        }
                    }
                }
            }

            // Dragged Stack Visual
            Box(
                modifier = Modifier
                    .offset { IntOffset(draggedStackOffset!!.x.roundToInt(), draggedStackOffset!!.y.roundToInt()) }
                    .offset((-40).dp, (-40).dp)
                    .size(80.dp)
                    .shadow(24.dp, RoundedCornerShape(12.dp))
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset((i * 4).dp, (-(i * 4)).dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 1f - i * 0.2f))
                            .background(Brush.linearGradient(listOf(Color(0xFF7CB342), Color(0xFF558B2F))), alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(8.dp, (-8).dp)
                        .size(28.dp)
                        .background(Color.Red, CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = count.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PhotoThumbnail(
    photo: GalleryPhoto,
    record: PhotoRecord?,
    isSelected: Boolean = false,
    showSelectionOverlay: Boolean = false,
    onBoundsReported: (Rect) -> Unit = {},
    onClick: () -> Unit,
    onRadialMenuStart: (Offset) -> Unit,
    onRadialMenuMove: (Offset) -> Unit,
    onRadialMenuEnd: () -> Unit
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier.aspectRatio(1f).onGloballyPositioned { 
                globalPosition = it.positionInWindow()
                onBoundsReported(it.boundsInWindow())
            }
            .clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(photo.colors))
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { localOffset -> onRadialMenuStart(globalPosition + localOffset) },
                    onDrag = { change, _ -> onRadialMenuMove(globalPosition + change.position) },
                    onDragEnd = { onRadialMenuEnd() },
                    onDragCancel = { onRadialMenuEnd() }
                )
            }.clickable { onClick() }.padding(6.dp)
    ) {
        if (photo.imageRes != null && photo.imageRes != 0) {
            Image(
                painter = painterResource(id = photo.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        
        if (showSelectionOverlay) {
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)).background(if (isSelected) Color(0xFF7CB342).copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.1f)))
            if (isSelected) {
                Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp).size(24.dp).background(Color(0xFF7CB342), CircleShape).shadow(2.dp, CircleShape), contentAlignment = Alignment.Center) {
                    Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (!record?.emotion.isNullOrBlank()) {
            Box(modifier = Modifier.align(Alignment.TopEnd).size(28.dp).background(Color.White.copy(alpha = 0.9f), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = record!!.emotion.take(2), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SelectionHeader(selectedCount: Int, onClose: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(62.dp).background(Color(0xFFEDEFF8)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onClose) { Text("취소", color = Color.Black, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "${selectedCount}개 선택됨", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
fun BatchEmotionBar(onApplyEmotion: (String) -> Unit) {
    Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), shape = RoundedCornerShape(32.dp), elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("선택한 항목에 감정 입히기", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("기쁨" to "😃", "평온" to "😐", "슬픔" to "😭", "화남" to "😡").forEach { (label, emoji) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onApplyEmotion(label) }.padding(8.dp)) {
                        Text(text = emoji, fontSize = 32.sp); Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onApplyEmotion("") }.padding(8.dp)) {
                    Text(text = "✕", fontSize = 32.sp, color = Color.Red); Text(text = "삭제", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun RadialEmotionPicker(info: RadialMenuInfo, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val emotions = listOf("기쁨" to "😃", "평온" to "😐", "슬픔" to "😭", "화남" to "😡")
    val dx = info.currentTouchPos.x - info.initialTouchPos.x
    val dy = info.currentTouchPos.y - info.initialTouchPos.y
    val dist = sqrt(dx*dx + dy*dy)
    val hoveredIndex = if (dist > 50f) {
        val deg = atan2(dy, dx) * 180 / PI
        when { deg in -45.0..45.0 -> 0; deg in 45.0..135.0 -> 1; deg > 135.0 || deg < -135.0 -> 2; else -> 3 }
    } else null
    val isCenterHovered = dist < 30f
    val density = LocalDensity.current
    BoxWithConstraints(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.changes.any { it.pressed.not() }) {
                    if (isCenterHovered) onConfirm("") else hoveredIndex?.let { onConfirm(emotions[it].first) }
                    onDismiss()
                }
            }
        }
    }) {
        val paletteRadius = with(density) { 140.dp.toPx() }
        val pivotX = info.initialTouchPos.x.coerceIn(paletteRadius, constraints.maxWidth.toFloat() - paletteRadius)
        val pivotY = info.initialTouchPos.y.coerceIn(paletteRadius, constraints.maxHeight.toFloat() - paletteRadius)
        Box(modifier = Modifier.offset { IntOffset((pivotX - paletteRadius).roundToInt(), (pivotY - paletteRadius).roundToInt()) }.size(280.dp).shadow(12.dp, CircleShape).background(Color.White.copy(alpha = 0.95f), CircleShape))
        Box(modifier = Modifier.offset { IntOffset(pivotX.roundToInt(), pivotY.roundToInt()) }.size(if (isCenterHovered) 40.dp else 12.dp).offset(if (isCenterHovered) (-20).dp else (-6).dp, if (isCenterHovered) (-20).dp else (-6).dp).background(if (isCenterHovered) Color(0xFFFF5252) else Color(0xFF7CB342), CircleShape).shadow(if (isCenterHovered) 4.dp else 2.dp, CircleShape), contentAlignment = Alignment.Center) {
            if (isCenterHovered) Text("✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        emotions.forEachIndexed { index, (label, emoji) ->
            val rad = (index * 90.0) * PI / 180
            val itemRadiusPx = with(density) { (if (hoveredIndex == index) 95.dp else 85.dp).toPx() }
            val itemX = pivotX + itemRadiusPx * kotlin.math.cos(rad).toFloat()
            val itemY = pivotY + itemRadiusPx * kotlin.math.sin(rad).toFloat()
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset { IntOffset(itemX.roundToInt(), itemY.roundToInt()) }.offset((-40).dp, (-40).dp).size(80.dp).scale(if (hoveredIndex == index) 1.25f else 1.0f).padding(4.dp), verticalArrangement = Arrangement.Center) {
                Text(text = emoji, fontSize = 34.sp)
                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (hoveredIndex == index) Color(0xFF7CB342) else Color.Black)
            }
        }
    }
}

@Composable
fun HeaderBar(
    presentationMode: PresentationMode,
    onToggleMode: () -> Unit,
    onResetAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF7CB342), CircleShape)
                    .clickable { onToggleMode() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (presentationMode) {
                        PresentationMode.V1_QUICK_BUTTON -> "V1"
                        PresentationMode.V2_RADIAL_PALETTE -> "V2"
                        PresentationMode.V3_MULTI_SELECT -> "V3"
                    },
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 소형 초기화 버튼
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE))
                    .clickable { onResetAll() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↺", color = Color(0xFFD32F2F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Mode: ${
                when (presentationMode) {
                    PresentationMode.V1_QUICK_BUTTON -> "Classic"
                    PresentationMode.V2_RADIAL_PALETTE -> "Radial"
                    PresentationMode.V3_MULTI_SELECT -> "Selection"
                }
            }",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "⌑",
            fontSize = 37.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { onToggleMode() }
        )
    }
}

@Composable
fun SearchBox(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    TextField(value = value, onValueChange = onValueChange, singleLine = true, leadingIcon = { Text(text = "⌕", color = Color(0xFF737783), fontSize = 21.sp) }, placeholder = { Text(text = placeholder, color = Color(0xFF8E929E), fontSize = 14.sp) }, trailingIcon = { if (value.text.isNotEmpty()) Text(text = "✕", color = Color(0xFF737783), fontSize = 18.sp, modifier = Modifier.clickable { onValueChange(TextFieldValue("")) }) }, textStyle = TextStyle(color = Color.Black, fontSize = 15.sp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), shape = RoundedCornerShape(28.dp), colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFEDEFF8), unfocusedContainerColor = Color(0xFFEDEFF8), disabledContainerColor = Color(0xFFEDEFF8), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, cursorColor = Color.Black), modifier = modifier.height(56.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPhotoScreen(
    photo: GalleryPhoto,
    record: PhotoRecord,
    presentationMode: PresentationMode,
    onToggleMode: () -> Unit,
    onResetAll: () -> Unit,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSaveMemo: (String) -> Unit,
    onSaveEmotion: (String) -> Unit
) {
    var showMemoPopup by remember { mutableStateOf(false) }
    var showEmotionSheet by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA)).statusBarsPadding().navigationBarsPadding()) {
        HeaderBar(
            presentationMode = presentationMode,
            onToggleMode = onToggleMode,
            onResetAll = onResetAll
        )
        Spacer(modifier = Modifier.height(28.dp))
        Box(modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().height(330.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(photo.colors)), contentAlignment = Alignment.Center) {
            if (photo.imageRes != null && photo.imageRes != 0) {
                Image(
                    painter = painterResource(id = photo.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp).fillMaxWidth().height(1.dp).background(Color.Black))
        Text(text = if (record.memo.isBlank()) "여기에 메모를 작성하거나\n감정을 기록하세요." else record.memo, fontSize = 24.sp, color = if (record.memo.isBlank()) Color(0xFF777777) else Color.Black, lineHeight = 30.sp, modifier = Modifier.padding(horizontal = 18.dp).clickable { showMemoPopup = true })
        if (record.emotion.isNotBlank()) { Spacer(modifier = Modifier.height(12.dp)); Text(text = "기록된 감정: ${record.emotion}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 18.dp)) }
        Spacer(modifier = Modifier.weight(1f))
        BottomActionBar(onBack = onBack, onMemo = { showMemoPopup = true }, onEmotion = { showEmotionSheet = true }, onNext = onNext)
    }
    if (showMemoPopup) MemoPopup(initialMemo = record.memo, onDismiss = { showMemoPopup = false }, onValueChange = onSaveMemo)
    if (showEmotionSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showEmotionSheet = false }, sheetState = sheetState, containerColor = Color.White, shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)) {
            EmotionSheet(selectedEmotion = record.emotion, onSelect = { emotion -> onSaveEmotion(if (record.emotion == emotion) "" else emotion); showEmotionSheet = false })
        }
    }
}

@Composable
fun BottomActionBar(onBack: () -> Unit, onMemo: () -> Unit, onEmotion: () -> Unit, onNext: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 28.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "←", fontSize = 42.sp, color = Color.Black, modifier = Modifier.clickable { onBack() })
        Text(text = "✎", fontSize = 38.sp, color = Color.Black, modifier = Modifier.clickable { onMemo() })
        Text(text = "☺", fontSize = 46.sp, color = Color.Black, modifier = Modifier.clickable { onEmotion() })
        Text(text = "→", fontSize = 42.sp, color = Color.Black, modifier = Modifier.clickable { onNext() })
    }
}

@Composable
fun MemoPopup(initialMemo: String, onDismiss: () -> Unit, onValueChange: (String) -> Unit) {
    var memoText by remember(initialMemo) { mutableStateOf(initialMemo) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) { delay(500); focusRequester.requestFocus(); keyboardController?.show() }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth().height(230.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Button(onClick = { onDismiss() }, modifier = Modifier.height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) { Text(text = "완료", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) } }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(value = memoText, onValueChange = { memoText = it; onValueChange(it) }, placeholder = { Text(text = "메모를 직접 작성하세요", color = Color(0xFF888888), fontSize = 18.sp) }, textStyle = TextStyle(color = Color.Black, fontSize = 20.sp, lineHeight = 28.sp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, cursorColor = Color.Black), modifier = Modifier.fillMaxWidth().weight(1f).focusRequester(focusRequester))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { TextButton(onClick = onDismiss) { Text(text = "닫기", color = Color.Black, fontSize = 15.sp) } }
            }
        }
    }
}

@Composable
fun EmotionSheet(selectedEmotion: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "감정 기록", fontSize = 31.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp, bottom = 26.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            EmotionItem(label = "기쁨", emoji = "😃", selected = selectedEmotion == "기쁨", onClick = { onSelect("기쁨") })
            EmotionItem(label = "평온", emoji = "😐", selected = selectedEmotion == "평온", onClick = { onSelect("평온") })
            EmotionItem(label = "슬픔", emoji = "😭", selected = selectedEmotion == "슬픔", onClick = { onSelect("슬픔") })
            EmotionItem(label = "화남", emoji = "😡", selected = selectedEmotion == "화남", onClick = { onSelect("화남") })
        }
    }
}

@Composable
fun EmotionItem(label: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Text(text = label, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = if (selected) Color(0xFF4CAF50) else Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = emoji, fontSize = 43.sp)
    }
}
