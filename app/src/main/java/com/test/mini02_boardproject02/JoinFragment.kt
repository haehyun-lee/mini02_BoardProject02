package com.test.mini02_boardproject02

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.databinding.FragmentJoinBinding
import com.test.mini02_boardproject02.vm.UserViewModel


class JoinFragment : Fragment() {

    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity

    lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinBinding = FragmentJoinBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        userViewModel.run {
            userId.observe(mainActivity){
                fragmentJoinBinding.textInputEditTextJoinUserId.setText(it)
            }
            userPw.observe(mainActivity){
                fragmentJoinBinding.textInputEditTextJoinUserPw.setText(it)

            }
            userPw2.observe(mainActivity){
                fragmentJoinBinding.textInputEditTextJoinUserPw2.setText(it)
            }
        }

        fragmentJoinBinding.run{
            mainActivity.showSoftInput(textInputEditTextJoinUserId, 200)

            // toolbar
            toolbarJoin.run{
                title = "회원가입"
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
            }

            textInputEditTextJoinUserPw2.setOnEditorActionListener { v, actionId, event ->
                next()
                false
            }

            // 다음 버튼
            buttonJoinNext.run{
                setOnClickListener {
                    next()
                }
            }
        }

        return fragmentJoinBinding.root
    }

    private fun next() {

        fragmentJoinBinding.run {
            // 입력한 회원가입 정보
            val joinUserId = textInputEditTextJoinUserId.text.toString()
            val joinUserPw = textInputEditTextJoinUserPw.text.toString()
            val joinUserPw2 = textInputEditTextJoinUserPw2.text.toString()

            // 입력란 공란
            if (joinUserId.isBlank()) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("아이디 입력 오류")
                    setMessage("아이디를 입력해주세요")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextJoinUserId, 100)
                    }
                }
                builder.show()
                return
            }

            // 패스워드 공백
            if (joinUserPw.isBlank()) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("비밀번호 입력 오류")
                    setMessage("비밀번호를 입력해주세요")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextJoinUserPw, 100)
                    }
                }
                builder.show()
                return
            }
            if (joinUserPw2.isBlank()) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("비밀번호 입력 오류")
                    setMessage("비밀번호 확인을 입력해주세요")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextJoinUserPw2, 100)
                    }
                }
                builder.show()
                return
            }

            // 비밀번호 불일치
            if (joinUserPw != joinUserPw2) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("비밀번호 입력 오류")
                    setMessage("비밀번호가 일치하지 않습니다.")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        textInputEditTextJoinUserPw.setText("")
                        textInputEditTextJoinUserPw2.setText("")
                        mainActivity.showSoftInput(textInputEditTextJoinUserPw2, 100)
                    }
                }
                builder.show()
                return
            }

            // 아이디, 비밀번호 전달
            val newBundle = Bundle()
            newBundle.putString("id", joinUserId)
            newBundle.putString("password", joinUserPw)
            mainActivity.replaceFragment(MainActivity.ADD_USER_INFO_FRAGMENT, true, newBundle)
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.reset()
    }
}