import javafx.application.Application
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import javafx.util.Callback
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable

fun main(args: Array<String>) {
    Application.launch(FileExplorer::class.java, *args)
}

class FileExplorer : Application() {
    private val mCurrentImage = SimpleObjectProperty<FileInputStream?>(null)

    //private val mHomeDirectory = Path.of(System.getProperty("user.home"))
    private val mHomeDirectory = Path.of("C://")
    private val mCurrentDirectory = SimpleObjectProperty(Path.of("/")).apply {
        addListener(InvalidationListener {
            updateCurrentDir(this.value)
        })
    }
    private val mDirectoryList = FXCollections.observableArrayList<File>()

    private fun updateCurrentDir(newPath: Path) {
        mDirectoryList.clear()
        Files.list(newPath).filter { it.isReadable() }.forEach { mDirectoryList.add(it.toFile()) }
    }

    private fun getSize(size: Long): String {
        val units = arrayOf("bytes", "KB", "MB", "GB", "TB")
        var i = 0
        var s = size.toDouble()
        while (s > 1024) {
            s /= 1024
            i++
        }
        return "%.2f %s".format(s, units[i])
    }

    private fun addButtonToToolbar(toolbar: ToolBar, text: String, iconPath: Icon): Button {
        val btn = Button().apply {
            graphic = iconPath.toIcon()
            this.text = text
            graphicTextGap = 2.0
        }
        toolbar.items.add(btn)
        return btn
    }

    private fun onRename(file: File): String {
        var result: String? = null
        TextInputDialog(file.name).apply {
            title = "Rename"
            headerText = "Rename ${file.name}"
            contentText = "Enter new name:"
        }.showAndWait().ifPresent {
            mCurrentImage.value?.close()
            mCurrentImage.value = null
            result = if (!file.renameTo(File(file.parentFile.absolutePath + "/" + it))) {
                Alert(
                    Alert.AlertType.ERROR,
                    "Failed to rename ${file.name}.\n\n" + "Please make sure:\n" +
                    "• The new name is not empty.\n" +
                    "• There isn't already a file with the same name in this location.\n" +
                    "• The new name doesn't contain any of the following characters: \\ / : * ? \" < > |",
                    ButtonType.OK
                ).showAndWait()
                file.absolutePath
            } else {
                updateCurrentDir(mCurrentDirectory.value)
                Alert(Alert.AlertType.INFORMATION, "Renamed ${file.name} to $it.", ButtonType.OK).showAndWait()
                file.parentFile.absolutePath + "/" + it
            }
        }
        return result ?: ""
    }

    private fun onMove(file: File?) {
        val target = DirectoryChooser().apply {
            title = "Move ${file?.name} to..."
            initialDirectory = mCurrentDirectory.value.toFile()
        }.showDialog(null)
        if (target != null) {
            Files.move(file!!.toPath(), target.toPath().resolve(file.name))
            mCurrentDirectory.value = target.toPath()
        }
    }

    private fun onDelete(file: File?) {
        if (file != null) {
            mCurrentImage.value?.close()
            mCurrentImage.value = null
            Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete ${file.name}?",
                ButtonType.YES,
                ButtonType.CANCEL
            ).showAndWait().ifPresent {
                if (it == ButtonType.YES) {
                    if (!file.delete()) {
                        Alert(Alert.AlertType.ERROR, "Failed to delete ${file.name}.", ButtonType.OK).showAndWait()
                    } else {
                        updateCurrentDir(mCurrentDirectory.value)
                        Alert(Alert.AlertType.INFORMATION, "Deleted ${file.name}.", ButtonType.OK).showAndWait()
                    }
                }
            }
        }
    }

    private fun newFolder() {
        mCurrentDirectory.value.toFile().mkdirs()
        TextInputDialog("New Folder").apply {
            title = "New Folder"
            headerText = "What would you like to name the new folder?"
            contentText = "Enter new name:"
        }.showAndWait().ifPresent {
            File(mCurrentDirectory.value.toFile().absolutePath + "/" + it).mkdirs()
            updateCurrentDir(mCurrentDirectory.value)
        }
    }

    private fun previewFile(pane: Pane, file: File?) {
        if (file == null) return
        val mimetype = file.getMimeType()
        if (file.isDirectory) {
            for (child in pane.children) {
                child.isVisible = child is Label
            }
            (pane.children[0] as Label).text = "Folder: ${file.name}"
        } else if (mimetype.startsWith("image")) {
            for (child in pane.children) {
                child.isVisible = child is ImageView
            }
            mCurrentImage.value = FileInputStream(file)
        } else if (mimetype == "text/html") {
            for (child in pane.children) {
                child.isVisible = child is WebView
            }
            (pane.children[2] as WebView).engine.load(file.toURI().toString())
        } else if (mimetype.startsWith("text")) {
            for (child in pane.children) {
                child.isVisible = child is TextArea
            }
            (pane.children[3] as TextArea).text = file.readText()
        } else {
            for (child in pane.children) {
                child.isVisible = child is Label
            }
            (pane.children[0] as Label).text = "File: ${file.name}"
        }
    }

    private fun resizeImage(pane: Pane, view: ImageView) {
        if (!view.isVisible || view.image == null) return
        val width = pane.width
        val height = pane.height
        val ratio = view.image.width / view.image.height
        val newRatio = width / height
        if (newRatio > ratio) {
            view.fitWidth = height * ratio
            view.fitHeight = height
            view.translateX = (width - view.fitWidth) / 2
            view.translateY = 0.0
        } else {
            view.fitWidth = width
            view.fitHeight = width / ratio
            view.translateX = 0.0
            view.translateY = (height - view.fitHeight) / 2
        }
    }

    override fun start(primaryStage: Stage?) {
        val cListview = ListView(mDirectoryList).apply {
            // When an item is "activated" == change directory
            fun onItemActivate() {
                val file = selectionModel.selectedItem
                if (file.isDirectory) {
                    mCurrentDirectory.value = file.toPath()
                }
            }
            cellFactory = Callback<ListView<File>, ListCell<File>> { FileListViewCell() }
            onMouseClicked = EventHandler { if (it.clickCount == 2) onItemActivate() }
            onKeyPressed = EventHandler {
                when (it.code) {
                    KeyCode.ENTER -> onItemActivate()
                    KeyCode.DELETE -> onDelete(selectionModel.selectedItem)
                    KeyCode.BACK_SPACE -> {
                        if (mCurrentDirectory.value.parent != null && mCurrentDirectory != mHomeDirectory) mCurrentDirectory.value =
                            mCurrentDirectory.value.parent
                    }

                    KeyCode.F2 -> selectionModel.select(File(onRename(selectionModel.selectedItem)))
                    KeyCode.F5 -> updateCurrentDir(mCurrentDirectory.value)
                    else -> {}
                }
            }
            focusedProperty().addListener { _, _, _ -> requestFocus() }
        }

        val cPreviewPane = Pane().apply {
            val pane = this
            mCurrentImage.value?.close()
            mCurrentImage.value = null
            children.add(Label("Select a supported file to preview").apply {
                translateXProperty().bind(pane.widthProperty().subtract(this.widthProperty()).divide(2))
                translateYProperty().bind(pane.heightProperty().subtract(this.heightProperty()).divide(2))
            })
            children.add(ImageView().apply {
                isPreserveRatio = true
                imageProperty().bind(
                    Bindings.createObjectBinding(
                        {
                            Platform.runLater { resizeImage(pane, this) }
                            mCurrentImage.value?.let { Image(it) }
                        }, mCurrentImage
                    )
                )
                pane.widthProperty().addListener { _, _, _ -> resizeImage(pane, this) }
                pane.heightProperty().addListener { _, _, _ -> resizeImage(pane, this) }
            })
            children.add(WebView().apply {
                prefHeightProperty().bind(pane.heightProperty())
                prefWidthProperty().bind(pane.widthProperty())
            })
            children.add(TextArea().apply {
                isEditable = false
                isWrapText = true
                prefHeightProperty().bind(pane.heightProperty())
                prefWidthProperty().bind(pane.widthProperty())
            })
            for (child in children) {
                child.isVisible = child is Label
            }
            cListview.selectionModel.selectedItemProperty().addListener { _, _, file ->
                previewFile(pane, file)
            }
        }

        // <editor-fold> Toolbar

        val cToolBar = ToolBar()
        addButtonToToolbar(cToolBar, "Home", Icon.HOME).apply {
            tooltip = Tooltip("Go to the home directory")
            onAction = EventHandler { mCurrentDirectory.value = mHomeDirectory }
        }
        addButtonToToolbar(cToolBar, "Previous", Icon.PREVIOUS).apply {
            tooltip = Tooltip("Go to the previous directory")
            isDisable = true
            mCurrentDirectory.addListener { _, _, newValue ->
                isDisable = (newValue == null || newValue == mHomeDirectory)
            }
            onMouseClicked = EventHandler {
                mCurrentDirectory.value = mCurrentDirectory.value.parent ?: mCurrentDirectory.value
            }
        }
        addButtonToToolbar(cToolBar, "Next", Icon.NEXT).apply {
            tooltip = Tooltip("Go into the selected directory")
            isDisable = true
            cListview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                isDisable = (newValue == null || !newValue.isDirectory)
            }
            onMouseClicked = EventHandler {
                mCurrentDirectory.value = cListview.selectionModel.selectedItem.toPath()
            }
        }
        addButtonToToolbar(cToolBar, "", Icon.NEW).apply {
            tooltip = Tooltip("Create a new folder")
            onAction = EventHandler { newFolder() }
        }
        cToolBar.items.add(TextField().apply {
            tooltip = Tooltip("The current directory path")
            onKeyPressed = EventHandler {
                if (it.code == KeyCode.ENTER) {
                    if (Files.exists(Paths.get(text)) && Files.isDirectory(Paths.get(text))) {
                        mCurrentDirectory.value = Paths.get(text)
                    } else if (Files.exists(Paths.get(text)) && Files.isRegularFile(Paths.get(text))) {
                        mCurrentDirectory.value = Paths.get(text).parent
                    } else {
                        Alert(Alert.AlertType.ERROR, "Oops — the entered path does not seem to exist.").show()
                    }
                }
            }
            mCurrentDirectory.addListener { _, _, new ->
                text = new.toString()
            }
            HBox.setHgrow(this, Priority.ALWAYS)
        })
        addButtonToToolbar(cToolBar, "", Icon.UP).apply {
            tooltip = Tooltip("Select the previous item")
            onMouseClicked = EventHandler {
                val index = cListview.selectionModel.selectedIndex
                if (index > 0) {
                    cListview.selectionModel.select(index - 1)
                } else if (index == 0) {
                    cListview.selectionModel.select(cListview.items.size - 1)
                } else {
                    cListview.selectionModel.select(0)
                }
            }
        }
        addButtonToToolbar(cToolBar, "", Icon.DOWN).apply {
            tooltip = Tooltip("Select the next item")
            onMouseClicked = EventHandler {
                val index = cListview.selectionModel.selectedIndex
                if (index < cListview.items.size - 1) {
                    cListview.selectionModel.select(index + 1)
                } else if (index == cListview.items.size - 1) {
                    cListview.selectionModel.select(0)
                } else {
                    cListview.selectionModel.select(0)
                }
            }
        }/*addButtonToToolbar(cToolBar, "Refresh", Icon.REFRESH).apply {
            onMouseClicked = EventHandler {
                updateCurrentDir(mCurrentDirectory.value)
            }
        }*/

        addButtonToToolbar(cToolBar, "Rename", Icon.RENAME).apply {
            tooltip = Tooltip("Rename selected file")
            isDisable = true
            cListview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                isDisable = (newValue == null)
            }
            onAction = EventHandler {
                cListview.selectionModel.select(
                    File(
                        onRename(cListview.selectionModel.selectedItem)
                    )
                )
            }
        }

        addButtonToToolbar(cToolBar, "Move", Icon.MOVE).apply {
            tooltip = Tooltip("Move selected file")
            isDisable = true
            cListview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                isDisable = (newValue == null)
            }
            onAction = EventHandler { onMove(cListview.selectionModel.selectedItem) }
        }
        addButtonToToolbar(cToolBar, "Delete", Icon.DELETE).apply {
            tooltip = Tooltip("Delete selected file")
            isDisable = true
            cListview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                isDisable = (newValue == null)
            }
            onAction = EventHandler { onDelete(cListview.selectionModel.selectedItem) }
        }

        // </editor-fold>

        // <editor-fold> MenuBar

        val myMenuBar = MenuBar().apply {
            menus.add(Menu("File").apply {
                items.addAll(MenuItem("Open Directory").apply {
                    onAction = EventHandler {
                        val target = DirectoryChooser().apply {
                            title = "Open Directory"
                        }.showDialog(primaryStage)
                        if (target != null) {
                            mCurrentDirectory.value = target.toPath()
                        }
                    }
                },
                    MenuItem("New Folder").apply { onAction = EventHandler { newFolder() } },
                    MenuItem("Exit").apply {
                        onAction = EventHandler { Platform.exit() }
                    })
            })
            menus.add(Menu("Actions").apply {
                isDisable = true
                cListview.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    isDisable = (newValue == null)
                }
                items.addAll(MenuItem("Rename").apply {
                    onAction = EventHandler {
                        cListview.selectionModel.select(File(onRename(cListview.selectionModel.selectedItem)))
                    }
                }, MenuItem("Move").apply {
                    onAction = EventHandler { onMove(cListview.selectionModel.selectedItem) }
                }, MenuItem("Delete").apply {
                    onAction = EventHandler { onDelete(cListview.selectionModel.selectedItem) }
                })
            })
        }

// </editor-fold> MenuBar

        val cContainer = BorderPane().apply {
            top = VBox().apply { children.addAll(myMenuBar, cToolBar) }
            center = SplitPane().apply {
                items.addAll(cListview, cPreviewPane)
                dividerPositions[0] = 0.3
            }
            bottom = HBox().apply {
                background = Background.fill(Color.ALICEBLUE)
                padding = Insets(5.0, 5.0, 5.0, 5.0)
                children.addAll(Label().apply {
                    // Bind label to number of items in current directory
                    textProperty().bind(Bindings.size(cListview.items).asString())
                }, Label(" items"), Separator(Orientation.VERTICAL).apply {
                    padding = Insets(0.0, 3.0, 0.0, 7.0)
                }, Label().apply {
                    // Bind label to selected item in listview or default text
                    textProperty().bind(
                        Bindings.`when`(Bindings.isNotNull(cListview.selectionModel.selectedItemProperty()))
                            .then(Bindings.selectString(cListview.selectionModel.selectedItemProperty().asString()))
                            .otherwise("Select a file to perform an action")
                    )
                }, Separator(Orientation.VERTICAL).apply {
                    padding = Insets(0.0, 3.0, 0.0, 7.0)
                }, Label().apply {
                    textProperty().bind(
                        Bindings.createStringBinding(
                            {
                                val selectedItem = cListview.selectionModel.selectedItem
                                if (selectedItem != null) {
                                    getSize(selectedItem.length())
                                } else {
                                    ""
                                }
                            }, cListview.selectionModel.selectedItemProperty()
                        )
                    )
                })
            }
        }

        // We ** must ** initialize properties here
        mCurrentDirectory.value = mHomeDirectory

        with(primaryStage!!) {
            scene = Scene(cContainer)
            title = "File Explorer"
            icons.add(Icon.LOGO.toIcon().image)
            width = 1000.0
            height = 750.0
            show()
        }
    }

}
