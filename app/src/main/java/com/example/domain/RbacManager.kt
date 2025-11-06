package com.example.domain

enum class UserRole(val level: Int) {
    AUDITOR(1), VIEWER(2), OPERATOR(3), ADMIN(4), OWNER(5)
}

data class Permission(val resource: String, val action: String, val minimumRole: UserRole)

object RbacManager {
    private val permissions = listOf(
        Permission("gpu_metrics",    "read",    UserRole.VIEWER),
        Permission("gpu_metrics",    "write",   UserRole.OPERATOR),
        Permission("audit_events",   "read",    UserRole.AUDITOR),
        Permission("thermal_config", "write",   UserRole.ADMIN),
        Permission("user_mgmt",      "write",   UserRole.OWNER),
        Permission("chaos_engine",   "execute", UserRole.ADMIN)
    )

    fun hasPermission(role: UserRole, resource: String, action: String): Boolean {
        val required = permissions.find { it.resource == resource && it.action == action }
            ?: return false
        return role.level >= required.minimumRole.level
    }

    fun getPermissions(role: UserRole): List<Permission> =
        permissions.filter { role.level >= it.minimumRole.level }
}
