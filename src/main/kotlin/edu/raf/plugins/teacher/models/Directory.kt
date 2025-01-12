package edu.raf.plugins.teacher.models

data class Directory(
    val fullPath: String,
    val basePath: String,
    val status: String,
    val gitInitialized: String
) {
    override fun toString(): String {
        return "Directory Response:\n" +
                "Full Path: $fullPath\n" +
                "Base Path: $basePath\n" +
                "Status: $status\n" +
                "Git Initialized: $gitInitialized"
    }
}
