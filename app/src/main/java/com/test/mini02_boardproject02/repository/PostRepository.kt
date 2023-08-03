package com.test.mini02_boardproject02.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.test.mini02_boardproject02.vm.Post
import kotlin.concurrent.thread

class PostRepository {
    companion object {
        private val postDataRef = FirebaseDatabase.getInstance().getReference("PostData")

        // 게시글 인덱스 번호를 가져온다.
        fun getPostIdx(callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            // 게시글 인덱스 번호
            val postIdxRef = database.getReference("PostIdx")
            postIdxRef.get().addOnCompleteListener(callback)
        }

        // 게시글 정보를 저장한다
        fun addPostInfo(post: Post, callback:(Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val postDataRef = database.getReference("PostData")
            postDataRef.push().setValue(post).addOnCompleteListener(callback)
        }

        // 게시글 번호를 저장한다.
        fun setPostIdx(postIdx: Long, callback: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            // 게시글 인덱스 번호
            val postIdxRef = database.getReference("PostIdx")
            postIdxRef.get().addOnCompleteListener {
                it.result.ref.setValue(postIdx).addOnCompleteListener(callback)
            }
        }

        // 이미지 업로드
        fun uploadImage(imageUploadUri: Uri, fileName: String, callback: (Task<UploadTask.TaskSnapshot>) -> Unit) {
            val storage = FirebaseStorage.getInstance()
            val imageRef = storage.reference.child(fileName)
            imageRef.putFile(imageUploadUri!!).addOnCompleteListener(callback)
        }

        // 게시글 정보를 가져온다.
        fun getPostInfo(postIdx: Double, callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val postDataRef = database.getReference("PostData")
            postDataRef.orderByChild("idx").equalTo(postIdx).get().addOnCompleteListener(callback)
        }

        // 게시글 이미지를 가져온다.
        fun getPostImage(fileName: String, callback: (Task<Uri>) -> Unit) {
            // Firebase 에서 이미지 내려받아서 세팅
            // 이미지가 첨부되어 있을 경우만 ViewModel의 image 값 세팅 -> 값이 새로 세팅될 때만 감시자 작동함
            val storage = FirebaseStorage.getInstance()
            val fileRef = storage.reference.child(fileName)

            fileRef.downloadUrl.addOnCompleteListener(callback)
        }

        // 게시글 정보 전체 가져오기
        fun getPostAll(callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val postDataRef = database.getReference("PostData")
            // idx 기준으로 오름차순 정렬
            postDataRef.orderByChild("idx").get().addOnCompleteListener(callback)
        }

        // 특정 게시판의 게시글 정보만 가져오기
        fun getPostOne(boardType: Int, callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val postDataRef = database.getReference("PostData")

            postDataRef.orderByChild("boardType").equalTo(boardType.toDouble())
                .orderByChild("idx").get().addOnCompleteListener(callback)
        }

        /*
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

        fun downloadImage(imageDownloadUri: Uri, fileName: String){
            if (fileName != "None") {
                val storage = FirebaseStorage.getInstance()
                val fileRef = storage.reference.child(fileName)

                fileRef.downloadUrl.addOnSuccessListener {
                    thread {
                        val url = URL(it.toString())
                        val httpURLConnection = url.openConnection() as HttpURLConnection
                        val bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)
                        postImage.value = bitmap
                    }
                }
            }
        }

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

         */


    }
}