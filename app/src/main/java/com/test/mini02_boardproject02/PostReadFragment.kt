package com.test.mini02_boardproject02

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.test.mini02_boardproject02.databinding.FragmentPostReadBinding
import com.test.mini02_boardproject02.vm.PostViewModel
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PostReadFragment() : Fragment() {
    lateinit var fragmentPostReadBinding: FragmentPostReadBinding
    lateinit var mainActivity: MainActivity

    lateinit var postViewModel: PostViewModel
    var postIdx :Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostReadBinding = FragmentPostReadBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        
        // 현재 표시할 게시글 번호
        postIdx = arguments?.getLong("postIdx", 0L)!!
        
        // ViewModel 객체
        postViewModel = ViewModelProvider(this@PostReadFragment)[PostViewModel::class.java]

        fragmentPostReadBinding.run{
            // 뷰 초기화
            textInputEditTextPostReadTitle.setText("")
            textInputEditTextPostReadContent.setText("")
            imageViewPostRead.setImageResource(0)

            // ViewModel Observer
            postViewModel.run {
                postLiveData.observe(viewLifecycleOwner){
                    textInputEditTextPostReadTitle.setText(it.title)
                    textInputEditTextPostReadContent.setText(it.content)

                    // Firebase에서 이미지 내려받아서 세팅
                    if (it.image != "None") {
                        val storage = FirebaseStorage.getInstance()
                        val fileRef = storage.reference.child(it.image)

                        fileRef.downloadUrl.addOnSuccessListener {
                            thread {
                                val url = URL(it.toString())
                                val httpURLConnection = url.openConnection() as HttpURLConnection
                                val bitmap = BitmapFactory.decodeStream(httpURLConnection.inputStream)
                                mainActivity.runOnUiThread {
                                    imageViewPostRead.setImageBitmap(bitmap)
                                }
                            }
                        }
                    }
                }
            }

            // 게시글 내용으로 ViewModel 데이터 변경
            postViewModel.getPostData(postIdx!!)

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

    override fun onResume() {
        super.onResume()
        // 게시글 내용으로 ViewModel 데이터 변경
        // postViewModel.getPostData(postIdx!!)

    }
}