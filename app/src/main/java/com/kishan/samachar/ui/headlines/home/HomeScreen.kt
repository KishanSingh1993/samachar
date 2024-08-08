package com.kishan.samachar.ui.headlines.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.kishan.samachar.data.remote.model.HeadlinesParams
import com.kishan.samachar.ui.base.NoNetworkStatusBar
import com.kishan.samachar.ui.base.ClickHandler
import com.kishan.samachar.ui.base.IconButton
import com.kishan.samachar.ui.base.PrimaryButton
import com.kishan.samachar.ui.base.Route
import com.kishan.samachar.ui.base.TextButton
import com.kishan.samachar.ui.base.UrlHandler
import com.kishan.samachar.utils.StringsHelper.BACK_TO_TOP
import com.kishan.samachar.utils.StringsHelper.SELECT_NEWS_SOURCE
import com.kishan.samachar.utils.StringsHelper.TOAST_NETWORK_ERROR
import com.kishan.samachar.ui.filters.country.CountriesBottomSheet
import com.kishan.samachar.ui.filters.country.CountriesViewModel
import com.kishan.samachar.ui.filters.language.LanguageViewModel
import com.kishan.samachar.ui.filters.language.LanguagesBottomSheet
import com.kishan.samachar.ui.filters.source.SourcesBottomSheet
import com.kishan.samachar.ui.filters.source.SourcesViewModel
import com.kishan.samachar.ui.headlines.LoadPaginatedHeadlines
import com.kishan.samachar.R
import com.kishan.samachar.ui.MainViewModel
import com.kishan.samachar.utils.AppConstants.DEFAULT_LANGUAGE_CODE
import com.kishan.samachar.utils.AppConstants.DEFAULT_SOURCE
import com.kishan.samachar.utils.StringsHelper.APP_NAME
import com.kishan.samachar.utils.StringsHelper.BOOKMARKED_NEWS
import com.kishan.samachar.utils.StringsHelper.LANGUAGE_BUTTON
import com.kishan.samachar.utils.StringsHelper.SAVED_TO_BOOKMARK
import com.kishan.samachar.utils.StringsHelper.SEARCH_BUTTON
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    context: Context,
    navController: NavController,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
    countriesViewModel: CountriesViewModel = hiltViewModel(),
    sourcesViewModel: SourcesViewModel = hiltViewModel(),
    onHeadlineClicked: UrlHandler
) {
    val networkConnectedState by mainViewModel.isNetworkConnected.collectAsState()

    val headlinesParamsState by mainViewModel.headlinesParams.collectAsState()

    val headlinesState = homeViewModel.headlineList.collectAsLazyPagingItems()

    val headlinesListState = rememberLazyListState()

    var showBackToTopButton by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var showSourcesBottomSheet by remember { mutableStateOf(false) }

    var showLanguagesBottomSheet by remember { mutableStateOf(false) }

    var showCountriesBottomSheet by remember { mutableStateOf(false) }

    if (networkConnectedState) {
        LaunchedEffect(headlinesParamsState) {
            fetchHeadlines(headlinesParamsState, homeViewModel)
        }
    }

    LaunchedEffect(headlinesListState.isScrollInProgress) {
        if (headlinesListState.isScrollInProgress) {
            showBackToTopButton = true
        } else {
            coroutineScope.launch {
                delay(2000)
                showBackToTopButton = false
            }
        }
    }

    if (showSourcesBottomSheet) {
        if (networkConnectedState) {
            SourcesBottomSheet(
                context = context,
                sourcesViewModel = sourcesViewModel,
                mainViewModel = mainViewModel,
                countryCode = headlinesParamsState.selectedCountry.code
            ) {
                sourcesViewModel.clearSources()
                showSourcesBottomSheet = false
            }
        } else {
            showSourcesBottomSheet = false
            Toast.makeText(context, TOAST_NETWORK_ERROR, Toast.LENGTH_SHORT).show()
        }
    }

    if (showLanguagesBottomSheet) {
        if (networkConnectedState) {
            LanguagesBottomSheet(
                context = context,
                languageViewModel = languageViewModel,
                mainViewModel = mainViewModel,
                selectedLanguageCode = headlinesParamsState.selectedLanguageCode
            ) { showLanguagesBottomSheet = false }
        } else {
            showLanguagesBottomSheet = false
            Toast.makeText(context, TOAST_NETWORK_ERROR, Toast.LENGTH_SHORT).show()
        }
    }

    if (showCountriesBottomSheet) {
        if (networkConnectedState) {
            CountriesBottomSheet(
                context = context,
                countriesViewModel = countriesViewModel,
                mainViewModel = mainViewModel
            ) { showCountriesBottomSheet = false }
        } else {
            showCountriesBottomSheet = false
            Toast.makeText(context, TOAST_NETWORK_ERROR, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navController = navController,
                headlinesParams = headlinesParamsState,
                showLanguagesBottomSheet = { showLanguagesBottomSheet = it },
                showCountriesBottomSheet = { showCountriesBottomSheet = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Route.BookmarksScreen.name) }) {
                Icon(
                    painter = painterResource(R.drawable.bookmarks),
                    contentDescription = BOOKMARKED_NEWS
                )
            }
        }
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            if (!networkConnectedState) {
                NoNetworkStatusBar { navController.navigate(Route.OfflineScreen.name) }
            } else {
                SelectNewsSourceButton { showSourcesBottomSheet = true }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LoadPaginatedHeadlines(
                    headlines = headlinesState,
                    isNetworkConnected = networkConnectedState,
                    bookmarkIcon = painterResource(id = R.drawable.add),
                    onHeadlineClicked = { onHeadlineClicked(it.url) },
                    onBookmarkClicked = {
                        homeViewModel.bookmarkHeadline(it)
                        Toast.makeText(context, SAVED_TO_BOOKMARK, Toast.LENGTH_SHORT).show()
                    },
                    onRetryClicked = {
                        fetchHeadlines(
                            headlinesParams = headlinesParamsState,
                            homeViewModel = homeViewModel
                        )
                    },
                    listState = headlinesListState
                )

                if (showBackToTopButton) {
                    BackToTopButton(modifier = Modifier.align(Alignment.TopCenter)) {
                        coroutineScope.launch { headlinesListState.animateScrollToItem(0) }
                    }
                }
            }
        }
    }
}

private fun fetchHeadlines(headlinesParams: HeadlinesParams, homeViewModel: HomeViewModel) {
    homeViewModel.apply {
        when {
            headlinesParams.selectedLanguageCode != DEFAULT_LANGUAGE_CODE -> {
                getHeadlinesByLanguage(
                    languageCode = headlinesParams.selectedLanguageCode,
                    countryCode = headlinesParams.selectedCountry.code
                )
            }

            headlinesParams.selectedSourceId != DEFAULT_SOURCE -> {
                getHeadlinesBySource(sourceId = headlinesParams.selectedSourceId)
            }

            else -> {
                getHeadlinesByCountry(countryCode = headlinesParams.selectedCountry.code)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    navController: NavController,
    headlinesParams: HeadlinesParams,
    showLanguagesBottomSheet: (Boolean) -> Unit,
    showCountriesBottomSheet: (Boolean) -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(1f), text = APP_NAME)

                Row {
                    SearchButton { navController.navigate(Route.SearchScreen.name) }

                    SelectLanguageButton { showLanguagesBottomSheet(true) }

                    SelectCountryButton(flag = headlinesParams.selectedCountry.flag) {
                        showCountriesBottomSheet(true)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

@Composable
private fun SearchButton(onClick: ClickHandler) {
    IconButton(
        modifier = Modifier,
        drawablePainter = painterResource(id = R.drawable.search),
        contentDescription = SEARCH_BUTTON
    ) { onClick() }
}

@Composable
private fun SelectLanguageButton(onClick: ClickHandler) {
    IconButton(
        modifier = Modifier,
        drawablePainter = painterResource(id = R.drawable.language),
        contentDescription = LANGUAGE_BUTTON
    ) { onClick() }
}

@Composable
private fun SelectCountryButton(flag: String, onClick: ClickHandler) {
    TextButton(
        modifier = Modifier,
        text = flag
    ) { onClick() }
}

@Composable
private fun SelectNewsSourceButton(onClick: ClickHandler) {
    PrimaryButton(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        text = SELECT_NEWS_SOURCE
    ) { onClick() }
}

@Composable
private fun BackToTopButton(modifier: Modifier, onClick: ClickHandler) {
    ElevatedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.arrow_up),
            contentDescription = BACK_TO_TOP
        )

        Text(modifier = Modifier.padding(start = 8.dp), text = BACK_TO_TOP)
    }
}
