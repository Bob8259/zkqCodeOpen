The application has a host loader and a dynamically loaded JAR module under `app/src/main/java/com/coc/zkqcode/jar`. Treat that path as an independent JAR-packaged module. The app runs in a Root environment.

UI Implementation Rules:

- Add new UI text through the existing localization layer instead of hardcoding it.
- When creating a screen or dialog, define the string key(s) first, then reference them from the UI code.
- Keep translations aligned across supported languages.

I/O and Networking Constraints:
Route JSON I/O outside the app's private path through the WebSocket server.

Technical Standards:

Show Message: Use the ShowMessage utility for all information showen to users. Import from: com.coc.zkqcode.core.util.basic.ShowMessage.

Debugging: Use showDebugInfo to show all debug information. Import from com.coc.zkqcode.core.util.fileactions.

Import Style: Use full package names in the import statements at the top of the file. Within the code body, use only the simple class name (e.g., import androidx.compose.ui.Modifierandroidx.compose.ui.Modifier; then use Modifier. Do not directly use androidx.compose.ui.Modifierandroidx.compose.ui.Modifier).

Language: Write all code comments in English. However, debug messages, and UI display strings can be written in both English and Chinese.

Procedural Requirements:

Clarity: If any instruction is unclear or ambiguous, you must ask for clarification. Do not proceed until you have a full understanding of the task.

Planning: For complex tasks, you are required to generate a development plan. 

Modification: If some files are changed during the chat, you should re-read them, and apply new changes based on the new file. Do not revert the changes.

Build: You should try gradle debug build and fix all bugs after every task.