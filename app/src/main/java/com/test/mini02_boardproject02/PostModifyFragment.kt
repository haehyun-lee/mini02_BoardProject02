package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.test.mini02_boardproject02.databinding.FragmentPostModifyBinding

class PostModifyFragment(var boardMainFragment: BoardMainFragment) : Fragment() {
    lateinit var fragmentPostModifyBinding : FragmentPostModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostModifyBinding = FragmentPostModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbarBoardMain)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_post_modify)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_post_modify_submit -> {
                    boardMainFragment.removeFragment(BoardMainFragment.POST_MODIFY_FRAGMENT)
                }
            }
            false
        }

        fragmentPostModifyBinding.run {

        }

        return fragmentPostModifyBinding.root
    }

}