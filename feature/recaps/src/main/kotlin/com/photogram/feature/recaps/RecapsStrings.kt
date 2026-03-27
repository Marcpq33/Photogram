package com.photogram.feature.recaps

internal data class RecapsStrings(
    val title: String,
    val backDesc: String,
    val settingsDesc: String,
    val createNewRecap: String,
    val yourPersonalRecaps: String,
    val playDesc: String,
    val navHome: String,
    val navRecaps: String,
    val navGallery: String,
    val navCreate: String,
    val navChat: String,
    val navProfile: String,
) {
    companion object {
        fun forCode(code: String): RecapsStrings = when (code) {
            "ES" -> RecapsStrings(
                title              = "Recaps",
                backDesc           = "Volver",
                settingsDesc       = "Ajustes",
                createNewRecap     = "✨  Crear nuevo Recap",
                yourPersonalRecaps = "Tus Recaps personales",
                playDesc           = "Reproducir",
                navHome            = "Inicio",
                navRecaps          = "Recaps",
                navGallery         = "Galería",
                navCreate          = "Crear",
                navChat            = "Chat",
                navProfile         = "Perfil",
            )
            "FR" -> RecapsStrings(
                title              = "Recaps",
                backDesc           = "Retour",
                settingsDesc       = "Paramètres",
                createNewRecap     = "✨  Créer un nouveau Recap",
                yourPersonalRecaps = "Vos Recaps personnels",
                playDesc           = "Lire",
                navHome            = "Accueil",
                navRecaps          = "Recaps",
                navGallery         = "Galerie",
                navCreate          = "Créer",
                navChat            = "Chat",
                navProfile         = "Profil",
            )
            "IT" -> RecapsStrings(
                title              = "Recap",
                backDesc           = "Indietro",
                settingsDesc       = "Impostazioni",
                createNewRecap     = "✨  Crea nuovo Recap",
                yourPersonalRecaps = "I tuoi Recap personali",
                playDesc           = "Riproduci",
                navHome            = "Home",
                navRecaps          = "Recap",
                navGallery         = "Galleria",
                navCreate          = "Crea",
                navChat            = "Chat",
                navProfile         = "Profilo",
            )
            "ZH" -> RecapsStrings(
                title              = "回顾",
                backDesc           = "返回",
                settingsDesc       = "设置",
                createNewRecap     = "✨  创建新回顾",
                yourPersonalRecaps = "您的个人回顾",
                playDesc           = "播放",
                navHome            = "首页",
                navRecaps          = "回顾",
                navGallery         = "相册",
                navCreate          = "创建",
                navChat            = "聊天",
                navProfile         = "我的",
            )
            "JA" -> RecapsStrings(
                title              = "リキャップ",
                backDesc           = "戻る",
                settingsDesc       = "設定",
                createNewRecap     = "✨  新しいリキャップを作成",
                yourPersonalRecaps = "あなたのリキャップ",
                playDesc           = "再生",
                navHome            = "ホーム",
                navRecaps          = "リキャップ",
                navGallery         = "ギャラリー",
                navCreate          = "作成",
                navChat            = "チャット",
                navProfile         = "プロフィール",
            )
            else -> RecapsStrings( // EN
                title              = "Recaps",
                backDesc           = "Back",
                settingsDesc       = "Notifications",
                createNewRecap     = "✨  Create new Recap",
                yourPersonalRecaps = "Your Personal Recaps",
                playDesc           = "Play",
                navHome            = "Home",
                navRecaps          = "Recaps",
                navGallery         = "Gallery",
                navCreate          = "Create",
                navChat            = "Chat",
                navProfile         = "Profile",
            )
        }
    }
}
