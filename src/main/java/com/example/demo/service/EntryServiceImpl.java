package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.EntryEntity;
import com.example.demo.repository.EntryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService {
	
	 private final EntryRepository entryRepository;

	    @Override
	    public List<EntryEntity> searchEntry(Long userId, Integer year, Integer month, Long categoryId) {
	        return entryRepository.searchEntry(userId, year, month, categoryId);
	    }

	    @Override
	    public int incomeTotal(Long userId, Integer year, Integer month, Long categoryId) {
	        return entryRepository.sumByType(userId, year, month, categoryId, "INCOME");
	    }

	    @Override
	    public int expenseTotal(Long userId, Integer year, Integer month, Long categoryId) {
	        return entryRepository.sumByType(userId, year, month, categoryId, "EXPENSE");
	    }

		@Override
		public void addEntry(EntryEntity entry) {
			entryRepository.addEntry(entry);
			
		}
		
		@Override
		public EntryEntity findByIdAndUserId(Long id, Long userId) {
			return entryRepository.findByIdAndUserId(id, userId);
		}

		@Override
		public void editEntry(EntryEntity entry) {
			entryRepository.editEntry(entry);
			
		}

		@Override
		public void deleteEntry(Long id, Long userId) {
			entryRepository.deleteEntry(id, userId);
			
		}



}
