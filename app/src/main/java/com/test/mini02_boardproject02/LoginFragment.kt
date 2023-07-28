package com.test.mini02_boardproject02

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.mini02_boardproject02.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity

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

            // 로그인 버튼
            buttonLoginSubmit.run{
                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.BOARD_MAIN_FRAGMENT, false, null)
                }
            }
        }

        return fragmentLoginBinding.root
    }

}