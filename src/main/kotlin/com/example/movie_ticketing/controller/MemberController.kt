package com.example.movie_ticketing.controller

import com.example.movie_ticketing.domain.Member
import com.example.movie_ticketing.repository.MemberRepository
import com.example.movie_ticketing.service.MemberService
import jakarta.validation.Valid
import logger
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class MemberController(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder) {
    /**
     * 멤버 로그인
     * 조금 더 수정해야함
     */
    //Springsecurity 기본 로그인/로그아웃 경로
    //
    @GetMapping("/login")
    fun login(): String = "login.html"

    @GetMapping("/logout")  //get요청시 자동 로그아웃 +메인페이지로
    fun logout(): String {
        return "redirect:/"
    }

    @PreAuthorize("isAuthenticated()") //로그인 했을때
    @GetMapping("/mypage1")
    fun mypage(auth: Authentication): String {
        return "mypage.html"
    }

    /**
     * 회원가입 페이지로 이동
     */
    @GetMapping("/join")
    fun createForm(model: Model): String {

        return "join"
    }

    @PostMapping("/addmember") //따로 페이지 안만들어도 됨.
    fun create(@Valid form: MemberForm, result: BindingResult): String {
        println(form)

        if (result.hasErrors()) {
            return "redirect:/join";
        }
        println(form)
        if (form.password != form.confirmPassword) {
            result.rejectValue(
                "confirmPassword", "passwordInCorrect",
                "패스워드가 일치하지 않습니다."
            )
            return "redirect:/join";
        }

        val hashpassword = passwordEncoder.encode(form.password)
        // member 객체 생성
        val member = Member().apply {
            name = form.name
            age = form.age
            email = form.email
            password = hashpassword
        }
        memberService.join(member)

        return "redirect:/joinComplete"
    }

    /**
     * 비밀번호 찾기
     * 해당 이메일이 DB에 있는지 확인 후 임시 비밀번호 전송
     * 임시 비밀번호를 현재 비밀번호로 변경
     */
    @GetMapping("/findPw")
    fun findPwForm(model : Model) : String {
        model.addAttribute("findPasswordForm", FindPasswordForm())
        return "createFindPasswordForm"
    }

    @PostMapping("/findPw")
    fun findPw(@Valid form : FindPasswordForm, result: BindingResult) : String{
        if(result.hasErrors()){
            return "createFindPasswordForm"
        }

        // todo 일단 pass...
        return ""
    }
}