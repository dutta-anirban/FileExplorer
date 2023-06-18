import javafx.scene.control.ListCell
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun File.getMimeType(): String {
    if (this.isDirectory) return "inode/directory"
    return Files.probeContentType(Paths.get(this.absolutePath)) ?: "application/octet-stream"
}

class FileListViewCell : ListCell<File>() {
    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null && !empty) {
            text = item.name
            graphic = when {
                item.getMimeType().startsWith("image") -> Icon.IMAGE.toIcon()
                item.getMimeType() == "inode/directory" -> Icon.FOLDER.toIcon()
                listOf("text/html", "text/javascript", "application/xhtml+xml", "application/xml", "text/xml",
                    "application/json", "application/ld+json", "text/css").contains(item.getMimeType()) -> Icon.CODE.toIcon()
                item.getMimeType().startsWith("text") || item.getMimeType() == "application/rtf" -> Icon.TEXT.toIcon()
                item.getMimeType() == "application/msword" || item.getMimeType() == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> Icon.DOC.toIcon()
                item.getMimeType() == "application/vnd.ms-excel" || item.getMimeType() == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> Icon.XLS.toIcon()
                item.getMimeType() == "application/vnd.ms-powerpoint" || item.getMimeType() == "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> Icon.PPT.toIcon()
                item.getMimeType() == "application/pdf" -> Icon.PDF.toIcon()
                item.getMimeType() == "audio" -> Icon.AUDIO.toIcon()
                item.getMimeType() == "video" -> Icon.VIDEO.toIcon()
                listOf("application/x-rar-compressed", "application/x-freearc", "application/x-bzip",
                    "application/x-bzip2", "application/gzip", "application/java-archive", "application/vnd.rar",
                    "application/x-tar", "application/zip", "application/x-zip-compressed", " multipart/x-zip",
                    "application/x-7z-compressed").contains(item.getMimeType()) -> Icon.ARCHIVE.toIcon()
                else -> Icon.FILE.toIcon()
            }
        } else {
            text = null
            graphic = null
        }
    }
}