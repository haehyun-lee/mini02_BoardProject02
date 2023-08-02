package com.test.mini02_boardproject02.repository

import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.vm.Post
import kotlin.concurrent.thread

class PostRepository {
    companion object {
        private val postDataRef = FirebaseDatabase.getInstance().getReference("PostData")

        // 조건에 맞는 게시글 1개 가져오기
        fun getPostDataOne(postIdx : Long, callback: (Post?) -> Unit) {
            // 인덱스에 해당하는 게시글 데이터
            postDataRef.orderByChild("idx").equalTo(postIdx.toDouble()).get().addOnSuccessListener {
                for (snapshot in it.children){
                    val title = snapshot.child("title").value as String
                    val content = snapshot.child("content").value as String
                    val image = snapshot.child("image").value as String
                    val authorIdx = snapshot.child("authorIdx").value as Long
                    val createDate = snapshot.child("createDate").value as String
                    val boardType = snapshot.child("boardType").value as Long

                    // 리스너 안에서 바로 값 세팅
                    val post = Post(postIdx, title, content, image, authorIdx, createDate, boardType)
                    callback(post)
                }
            }.addOnFailureListener {
                callback(null)
            }
        }

        // 전체 게시글 정보 가져오기
        fun getPostDataAll(callback: (MutableList<Post>?) -> Unit) {
            val postList = mutableListOf<Post>()

            thread {
                postDataRef.get().addOnSuccessListener {
                    for (snapshot in it.children) {
                        val idx = snapshot.child("idx").value as Long
                        val title = snapshot.child("title").value as String
                        val content = snapshot.child("content").value as String
                        val image = snapshot.child("image").value as String
                        val authorIdx = snapshot.child("authorIdx").value as Long
                        val createDate = snapshot.child("createDate").value as String
                        val boardType = snapshot.child("boardType").value as Long

                        val post = Post(idx, title, content, image, authorIdx, createDate, boardType)
                        postList.add(post)
                    }
                    callback(postList)
                }.addOnFailureListener {
                    callback(null)
                }
            }
        }
    }
}