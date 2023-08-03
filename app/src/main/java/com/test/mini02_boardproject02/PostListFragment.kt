package com.test.mini02_boardproject02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.mini02_boardproject02.databinding.FragmentPostListBinding
import com.test.mini02_boardproject02.databinding.RowPostListBinding
import com.test.mini02_boardproject02.repository.UserRepository
import com.test.mini02_boardproject02.vm.PostViewModel

// replaceFragment, removeFragment 메서드 사용을 위해 BoardMainFragment 받기
class PostListFragment() : Fragment() {
    lateinit var fragmentPostListBinding: FragmentPostListBinding
    lateinit var mainActivity: MainActivity
    // lateinit var boardMainFragment: BoardMainFragment

    lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPostListBinding = FragmentPostListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        // fragmentContainer를 포함하고 있는 Fragment 객체
        // boardMainFragment = mainActivity.newFragment as BoardMainFragment

        postViewModel = ViewModelProvider(mainActivity)[PostViewModel::class.java]
        postViewModel.run {
            postDataList.observe(mainActivity){
                // 게시물 목록이 변경되면 RecyclerView 갱신
                fragmentPostListBinding.recyclerViewPostListAll.adapter?.notifyDataSetChanged()
                fragmentPostListBinding.recyclerViewPostListResult.adapter?.notifyDataSetChanged()
            }
        }

        fragmentPostListBinding.run{
            // val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbarBoardMain)
            // toolbar.menu.clear()

            // SearchBar 메뉴 설정
            searchBarPostList.run {
                hint = "검색어를 입력해주세요"
                inflateMenu(R.menu.menu_post_list)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_post_list_add -> {
                            // 글 추가
                            mainActivity.replaceFragment(MainActivity.POST_WRITE_FRAGMENT, true, null)
                        }
                    }
                    false
                }
            }

            recyclerViewPostListAll.run{
                adapter = AllRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            recyclerViewPostListResult.run{
                adapter = ResultRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            // 선택한 게시판 타입에 따라 게시글 정보를 가져온다.
            postViewModel.getPostAll(arguments?.getLong("boardType")!!)
        }

        return fragmentPostListBinding.root
    }

    // 모든 게시글 목록을 보여주는 리사이클러 뷰의 어뎁터
    inner class AllRecyclerViewAdapter : RecyclerView.Adapter<AllRecyclerViewAdapter.AllViewHolder>(){
        inner class AllViewHolder(rowPostListBinding: RowPostListBinding) : RecyclerView.ViewHolder(rowPostListBinding.root){
            var postIdx = 0L

            val rowPostListSubject:TextView
            val rowPostListNickName:TextView

            init{
                rowPostListSubject = rowPostListBinding.rowPostListSubject
                rowPostListNickName = rowPostListBinding.rowPostListNickName

                rowPostListBinding.root.setOnClickListener {
                    // 글 보기
                    val newBundle = Bundle()
                    newBundle.putLong("postIdx", postIdx)
                    mainActivity.replaceFragment(MainActivity.POST_READ_FRAGMENT, true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {
            val rowPostListBinding = RowPostListBinding.inflate(layoutInflater)
            val allViewHolder = AllViewHolder(rowPostListBinding)

            rowPostListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return postViewModel.postDataList.value?.size!!
        }

        override fun onBindViewHolder(holder: AllViewHolder, position: Int) {
            val curPost = postViewModel.postDataList.value?.get(position)!!

            holder.postIdx = curPost.idx
            holder.rowPostListSubject.text = curPost.title
//            holder.rowPostListNickName.text = postViewModel.postAuthorNicknameList.value?.get(position)
        }
    }


    // 검색 결과 게시글 목록을 보여주는 리사이클러 뷰의 어뎁터
    inner class ResultRecyclerViewAdapter : RecyclerView.Adapter<ResultRecyclerViewAdapter.ResultViewHolder>(){
        inner class ResultViewHolder(rowPostListBinding: RowPostListBinding) : RecyclerView.ViewHolder(rowPostListBinding.root){
            var postIdx = 0L

            val rowPostListSubject:TextView
            val rowPostListNickName:TextView

            init{
                rowPostListSubject = rowPostListBinding.rowPostListSubject
                rowPostListNickName = rowPostListBinding.rowPostListNickName
                rowPostListBinding.root.setOnClickListener {
                    val newBundle = Bundle()
                    newBundle.putLong("postIdx", postIdx)
                    mainActivity.replaceFragment(MainActivity.POST_READ_FRAGMENT, true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
            val rowPostListBinding = RowPostListBinding.inflate(layoutInflater)
            val allViewHolder = ResultViewHolder(rowPostListBinding)

            rowPostListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return postViewModel.postDataList.value?.size!!
        }

        override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
            val curPost = postViewModel.postDataList.value?.get(position)!!

            holder.postIdx = curPost.idx
            holder.rowPostListSubject.text = curPost.title
         //   holder.rowPostListNickName.text = postViewModel.postAuthorNicknameList.value?.get(position)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}








