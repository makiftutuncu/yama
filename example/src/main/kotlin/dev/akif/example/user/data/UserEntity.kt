package dev.akif.example.user.data

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.time.LocalDate
import java.util.Objects

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    val name: String,
    @Column
    val dateOfBirth: LocalDate?,
    @Column
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this.klass != other.klass) return false
        other as UserEntity
        return id == other.id &&
            email == other.email &&
            name == other.name &&
            dateOfBirth == other.dateOfBirth &&
            createdAt == other.createdAt &&
            updatedAt == other.updatedAt
    }

    final override fun hashCode(): Int = Objects.hash(id, email, name, dateOfBirth, createdAt, updatedAt)

    override fun toString(): String =
        "User(id=$id, email=$email, name=$name, dateOfBirth=$dateOfBirth, createdAt=$createdAt, updatedAt=$updatedAt)"

    private val Any.klass: Class<*>
        get() = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else javaClass
}
