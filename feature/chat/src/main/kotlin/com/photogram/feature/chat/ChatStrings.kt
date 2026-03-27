package com.photogram.feature.chat

internal data class ChatStrings(
    // ChatListScreen
    val searchChats: String,
    // ChatDetailScreen
    val back: String,
    val attachMedia: String,
    val messagePlaceholder: String,
    val send: String,
    val recordAudio: String,
    val cancelRecording: String,
    val recording: String,
    val playVideo: String,
    val micPermissionRequired: String,
    val snackbarNewConversation: String,
) {
    companion object {
        fun forCode(code: String): ChatStrings = when (code) {
            "ES" -> ChatStrings(
                searchChats           = "Buscar chats...",
                back                  = "Volver",
                attachMedia           = "Adjuntar archivo",
                messagePlaceholder    = "Mensaje…",
                send                  = "Enviar",
                recordAudio           = "Grabar audio",
                cancelRecording       = "Cancelar grabación",
                recording             = "Grabando",
                playVideo             = "Reproducir vídeo",
                micPermissionRequired = "Se requiere permiso de micrófono para grabar audio",
                snackbarNewConversation = "Nueva conversación — próximamente",
            )
            "FR" -> ChatStrings(
                searchChats           = "Rechercher des chats...",
                back                  = "Retour",
                attachMedia           = "Joindre un fichier",
                messagePlaceholder    = "Message…",
                send                  = "Envoyer",
                recordAudio           = "Enregistrer un audio",
                cancelRecording       = "Annuler l'enregistrement",
                recording             = "Enregistrement",
                playVideo             = "Lire la vidéo",
                micPermissionRequired = "L'autorisation du microphone est requise pour enregistrer de l'audio",
                snackbarNewConversation = "Nouvelle conversation — bientôt disponible",
            )
            "IT" -> ChatStrings(
                searchChats           = "Cerca chat...",
                back                  = "Indietro",
                attachMedia           = "Allega file",
                messagePlaceholder    = "Messaggio…",
                send                  = "Invia",
                recordAudio           = "Registra audio",
                cancelRecording       = "Annulla registrazione",
                recording             = "Registrazione",
                playVideo             = "Riproduci video",
                micPermissionRequired = "Autorizzazione microfono richiesta per registrare l'audio",
                snackbarNewConversation = "Nuova conversazione — prossimamente",
            )
            "ZH" -> ChatStrings(
                searchChats           = "搜索聊天...",
                back                  = "返回",
                attachMedia           = "附加文件",
                messagePlaceholder    = "消息…",
                send                  = "发送",
                recordAudio           = "录音",
                cancelRecording       = "取消录音",
                recording             = "录音中",
                playVideo             = "播放视频",
                micPermissionRequired = "录音需要麦克风权限",
                snackbarNewConversation = "新对话 — 即将推出",
            )
            "JA" -> ChatStrings(
                searchChats           = "チャットを検索...",
                back                  = "戻る",
                attachMedia           = "ファイルを添付",
                messagePlaceholder    = "メッセージ…",
                send                  = "送信",
                recordAudio           = "音声を録音",
                cancelRecording       = "録音をキャンセル",
                recording             = "録音中",
                playVideo             = "動画を再生",
                micPermissionRequired = "音声を録音するにはマイクの権限が必要です",
                snackbarNewConversation = "新しい会話 — 近日公開",
            )
            else -> ChatStrings( // EN
                searchChats           = "Search chats...",
                back                  = "Back",
                attachMedia           = "Attach media",
                messagePlaceholder    = "Message…",
                send                  = "Send",
                recordAudio           = "Record audio",
                cancelRecording       = "Cancel recording",
                recording             = "Recording",
                playVideo             = "Play video",
                micPermissionRequired = "Microphone permission is required to record audio",
                snackbarNewConversation = "New conversation — coming soon",
            )
        }
    }
}
