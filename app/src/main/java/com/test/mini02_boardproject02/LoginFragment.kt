package com.test.mini02_boardproject02

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.test.mini02_boardproject02.databinding.FragmentLoginBinding
import com.test.mini02_boardproject02.repository.UserRepository
import com.test.mini02_boardproject02.vm.UserViewModel
import kotlin.concurrent.thread

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    lateinit var userViewModel:UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        userViewModel.run{
            userId.observe(mainActivity){
                // 테스트용 임시 로그인 정보
                // fragmentLoginBinding.textInputEditTextLoginUserId.setText(it)
                fragmentLoginBinding.textInputEditTextLoginUserId.setText("test")
            }
            userPw.observe(mainActivity){
                // fragmentLoginBinding.textInputEditTextLoginUserPw.setText(it)
                fragmentLoginBinding.textInputEditTextLoginUserPw.setText("1234")
            }
        }

        fragmentLoginBinding.run{

            // toolbar
            toolbarLogin.run{
                title = "로그인"
            }

            // 회원가입 버튼
            buttonLoginJoin.run{
                setOnClickListener {
                    // JoinFragment를 보이게 한다.
                    mainActivity.replaceFragment(MainActivity.JOIN_FRAGMENT, true, null)
                }
            }

            textInputEditTextLoginUserPw.setOnEditorActionListener { v, actionId, event ->
                loginSubmit()
                false
            }

            // 로그인 버튼
            buttonLoginSubmit.run{
                setOnClickListener {
                    loginSubmit()
                }
            }
        }

        return fragmentLoginBinding.root
    }

    private fun loginSubmit() {
        fragmentLoginBinding.run {
            val loginUserId = textInputEditTextLoginUserId.text.toString()
            val loginUserPw = textInputEditTextLoginUserPw.text.toString()

            // 아이디 공백
            if (loginUserId.isBlank()) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("아이디 입력 오류")
                    setMessage("아이디를 입력해주세요")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextLoginUserId, 100)
                    }
                }
                builder.show()
                return
            }

            // 패스워드 공백
            if (loginUserPw.isBlank()) {
                val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                    setTitle("비밀번호 입력 오류")
                    setMessage("비밀번호를 입력해주세요")
                    setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        mainActivity.showSoftInput(textInputEditTextLoginUserPw, 100)
                    }
                }
                builder.show()
                return
            }

            thread {
                // 아이디 탐색
                UserRepository.getUserInfoByUserId(loginUserId) {
                        if (!it.result.exists()) {
                            // 일치하는 아이디 없다면
                            val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                setTitle("로그인 오류")
                                setMessage("존재하지 않는 아이디입니다.")
                                setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                    textInputEditTextLoginUserId.setText("")
                                    mainActivity.showSoftInput(textInputEditTextLoginUserId, 100)
                                }
                            }
                            builder.show()
                        } else {
                            // 일치하는 아이디 있다면
                            for (dataSnapshot in it.result.children) {
                                val userPw = dataSnapshot.child("password").value as String

                                // 비밀번호 일치 여부 체크
                                if (userPw != loginUserPw) {
                                    // 비밀번호 불일치
                                    val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                        setTitle("로그인 오류")
                                        setMessage("잘못된 비밀번호 입니다.")
                                        setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                            textInputEditTextLoginUserPw.setText("")
                                            mainActivity.showSoftInput(textInputEditTextLoginUserPw, 100)
                                        }
                                    }
                                    builder.show()
                                } else {
                                    // 로그인 유저 저장
                                    // MainActivity.userIdx = dataSnapshot.child("idx").value as Long
                                    val idx = dataSnapshot.child("idx").value as Long
                                    val id = dataSnapshot.child("id").value as String
                                    val password = dataSnapshot.child("password").value as String
                                    val nickName = dataSnapshot.child("nickName").value as String
                                    val age = dataSnapshot.child("age").value as Long
                                    val hobbyList = dataSnapshot.child("hobby").value as MutableList<String>

                                    mainActivity.loginUser = User(idx, id, password, nickName, age, hobbyList)

                                    Snackbar.make(fragmentLoginBinding.root, "로그인 되었습니다", Snackbar.LENGTH_SHORT).show()
                                    mainActivity.replaceFragment(MainActivity.BOARD_MAIN_FRAGMENT, false, null)
                                }
                            }
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.reset()
    }

}