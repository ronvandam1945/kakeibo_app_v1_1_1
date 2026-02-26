package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.form.LoginForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

	//	private final LoginService loginService;

	@GetMapping("/login")
	public String showLogin(Model model) {
		model.addAttribute("loginForm", new LoginForm());
		return "login";
	}

}
