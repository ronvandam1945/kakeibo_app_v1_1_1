package com.example.demo.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.EntryType;
import com.example.demo.entity.CategoryEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public List<CategoryEntity> findByUserId(long userId) {

		String sql = """
				    SELECT
				      id,
				      user_id,
				      name,
				      type,
				      sort_order
				    FROM categories
				    WHERE user_id = ?
				    ORDER BY sort_order
				""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			CategoryEntity c = new CategoryEntity();
			c.setId(rs.getInt("id"));
			c.setUserId(rs.getInt("user_id"));
			c.setName(rs.getString("name"));
			c.setType(
					EntryType.valueOf(rs.getString("type")));
			c.setSortOrder(rs.getInt("sort_order"));
			return c;
		}, userId);
	}

	@Override
	public void addCategory(CategoryEntity category) {
		String sql = """
				INSERT INTO categories
				(user_id, name, type, sort_order)
				VALUES (?, ?, ?, ?)
				""";
		jdbcTemplate.update(sql,
				category.getUserId(),
				category.getName(),
				category.getType().name(),
				category.getSortOrder());

	}

	@Override
	public CategoryEntity findByIdAndUserId(long id, long userId) {
		String sql = """
				    SELECT id, user_id, name, type, sort_order
				    FROM categories
				    WHERE id = ? AND user_id = ?
				""";
		List<CategoryEntity> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
			CategoryEntity c = new CategoryEntity();
			c.setId(rs.getLong("id"));
			c.setUserId(rs.getLong("user_id"));
			c.setName(rs.getString("name"));
			c.setType(EntryType.valueOf(rs.getString("type")));
			c.setSortOrder(rs.getInt("sort_order"));
			return c;
		}, id, userId);

		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public void editCategory(CategoryEntity category) {
		String sql = """
				UPDATE  categories
				SET
				   name =  ? ,
				   type =  ? ,
				   sort_order = ?
				WHERE
				   id = ?
				   AND
				   user_id = ?
				""";
		jdbcTemplate.update(sql,
				category.getName(),
				category.getType().name(),
				category.getSortOrder(),
				category.getId(),
				category.getUserId());
	}

	@Override
	public void deleteCategory(long id, long userId) {
		String sql = """
				DELETE FROM  categories
				WHERE
				   id = ?
				AND
				   user_id = ?
				""";
		jdbcTemplate.update(sql, id, userId);

	}
}
