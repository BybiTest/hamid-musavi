private fun getNaturalLocalReply(message: String): String? {
    val text = message.trim().lowercase()
    return when {
        text.contains("سلام") || text.contains("درود") || text.contains("hello") || text.contains("hi") -> 
            "سلام دوست من! 👋😊\nخوش اومدی. بگو ببینم چطور می‌تونم کمکت کنم؟"
        text.contains("یه سوال دارم") || text.contains("یک سوال دارم") || text.contains("سؤال دارم") || text.contains("سوالی دارم") -> 
            "حتماً 😊 بپرس، گوشم با توئه. هر چیزی که می‌خوای بدونی رو بگو."
        text.contains("خوبی") || text.contains("چطوری") || text.contains("چه خبر") -> 
            "مرسی که پرسیدی 😊 آماده‌ام کمکت کنم. بگو چه کاری داریم؟"
        text.contains("ممنون") || text.contains("مرسی") || text.contains("دمت گرم") || text.contains("عالی بود") -> 
            "خواهش می‌کنم ❤️ خوشحالم که تونستم کمکت کنم."
        text.contains("خداحافظ") || text.contains("فعلاً") || text.contains("بای") || text.contains("bye") -> 
            "فعلاً دوست من 👋 هر وقت برگشتی من اینجام."
        else -> null
    }
}
