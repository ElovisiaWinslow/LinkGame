package com.example.linkgame.game.engine

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkgame.audio.AudioManager
import com.example.linkgame.data.model.LeaderboardEntry
import com.example.linkgame.data.repository.LeaderboardRepository
import com.example.linkgame.data.repository.NicknameRepository
import com.example.linkgame.game.logic.*
import com.example.linkgame.game.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameController(
    private val context: Context,
    private val mode: GameMode
) : ViewModel() {

    data class UiState(
        val board: Board,
        val selectedFirst: Pair<Int, Int>? = null,
        val selectedSecond: Pair<Int, Int>? = null,
        val pathCoords: List<Pair<Int, Int>>? = null,
        val score: Int = 0,
        val timeLeft: Int,
        val levelCleared: Boolean = false,
        val gameFinished: Boolean = false,
        val showSaveDialog: Boolean = false,
        val showNicknameForSave: Boolean = false,
        val showExitGameDialog: Boolean = false,    // 新增
        val totalTimeSeconds: Int = 0,
        val title: String = "",
        val challengeIndex: Int = 0,
        val endlessLevelNum: Int = 1,
        val currentConfig: LevelConfig
    )

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<UiState> = _uiState

    private var startTime = System.currentTimeMillis()
    private var timerJob: kotlinx.coroutines.Job? = null
    private var returnCallback: (() -> Unit)? = null

    fun setReturnCallback(callback: (() -> Unit)?) {
        returnCallback = callback
    }

    private fun createInitialState(): UiState {
        val config = when (mode) {
            is GameMode.Challenge -> ALL_LEVELS[0]
            is GameMode.Endless -> mode.difficulty
        }
        return UiState(
            board = generateSolvableBoardForLevel(config),
            timeLeft = config.timeLimit,
            currentConfig = config,
            title = buildTitle(config, endlessLevelNum = 1, challengeIndex = 0)
        )
    }

    private fun buildTitle(config: LevelConfig, endlessLevelNum: Int = 1, challengeIndex: Int = 0): String {
        return when (mode) {
            is GameMode.Challenge -> "挑战模式 - ${config.name}"
            is GameMode.Endless -> "无尽模式 - ${config.name} - 第 $endlessLevelNum 关"
        }
    }

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (state.gameFinished || state.levelCleared) break
                if (state.timeLeft > 0) {
                    _uiState.value = state.copy(
                        timeLeft = state.timeLeft - 1,
                        totalTimeSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                    )
                }
                if (_uiState.value.timeLeft == 0 && !state.levelCleared && !state.gameFinished) {
                    finishGame()
                }
            }
        }
    }

    fun onTileClick(r: Int, c: Int) {
        val state = _uiState.value
        if (state.levelCleared || state.gameFinished) return

        if (state.selectedFirst == null) {
            AudioManager.playClick()
            _uiState.value = state.copy(selectedFirst = Pair(r, c))
        } else if (state.selectedFirst?.first == r && state.selectedFirst?.second == c) {
            _uiState.value = state.copy(selectedFirst = null)
        } else {
            val first = state.selectedFirst!!
            val second = Pair(r, c)
            _uiState.value = state.copy(selectedSecond = second)

            if (state.board.cells[first.first][first.second] == state.board.cells[second.first][second.second] &&
                canConnectWithPadding(state.board, first.first, first.second, second.first, second.second)
            ) {
                AudioManager.playEliminate()
                val path = findConnectPath(state.board, first.first, first.second, second.first, second.second)
                _uiState.value = state.copy(pathCoords = path ?: listOf(first, second))

                val newBoard = removeTiles(state.board, first.first, first.second, second.first, second.second)
                val newScore = state.score + 10
                val cleared = isBoardCleared(newBoard)

                _uiState.value = _uiState.value.copy(
                    board = newBoard,
                    score = newScore,
                    selectedFirst = null,
                    selectedSecond = null,
                    levelCleared = cleared
                )

                if (cleared) {
                    handleLevelCleared()
                }

                viewModelScope.launch {
                    delay(1000)
                    _uiState.value = _uiState.value.copy(pathCoords = null)
                }
            } else {
                _uiState.value = state.copy(
                    selectedFirst = null,
                    selectedSecond = null,
                    pathCoords = null
                )
            }
        }
    }

    private fun handleLevelCleared() {
        val state = _uiState.value
        if (mode is GameMode.Challenge) {
            val nextIndex = state.challengeIndex + 1
            if (nextIndex >= ALL_LEVELS.size) {
                finishGame()
            } else {
                val nextConfig = ALL_LEVELS[nextIndex]
                _uiState.value = state.copy(
                    challengeIndex = nextIndex,
                    currentConfig = nextConfig,
                    board = generateSolvableBoardForLevel(nextConfig),
                    timeLeft = nextConfig.timeLimit,
                    score = state.score,
                    levelCleared = false,
                    title = buildTitle(nextConfig, challengeIndex = nextIndex)
                )
                startTimer()
            }
        } else if (mode is GameMode.Endless) {
            val newLevelNum = state.endlessLevelNum + 1
            _uiState.value = state.copy(
                endlessLevelNum = newLevelNum,
                board = generateSolvableBoardForLevel(state.currentConfig),
                timeLeft = state.currentConfig.timeLimit,
                levelCleared = false,
                title = buildTitle(state.currentConfig, endlessLevelNum = newLevelNum)
            )
            startTimer()
        }
    }

    fun nextLevel() {
        val state = _uiState.value
        if (state.levelCleared && !state.gameFinished) {
            if (mode is GameMode.Challenge && state.challengeIndex == ALL_LEVELS.size - 1) {
                finishGame()
            } else {
                handleLevelCleared()
            }
        }
    }

    fun exitGame() {
        finishGame()
    }

    private fun finishGame() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            gameFinished = true,
            showExitGameDialog = true   // 改为显示退出选择对话框
        )
    }

    // 显示退出选择对话框
    fun showExitGameDialog() {
        _uiState.value = _uiState.value.copy(showExitGameDialog = true)
    }

    fun dismissExitGameDialog() {
        _uiState.value = _uiState.value.copy(showExitGameDialog = false)
    }

    fun returnWithoutSaving() {
        dismissExitGameDialog()
        returnCallback?.invoke()
    }

    // 保存分数（需要昵称）
    fun saveScoreAndReturn() {
        viewModelScope.launch {
            val state = _uiState.value
            val nickname = NicknameRepository.getNickname(context)
            if (nickname.isNullOrBlank()) {
                _uiState.value = state.copy(
                    showExitGameDialog = false,
                    showNicknameForSave = true
                )
            } else {
                saveScoreWithNickname(nickname)
                returnCallback?.invoke()
            }
        }
    }

    fun saveScoreWithNickname(nickname: String) {
        viewModelScope.launch {
            val state = _uiState.value
            NicknameRepository.saveNickname(context, nickname)
            val entry = LeaderboardEntry(
                nickname = nickname,
                score = state.score,
                timeSeconds = state.totalTimeSeconds,
                mode = when (mode) {
                    is GameMode.Challenge -> "challenge"
                    is GameMode.Endless -> "endless"
                },
                difficulty = if (mode is GameMode.Endless) state.currentConfig.name else null
            )
            LeaderboardRepository.addEntry(context, entry)

            _uiState.value = state.copy(
                showExitGameDialog = false,
                showNicknameForSave = false,
                gameFinished = true
            )
            returnCallback?.invoke()
        }
    }

    fun dismissSaveDialogAndReturn() {
        _uiState.value = _uiState.value.copy(
            showSaveDialog = false,
            gameFinished = true
        )
        returnCallback?.invoke()
    }

    fun dismissSaveDialog() {
        _uiState.value = _uiState.value.copy(showSaveDialog = false)
    }

    fun dismissNicknameDialogOnly() {
        _uiState.value = _uiState.value.copy(
            showNicknameForSave = false,
            gameFinished = true
        )
        // 不调用 returnCallback，仅关闭对话框
    }

    fun dismissNicknameDialog() {
        _uiState.value = _uiState.value.copy(
            showNicknameForSave = false,
            gameFinished = true
        )
        returnCallback?.invoke()
    }
}