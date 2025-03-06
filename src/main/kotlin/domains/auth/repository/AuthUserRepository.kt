package org.example.domains.auth.repository

import io.lettuce.core.dynamic.annotation.Param
import org.example.types.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface AuthUserRepository : JpaRepository<User, String> {
    fun findByUsername(username: String): User?
    fun existsByUsernameAndPlatform(username: String, platform: String): Boolean

    @Modifying
    @Query("UPDATE User u SET u.accessToken = :accessToken WHERE u.username = :username AND u.platform = :platform")
    fun updateAccessTokenByUsernameAndPlatform(
        @Param("username") username: String,
        @Param("platform") platform: String,
        @Param("accessToken") accessToken: String?
    )
}