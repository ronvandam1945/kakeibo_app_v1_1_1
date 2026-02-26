package com.example.demo.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.EntryType;
import com.example.demo.entity.EntryEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EntryRepositoryImpl implements EntryRepository {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public List<EntryEntity> searchEntry(Long userId, Integer year, Integer month, Long categoryId) {

	    StringBuilder sql = new StringBuilder("""
	        SELECT
	          e.id,
	          e.user_id,
	          e.category_id,
	          c.name AS category_name,
	          e.date,
	          c.type,
	          e.amount,
	          e.memo
	        FROM entries e
	          LEFT OUTER JOIN categories c
	            ON e.category_id = c.id
	        WHERE
	          e.user_id = ?
	    """);

	    java.util.List<Object> params = new java.util.ArrayList<>();
	    params.add(userId);

	    // 年が選択されているなら絞る
	    if (year != null) {
	        sql.append(" AND YEAR(e.date) = ? ");
	        params.add(year);
	    }

	    // 月が選択されているなら絞る
	    if (month != null) {
	        sql.append(" AND MONTH(e.date) = ? ");
	        params.add(month);
	    }

	    // カテゴリが選択されているなら絞る
	    if (categoryId != null) {
	        sql.append(" AND e.category_id = ? ");
	        params.add(categoryId);
	    }

	    sql.append(" ORDER BY e.date ");

	    return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
	        EntryEntity e = new EntryEntity();
	        e.setId(rs.getLong("id"));
	        e.setUserId(rs.getLong("user_id"));
	        e.setCategoryId(rs.getLong("category_id"));
	        e.setCategoryName(rs.getString("category_name"));
	        e.setEntryDate(rs.getDate("date").toLocalDate());
	        e.setAmount(rs.getInt("amount"));
	        e.setMemo(rs.getString("memo"));
	        return e;
	    }, params.toArray());
	}


	@Override
	public int sumByType(Long userId, Integer year, Integer month, Long categoryId, String type) {

	    StringBuilder sql = new StringBuilder("""
	        SELECT COALESCE(SUM(e.amount), 0)
	        FROM entries e
	        LEFT OUTER JOIN categories c
	          ON e.category_id = c.id
	        WHERE
	          e.user_id = ?
	          AND c.type = ?
	    """);

	    java.util.List<Object> params = new java.util.ArrayList<>();
	    params.add(userId);
	    params.add(type);

	    if (year != null) {
	        sql.append(" AND YEAR(e.date) = ? ");
	        params.add(year);
	    }
	    if (month != null) {
	        sql.append(" AND MONTH(e.date) = ? ");
	        params.add(month);
	    }
	    if (categoryId != null) {
	        sql.append(" AND e.category_id = ? ");
	        params.add(categoryId);
	    }

	    Integer result = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
	    return (result != null) ? result : 0;
	}


	@Override
	public void addEntry(EntryEntity entry) {
		String sql = """
				INSERT INTO entries
				(user_id, category_id, date, type, amount, memo)
				VALUES (?, ?, ?, ?, ?, ?)
				""";
		jdbcTemplate.update(sql,
				entry.getUserId(),
				entry.getCategoryId(),
				entry.getEntryDate(),
				entry.getType().name(),
				entry.getAmount(),
				entry.getMemo());		
	}


	@Override
	public void editEntry(EntryEntity entry) {
		String sql = """
				UPDATE  entries
				SET
				   category_id =  ? ,
				   date =  ? ,
				   type = ?,
				   amount = ? ,
				   memo = ?
				WHERE
				   id = ? AND
				   user_id = ?
				""";
		jdbcTemplate.update(sql,
				entry.getCategoryId(),
				entry.getEntryDate(),
				entry.getType().name(),
				entry.getAmount(),
				entry.getMemo(),
				entry.getId(),
				entry.getUserId());		
	}


	@Override
	public EntryEntity findByIdAndUserId(Long id, Long userId) {
		String sql = """
		        SELECT
		          e.id,
		          e.user_id,
		          e.category_id,
		          c.name AS category_name,
		          e.date,
		          c.type,
		          e.amount,
		          e.memo
		        FROM entries e
		        LEFT OUTER JOIN categories c
		          ON e.category_id = c.id
		        WHERE e.id = ?
		          AND e.user_id = ?
		    """;

		    List<EntryEntity> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
		        EntryEntity e = new EntryEntity();
		        e.setId(rs.getLong("id"));
		        e.setUserId(rs.getLong("user_id"));
		        e.setCategoryId(rs.getLong("category_id"));
		        e.setCategoryName(rs.getString("category_name"));
		        e.setEntryDate(rs.getDate("date").toLocalDate());
		        e.setType(EntryType.valueOf(rs.getString("type")));
		        e.setAmount(rs.getInt("amount"));
		        e.setMemo(rs.getString("memo"));
		        return e;
		    }, id, userId);

		    return list.isEmpty() ? null : list.get(0);
	}


	@Override
	public void deleteEntry(Long id, Long userId) {
		String sql = """
				DELETE FROM  entries
				WHERE
				   id = ?
				AND
				   user_id = ?
				""";
		jdbcTemplate.update(sql, id, userId);
		
	}


}
