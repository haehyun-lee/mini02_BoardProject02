package com.test.mini02_boardproject02

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.databinding.FragmentAddUserInfoBinding


class AddUserInfoFragment : Fragment() {

    lateinit var fragmentAddUserInfoBinding: FragmentAddUserInfoBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentAddUserInfoBinding = FragmentAddUserInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentAddUserInfoBinding.run{
            mainActivity.showSoftInput(textInputEditTextAddUserInfoNickName, 200)

            // toolbar
            toolbarAddUserInfo.run{
                title = "회원가입"
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ADD_USER_INFO_FRAGMENT)
                }
            }

            // 가입 완료 버튼
            buttonAddUserInfoSubmit.run{
                setOnClickListener {
                    fragmentAddUserInfoBinding.run{
                        
                        val joinUserNickName = textInputEditTextAddUserInfoNickName.text.toString()
                        val joinUserAge = textInputEditTextAddUserInfoAge.text.toString()

                        if (joinUserNickName.isBlank()) {
                            val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                setTitle("닉네임 입력 오류")
                                setMessage("닉네임을 입력해주세요")
                                setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                    mainActivity.showSoftInput(textInputEditTextAddUserInfoNickName, 200)
                                }
                            }
                            builder.show()
                            return@setOnClickListener
                        }

                        if (joinUserAge.isBlank()) {
                            val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                setTitle("나이 입력 오류")
                                setMessage("나이를 입력해주세요")
                                setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                    mainActivity.showSoftInput(
                                        textInputEditTextAddUserInfoNickName, 200
                                    )
                                }
                            }
                            builder.show()
                            return@setOnClickListener
                        }

                        // 사용자 인덱스 값을 가져온다. 인덱스 값은 전역변수처럼 사용됨
                        val database = FirebaseDatabase.getInstance()
                        val userIdxRef = database.getReference("UserIdx")
                        // var userIdx = 0L
                        userIdxRef.get().addOnCompleteListener {
//                            for (a1 in it.result.children) {
//                                userIdx = a1.value as Long
//                            }
                            // 현재의 사용자 인덱스를 가져온다.
                            // 정수, 실수 1개 단위는 아래 방법으로 가져온다.
                            var userIdx = it.result.value as Long

                            // 저장할 데이터 담기
                            val userId = arguments?.getString("id")
                            val userPw = arguments?.getString("password")
                            // 취미 목록 가져오기
                            val hobbyList = mutableListOf<String>()
                            for (view in materialCheckBoxGroupUserInfo1.children) {
                                view as MaterialCheckBox
                                if (view.isChecked) hobbyList.add(view.text.toString())
                            }
                            for (view in materialCheckBoxGroupUserInfo2.children) {
                                view as MaterialCheckBox
                                if (view.isChecked) hobbyList.add(view.text.toString())
                            }

                            // 사용자 인덱스 번호 1 증가
                            // 기존 인덱스 번호에서 1 증가한 값을 현재 추가하는 사용자 인덱스 값으로 사용
                            userIdx++

                            val user = User(userIdx, userId!!, userPw!!, joinUserNickName,
                                joinUserAge.toLong(), hobbyList)

                            val userDataRef = database.getReference("UserData")

                            userDataRef.push().setValue(user).addOnCompleteListener {

                                userIdxRef.get().addOnCompleteListener {
                                    // 사용자 인덱스 관리 객체에 접근해서 값 수정
                                    it.result.ref.setValue(userIdx)

                                    Snackbar.make(fragmentAddUserInfoBinding.root, "가입이 완료되었습니다", Snackbar.LENGTH_SHORT).show()

                                    mainActivity.removeFragment(MainActivity.ADD_USER_INFO_FRAGMENT)
                                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                                }
                            }
                        }
                    }
                }
            }

            // 취미 전체 체크박스
            materialCheckBoxAddUserInfoAll.run {
                setOnCheckedChangeListener { buttonView, isChecked ->
                    // 각 체크 박스를 가지고 있는 레이아웃을 통해 그 안에 있는 뷰들의 체크 상태를 변경한다.
                    // 부모 체크박스로 자식 체크박스 일괄 컨트롤
                    for (v1 in materialCheckBoxGroupUserInfo1.children) {
                        // 형변환
                        v1 as MaterialCheckBox
                        // 취미 전체가 체크되어 있다면
                        if (isChecked) {
                            v1.checkedState = MaterialCheckBox.STATE_CHECKED
                        } else {
                            v1.checkedState = MaterialCheckBox.STATE_UNCHECKED
                        }
                    }
                    for (v1 in materialCheckBoxGroupUserInfo2.children) {
                        v1 as MaterialCheckBox
                        if (isChecked) {
                            v1.checkedState = MaterialCheckBox.STATE_CHECKED
                        } else {
                            v1.checkedState = MaterialCheckBox.STATE_UNCHECKED
                        }
                    }

                    // 다른 체크박스들
                    val checkboxCount = materialCheckBoxGroupUserInfo1.childCount
                    for (v1 in materialCheckBoxGroupUserInfo1.children) {
                        v1 as MaterialCheckBox
                        v1.setOnCheckedChangeListener { buttonView, isChecked ->
                            setParentCheckBoxState()
                        }
                    }
                    for (v1 in materialCheckBoxGroupUserInfo2.children) {
                        v1 as MaterialCheckBox
                        v1.setOnCheckedChangeListener { buttonView, isChecked ->
                            setParentCheckBoxState()
                        }
                    }
                }
            }
        }

        return fragmentAddUserInfoBinding.root
    }

    // 하위의 체크박스들의 상태를 보고 상위 체크 박스 상태를 세팅한다.
    fun setParentCheckBoxState() {
        fragmentAddUserInfoBinding.run {
            // 체크박스의 개수를 구한다.
            val checkBoxCount = materialCheckBoxGroupUserInfo1.childCount + materialCheckBoxGroupUserInfo2.childCount
            // 체크되어 있는 체크박스의 개수
            var checkedCount = 0
            for(v1 in materialCheckBoxGroupUserInfo1.children){
                v1 as MaterialCheckBox
                if(v1.checkedState == MaterialCheckBox.STATE_CHECKED){
                    checkedCount++
                }
            }
            for(v1 in materialCheckBoxGroupUserInfo2.children){
                v1 as MaterialCheckBox
                if(v1.checkedState == MaterialCheckBox.STATE_CHECKED){
                    checkedCount++
                }
            }

            // 만약 체크되어 있는 것이 없다면
            if(checkedCount == 0){
                materialCheckBoxAddUserInfoAll.checkedState = MaterialCheckBox.STATE_UNCHECKED
            }
            // 모두 체크되어 있다면
            else if(checkedCount == checkBoxCount){
                materialCheckBoxAddUserInfoAll.checkedState = MaterialCheckBox.STATE_CHECKED
            }
            // 일부만 체크되어 있다면
            else {
                materialCheckBoxAddUserInfoAll.checkedState = MaterialCheckBox.STATE_INDETERMINATE
            }

        }
    }
}