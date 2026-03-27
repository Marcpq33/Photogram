package com.photogram.feature.upload

internal data class CameraStrings(
    val closeCamera: String,
    val toggleFlash: String,
    val openGallery: String,
    val flipCamera: String,
    val cameraAccessNeeded: String,
    val enableCameraPermission: String,
) {
    companion object {
        fun forCode(code: String): CameraStrings = when (code) {
            "ES" -> CameraStrings(
                closeCamera            = "Cerrar cámara",
                toggleFlash            = "Activar/desactivar flash",
                openGallery            = "Abrir galería",
                flipCamera             = "Cambiar cámara",
                cameraAccessNeeded     = "Se necesita acceso a la cámara",
                enableCameraPermission = "Activa el permiso de cámara en Ajustes para usar esta función.",
            )
            "FR" -> CameraStrings(
                closeCamera            = "Fermer la caméra",
                toggleFlash            = "Activer/désactiver le flash",
                openGallery            = "Ouvrir la galerie",
                flipCamera             = "Retourner la caméra",
                cameraAccessNeeded     = "Accès à la caméra requis",
                enableCameraPermission = "Activez l'autorisation de la caméra dans Paramètres pour utiliser cette fonctionnalité.",
            )
            "IT" -> CameraStrings(
                closeCamera            = "Chiudi fotocamera",
                toggleFlash            = "Attiva/disattiva flash",
                openGallery            = "Apri galleria",
                flipCamera             = "Cambia fotocamera",
                cameraAccessNeeded     = "Accesso fotocamera necessario",
                enableCameraPermission = "Abilita il permesso della fotocamera nelle Impostazioni per usare questa funzione.",
            )
            "ZH" -> CameraStrings(
                closeCamera            = "关闭相机",
                toggleFlash            = "切换闪光灯",
                openGallery            = "打开相册",
                flipCamera             = "翻转相机",
                cameraAccessNeeded     = "需要相机访问权限",
                enableCameraPermission = "请在设置中启用相机权限以使用此功能。",
            )
            "JA" -> CameraStrings(
                closeCamera            = "カメラを閉じる",
                toggleFlash            = "フラッシュの切り替え",
                openGallery            = "ギャラリーを開く",
                flipCamera             = "カメラを反転",
                cameraAccessNeeded     = "カメラのアクセスが必要です",
                enableCameraPermission = "この機能を使用するには、設定でカメラの権限を有効にしてください。",
            )
            else -> CameraStrings( // EN
                closeCamera            = "Close camera",
                toggleFlash            = "Toggle flash",
                openGallery            = "Open gallery",
                flipCamera             = "Flip camera",
                cameraAccessNeeded     = "Camera access needed",
                enableCameraPermission = "Enable camera permission in Settings to use this feature.",
            )
        }
    }
}
