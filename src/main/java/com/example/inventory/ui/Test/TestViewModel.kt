package com.example.inventory.ui.Test

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.reflect.Array.get

class TestViewModel( itemsRepository: ItemsRepository) : ViewModel() {
    val testUiState: StateFlow<TestItems> = itemsRepository.getAllItemsStream()
        .map { TestItems(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TestItems()
        )
    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()
    var userAnswer by mutableStateOf("")
        private set

    private var usedWords: MutableList<Item> = mutableListOf()
    private lateinit var currentWord: String
    private lateinit var correctAnswer: String
    //    private val itemsList: StateFlow<TestItems> = itemsRepository.getAllItemsStream().map { TestItems(it) }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(
//      TIMEOUT_MILLIS), initialValue = TestItems()
//    )
//    private var itemsList: List<Item> = testUiState.value.itemList

    init {

        try {
            correctAnswer=""
            resetGame()
        }catch (e : NoSuchElementException){
            e.stackTrace
        }
    }

    private val itemsList: List<Item>
        get() = testUiState.value.itemList
//val itemsListList: MutableList<Item> = mutableListOf()

    val listSize: Int
        get() = itemsList.size

    fun resetGame() {
        usedWords.clear()
        pickRandomObject()
        _uiState.value = TestUiState(
            currentWord = currentWord,
            currentCorrectAnswer = correctAnswer
        )
    }

    fun updateUserAnswer(answeredWord: String) {
        userAnswer = answeredWord
    }

    fun checkUserAnswer() {
        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.inc()
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState -> currentState.copy(isAnswerWrong = true) }
            val score = _uiState.value.score
            updateGameState(score)
        }
        updateUserAnswer("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == listSize) {
            _uiState.update { currentState -> currentState.copy(isAnswerWrong = false, score = updatedScore, isGameOver = true) }
        } else {
            pickRandomObject()
            _uiState.update { currentState -> currentState.copy(isAnswerWrong = false, currentWord = currentWord, currentCorrectAnswer = correctAnswer, currentWordCount = currentState.currentWordCount.inc(), score = updatedScore) }
        }
    }

    private fun pickRandomObject() {
        val Object = itemsList.random()
        currentWord = Object.wordPl
        correctAnswer = Object.wordEng
        if (usedWords.contains(Object)) {
            pickRandomObject()
        } else {
            usedWords.add(Object)
        }
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class TestItems(val itemList: List<Item> = listOf())