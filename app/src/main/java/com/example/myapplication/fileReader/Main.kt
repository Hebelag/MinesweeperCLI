package com.example.myapplication.fileReader

import java.io.File
import kotlin.NumberFormatException

class Main {
}

fun main() {
    val filePath = "C:\\Users\\denni\\Downloads\\text.txt"
    val file = File(filePath).readLines().reduce {acc, str -> acc + str}.split(" ").size
    println(file)
}