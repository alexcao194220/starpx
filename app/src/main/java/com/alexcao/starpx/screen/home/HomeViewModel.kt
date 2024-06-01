package com.alexcao.starpx.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alexcao.starpx.model.ImageSet
import com.alexcao.starpx.repository.ImagePagingSource
import com.alexcao.starpx.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val PAGING_SIZE = 30

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val pagingDataFlow: Flow<PagingData<ImageSet>> = Pager(
            PagingConfig(pageSize = PAGING_SIZE)
        ) {
            ImagePagingSource(repository)
        }.flow.cachedIn(viewModelScope)

    fun logout() {
        repository.logout()
    }
}