package com.alexcao.starpx.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexcao.starpx.navigation.NavigationItem
import java.net.URLEncoder

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val imageSets = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Home.route) {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(text = "Logout")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(padding),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(imageSets.itemCount) { index ->
                    val url = imageSets[index]!!.imageDetail.thumbs.small
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    val fullUrl = imageSets[index]!!.imageDetail.fullUrl
                                    val encodedUrl = URLEncoder.encode(fullUrl, "utf-8")
                                    navController.navigate(
                                        "${NavigationItem.ImageDetail.route}/$encodedUrl"
                                    )
                                }
                            ),
                        model = ImageRequest.Builder(context = LocalContext.current).data(url)
                            .crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            when {
                imageSets.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator()
                }
                imageSets.loadState.append is LoadState.Loading -> {
                    CircularProgressIndicator()
                }
                imageSets.loadState.refresh is LoadState.Error -> {
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Home.route) {
                            inclusive = true
                        }
                    }
                }
                imageSets.loadState.append is LoadState.Error -> {}
            }
        }
    }
}