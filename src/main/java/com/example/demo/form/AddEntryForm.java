package com.example.demo.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.EntryType;

import lombok.Data;

@Data
public class AddEntryForm {
	
	@NotNull(message = "カテゴリを選択してください。")
    private Long categoryId; // カテゴリID

    @NotNull(message = "日付を入力してください。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "今日以前の日付を入力してください。")
    private LocalDate date; // 日付（java.sql.DateよりLocalDate推奨）

    @NotNull(message = "収支区分を選択してください。")
    private EntryType type; // 収支区分

    @NotNull(message = "金額を入力してください。")
    @Min(value = 1, message = "正の整数を入力してください。")
    private Integer amount;

    private String memo; // 備考

}
