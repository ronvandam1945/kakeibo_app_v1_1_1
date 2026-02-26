package com.example.demo.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.UserEntity;

@Repository
public interface UserRepository {
//	UserEntity findByLoginId(String loginId , String password_hash);
	Optional<UserEntity> findByLoginId(String loginId );

}