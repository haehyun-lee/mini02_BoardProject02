package com.test.mini02_boardproject02.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.mini02_boardproject02.repository.PostRepository

class PostViewModel:ViewModel() {
    val postLiveData = MutableLiveData<Post>()

    fun getPostData(postIdx: Long) {
        PostRepository.getPostDataOne(postIdx) { post ->
            postLiveData.value = post
        }
    }
}

// 게시글
data class Post(
    var idx: Long,                  // 게시글 인덱스 번호
    var title: String,              // 제목
    var content: String,            // 내용
    var imageUri: String,           // 이미지
    var authorIdx: Long,            // 작성자 인덱스 번호
    var createDate: String,         // 작성일
    var boardType: Long             // 게시판 종류
)
