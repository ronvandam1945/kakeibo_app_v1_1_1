package com.example.demo.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.CategoryEntity;

@Repository
public interface CategoryRepository {
	
	List<CategoryEntity> findByUserId(long userId);
	
	void addCategory(CategoryEntity category);
	
	CategoryEntity findByIdAndUserId(long id, long userId);
	
	void editCategory(CategoryEntity category);
	
	void deleteCategory(long id, long userId);

}