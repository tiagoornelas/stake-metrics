package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.UserTypes
import jakarta.persistence.*
import java.time.ZoneOffset
import java.util.UUID
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.enums.Languages
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
    val telegramChats: MutableSet<MessengerChatModel> = mutableSetOf(),
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    val strategies: MutableSet<FifaStrategyModel> = mutableSetOf(),
    val timezoneOffset: ZoneOffset = ZoneOffset.of("-03:00"),
    val language: Languages = Languages.PORTUGUESE,
    val avoidRepeatedBets: Boolean = false
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return when (type) {
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
        return User(
            id = id,
            email = email,
            name = name,
            password = password,
            timezoneOffset = timezoneOffset,
            language = language,
            avoidRepeatedBets = avoidRepeatedBets
        )
    }
}

fun User.toModel(): UserModel {
    return UserModel(
        id = id,
        email = email,
        name = name,
        passwordHash = password,
        type = type,
        timezoneOffset = timezoneOffset,
        language = language,
        avoidRepeatedBets = avoidRepeatedBets
    )
}
