package com.lfgit.importer;

public enum Arch {
    x86(0),
    arm64_v8a(1);
    int value;

    Arch(int value) {
        this.value = value;
    }
}
