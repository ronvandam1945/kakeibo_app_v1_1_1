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
import com.example.demo.entity.EntryEntity;
import com.example.demo.form.AddEntryForm;
import com.example.demo.form.DeleteEntryForm;
import com.example.demo.form.EditEntryForm;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CategoryService;
import com.example.demo.service.EntryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EntryController {

	private final CategoryService categoryService;
	private final EntryService entryService;

	/*--- 家計簿一覧画面表示リクエスト ---*/
	@GetMapping("/show-entry-list")
	public String showEntryList(
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Long categoryId,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		// ★ 年/月の選択肢を作る（例：今年±2年）
		int thisYear = java.time.LocalDate.now().getYear();
		List<Integer> years = java.util.stream.IntStream.rangeClosed(thisYear - 2, thisYear + 1)
				.boxed().toList();
		List<Integer> months = java.util.stream.IntStream.rangeClosed(1, 12)
				.boxed().toList();

		model.addAttribute("years", years);
		model.addAttribute("months", months);

		// ★ カテゴリ一覧（ユーザーのカテゴリ）
		Long userId = principal.getId();
		List<CategoryEntity> categoryList = categoryService.findByUserId(userId);
		model.addAttribute("categoryList", categoryList);

		// ★ 選択状態を戻す（未指定ならnullでOK）
		model.addAttribute("selectedYear", year);
		model.addAttribute("selectedMonth", month);
		model.addAttribute("selectedCategoryId", categoryId);

		// 初回は結果なし
		model.addAttribute("entryList", null);

		boolean firstVisit = (year == null && month == null && categoryId == null);
		model.addAttribute("firstVisit", firstVisit);

		if (firstVisit) {
			model.addAttribute("entryList", null);
			model.addAttribute("incomeTotal", 0);
			model.addAttribute("expenseTotal", 0);
			return "entry-list";
		}

		// 2回目以降（何か選択された）
		int incomeTotal = entryService.incomeTotal(
				userId,
				year,
				month,
				categoryId);

		int expenseTotal = entryService.expenseTotal(
				userId,
				year,
				month,
				categoryId);

		// entries検索（JDBC）
		List<EntryEntity> entryList = entryService.searchEntry(userId, year, month, categoryId);
		model.addAttribute("entryList", entryList);
		model.addAttribute("incomeTotal", incomeTotal);
		model.addAttribute("expenseTotal", expenseTotal);

		return "entry-list";

	}

	/*--- 家計簿登録画面表示リクエスト ---*/
	@GetMapping("/add-entry")
	public String addEntry(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

		model.addAttribute("addEntryForm", new AddEntryForm());
		// 例：DBから取得
		Long userId = principal.getId();
		List<CategoryEntity> categories = categoryService.findByUserId(userId);
		model.addAttribute("categories", categories);

		return "add-entry";

	}

	@PostMapping("/back-add-entry")
	public String backAddEntry(
			@ModelAttribute("addEntryForm") AddEntryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		Long userId = principal.getId();
		model.addAttribute("addEntryForm", form);
		model.addAttribute("categories", categoryService.findByUserId(userId));
		return "add-entry";
	}

	/*--- エントリー登録確認画面表示リクエスト ---*/
	@PostMapping("/confirm-add-entry")
	public String confirmAddEntry(
			@Valid @ModelAttribute("addEntryForm") AddEntryForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		Long userId = principal.getId();
		if (bindingResult.hasErrors()) {
			List<CategoryEntity> categories = categoryService.findByUserId(userId);
			model.addAttribute("categories", categories);
			return "add-entry";
		}

		// ② 画面に渡す
		CategoryEntity category = categoryService.findByIdAndUserId(form.getCategoryId(), userId);
		model.addAttribute("categoryName", category.getName());
		model.addAttribute("addEntryForm", form);

		return "confirm-add-entry";
	}

	@PostMapping("/do-add-entry")
	public String doAddEntry(
			@ModelAttribute("addEntryForm") AddEntryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();
		EntryEntity entry = new EntryEntity();
		entry.setUserId(userId); // ★セッションから入れる
		entry.setCategoryId(form.getCategoryId());
		entry.setEntryDate(form.getDate());
		entry.setType(form.getType());
		entry.setAmount(form.getAmount());
		entry.setMemo(form.getMemo());

		entryService.addEntry(entry);

		redirectAttributes.addFlashAttribute("msg", "明細を登録しました。");

		return "redirect:/complete";

	}

	@PostMapping("/edit-entry")
	public String editEntry(
			@RequestParam("id") Long id,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Long categoryId,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		// ★自分の明細だけ取得（他人のを編集できないように）
		Long userId = principal.getId();
		EntryEntity entry = entryService.findByIdAndUserId(id, userId);
		if (entry == null) {
			return "redirect:/show-entry-list"; // 見つからない/権限なし
		}

		EditEntryForm form = new EditEntryForm();
		form.setId(entry.getId());
		form.setCategoryId(entry.getCategoryId());
		form.setDate(entry.getEntryDate());
		form.setType(entry.getType());
		form.setAmount(entry.getAmount());
		form.setMemo(entry.getMemo());

		// 検索条件をフォームに保持
		model.addAttribute("selectedYear", year);
		model.addAttribute("selectedMonth", month);
		model.addAttribute("selectedCategoryId", categoryId);

		model.addAttribute("editEntryForm", form);
		model.addAttribute("categories",
				categoryService.findByUserId(userId));

		return "edit-entry";

	}

	@PostMapping("/back-edit-entry")
	public String backEditEntry(
			@ModelAttribute("editEntryForm") EditEntryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		Long userId = principal.getId();
		model.addAttribute("editEntryForm", form);
		model.addAttribute("categories", categoryService.findByUserId(userId));
		return "edit-entry";
	}

	/*--- エントリー編集確認画面表示リクエスト ---*/
	@PostMapping("/confirm-edit-entry")
	public String confirmEditEntry(
			@Valid @ModelAttribute("editEntryForm") EditEntryForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		Long userId = principal.getId();
		if (bindingResult.hasErrors()) {
			List<CategoryEntity> categories = categoryService.findByUserId(userId);
			model.addAttribute("categories", categories);
			return "edit-entry";
		}

		// ② 画面に渡す
		CategoryEntity category = categoryService.findByIdAndUserId(form.getCategoryId(), userId);
		model.addAttribute("categoryName", category.getName());
		model.addAttribute("editEntryForm", form);

		return "confirm-edit-entry";
	}

	@PostMapping("/do-edit-entry")
	public String doEditEntry(
			@ModelAttribute("editEntryForm") EditEntryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();
		EntryEntity entry = new EntryEntity();
		entry.setId(form.getId());
		entry.setUserId(userId); // ★セッションから入れる
		entry.setCategoryId(form.getCategoryId());
		entry.setEntryDate(form.getDate());
		entry.setType(form.getType());
		entry.setAmount(form.getAmount());
		entry.setMemo(form.getMemo());

		entryService.editEntry(entry);

		redirectAttributes.addFlashAttribute("msg", "明細を編集しました。");

		return "redirect:/complete";

	}

	/*--- エントリー削除確認画面表示リクエスト ---*/
	@PostMapping("/confirm-delete-entry")
	public String confirmDeleteEntry(
			@RequestParam("id") Long id,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Long categoryId,
			@AuthenticationPrincipal CustomUserDetails principal,
			Model model) {

		// ② 画面に渡す
		Long userId = principal.getId();
		EntryEntity entry = entryService.findByIdAndUserId(id, userId);
		if (entry == null)
			return "redirect:/show-entry-list";

		CategoryEntity category = categoryService.findByIdAndUserId(entry.getCategoryId(), userId);

		DeleteEntryForm form = new DeleteEntryForm();
		form.setId(entry.getId());
		form.setCategoryId(entry.getCategoryId());
		form.setDate(entry.getEntryDate());
		form.setType(entry.getType());
		form.setAmount(entry.getAmount());
		form.setMemo(entry.getMemo());

		model.addAttribute("categoryName", category.getName());
		model.addAttribute("deleteEntryForm", form);

		// ★戻る用に保持
		model.addAttribute("selectedYear", year);
		model.addAttribute("selectedMonth", month);
		model.addAttribute("selectedCategoryId", categoryId);

		return "confirm-delete-entry";
	}

	@PostMapping("/do-delete-entry")
	public String doDeleteEntry(
			@ModelAttribute("deleteEntryForm") DeleteEntryForm form,
			@AuthenticationPrincipal CustomUserDetails principal,
			RedirectAttributes redirectAttributes) {

		Long userId = principal.getId();
		entryService.deleteEntry(form.getId(), userId);

		redirectAttributes.addFlashAttribute("msg", "明細を削除しました。");

		return "redirect:/complete";

	}

}
