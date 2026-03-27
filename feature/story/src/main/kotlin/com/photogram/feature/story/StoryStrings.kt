package com.photogram.feature.story

internal data class StoryStrings(
    val closeStory: String,
    val sendReplyDesc: String,
    val heartReactDesc: String,
    val replySentPlaceholder: String,
    val replyDefaultPlaceholder: String,
) {
    companion object {
        fun forCode(code: String): StoryStrings = when (code) {
            "ES" -> StoryStrings(
                closeStory              = "Cerrar historia",
                sendReplyDesc           = "Enviar respuesta",
                heartReactDesc          = "Reaccionar con corazón",
                replySentPlaceholder    = "¡Enviado!",
                replyDefaultPlaceholder = "Envía un mensaje...",
            )
            "FR" -> StoryStrings(
                closeStory              = "Fermer la story",
                sendReplyDesc           = "Envoyer la réponse",
                heartReactDesc          = "Réagir avec un cœur",
                replySentPlaceholder    = "Envoyé !",
                replyDefaultPlaceholder = "Envoyer un message...",
            )
            "IT" -> StoryStrings(
                closeStory              = "Chiudi storia",
                sendReplyDesc           = "Invia risposta",
                heartReactDesc          = "Reagisci con cuore",
                replySentPlaceholder    = "Inviato!",
                replyDefaultPlaceholder = "Invia un messaggio...",
            )
            "ZH" -> StoryStrings(
                closeStory              = "关闭故事",
                sendReplyDesc           = "发送回复",
                heartReactDesc          = "用心形回应",
                replySentPlaceholder    = "已发送！",
                replyDefaultPlaceholder = "发送消息...",
            )
            "JA" -> StoryStrings(
                closeStory              = "ストーリーを閉じる",
                sendReplyDesc           = "返信を送る",
                heartReactDesc          = "ハートでリアクション",
                replySentPlaceholder    = "送信済み！",
                replyDefaultPlaceholder = "メッセージを送る...",
            )
            else -> StoryStrings( // EN
                closeStory              = "Close story",
                sendReplyDesc           = "Send reply",
                heartReactDesc          = "React with heart",
                replySentPlaceholder    = "Sent!",
                replyDefaultPlaceholder = "Send a message...",
            )
        }
    }
}
