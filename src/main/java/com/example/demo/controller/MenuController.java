package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

	/*--- 管理メニュー画面表示リクエスト ---*/
	@GetMapping("/menu")
	public String showMenu() {
		return "menu";
	}

}
