package com.test.mini02_boardproject02.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.mini02_boardproject02.repository.PostRepository

class PostListViewModel : ViewModel() {
    var postListLiveData = MutableLiveData<MutableList<Post>>()

    init {
        postListLiveData.value = mutableListOf()
    }

    fun getPostAll() {
        PostRepository.getPostDataAll() { postList ->
            postListLiveData.value = postList
        }
        // postList.value = PostRepository.getPostDataAll()
    }

}