CI/CD pipelines on: https://gitlab.com/MarekPetr/LfGit/pipelines

Debug guide:
1) Install the app.
2) Hit the INSTALL button to copy files to device
3) hit the ACTION buttonto perform the action defined in MainActivity in onClickListener on line 59.
The exit code should be written on screen and strace output to file "strace_log.txt" in your external storage root directory (/storage/emulated/0/strace_log.txt).

If you have installed it already, faster way of making small changes in scripts is using Android studio's Device File Explorer to upload changed scripts and then just run the script with ACTION button. Now it runs only annex without strace.
