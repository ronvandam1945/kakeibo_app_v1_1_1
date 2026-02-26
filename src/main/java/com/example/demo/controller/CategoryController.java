package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.CategoryEntity;
import com.example.demo.form.AddCategoryForm;
import com.example.demo.form.DeleteCategoryForm;
import com.example.demo.form.EditCategoryForm;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	/*--- カテゴリー一覧画面表示リクエスト ---*/
	@GetMapping("/category")
	public String showCategory(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

		Long userId = principal.getId();

		//  userId でカテゴリ検索
		List<CategoryEntity> categoryList = categoryService.findByUserId(userId);

		//  画面に渡す
		model.addAttribute("categoryList", categoryList);

		return "category-list";
	}

	/*--- カテゴリー登録画面表示リクエスト ---*/
	@PostMapping("/add-category")
	public String addCategory(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

		// 画面に渡す
		model.addAttribute("addCategoryForm", new AddCategoryForm());

		return "add-category";
	}

	@PostMapping("/back-add-category")
	public String backAddCategory(
			@ModelAttribute("addCategoryForm") AddCategoryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		model.addAttribute("addCategoryForm", form);
		return "add-category";
	}

	/*--- カテゴリー登録確認画面表示リクエスト ---*/
	@PostMapping("/confirm-add-category")
	public String confirmAddCategory(
			@Valid @ModelAttribute("addCategoryForm") AddCategoryForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "add-category";
		}

		// 画面に渡す
		model.addAttribute("addCategoryForm", form);

		return "confirm-add-category";
	}

	/*--- カテゴリー登録実施リクエスト ---*/
	@PostMapping("/do-add-category")
	public String doAddCategory(
			@ModelAttribute("addCategoryForm") AddCategoryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();

		CategoryEntity category = new CategoryEntity();
		category.setUserId(userId); // 
		category.setName(form.getName());
		category.setType(form.getType());
		category.setSortOrder(form.getSortOrder());

		categoryService.addCategory(category);

		redirectAttributes.addFlashAttribute("msg", "カテゴリを登録しました。");

		return "redirect:/complete";

	}

	/*--- カテゴリー編集画面表示リクエスト ---*/
	@PostMapping("/edit-category")
	public String editCategory(
			@RequestParam("id") long id,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		Long userId = principal.getId();
		// 自分のカテゴリだけ取得（他人のを編集できないように）
		CategoryEntity category = categoryService.findByIdAndUserId(id, userId);
		if (category == null) {
			return "redirect:/category"; // 見つからない/権限なし
		}

		EditCategoryForm form = new EditCategoryForm();
		form.setId(category.getId());
		form.setName(category.getName());
		form.setType(category.getType());
		form.setSortOrder(category.getSortOrder());

		//  画面に渡す
		model.addAttribute("editCategoryForm", form);

		return "edit-category";
	}

	/*--- カテゴリー編集確認画面表示リクエスト ---*/
	@PostMapping("/confirm-edit-category")
	public String confirmEditCategory(
			@Valid @ModelAttribute("editCategoryForm") EditCategoryForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "edit-category";
		}

		// 画面に渡す
		model.addAttribute("editCategoryForm", form);

		return "confirm-edit-category";
	}

	/*--- カテゴリー編集実施リクエスト ---*/
	@PostMapping("/do-edit-category")
	public String doEditCategory(
			@ModelAttribute("editCategoryForm") EditCategoryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();
		CategoryEntity category = new CategoryEntity();
		category.setId(form.getId());
		category.setUserId(userId); 
		category.setName(form.getName());
		category.setType(form.getType());
		category.setSortOrder(form.getSortOrder());

		categoryService.editCategory(category);

		redirectAttributes.addFlashAttribute("msg", "カテゴリを編集しました。");

		return "redirect:/complete";

	}

	@PostMapping("/back-edit-category")
	public String backEditCategory(
			@ModelAttribute("editCategoryForm") EditCategoryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		model.addAttribute("editCategoryForm", form);
		return "edit-category";
	}

	/*--- カテゴリー削除画面表示リクエスト ---*/
	@PostMapping("/confirm-delete-category")
	public String deleteCategory(
			//			BindingResult bindingResult,
			@Valid @ModelAttribute("deleteCategoryForm") DeleteCategoryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		//  画面に渡す
		model.addAttribute("deleteCategoryForm", form);

		return "confirm-delete-category";
	}

	/*--- カテゴリー削除実施リクエスト ---*/
	@PostMapping("/do-delete-category")
	public String doDeleteCategory(
			@RequestParam("id") long id,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();
		categoryService.deleteCategory(id, userId);

		redirectAttributes.addFlashAttribute("msg", "カテゴリを削除しました。");

		return "redirect:/complete";

	}

}
