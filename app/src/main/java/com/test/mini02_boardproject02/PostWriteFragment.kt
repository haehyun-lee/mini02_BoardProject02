package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.test.mini02_boardproject02.databinding.FragmentPostWriteBinding

class PostWriteFragment(var boardMainFragment: BoardMainFragment) : Fragment() {
    lateinit var fragmentPostWriteBinding: FragmentPostWriteBinding
    lateinit var mainActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostWriteBinding = FragmentPostWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // Toolbar에 메뉴 등록
        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbarBoardMain)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_post_write)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_post_wirte_camera -> {}
                R.id.item_post_write_album -> {}
                R.id.item_post_write_done -> {
                    boardMainFragment.removeFragment(BoardMainFragment.POST_WRITE_FRAGMENT)
                }
            }
            true
        }

        return fragmentPostWriteBinding.root
    }

}