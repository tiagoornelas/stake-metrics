package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.enums.UserTypes
import jakarta.persistence.*
import java.time.ZoneOffset
import java.util.UUID
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"])],
    indexes = [Index(columnList = "id")]
)
data class UserModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val email: String = "",
    val name: String = "",
    @Column(name = "password_hash")
    val passwordHash: String = "",
    val type: UserTypes = UserTypes.USER,
    @OneToMany(cascade = [CascadeType.ALL])
    val telegramChats: MutableSet<TelegramChatModel> = mutableSetOf(),
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    val strategies: MutableSet<FifaStrategyModel> = mutableSetOf(),
    val timezoneOffset: ZoneOffset = ZoneOffset.of("-03:00")
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return when (type) {
            UserTypes.SERVICE -> listOf(SimpleGrantedAuthority("ROLE_SERVICE"))
            else -> listOf(SimpleGrantedAuthority("ROLE_USER"))
        }
    }

    override fun getPassword(): String = passwordHash

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    fun toDomain(): User {
        return User(id, email, name, password, type)
    }
}
