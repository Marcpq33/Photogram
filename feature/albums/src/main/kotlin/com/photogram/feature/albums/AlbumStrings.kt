package com.photogram.feature.albums

internal data class AlbumStrings(
    val back: String,
    val share: String,
    val private: String,
    val upload: String,
    val snackbarShareComingSoon: String,
    val snackbarUploadComingSoon: String,
    val snackbarPhotoComingSoon: String,
    val snackbarDownloadComingSoon: String,
    val snackbarCreateAlbumComingSoon: String,
) {
    companion object {
        fun forCode(code: String): AlbumStrings = when (code) {
            "ES" -> AlbumStrings(
                back    = "Atrás",
                share   = "Compartir",
                private = "Privado",
                upload  = "SUBIR",
                snackbarShareComingSoon       = "Compartir — próximamente",
                snackbarUploadComingSoon      = "Subir foto — próximamente",
                snackbarPhotoComingSoon       = "Foto — próximamente",
                snackbarDownloadComingSoon    = "Descargar — próximamente",
                snackbarCreateAlbumComingSoon = "Crear álbum — próximamente",
            )
            "FR" -> AlbumStrings(
                back    = "Retour",
                share   = "Partager",
                private = "Privé",
                upload  = "UPLOADER",
                snackbarShareComingSoon       = "Partager — bientôt disponible",
                snackbarUploadComingSoon      = "Uploader une photo — bientôt disponible",
                snackbarPhotoComingSoon       = "Photo — bientôt disponible",
                snackbarDownloadComingSoon    = "Télécharger — bientôt disponible",
                snackbarCreateAlbumComingSoon = "Créer un album — bientôt disponible",
            )
            "IT" -> AlbumStrings(
                back    = "Indietro",
                share   = "Condividi",
                private = "Privato",
                upload  = "CARICA",
                snackbarShareComingSoon       = "Condividi — prossimamente",
                snackbarUploadComingSoon      = "Carica foto — prossimamente",
                snackbarPhotoComingSoon       = "Foto — prossimamente",
                snackbarDownloadComingSoon    = "Scarica — prossimamente",
                snackbarCreateAlbumComingSoon = "Crea album — prossimamente",
            )
            "ZH" -> AlbumStrings(
                back    = "返回",
                share   = "分享",
                private = "私密",
                upload  = "上传",
                snackbarShareComingSoon       = "分享 — 即将推出",
                snackbarUploadComingSoon      = "上传照片 — 即将推出",
                snackbarPhotoComingSoon       = "照片 — 即将推出",
                snackbarDownloadComingSoon    = "下载 — 即将推出",
                snackbarCreateAlbumComingSoon = "创建相册 — 即将推出",
            )
            "JA" -> AlbumStrings(
                back    = "戻る",
                share   = "シェア",
                private = "プライベート",
                upload  = "アップロード",
                snackbarShareComingSoon       = "シェア — 近日公開",
                snackbarUploadComingSoon      = "写真をアップロード — 近日公開",
                snackbarPhotoComingSoon       = "写真 — 近日公開",
                snackbarDownloadComingSoon    = "ダウンロード — 近日公開",
                snackbarCreateAlbumComingSoon = "アルバム作成 — 近日公開",
            )
            else -> AlbumStrings( // EN
                back    = "Back",
                share   = "Share",
                private = "Private",
                upload  = "UPLOAD",
                snackbarShareComingSoon       = "Share — coming soon",
                snackbarUploadComingSoon      = "Upload photo — coming soon",
                snackbarPhotoComingSoon       = "Photo — coming soon",
                snackbarDownloadComingSoon    = "Download — coming soon",
                snackbarCreateAlbumComingSoon = "Create album — coming soon",
            )
        }
    }
}
