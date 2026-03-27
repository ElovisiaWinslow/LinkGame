package com.example.linkgame.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linkgame.audio.AudioManager
import com.example.linkgame.data.repository.NicknameRepository
import com.example.linkgame.data.repository.SettingsRepository
import com.example.linkgame.game.model.ALL_LEVELS
import com.example.linkgame.game.model.LevelConfig
import com.example.linkgame.ui.components.NicknameDialog
import com.example.linkgame.ui.components.SettingsDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    onStartChallenge: () -> Unit,
    onStartEndless: (LevelConfig) -> Unit,
    onShowLeaderboard: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showEndlessOptions by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(ALL_LEVELS[0]) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var nickname by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        nickname = NicknameRepository.getNickname(context)
    }

    val bgmEnabled by SettingsRepository.isBgmEnabled(context).collectAsState(initial = true)
    val soundEnabled by SettingsRepository.isSoundEnabled(context).collectAsState(initial = true)

    BackHandler(enabled = true) {
        when {
            showSettingsDialog -> showSettingsDialog = false
            showNicknameDialog -> showNicknameDialog = false
            showExitDialog -> showExitDialog = false
            else -> showExitDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("连连看") },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Games, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text("连连看", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("经典配对消除游戏", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(32.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("昵称：${nickname ?: "未设置"}", fontSize = 14.sp)
                        }
                        TextButton(onClick = { showNicknameDialog = true }) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (nickname == null) "设置昵称" else "修改昵称")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Bolt, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("挑战模式", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("依次挑战4个难度关卡，全部通关即获胜", fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onStartChallenge,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Icon(Icons.Default.Bolt, null)
                            Spacer(Modifier.width(8.dp))
                            Text("开始挑战")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = if (showEndlessOptions) 8.dp else 16.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(8.dp))
                            Text("无尽模式", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("选择难度后不断挑战，挑战更高关卡", fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { showEndlessOptions = !showEndlessOptions },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Icon(if (showEndlessOptions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
                            Spacer(Modifier.width(4.dp))
                            Text(if (showEndlessOptions) "收起难度选择" else "选择难度")
                        }
                    }
                }

                if (showEndlessOptions) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Text("选择难度", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(ALL_LEVELS) { level ->
                                    FilterChip(
                                        selected = selectedDifficulty == level,
                                        onClick = { selectedDifficulty = level },
                                        label = { Text(level.name) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = { onStartEndless(selectedDifficulty) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.PlayArrow, null)
                                Spacer(Modifier.width(8.dp))
                                Text("开始无尽模式 (${selectedDifficulty.name})")
                            }
                        }
                    }
                }

                Button(
                    onClick = onShowLeaderboard,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.List, null)
                    Spacer(Modifier.width(8.dp))
                    Text("查看排行榜")
                }

                OutlinedButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.ExitToApp, null)
                    Spacer(Modifier.width(8.dp))
                    Text("退出游戏")
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            bgmEnabled = bgmEnabled,
            soundEnabled = soundEnabled,
            onDismiss = { showSettingsDialog = false },
            onConfirm = { newBgm, newSound ->
                scope.launch {
                    SettingsRepository.setBgmEnabled(context, newBgm)
                    SettingsRepository.setSoundEnabled(context, newSound)
                    AudioManager.setBgmEnabled(newBgm)
                    AudioManager.setSoundEnabled(newSound)
                }
                showSettingsDialog = false
            }
        )
    }

    if (showNicknameDialog) {
        NicknameDialog(
            currentNickname = nickname,
            onDismiss = { showNicknameDialog = false },
            onConfirm = { newNickname ->
                scope.launch {
                    NicknameRepository.saveNickname(context, newNickname)
                    nickname = newNickname
                }
                showNicknameDialog = false
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("退出游戏") },
            text = { Text("确定要退出游戏吗？") },
            confirmButton = {
                TextButton(onClick = { onExit() }) {
                    Text("退出")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}