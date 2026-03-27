package com.photogram.feature.settings

internal data class StorageDetailStrings(
    val title: String,
    val sectionBreakdown: String,
    val photos: String,
    val videos: String,
    val messages: String,
    val sectionManagement: String,
    val freeSpace: String,
    val freeSpaceSubtitle: String,
    val clearCache: String,
    val ofGb: String,
    val usedPercent: String,
    val ofTempData: String,
) {
    companion object {
        fun forCode(code: String): StorageDetailStrings = when (code) {
            "ES" -> StorageDetailStrings(
                title            = "Almacenamiento",
                sectionBreakdown = "DESGLOSE",
                photos           = "Fotos",
                videos           = "Vídeos",
                messages         = "Mensajes",
                sectionManagement = "GESTIÓN",
                freeSpace        = "Liberar espacio",
                freeSpaceSubtitle = "Eliminar fotos en baja calidad",
                clearCache       = "Vaciar caché",
                ofGb             = "de",
                usedPercent      = "utilizado",
                ofTempData       = "en datos temporales",
            )
            "FR" -> StorageDetailStrings(
                title            = "Stockage",
                sectionBreakdown = "DÉTAILS",
                photos           = "Photos",
                videos           = "Vidéos",
                messages         = "Messages",
                sectionManagement = "GESTION",
                freeSpace        = "Libérer de l'espace",
                freeSpaceSubtitle = "Supprimer les photos de basse qualité",
                clearCache       = "Vider le cache",
                ofGb             = "sur",
                usedPercent      = "utilisé",
                ofTempData       = "de données temporaires",
            )
            "IT" -> StorageDetailStrings(
                title            = "Spazio",
                sectionBreakdown = "DETTAGLIO",
                photos           = "Foto",
                videos           = "Video",
                messages         = "Messaggi",
                sectionManagement = "GESTIONE",
                freeSpace        = "Libera spazio",
                freeSpaceSubtitle = "Elimina foto a bassa qualità",
                clearCache       = "Svuota cache",
                ofGb             = "su",
                usedPercent      = "utilizzato",
                ofTempData       = "di dati temporanei",
            )
            "ZH" -> StorageDetailStrings(
                title            = "存储",
                sectionBreakdown = "详情",
                photos           = "照片",
                videos           = "视频",
                messages         = "消息",
                sectionManagement = "管理",
                freeSpace        = "释放空间",
                freeSpaceSubtitle = "删除低质量照片",
                clearCache       = "清除缓存",
                ofGb             = "共",
                usedPercent      = "已使用",
                ofTempData       = "临时数据",
            )
            "JA" -> StorageDetailStrings(
                title            = "ストレージ",
                sectionBreakdown = "内訳",
                photos           = "写真",
                videos           = "動画",
                messages         = "メッセージ",
                sectionManagement = "管理",
                freeSpace        = "空き容量を増やす",
                freeSpaceSubtitle = "低画質の写真を削除",
                clearCache       = "キャッシュをクリア",
                ofGb             = "/",
                usedPercent      = "使用中",
                ofTempData       = "の一時データ",
            )
            else -> StorageDetailStrings( // EN
                title            = "Storage",
                sectionBreakdown = "BREAKDOWN",
                photos           = "Photos",
                videos           = "Videos",
                messages         = "Messages",
                sectionManagement = "MANAGEMENT",
                freeSpace        = "Free up space",
                freeSpaceSubtitle = "Remove low-quality photos",
                clearCache       = "Clear cache",
                ofGb             = "of",
                usedPercent      = "used",
                ofTempData       = "of temporary data",
            )
        }
    }
}
