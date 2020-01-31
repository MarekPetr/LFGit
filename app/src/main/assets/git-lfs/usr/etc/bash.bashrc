if [ -x /data/data/com.lfgit/files/usr/libexec/termux/command-not-found ]; then
	command_not_found_handle() {
		/data/data/com.lfgit/files/usr/libexec/termux/command-not-found "$1"
	}
fi

PS1='\$ '
