package com.example.inventory.ui.Test

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import java.text.NumberFormat

object TestDestination : NavigationDestination {
    override val route = "test"
    override val titleRes = R.string.test_screen_tittle
}


@Composable
fun TestScreen(modifier: Modifier=Modifier, testViewModel: TestViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    val homeUiState by testViewModel.testUiState.collectAsState()
    val testUiState by testViewModel.uiState.collectAsState()
    Column(modifier = modifier
        .verticalScroll(rememberScrollState())
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = homeUiState.itemList.toString())
        TestStatus(wordCount = testUiState.currentWordCount, maxWords = testViewModel.listSize)
        TestLayout(currentWord = testUiState.currentWord, userGuess = testViewModel.userAnswer, onUserGuessChange = {testViewModel.updateUserAnswer(it)}, onKeyboardDone = {testViewModel.checkUserAnswer()})
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {testViewModel.checkUserAnswer()}, modifier = Modifier.weight(1f)) {
                Text(text = "Zatwierdz")
            }
        }
        if (testUiState.isGameOver){
            FinalScoreDialog(score = testUiState.score, onPlayAgain = { testViewModel.resetGame()})
        }
    }
}


@Composable
fun TestStatus(wordCount: Int, maxWords: Int, modifier: Modifier=Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
        .size(48.dp), horizontalArrangement = Arrangement.Center) {
        Text(text = "$wordCount z $maxWords słów", fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun TestLayout(currentWord: String, modifier: Modifier=Modifier, userGuess: String, onUserGuessChange: (String) -> Unit, onKeyboardDone: () ->Unit){
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(text = currentWord, fontSize = 45.sp, modifier=modifier.align(Alignment.CenterHorizontally))
        Text(text = "Przetłumacz słowo", fontSize = 17.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        OutlinedTextField(value = userGuess, singleLine = true, modifier = Modifier.fillMaxWidth(), onValueChange = onUserGuessChange, label = {Text("Wpisz słowo")},
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = {onKeyboardDone()}))
    }
}

@Composable
private fun FinalScoreDialog(score: Int, onPlayAgain: () -> Unit, modifier: Modifier = Modifier){
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text("Gratulacje!") },
        text = { Text("Udzieliłeś: $score poprawnych odpowiedzi") },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = "Wyjdź")
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = "Zagraj ponownie")
            }
        }
    )
}
@Composable
fun shareList(testViewModel: TestViewModel = viewModel(factory = AppViewModelProvider.Factory)): List<Item> {
    val homeUiState by testViewModel.testUiState.collectAsState()
    return homeUiState.itemList
}
//fun getHomeItemList(testViewModel: TestViewModel): List<Item> {
//    val homeUiState by testViewModel.testUiState.collectAsState()
//    return homeUiState.itemList
//}

