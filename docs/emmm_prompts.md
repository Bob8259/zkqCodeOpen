帮我从上游仓库https://github.com/Bob8259/zkqCode-collaborate 拉取最新代码：
只修改app\src\main\java\com\coc\zkqcode\jar下的内容，禁止修改其他内容。我希望完全同步本地和远程代码，也就是说：
如果本地没有而远程仓库有->新增
如果本地和远程仓库都有->覆盖
如果本地有而远程仓库没有->删除
合并后，写一个.md文件帮我列出具体更改的内容，方便我检查。同时将所有变更暂存，方便在出现意外错误时回退。

帮我发送一个pull request到上游仓库https://github.com/Bob8259/zkqCode-collaborate。你需要使用gh，并写上合适的提交信息。如果要改动本地则先备份再修改