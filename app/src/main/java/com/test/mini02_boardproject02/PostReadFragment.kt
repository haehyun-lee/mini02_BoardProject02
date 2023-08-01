package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.mini02_boardproject02.databinding.FragmentPostReadBinding

class PostReadFragment() : Fragment() {
    lateinit var fragmentPostReadBinding: FragmentPostReadBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostReadBinding = FragmentPostReadBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        
        // 현재 표시할 게시글 번호
        val postIdx = arguments?.getLong("postIdx")
        
        // 데이터베이스에서 포스트 읽어오기
        // TODO MVVM 모델 사용 예정

        fragmentPostReadBinding.run{
            toolbarPostRead.run{
                title = "글읽기"
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    // 해당 Fragment가 BackStack에 없으면 반응X
                    // 글 작성 완료 -> 글 읽기 화면으로 올 경우를 대비한 코드
                    mainActivity.removeFragment(MainActivity.POST_WRITE_FRAGMENT)
                    mainActivity.removeFragment(MainActivity.POST_READ_FRAGMENT)
                }
                inflateMenu(R.menu.menu_post_read)

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_post_read_modify -> {
                            /*
                            // 수정 Fragment 없이 버튼으로 입력 칸 활성화 하기
                            if(!textInputEditTextPostReadSubject.isEnabled) {
                                textInputEditTextPostReadSubject.isEnabled = true
                                textInputEditTextPostReadText.isEnabled = true
                            } else {
                                textInputEditTextPostReadSubject.isEnabled = false
                                textInputEditTextPostReadText.isEnabled = false
                            }
                             */
                            mainActivity.replaceFragment(MainActivity.POST_MODIFY_FRAGMENT, true, null)
                        }
                        R.id.item_post_read_delete -> {
                            mainActivity.removeFragment(MainActivity.POST_WRITE_FRAGMENT)
                            mainActivity.removeFragment(MainActivity.POST_READ_FRAGMENT)
                        }
                    }

                    true
                }

            }
        }

        return fragmentPostReadBinding.root
    }

}