package com.example.demo.entity;

import java.time.LocalDate;

import com.example.demo.EntryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryEntity {
	private long id;
	private long userId;
	private long categoryId;
    private String categoryName; // 一覧表示用
	private LocalDate entryDate; //日付
	private EntryType type; //収支区分
	private int amount; //金額
	private String memo;  //メモ
}
