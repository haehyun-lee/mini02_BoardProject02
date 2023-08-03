package com.test.mini02_boardproject02.vm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.mini02_boardproject02.repository.PostRepository
import com.test.mini02_boardproject02.repository.UserRepository
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PostViewModel:ViewModel() {
    var postTitle = MutableLiveData<String>()
    var postAuthorName = MutableLiveData<String>()
    var postCreateDate = MutableLiveData<String>()
    var postContent = MutableLiveData<String>()
    var postImage = MutableLiveData<Bitmap>()
    // 이미지 파일 이름
    var postFileName = MutableLiveData<String>()
    
    // 게시글 목록
    var postDataList = MutableLiveData<MutableList<Post>>()

    // 게시글 작성자 닉네임
    var postAuthorNicknameList = MutableLiveData<MutableList<String>>()

    init {
        postDataList.value = mutableListOf()
        postAuthorNicknameList.value = mutableListOf()
    }

    // 게시글 읽기 화면
    fun setPostReadData(postIdx: Double) {
        // 게시글 데이터를 가져온다
        PostRepository.getPostInfo(postIdx) { postTask ->
            for (postData in postTask.result.children){
                postTitle.value = postData.child("title").value as String
                postCreateDate.value = postData.child("createDate").value as String
                postContent.value = postData.child("content").value as String
                postFileName.value = postData.child("image").value as String
                
                // Post 정보에 담긴 authorIdx
                val postAuthorIdx = postData.child("authorIdx").value as Long
                // authorIndx로 User 정보 구하기
                UserRepository.getUserInfoByUserIdx(postAuthorIdx){ userTask ->
                    for (userData in userTask.result.children) {
                        postAuthorName.value = userData.child("nickName").value as String
                    }
                }

                if (postFileName.value != "None") {
                    PostRepository.getPostImage(postFileName.value!!){
                        thread {
                            val url = URL(it.result.toString())
                            val httpURLConnection = url.openConnection() as HttpURLConnection
                            val bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)
                            // UI 스레드에서 작업
                            postImage.postValue(bitmap)
                        }
                    }
                }
            }
        }
    }

    fun getPostAll(selectBoardType: Long) {
        val tempList = mutableListOf<Post>()
        postAuthorNicknameList.value = mutableListOf<String>()

        PostRepository.getPostAll() { postTask ->
            for (dataSnapshot in postTask.result.children) {
                val postIdx = dataSnapshot.child("idx").value as Long
                val postTitle = dataSnapshot.child("title").value as String
                val postAuthorIdx = dataSnapshot.child("authorIdx").value as Long
                val postCreateDate = dataSnapshot.child("createDate").value as String
                val postContent = dataSnapshot.child("content").value as String
                val postImage = dataSnapshot.child("image").value as String
                val postBoardType = dataSnapshot.child("boardType").value as Long

                // 게시판에 해당하는 게시글만 리스트에 추가 예정
                if (selectBoardType != 0L && postBoardType != selectBoardType) {
                    continue
                }
                
                val post = Post(postIdx, postTitle, postContent, postImage, postAuthorIdx, postCreateDate, postBoardType)
                tempList.add(post)


                UserRepository.getUserInfoByUserIdx(postAuthorIdx){ userTask ->
                    for (dataSnapshot2 in userTask.result.children) {
                        val postAuthorNickname =dataSnapshot2.child("nickName").value as String
                        postAuthorNicknameList.value?.add(postAuthorNickname)
                    }
                }
            }

            // 데이터가 postIdx를 기준으로 오름차순 정렬되어 있기 때문에 순서 뒤집기
            tempList.reverse()
            postDataList.value = tempList
        }
    }
}

// 게시글
data class Post(
    var idx: Long,                  // 게시글 인덱스 번호
    var title: String,              // 제목
    var content: String,            // 내용
    var image: String,              // 이미지
    var authorIdx: Long,            // 작성자 인덱스 번호
    var createDate: String,         // 작성일
    var boardType: Long             // 게시판 종류
)
