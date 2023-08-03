package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.test.mini02_boardproject02.databinding.FragmentPostReadBinding
import com.test.mini02_boardproject02.vm.PostViewModel

class PostReadFragment() : Fragment() {
    lateinit var fragmentPostReadBinding: FragmentPostReadBinding
    lateinit var mainActivity: MainActivity

    lateinit var postViewModel: PostViewModel

    // 게시글 인덱스 번호
    var readPostIdx = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostReadBinding = FragmentPostReadBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        
        // ViewModel 객체
        postViewModel = ViewModelProvider(mainActivity)[PostViewModel::class.java]

        fragmentPostReadBinding.run{
            // 뷰 초기화
            textInputEditTextPostReadTitle.setText("")
            textInputEditTextPostReadContent.setText("")
            imageViewPostRead.setImageResource(0)

            // ViewModel Observer
            postViewModel.run {
                postTitle.observe(mainActivity){
                    textInputEditTextPostReadTitle.setText(it)
                }
                postAuthorName.observe(mainActivity){
                    textInputEditTextPostReadAuthor.setText(it)
                }
                postCreateDate.observe(mainActivity){
                    textInputEditTextPostReadDate.setText(it)
                }
                postContent.observe(mainActivity){
                    textInputEditTextPostReadContent.setText(it)
                }
                postFileName.observe(mainActivity){
                    if (it == "None") {
                        fragmentPostReadBinding.imageViewPostRead.visibility = View.INVISIBLE
                    }
                }
                postImage.observe(mainActivity){
                    fragmentPostReadBinding.imageViewPostRead.visibility = View.VISIBLE
                    fragmentPostReadBinding.imageViewPostRead.setImageBitmap(it)
                }
            }

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
                            val newBundle = Bundle()
                            newBundle.putLong("postIdx", readPostIdx)
                            mainActivity.replaceFragment(MainActivity.POST_MODIFY_FRAGMENT, true, newBundle)
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

        // 현재 표시할 게시글 인덱스 번호를 받는다
        readPostIdx = arguments?.getLong("postIdx")!!
        // 게시글 정보를 가져온다. 게시글 내용으로 ViewModel 데이터 변경
        postViewModel.setPostReadData(readPostIdx.toDouble())

        return fragmentPostReadBinding.root
    }

    override fun onResume() {
        super.onResume()
        // 게시글 내용으로 ViewModel 데이터 변경
        // postViewModel.getPostData(postIdx!!)
    }
}