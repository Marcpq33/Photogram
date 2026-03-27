package com.photogram.feature.settings

internal data class ChangePasswordStrings(
    val title: String,
    val sectionSecurity: String,
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String,
    val passwordHint: String,
    val changePasswordButton: String,
    val hide: String,
    val show: String,
) {
    companion object {
        fun forCode(code: String): ChangePasswordStrings = when (code) {
            "ES" -> ChangePasswordStrings(
                title                = "Contraseña",
                sectionSecurity      = "SEGURIDAD",
                currentPassword      = "Contraseña actual",
                newPassword          = "Nueva contraseña",
                confirmPassword      = "Confirmar nueva",
                passwordHint         = "La contraseña debe tener al menos 8 caracteres.",
                changePasswordButton = "CAMBIAR CONTRASEÑA",
                hide                 = "Ocultar",
                show                 = "Mostrar",
            )
            "FR" -> ChangePasswordStrings(
                title                = "Mot de passe",
                sectionSecurity      = "SÉCURITÉ",
                currentPassword      = "Mot de passe actuel",
                newPassword          = "Nouveau mot de passe",
                confirmPassword      = "Confirmer le nouveau",
                passwordHint         = "Le mot de passe doit contenir au moins 8 caractères.",
                changePasswordButton = "CHANGER LE MOT DE PASSE",
                hide                 = "Masquer",
                show                 = "Afficher",
            )
            "IT" -> ChangePasswordStrings(
                title                = "Password",
                sectionSecurity      = "SICUREZZA",
                currentPassword      = "Password attuale",
                newPassword          = "Nuova password",
                confirmPassword      = "Conferma nuova",
                passwordHint         = "La password deve contenere almeno 8 caratteri.",
                changePasswordButton = "CAMBIA PASSWORD",
                hide                 = "Nascondi",
                show                 = "Mostra",
            )
            "ZH" -> ChangePasswordStrings(
                title                = "密码",
                sectionSecurity      = "安全",
                currentPassword      = "当前密码",
                newPassword          = "新密码",
                confirmPassword      = "确认新密码",
                passwordHint         = "密码长度至少为8个字符。",
                changePasswordButton = "更改密码",
                hide                 = "隐藏",
                show                 = "显示",
            )
            "JA" -> ChangePasswordStrings(
                title                = "パスワード",
                sectionSecurity      = "セキュリティ",
                currentPassword      = "現在のパスワード",
                newPassword          = "新しいパスワード",
                confirmPassword      = "新しいパスワードを確認",
                passwordHint         = "パスワードは8文字以上である必要があります。",
                changePasswordButton = "パスワードを変更",
                hide                 = "非表示",
                show                 = "表示",
            )
            else -> ChangePasswordStrings( // EN
                title                = "Password",
                sectionSecurity      = "SECURITY",
                currentPassword      = "Current password",
                newPassword          = "New password",
                confirmPassword      = "Confirm new",
                passwordHint         = "Password must be at least 8 characters.",
                changePasswordButton = "CHANGE PASSWORD",
                hide                 = "Hide",
                show                 = "Show",
            )
        }
    }
}
