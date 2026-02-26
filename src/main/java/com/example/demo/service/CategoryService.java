package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.CategoryEntity;

@Service
public interface CategoryService {

	List<CategoryEntity> findByUserId(long userId);
	
	void addCategory(CategoryEntity category);
	
	CategoryEntity findByIdAndUserId(long id, long userId);
	
	void editCategory(CategoryEntity category);
	
	void deleteCategory(long id, long userId);
}
