package com.photogram.feature.profile

internal data class ProfileStrings(
    val favoriteMemory: String,
    val capturingSince: String,
    val totalPhotos: String,
    val albums: String,
    val daysStreak: String,
    val yourRecaps: String,
    val viewAll: String,
    val yourAlbums: String,
    val navHome: String,
    val navGallery: String,
    val navEvents: String,
    val navProfile: String,
) {
    companion object {
        fun forCode(code: String): ProfileStrings = when (code) {
            "ES" -> ProfileStrings(
                favoriteMemory = "recuerdo favorito",
                capturingSince = "CAPTURANDO VIDA DESDE",
                totalPhotos    = "FOTOS TOTALES",
                albums         = "ÁLBUMES",
                daysStreak     = "DÍAS SEGUIDOS",
                yourRecaps     = "Tus Recaps",
                viewAll        = "VER TODO",
                yourAlbums     = "Tus Álbumes",
                navHome        = "Inicio",
                navGallery     = "Galería",
                navEvents      = "Eventos",
                navProfile     = "Perfil",
            )
            "FR" -> ProfileStrings(
                favoriteMemory = "souvenir préféré",
                capturingSince = "CAPTURER LA VIE DEPUIS",
                totalPhotos    = "TOTAL PHOTOS",
                albums         = "ALBUMS",
                daysStreak     = "JOURS D'AFFILÉE",
                yourRecaps     = "Vos Recaps",
                viewAll        = "TOUT VOIR",
                yourAlbums     = "Vos Albums",
                navHome        = "Accueil",
                navGallery     = "Galerie",
                navEvents      = "Événements",
                navProfile     = "Profil",
            )
            "IT" -> ProfileStrings(
                favoriteMemory = "ricordo preferito",
                capturingSince = "CATTURANDO LA VITA DAL",
                totalPhotos    = "FOTO TOTALI",
                albums         = "ALBUM",
                daysStreak     = "GIORNI DI FILA",
                yourRecaps     = "I tuoi Recap",
                viewAll        = "VEDI TUTTO",
                yourAlbums     = "I tuoi Album",
                navHome        = "Home",
                navGallery     = "Galleria",
                navEvents      = "Eventi",
                navProfile     = "Profilo",
            )
            "ZH" -> ProfileStrings(
                favoriteMemory = "最爱的记忆",
                capturingSince = "记录生活自",
                totalPhotos    = "总照片数",
                albums         = "相册数",
                daysStreak     = "连续天数",
                yourRecaps     = "您的回顾",
                viewAll        = "查看全部",
                yourAlbums     = "您的相册",
                navHome        = "首页",
                navGallery     = "相册",
                navEvents      = "活动",
                navProfile     = "我的",
            )
            "JA" -> ProfileStrings(
                favoriteMemory = "お気に入りの思い出",
                capturingSince = "ライフキャプチャ開始：",
                totalPhotos    = "総写真数",
                albums         = "アルバム",
                daysStreak     = "連続日数",
                yourRecaps     = "あなたのリキャップ",
                viewAll        = "すべて見る",
                yourAlbums     = "あなたのアルバム",
                navHome        = "ホーム",
                navGallery     = "ギャラリー",
                navEvents      = "イベント",
                navProfile     = "プロフィール",
            )
            else -> ProfileStrings( // EN
                favoriteMemory = "favorite memory",
                capturingSince = "CAPTURING LIFE SINCE",
                totalPhotos    = "TOTAL PHOTOS",
                albums         = "ALBUMS",
                daysStreak     = "DAYS STREAK",
                yourRecaps     = "Your Recaps",
                viewAll        = "VIEW ALL",
                yourAlbums     = "Your Albums",
                navHome        = "Home",
                navGallery     = "Gallery",
                navEvents      = "Events",
                navProfile     = "Profile",
            )
        }
    }
}
