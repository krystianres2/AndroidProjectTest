/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
import java.text.NumberFormat

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    navigateToTest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false
            )
        },
    ) { innerPadding ->
        HomeBody(
            itemList = homeUiState.itemList,
            onItemClick = navigateToItemUpdate,
            navigateToItemEntry,
            navigateToTest,
            modifier = modifier.padding(innerPadding)
        )

    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    navigateToItemEntry: () -> Unit,
    navigateToTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InventoryListHeader()
        Divider(Modifier.height(4.dp))
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                style = MaterialTheme.typography.subtitle2
            )
        } else {
            Box(modifier = Modifier.weight(1f)) {
                InventoryList(itemList = itemList, onItemClick = { onItemClick(it.id) })
            }
        }
        BottomBar(Modifier, navigateToItemEntry, navigateToTest)

    }
}

@Composable
private fun BottomBar(modifier: Modifier = Modifier, navigateToItemEntry: () -> Unit, navigateToTest: () -> Unit){
    Divider(Modifier.height(4.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Button(
                onClick = navigateToTest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Test")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Button(
                onClick = navigateToItemEntry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Dodaj")
            }
        }
    }
}


@Composable
private fun InventoryList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(0.dp)) {
        items(items = itemList, key = { it.id }) { item ->
            InventoryItem(item = item, onItemClick = onItemClick)
            Divider()
        }
    }
}

@Composable
private fun InventoryListHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        headerList.forEach {
            Text(
                text = stringResource(it.headerStringId),
                modifier = Modifier
                    .weight(it.weight)
                    .padding(start = 8.dp),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InventoryItem(
    item: Item,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick(item) }
        .padding(vertical = 8.dp)
//        .background(color = Color.Cyan)
    ) {
        Text(
            text = item.wordPl,
            modifier = Modifier
                .weight(1.5f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.wordEng,
            modifier = Modifier
                .weight(1.5f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

private data class InventoryHeader(@StringRes val headerStringId: Int, val weight: Float)

private val headerList = listOf(
    InventoryHeader(headerStringId = R.string.wordPl, weight = 1.5f),
    InventoryHeader(headerStringId = R.string.wordEng, weight = 1.5f),
)

@Preview(showBackground = true)
@Composable
fun HomeScreenRoutePreview() {
    InventoryTheme {
        HomeBody(
            listOf(
                Item(1, "Pies","Dog" ),
                Item(2, "Kot", "Cat"),
                Item(3, "Mysz", "Mouse")
            ),
            onItemClick = {}, navigateToItemEntry = {}, navigateToTest = {}
        )
    }
}