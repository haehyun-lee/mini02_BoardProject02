package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.mini02_boardproject02.databinding.FragmentJoinBinding


class JoinFragment : Fragment() {

    lateinit var fragmentJoinBinding: FragmentJoinBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentJoinBinding = FragmentJoinBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentJoinBinding.run{
            // toolbar
            toolbarJoin.run{
                title = "회원가입"
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.JOIN_FRAGMENT)
                }
            }
            // 다음 버튼
            buttonJoinNext.run{
                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.ADD_USER_INFO_FRAGMENT, true, null)
                }
            }
        }

        return fragmentJoinBinding.root
    }

}








