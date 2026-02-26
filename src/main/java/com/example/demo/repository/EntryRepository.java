package com.example.demo.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.EntryEntity;

@Repository
public interface EntryRepository {
	
	List<EntryEntity> searchEntry(Long userId, Integer year, Integer month, Long categoryId);
	
	int sumByType(Long userId, Integer year, Integer month, Long categoryId, String type);
	
	void addEntry(EntryEntity entry);
	
	EntryEntity findByIdAndUserId(Long id, Long userId);
	
	void editEntry(EntryEntity entry);
	
	void deleteEntry(Long id, Long userId);

}