package com.test.mini02_boardproject02

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialSharedAxis
import com.test.mini02_boardproject02.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null

    companion object {
        val LOGIN_FRAGMENT = "LoginFragment"
        val JOIN_FRAGMENT = "JoinFragment"
        val ADD_USER_INFO_FRAGMENT = "AddUserInfoFragment"
        val BOARD_MAIN_FRAGMENT = "BoardMainFragment"

        val POST_WRITE_FRAGMENT = "PostWriteFragment"
        val POST_READ_FRAGMENT = "PostReadFragment"
        val POST_MODIFY_FRAGMENT = "PostModifyFragment"
    }

    // 키보드 관리자
    lateinit var inputMethodManager: InputMethodManager

    // 로그인한 사용자의 정보를 담을 객체
    lateinit var loginUser: User

    // 게시판 종류
    val boardTypeList = arrayOf(
        "자유게시판", "유머게시판", "질문게시판", "스포츠게시판"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        // splashScreenCustomizing(splashScreen)

        // SystemClock.sleep(3000)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        replaceFragment(LOGIN_FRAGMENT, false, null)
    }

    // SplashScreen 커스터마이징
    fun splashScreenCustomizing(splashScreen: SplashScreen) {
        // SplashScreen이 사라질 때 동작하는 리스너를 설정한다.
        splashScreen.setOnExitAnimationListener {
            // 가로 비율 애니메이션
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2f, 1f, 0f)
            // 세로 비율 애니메이션
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2f, 1f, 0f)
            // 투명도
            val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 1f, 0.5f, 0f)

            // 애니메이션 관리 객체를 생성한다.
            // 첫 번째 : 애니메이션을 적용할 뷰
            // 나머지는 적용한 애니메이션 종류
            val objectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(it.iconView, scaleX, scaleY, alpha)
            // 애니메이션 적용을 위한 에이징
            objectAnimator.interpolator = AnticipateInterpolator()
            // 애니메이션 동작 시간
            objectAnimator.duration = 1000
            // 애니메이션이 끝났을 때 동작할 리스너
            objectAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    // SplashScreen을 제거한다.
                    it.remove()
                }
            })

            // 애니메이션 가동
            objectAnimator.start()
        }
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle?) {
        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
        if (newFragment != null) {
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when (name) {
            LOGIN_FRAGMENT -> LoginFragment()
            JOIN_FRAGMENT -> JoinFragment()
            ADD_USER_INFO_FRAGMENT -> AddUserInfoFragment()
            BOARD_MAIN_FRAGMENT -> BoardMainFragment()

            POST_WRITE_FRAGMENT -> PostWriteFragment()
            POST_READ_FRAGMENT -> PostReadFragment()
            POST_MODIFY_FRAGMENT -> PostModifyFragment()
            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if (newFragment != null) {

            // oldFragment -> newFragment로 이동
            // oldFramgent : exit
            // newFragment : enter

            // oldFragment <- newFragment 로 되돌아가기
            // oldFragment : reenter
            // newFragment : return

            // 애니메이션 설정
            if (oldFragment != null) {
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.mainContainer, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // 키보드를 올려주는 메서드
    fun showSoftInput(view: View, delay: Long) {
        view.requestFocus()
        thread {
            SystemClock.sleep(delay)
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    // 키보드를 내려주는 메서드
    fun hideSoftInput() {
        if (currentFocus != null) {

            inputMethodManager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

// 사용자 정보
data class User(
    var idx: Long,                  // 인덱스
    var id: String,                 // 사용자 아이디
    var password: String,           // 비밀번호
    var nickName: String,           // 닉네임
    var age: Long,                  // 나이
    var hobby: MutableList<String>  // 취미 목록
)
