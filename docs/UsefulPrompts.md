## Git Remotes

| Name | URL |
|------|-----|
| `origin` | https://github.com/Bob8259/zkqCode.git |
| `collaborate` | https://github.com/Bob8259/zkqCode-collaborate.git |
| `open` | https://github.com/Bob8259/zkqCodeOpen.git |

Please help me analyze this file to create an optimization plan. I want to maintain all existing functionality while removing code redundancy. File Path:

Help me modify app\src\main\java\com\coc\zkqcode\jar\code\colorschema\MyColors.kt to move the colors into corresponding files under app\src\main\java\com\coc\zkqcode\jar\code\colorschema\colorpackage. If no corresponding file is found, you can create a new one.

Help me commit and push my code to both | `origin` | https://github.com/Bob8259/zkqCode.git |
| `collaborate` | https://github.com/Bob8259/zkqCode-collaborate.git |, and switch back to origin. You can write a concise commit message. If there are any conflicts, keep my local code. NEVER pull the remote code.

Help me read the newest pull request of https://github.com/Bob8259/zkqCode-collaborate.git and list the changes under `app/src/main/java/com/coc/zkqcode/jar` except `suspend fun displayAds()`. First find the newest PR with `git ls-remote collaborate "refs/pull/*/head"`, use the highest PR number, force-fetch it with `git fetch collaborate pull/<PR_NUMBER>/head:pr-collaborate-<PR_NUMBER> --force`, then diff with `git diff main pr-collaborate-<PR_NUMBER> -- app/src/main/java/com/coc/zkqcode/jar`. List changes only; do not modify any files.

Help me modify this file to add a time guard. I want to perform the operation once per day using memory file at app\src\main\java\com\coc\zkqcode\jar\code\universal\smalltools\data\Memory.kt