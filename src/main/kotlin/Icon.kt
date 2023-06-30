import javafx.scene.image.Image
import javafx.scene.image.ImageView

enum class Icon {
    ARCHIVE, AUDIO, CODE, DELETE, DOC, DOWN, FILE, FOLDER, HOME, IMAGE, LOGO,
    MOVE, NEXT, NEW, PDF, PPT, PREVIOUS, RENAME, TEXT, UP, VIDEO, XLS;

    override fun toString(): String = when (this) {
        ARCHIVE -> "archive.png"      // https://icons8.com/icon/q111amA74GPg/archive-folder
        AUDIO -> "audio.png"          // https://icons8.com/icon/IFjBHjd_rSAX/audio-file
        CODE -> "code.png"            // https://icons8.com/icon/DZAkQvVaXhk6/code-file
        DELETE -> "delete.png"        // https://icons8.com/icon/KPhFC2OwpbWV/delete
        DOC -> "doc.png"              // https://icons8.com/icon/SalonH5fQHge/word
        DOWN -> "down.png"            // https://icons8.com/icon/Ur8O9DQXqyVi/scroll-down
        FILE -> "file.png"            // https://icons8.com/icon/mEF_vyjYlnE3/file
        FOLDER -> "folder.png"        // https://icons8.com/icon/JXYalxb9XWWd/folder
        HOME -> "home.png"            // https://icons8.com/icon/i6fZC6wuprSu/home
        IMAGE -> "image.png"          // https://icons8.com/icon/Ns9_HWz9C14k/picture
        LOGO -> "logo.png"            // https://icons8.com/icon/zaUfo6s7ktgf/documents-folder
        MOVE -> "move.png"            // https://icons8.com/icon/qW6DUqkxQGdU/copy-move-folder
        NEXT -> "next.png"            // https://icons8.com/icon/hiJvRiVWkx1i/circled-right
        NEW -> "new.png"              // https://icons8.com/icon/IM9TVEaLZ3Qq/add-folder
        PDF -> "pdf.png"              // https://icons8.com/icon/f-l7OdA6tJxz/pdf
        PPT-> "ppt.png"               // https://icons8.com/icon/L9zmzlVLeXBM/powerpoint
        PREVIOUS -> "previous.png"    // https://icons8.com/icon/AO1h97ca7e0A/back-arrow
        RENAME -> "rename.png"        // https://icons8.com/icon/DqFY5OwPY5gJ/rename
        TEXT -> "text.png"            // https://icons8.com/icon/dt-vg8aqPJDT/document
        UP -> "up.png"                // https://icons8.com/icon/tE02Qj04SXGv/scroll-up
        VIDEO -> "video.png"          // https://icons8.com/icon/4DSSZDmZIKsY/video-file
        XLS -> "xls.png"              // https://icons8.com/icon/3vssC5n6aNw3/xls
    }

    fun toIcon(): ImageView {
        return ImageView(Image(javaClass.getResourceAsStream(this.toString()))).apply {
            when (this@Icon) {
                ARCHIVE, AUDIO, CODE, DOC, FILE, FOLDER, IMAGE, LOGO, PDF, PPT, TEXT, VIDEO, XLS -> {
                    fitWidth = 16.0
                    fitHeight = 16.0
                }
                DELETE, DOWN, HOME, MOVE, NEXT, NEW, PREVIOUS, RENAME, UP -> {
                    fitWidth = 20.0
                    fitHeight = 20.0
                }
            }
        }
    }
}
