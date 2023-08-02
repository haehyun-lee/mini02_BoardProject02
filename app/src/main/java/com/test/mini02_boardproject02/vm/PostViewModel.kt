package com.test.mini02_boardproject02.vm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.test.mini02_boardproject02.repository.PostRepository
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import kotlin.concurrent.thread

class PostViewModel:ViewModel() {
    // val post = MutableLiveData<Post>()
    var title = MutableLiveData<String>()
    var content = MutableLiveData<String>()
    var imageUri = MutableLiveData<Bitmap>()

    fun getPostData(postIdx: Long) {
        // Firebase에서 데이터 가져오기
        val post = PostRepository.getPostOne(postIdx)

        // 데이터 세팅
        if (post != null) {
            title.value = post.title
            content.value = post.content

            // Firebase에서 이미지 내려받아서 세팅
            val storage = FirebaseStorage.getInstance()
            val fileRef = storage.reference.child(post.imageUri)

            fileRef.downloadUrl.addOnSuccessListener {
                thread {
                    val url = URL(it.toString())
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    val bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)

                    imageUri.value = bitmap
                }
            }
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
