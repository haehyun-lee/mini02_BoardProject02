package com.test.mini02_boardproject02

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.test.mini02_boardproject02.databinding.FragmentPostWriteBinding
import com.test.mini02_boardproject02.repository.PostRepository
import com.test.mini02_boardproject02.vm.Post
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostWriteFragment : Fragment() {
    lateinit var fragmentPostWriteBinding: FragmentPostWriteBinding
    lateinit var mainActivity : MainActivity

    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.INTERNET,
    )

    var imageUploadUri:Uri? = null

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

        requestPermissions(permissionList, 0)

        cameraLauncher = cameraSetting(fragmentPostWriteBinding.imageViewPostWrite)

        // 갤러리 런처
        albumLauncher = albumSetting(fragmentPostWriteBinding.imageViewPostWrite)

        fragmentPostWriteBinding.run {
            imageViewPostWrite.setImageResource(0)

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
                            // 사진이 저장될 파일 이름
                            val fileName = "/temp_upload.jpg"
                            // 경로
                            val filePath = mainActivity.getExternalFilesDir(null).toString()
                            // 경로 + 파일이름
                            val picPath = "${filePath}/${fileName}"

                            // 사진이 저장될 경로를 관리할 Uri 객체를 만들어준다.
                            // 업로드할 때 사용할 Uri 이다.
                            val file = File(picPath)
                            imageUploadUri = FileProvider.getUriForFile(mainActivity, "com.test.mini02_boardproject02.file_provider", file)
                            newIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUploadUri)

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

                                PostRepository.getPostIdx {
                                    // 게시글 인덱스
                                    var postIdx = it.result.value as Long
                                    // 게시글 인덱스 증가
                                    postIdx++

                                    // 입력 데이터
                                    val title = textInputEditTextPostWriteTitle.text.toString()
                                    val content = textInputEditTextPostWriteContent.text.toString()
                                    val createDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(Date())

                                    // 이미지 유무 체크
                                    val fileName = if (imageUploadUri == null) {
                                        "None"
                                    } else {
                                        "image/img_${System.currentTimeMillis()}.jpg"
                                    }

                                    val post = Post(postIdx, title, content,
                                        fileName, mainActivity.loginUser.idx,
                                        createDate, boardType.toLong())

                                    PostRepository.addPostInfo(post) {
                                        // 게시글 번호를 번들에 담아준다.
                                        val newBundle = Bundle()
                                        newBundle.putLong("postIdx", post.idx)

                                        PostRepository.setPostIdx(postIdx) {
                                            if (imageUploadUri != null) {
                                                PostRepository.uploadImage(imageUploadUri!!, fileName){
                                                    Snackbar.make(fragmentPostWriteBinding.root, "게시글이 등록되었습니다.", Snackbar.LENGTH_SHORT).show()
                                                    mainActivity.replaceFragment(MainActivity.POST_READ_FRAGMENT, true, newBundle)
                                                }
                                            }

                                            Snackbar.make(fragmentPostWriteBinding.root, "게시글이 등록되었습니다.", Snackbar.LENGTH_SHORT).show()
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

    // 카메라 관련 설정
    fun cameraSetting(previewImageView: ImageView): ActivityResultLauncher<Intent> {
        // 사진 촬영을 위한 런처
        // 카메라 런처
        val cameraContract = ActivityResultContracts.StartActivityForResult()
        cameraLauncher = registerForActivityResult(cameraContract) {
            if (it?.resultCode == Activity.RESULT_OK) {
                // URI를 이용해 이미지에 접근하여 Bitmap 객체로 생성한다.
                val bitmap = BitmapFactory.decodeFile(imageUploadUri?.path)

                // 이미지 크기 조정
                val ratio = 1024.0 / bitmap.width
                val targetHeight = (bitmap.height * ratio).toInt()
                val bitmap2 = Bitmap.createScaledBitmap(bitmap, 1024, targetHeight, false)

                // 회전 각도값을 가져온다.
                val degree = getDegree(imageUploadUri!!)

                // 회전 이미지를 생성한다.
                val matrix = Matrix()
                matrix.postRotate(degree.toFloat())
                val bitmap3 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.width, bitmap2.height, matrix, false)
                previewImageView.setImageBitmap(bitmap3)
            }
        }

        return cameraLauncher
    }

    // 이미지 파일에 기록되어 있는 회전 정보를 가져온다.
    fun getDegree(uri:Uri) : Int{

        var exifInterface: ExifInterface? = null

        // 사진 파일로 부터 tag 정보를 관리하는 객체를 추출한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val photoUri = MediaStore.setRequireOriginal(uri)
            // 스트림을 추출한다.
            val inputStream = mainActivity.contentResolver.openInputStream(photoUri)
            // ExifInterface 정보를 읽어온다.
            exifInterface = ExifInterface(inputStream!!)
        } else {
            exifInterface = ExifInterface(uri.path!!)
        }

        var degree = 0
        if(exifInterface != null){
            // 각도 값을 가지고 온다.
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

            when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        }
        return degree
    }

    // 앨범 관련 설정
    fun albumSetting(previewImageView: ImageView): ActivityResultLauncher<Intent> {
        val contractAlbum = ActivityResultContracts.StartActivityForResult()
        albumLauncher = registerForActivityResult(contractAlbum) {
            if (it?.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지에 접근할 수 있는 Uri 객체를 추출한다.
                if (it.data?.data != null) {
                    imageUploadUri = it.data?.data

                    // 안드로이드 10 (Q) 이상이라면...
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 이미지를 생성할 수 있는 디코더를 생성한다.
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, imageUploadUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)

                        previewImageView.setImageBitmap(bitmap)
                    } else {
                        // Content Provider를 통해 이미지 데이터 정보를 가져온다.
                        val cursor = mainActivity.contentResolver.query(imageUploadUri!!, null, null, null, null)
                        if (cursor != null) {
                            cursor.moveToNext()

                            // 이미지의 경로를 가져온다.
                            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = cursor.getString(idx)

                            // 이미지를 생성하여 보여준다.
                            val bitmap = BitmapFactory.decodeFile(source)
                            previewImageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }

        return albumLauncher
    }
}