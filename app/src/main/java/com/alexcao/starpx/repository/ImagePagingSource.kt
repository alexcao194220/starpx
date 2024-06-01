package com.alexcao.starpx.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alexcao.starpx.model.ImageSet

class ImagePagingSource(
    private val repository: Repository,
) : PagingSource<String, ImageSet>() {
    override fun getRefreshKey(state: PagingState<String, ImageSet>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ImageSet> {
        return try {
            val images = repository.getImages(repository.getNextToken(), params.loadSize)
            val nextToken = repository.getNextToken()
            LoadResult.Page(
                data = images,
                prevKey = null,
                nextKey = nextToken
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}