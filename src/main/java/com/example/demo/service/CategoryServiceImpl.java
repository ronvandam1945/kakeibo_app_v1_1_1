package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.CategoryEntity;
import com.example.demo.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	
	 private final CategoryRepository categoryRepository;

	    @Override
	    public List<CategoryEntity> findByUserId(long userId) {
	        return categoryRepository.findByUserId(userId);
	    }

		@Override
		public void addCategory(CategoryEntity category) {
			categoryRepository.addCategory(category);
		}

		@Override
		public CategoryEntity findByIdAndUserId(long id, long userId) {
			return categoryRepository.findByIdAndUserId(id, userId);
		}

		@Override
		public void editCategory(CategoryEntity category) {
			categoryRepository.editCategory(category);
			
		}

		@Override
		public void deleteCategory(long id, long userId) {
			categoryRepository.deleteCategory(id, userId);
			
		}


}
