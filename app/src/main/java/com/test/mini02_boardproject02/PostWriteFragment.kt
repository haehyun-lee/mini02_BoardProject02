package com.test.mini02_boardproject02

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.test.mini02_boardproject02.databinding.FragmentPostWriteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostWriteFragment : Fragment() {
    lateinit var fragmentPostWriteBinding: FragmentPostWriteBinding
    lateinit var mainActivity : MainActivity

    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.INTERNET
    )

    var uploadUri:Uri? = null
    var imageDownloadUrl : String = ""

    // 게시판 종류
    var boardType = 0

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPostWriteBinding = FragmentPostWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        /*
        // 기존 Toolbar 메뉴 교체
        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbarBoardMain)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.menu_post_write)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_post_wirte_camera -> {}
                R.id.item_post_write_album -> {}
                R.id.item_post_write_done -> {
                    mainActivity.removeFragment(MainActivity.POST_WRITE_FRAGMENT)
                }
            }
            true
        }
        */

        // 카메라 런처
        val contractCamera = ActivityResultContracts.StartActivityForResult()
        cameraLauncher = registerForActivityResult(contractCamera) {
            if (it?.resultCode == Activity.RESULT_OK) {
                // 사용자가 선택한 이미지 URI 업로드 후 다운로드 URL 저장
                imageDownloadUrl = uploadImageToFirebaseStorage(it.data?.data!!)
            }
        }
        // 갤러리 런처
        val contractAlbum = ActivityResultContracts.StartActivityForResult()
        albumLauncher = registerForActivityResult(contractAlbum) {
            if (it?.resultCode == Activity.RESULT_OK) {
                imageDownloadUrl = uploadImageToFirebaseStorage(it.data?.data!!)

                fragmentPostWriteBinding.imageViewPostWrite.setImageURI(it.data?.data!!)
            }
        }

        fragmentPostWriteBinding.run {
            toolbarPostWrite.run {
                title = "글 작성"
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.POST_WRITE_FRAGMENT)
                }
                inflateMenu(R.menu.menu_post_write)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_post_wirte_camera -> {
                            // 사진 촬영
                            val newIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraLauncher.launch(newIntent)
                        }
                        R.id.item_post_write_album -> {
                            // 갤러리 사진 선택
                            val newIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                            }
                            albumLauncher.launch(newIntent)
                        }
                        R.id.item_post_write_done-> {
                            // 글 작성 완료 -> 글 보기
                            // mainActivity.removeFragment(MainActivity.POST_WRITE_FRAGMENT)

                            // 유효성 검사
                            fragmentPostWriteBinding.run {

                                val subject = textInputEditTextPostWriteTitle.text.toString()
                                val content = textInputEditTextPostWriteContent.text.toString()

                                if (subject.isBlank()) {
                                    val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                        setTitle("제목 입력 오류")
                                        setMessage("제목을 입력해주세요")
                                        setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                            mainActivity.showSoftInput(textInputEditTextPostWriteTitle, 200)
                                        }
                                    }
                                    builder.show()
                                    return@setOnMenuItemClickListener true
                                }

                                if (content.isBlank()) {
                                    val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                        setTitle("글 입력 오류")
                                        setMessage("내용을 입력해주세요")
                                        setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                            mainActivity.showSoftInput(textInputEditTextPostWriteContent, 200)
                                        }
                                    }
                                    builder.show()
                                    return@setOnMenuItemClickListener true
                                }

                                if (boardType == 0) {
                                    val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                                        setTitle("게시판 종류 선택 오류")
                                        setMessage("게시판 종류를 선택해주세요.")
                                        setPositiveButton("확인", null)
                                    }
                                    builder.show()
                                    return@setOnMenuItemClickListener true
                                }

                                // 게시글 데이터 저장
                                val database = FirebaseDatabase.getInstance()
                                val postIdxRef = database.getReference("PostIdx")

                                postIdxRef.get().addOnCompleteListener {
                                    // 게시글 인덱스
                                    var postIdx = it.result.value as Long

                                    // 입력 데이터
                                    val title = textInputEditTextPostWriteTitle.text.toString()
                                    val content = textInputEditTextPostWriteContent.text.toString()
                                    val timestamp = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(Date())
                                    // TODO 게시판 종류별로 게시글 구분

                                    postIdx++

                                    // Firebase realtime databse에 저장
                                    // 이미지 선택 안 할 경우 imageUrl 공백 저장
                                    val post = Post(postIdx, title, content,
                                        imageDownloadUrl, mainActivity.loginUser.idx, timestamp, boardType)

                                    val postDataRef = database.getReference("PostData")
                                    postDataRef.push().setValue(post).addOnCompleteListener {
                                        postIdxRef.get().addOnCompleteListener {
                                            it.result.ref.setValue(postIdx)

                                            Snackbar.make(fragmentPostWriteBinding.root, "게시글이 등록되었습니다.", Snackbar.LENGTH_SHORT).show()

                                            val newBundle = Bundle()
                                            newBundle.putLong("postIdx", post.idx)
                                            mainActivity.replaceFragment(MainActivity.POST_READ_FRAGMENT, true, newBundle)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    true
                }
            }

            // 게시판 종류 버튼
            buttonPostWriteCategory.run {
                setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(mainActivity).apply {
                        setTitle("게시판 종류")
                        setItems(mainActivity.boardTypeList) { dialogInterface: DialogInterface, i: Int ->
                            boardType = i + 1
                            text = mainActivity.boardTypeList[i]
                        }
                        setNegativeButton("취소", null)
                    }
                    builder.show()
                }
            }

        }

        return fragmentPostWriteBinding.root
    }

    // 이미지를 Firebase Storage에 업로드한 뒤 파일의 다운로드 URL을 반환한다.
    private fun uploadImageToFirebaseStorage(imageUri: Uri): String {
        var imageDownloadUrl = ""

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Firebase Storage에 저장할 파일명 생성
        val fileName = "post_${System.currentTimeMillis()}.jpg"
        // 업로드할 파일의 참조를 생성
        val imageRef = storageRef.child(fileName)

        // 이미지 URI를 Firebase Storage에 업로드
        imageRef.putFile(imageUri)
            .addOnSuccessListener {taskSnapshot ->
                // 업로드 성공 시 이미지의 다운로드 URL을 가져온다.
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {downloadUrl ->
                    // Realtime Database에 저장 예정
                    imageDownloadUrl = downloadUrl.toString()
                }
                Snackbar.make(fragmentPostWriteBinding.root, "이미지를 업로드 했습니다.", Snackbar.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // 업로드 실패
                Snackbar.make(fragmentPostWriteBinding.root, "이미지를 업로드하지 못했습니다.", Snackbar.LENGTH_SHORT).show()
            }

        return imageDownloadUrl
    }

}