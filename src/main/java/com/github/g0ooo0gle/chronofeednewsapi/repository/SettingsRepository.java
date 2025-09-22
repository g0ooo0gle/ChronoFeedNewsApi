package com.github.g0ooo0gle.chronofeednewsapi.repository;

import com.github.g0ooo0gle.chronofeednewsapi.entity.SettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<SettingsEntity, Long> {
}
