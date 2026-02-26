package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.EntryEntity;

@Service
public interface EntryService {

	/**
     * 明細一覧を検索（userId + 年月 + カテゴリ）
     */
	List<EntryEntity> searchEntry(Long userId, Integer year, Integer month, Long categoryId);
	

	/**
     * 収入合計
     */
    int incomeTotal(Long userId, Integer year, Integer month, Long categoryId);

    /**
     * 支出合計
     */
    int expenseTotal(Long userId, Integer year, Integer month, Long categoryId);
    
	/**
     * 明細を追加
     */    
    void addEntry(EntryEntity entry);
    
    
    /**
     * 明細を取得
     */    
    EntryEntity findByIdAndUserId(Long id, Long userId);

    /**
     * 明細を編集
     */    
    void editEntry(EntryEntity entry);
    
    /**
    * 明細を削除
    */    
   void deleteEntry(Long id, Long userId);
	
}
