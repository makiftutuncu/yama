package dev.akif.example.user.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.id = :id")
    fun delete(id: Long): Int
}
