package com.test.mini02_boardproject02

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.mini02_boardproject02.databinding.FragmentModifyUserBinding

class ModifyUserFragment : Fragment() {
    lateinit var fragmentModifyUserBinding: FragmentModifyUserBinding
    lateinit var mainActivity:MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentModifyUserBinding = FragmentModifyUserBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentModifyUserBinding.run {

            buttonModifyUser.setOnClickListener {

                fragmentModifyUserBinding.run{// 비밀번호 일치 여부 체크
                    val modifyUserPw1 = textInputEditTextAModifyUserPw1.text.toString()
                    val modifyUserPw2 = textInputEditTextAModifyUserPw2.text.toString()
                    val modifyUserNickname = textInputEditTextAModifyUserNickName.text.toString()
                    val modifyUserAge = textInputEditTextModifyUserAge.text.toString()

                    if (modifyUserPw1 != modifyUserPw2) {
                        val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                            setTitle("비밀번호 입력 오류")
                            setMessage("비밀번호가 일치하지 않습니다.")
                            setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                textInputEditTextAModifyUserPw1.setText("")
                                textInputEditTextAModifyUserPw2.setText("")
                                mainActivity.showSoftInput(textInputEditTextAModifyUserPw2, 200)
                            }
                        }
                        builder.show()
                        return@setOnClickListener
                    }

                    if (modifyUserNickname.isBlank()) {
                        val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                            setTitle("닉네임 입력 오류")
                            setMessage("닉네임을 입력해주세요")
                            setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                mainActivity.showSoftInput(textInputEditTextAModifyUserNickName, 200)
                            }
                        }
                        builder.show()
                        return@setOnClickListener
                    }

                    if (modifyUserAge.isBlank()) {
                        val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                            setTitle("나이 입력 오류")
                            setMessage("나이 입력해주세요")
                            setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                mainActivity.showSoftInput(textInputEditTextModifyUserAge, 200)
                            }
                        }
                        builder.show()
                        return@setOnClickListener
                    }

                    // 유효성 검사 통과 -> 수정 완료
                }
            }
        }

        return fragmentModifyUserBinding.root
    }
}