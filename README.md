# File Explorer
``` 
kotlinc-jvm 1.8.21-release-380 (JRE 1.8.0_371-b11)
Windows 10 Home 22H2 19044.1387
```

## Project Specification

### Menu Bar
The Menu Bar contains the `File` and `Actions` menus. They can be accessed through
the mouse pointer.

#### File Menu
The File menu contains the following options:
- `New Folder`: Prompts the user for a new folder name and creates it in the current directory.
- `Open Directory`: Prompts the user with a Directory Chooser and opens it.
- `Exit`: Exits the program.

#### Actions Menu
The Actions menu is disabled when no file is selected, and enabled accordingly. It contains the following options:
- `Rename`: Prompts the user for a new name and renames the selected file.
- `Move`: Prompts the user with a Directory Chooser and moves the selected file to the chosen directory. It opens the
new directory of the moved file as the current directory.
- `Delete`: Deletes the selected file with a confirmation dialog.

### Tool Bar
The Tool Bar contains the following items:
- `Home`: It opens the home directory, which is set as the test directory mentioned in the assignment description. It
is indicated with an icon, a name, and a tooltip, and does nothing when the current directory is the home directory is the
home directory.
- `Previous`: It opens the previous directory, which is the parent directory of the current directory. It is indicated
with an icon, a name, and a tooltip, and does nothing when the current directory is the home directory. It can also be
activated with the hotkey `Backspace`, when a file is selected.
- `Next`: It opens the selected directory, which is the child directory of the current directory. It is indicated with
an icon, a name, and a tooltip, and is disabled when nothing is selected or if the selected item is not a directory. It
can also be activated with the hotkey `Enter`, when a directory is selected.
- `New Folder`: It prompts the user for a new folder name and creates it in the current directory. It is indicated 
with an icon and a tooltip, but no text for the sake of the Tool Bar's compactness.
- `Address Bar`: It shows the current directory's path. It is indicated with a text field and a tooltip, but no icon.
The users can put in the path of the directory they want to open and press `Enter` to open it. This may let users access
the files outside the home directory. It behaves as follows:
  - If the input path is of a valid directory, it sets that as the current directory.
  - If the selected path is of a valid non-directory file, it opens up its parent directory.
  - If the input path is invalid, it shows an error dialog.
- `Up`: It moves the selection focus in the file list from the selected file to the previous one. If no file is selected,
it selects the first file, and if the first file is selected, it selects the last file. It is indicated with an icon, a
name, and a tooltip, and is never disabled.
- `Down`: It moves the selection focus in the file list from the selected file to the next one. If no file is selected,
it selects the first file, and if the last file is selected, it selects the first file. It is indicated with an icon, a
name, and a tooltip, and is never disabled.
- `Rename`: It prompts the user for a new name and renames the selected file. It is indicated with an icon, a name, and
a tooltip but is only enabled when a file is selected. It shows a success message and error dialog accordingly. The error dialog lists the possible causes for the rename
failure as:
  - The new name is empty.
  - There is already a file with the same name in this location.
  - The new name contains any of the following characters: \\ / : * ? \" < > |
- `Move`: It prompts the user with a Directory Chooser and moves the selected file to the chosen directory. It is 
indicated with an icon, a name, and a tooltip but is only enabled when a file is selected. It shows a success message
upon success. It opens the new directory of the moved file as the current directory.
- `Delete`: It deletes the selected file with a confirmation dialog. It is indicated with an icon, a name, and a tooltip
but is only enabled when a file is selected. It shows a success message upon deletion. It can also be activated with the
hotkey `Delete`, when a file is selected.

The Tool Bar is resizeable and moves some of the buttons in a drop-down menu when it is compressed beyond a certain size.

### File View
On the left, the File List shows the files and directories in the current directory (the home directory, upon start-up). It is indicated
with a listview of the files and directories, with their file extension and an icon for human friendliness. It is also
sorted in alphabetical order. The user can select a file or directory by clicking on it, and it can be navigated using the
`Previous`, `Next`, `Up`, and `Down` buttons. The user can also double-click or select and press `Enter`, `Backspace`, and 
`Delete` on a file to attempt to go to the parent directory, the sub-directory, or delete the selected file, respectively,
and accordingly.

On the right, there is a preview panel that tries to preview the selected file. It has a resizeable divider that can be
dragged to resize the preview panel. It is indicated with a preview of the selected file, or a message if the file is not
supported. The preview panel has a default text when no file is selected. The file types currently supported are:
- Text files (.txt)
- Image files (.png, .jpg, .jpeg, .gif, .bmp)
- Markdown files (.md)
- HTML files (.html)

### Status Bar

The Status Bar shows the following:
- The number of items in the current directory.
- The path of the selected file.
- The size of the selected file.
- If no file is selected, it shows a default text.
