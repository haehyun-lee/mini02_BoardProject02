package com.test.mini02_boardproject02.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.vm.Post

class PostRepository {
    companion object {
        // 조건에 맞는 게시글 1개 가져오기
        fun getPostOne(postIdx : Long): Post? {
            var post: Post? = null

            // Firebase로 데이터 가져오기
            val database = FirebaseDatabase.getInstance()
            val postDataRef = database.getReference("PostData")

            // 인덱스에 해당하는 게시글 데이터
            postDataRef.orderByChild("idx").equalTo(postIdx.toDouble()).get().addOnSuccessListener {
                for (dataSnapshot in it.children) {
                    // imageUri 컬럼명 image로 바꾸기
                    val title = dataSnapshot.child("title").value as String
                    val content = dataSnapshot.child("content").value as String
                    val imageUri = dataSnapshot.child("imageUri").value as String
                    val authorIdx = dataSnapshot.child("authorIdx").value as Long
                    val createDate = dataSnapshot.child("createDate").value as String
                    val boardType = dataSnapshot.child("boardType").value as Long

                    post = Post(postIdx, title, content, imageUri, authorIdx, createDate, boardType)
                }
            }.addOnFailureListener { 
                Log.d("firebase", "Realtime Database 데이터 불러오기 실패")
            }

            return post
        }
    }
}