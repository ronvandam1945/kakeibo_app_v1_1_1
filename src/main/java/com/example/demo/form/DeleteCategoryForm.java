package com.example.demo.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.demo.EntryType;

import lombok.Data;

@Data
public class DeleteCategoryForm {

	@NotNull(message = "入力してください。")
	@Min(value = 1, message = "1以上の整数を入力してください。")
	private Long id; // ID

	@NotBlank(message = "入力してください。")
	private String name; // カテゴリ名

	@NotNull(message = "収支区分を選択してください")
	private EntryType type; // 収支区分

	@NotNull(message = "入力してください。")
	@Min(value = 0, message = "0以上の整数を入力してください。")
	private Integer sortOrder; // ソート順

}
