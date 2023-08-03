package com.test.mini02_boardproject02.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.User

class UserRepository {
    companion object {
        // 사용자 인덱스 번호를 가져온다.
        fun getUserIdx(calback : (Task<DataSnapshot>) -> Unit) {
            // 사용자 인덱스 값을 가져온다. 인덱스 값은 전역변수처럼 사용됨
            val database = FirebaseDatabase.getInstance()
            val userIdxRef = database.getReference("UserIdx")

            userIdxRef.get().addOnCompleteListener(calback)
        }

        // 사용자 인덱스 번호를 설정한다.
        fun setUserIdx(userIdx: Long, callback: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val userIdxRef = database.getReference("UserIdx")

            // 사용자 인덱스 관리 객체에 접근해서 값 수정
            userIdxRef.get().addOnCompleteListener {
                it.result.ref.setValue(userIdx).addOnCompleteListener(callback)
            }
        }

        // 사용자 정보 저장
        fun addUserInfo(user: User, callback: (Task<Void>) -> Unit) {
            // 사용자 인덱스 값을 가져온다. 인덱스 값은 전역변수처럼 사용됨
            val database = FirebaseDatabase.getInstance()
            val userDataRef = database.getReference("UserData")
            userDataRef.push().setValue(user).addOnCompleteListener(callback)
        }

        // 사용자 아이디를 통해 사용자 정보를 가져온다.
        fun getUserInfoByUserId(loginUserId: String, callback: (Task<DataSnapshot>) -> Unit) {
            // Firebase 에서 회원 정보 가져오기
            val database = FirebaseDatabase.getInstance()
            val userDataRef = database.getReference("UserData")

            userDataRef.orderByChild("id").equalTo(loginUserId).get().addOnCompleteListener(callback)
        }

        // 사용자 인덱스를 통해 사용자 정보를 가져온다.
        fun getUserInfoByUserIdx(userIdx: Long, callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val userDataRef = database.getReference("UserData")

            userDataRef.orderByChild("idx").equalTo(userIdx.toDouble()).get().addOnCompleteListener(callback)
        }

    }
}