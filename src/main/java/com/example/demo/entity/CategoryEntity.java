package com.example.demo.entity;

import com.example.demo.EntryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity {
	private long id;
	private long userId;
	private String name; //食費
	private EntryType type; //収支区分
	private int sortOrder; //ソート順
}
