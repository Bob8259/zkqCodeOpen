The application consists of a host loader and a JAR module. Code is dynamically loaded from the source path: app/src/main/java/com/coc/zkqcode/jar. You must treat this directory as an independent module intended for JAR packaging. The application operates within a Root environment.

UI Implementation Rules:

You must add localization for new UI.

I/O and Networking Constraints:
All JSON file reading and writing operations performed outside of the application's private path must be routed through the WebSocket server.

Technical Standards:

Debugging: Use the ShowMessage utility for all debug information. It must be imported from: com.coc.zkqcode.core.util.basic.ShowMessage.

Import Style: Use full package names in the import statements at the top of the file. Within the code body, use only the simple class name (e.g., import androidx.compose.ui.Modifierandroidx.compose.ui.Modifier; then use Modifier. Do not directly use androidx.compose.ui.Modifierandroidx.compose.ui.Modifier).

Language: Write all code comments in English. However, debug messages, and UI display strings can be written in both English and Chinese.

Procedural Requirements:

Clarity: If any instruction is unclear or ambiguous, you must ask for clarification. Do not proceed until you have a full understanding of the task.

Planning: For complex tasks, you are required to generate a development plan. 

Modification: If some files are changed during the chat, you should re-read them, and apply new changes based on the new file. Do not revert the changes.

Build: You should try gradle debug build and fix all bugs after each task.