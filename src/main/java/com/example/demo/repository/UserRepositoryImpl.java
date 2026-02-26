package com.example.demo.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public Optional<UserEntity>  findByLoginId(String loginId ) {
		String sql = """
				SELECT  
				   id ,         
				   login_id ,       
				   password_hash ,        
				   name              
				 FROM              
				   users       
				 WHERE         
				   login_id      = ?      
				""";

		// SQLで検索
		try {
	        Map<String, Object> result = jdbcTemplate.queryForMap(sql, loginId);

	        UserEntity user = new UserEntity();
	        Number idNum = (Number) result.get("id");
	        user.setId(idNum.longValue());
	        user.setLoginId((String) result.get("login_id"));
	        user.setPasswordHash((String) result.get("password_hash"));
	        user.setName((String) result.get("name"));
	        return Optional.of(user);

	    } catch (EmptyResultDataAccessException e) {
	        return Optional.empty();
	    }

	}

}