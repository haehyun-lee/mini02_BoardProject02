package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.test.mini02_boardproject02.databinding.FragmentPostReadBinding

class PostReadFragment(var boardMainFragment: BoardMainFragment) : Fragment() {
    lateinit var fragmentPostReadBinding: FragmentPostReadBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostReadBinding = FragmentPostReadBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // 메뉴 세팅
        // boardMainFragment.replaceToolbarMenu(R.menu.menu_post_read)
        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbarBoardMain)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_post_read)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_post_read_edit -> {
                    boardMainFragment.replaceFragment(BoardMainFragment.POST_MODIFY_FRAGMENT, true, true, null)
                }
                R.id.item_post_read_delete -> {
                    boardMainFragment.removeFragment(BoardMainFragment.POST_READ_FRAGMENT)
                }
            }
            true
        }




        return fragmentPostReadBinding.root
    }
}