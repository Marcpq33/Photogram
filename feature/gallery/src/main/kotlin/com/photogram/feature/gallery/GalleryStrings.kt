package com.photogram.feature.gallery

internal data class GalleryStrings(
    val title: String,
    val back: String,
    val closeSearch: String,
    val openSearch: String,
    val searchPlaceholder: String,
    val noResultsFor: String,
    val trySearchHint: String,
    val navHome: String,
    val navAlbums: String,
    val navRecaps: String,
    val navProfile: String,
) {
    companion object {
        fun forCode(code: String): GalleryStrings = when (code) {
            "ES" -> GalleryStrings(
                title            = "Galería",
                back             = "Volver",
                closeSearch      = "Cerrar búsqueda",
                openSearch       = "Buscar",
                searchPlaceholder = "Buscar fotos…",
                noResultsFor     = "Sin resultados para",
                trySearchHint    = "Prueba con el nombre del mes, día o álbum",
                navHome          = "Inicio",
                navAlbums        = "Álbumes",
                navRecaps        = "Recaps",
                navProfile       = "Perfil",
            )
            "FR" -> GalleryStrings(
                title            = "Galerie",
                back             = "Retour",
                closeSearch      = "Fermer la recherche",
                openSearch       = "Rechercher",
                searchPlaceholder = "Rechercher des photos…",
                noResultsFor     = "Aucun résultat pour",
                trySearchHint    = "Essayez le nom du mois, du jour ou de l'album",
                navHome          = "Accueil",
                navAlbums        = "Albums",
                navRecaps        = "Recaps",
                navProfile       = "Profil",
            )
            "IT" -> GalleryStrings(
                title            = "Galleria",
                back             = "Indietro",
                closeSearch      = "Chiudi ricerca",
                openSearch       = "Cerca",
                searchPlaceholder = "Cerca foto…",
                noResultsFor     = "Nessun risultato per",
                trySearchHint    = "Prova con il nome del mese, giorno o album",
                navHome          = "Home",
                navAlbums        = "Album",
                navRecaps        = "Recap",
                navProfile       = "Profilo",
            )
            "ZH" -> GalleryStrings(
                title            = "相册",
                back             = "返回",
                closeSearch      = "关闭搜索",
                openSearch       = "搜索",
                searchPlaceholder = "搜索照片…",
                noResultsFor     = "未找到相关结果：",
                trySearchHint    = "尝试输入月份、日期或相册名称",
                navHome          = "首页",
                navAlbums        = "相册",
                navRecaps        = "回顾",
                navProfile       = "我的",
            )
            "JA" -> GalleryStrings(
                title            = "ギャラリー",
                back             = "戻る",
                closeSearch      = "検索を閉じる",
                openSearch       = "検索",
                searchPlaceholder = "写真を検索…",
                noResultsFor     = "検索結果なし：",
                trySearchHint    = "月名、日付、またはアルバム名で試してください",
                navHome          = "ホーム",
                navAlbums        = "アルバム",
                navRecaps        = "リキャップ",
                navProfile       = "プロフィール",
            )
            else -> GalleryStrings( // EN
                title            = "Gallery",
                back             = "Back",
                closeSearch      = "Close search",
                openSearch       = "Search",
                searchPlaceholder = "Search photos…",
                noResultsFor     = "No results for",
                trySearchHint    = "Try the name of the month, day, or album",
                navHome          = "Home",
                navAlbums        = "Albums",
                navRecaps        = "Recaps",
                navProfile       = "Profile",
            )
        }
    }
}
