package com.example.myapplication.passwordGenerator

class Russe {
}
fun main() {
    val x = readLine()!!.split(" ")
    var q = ""
    for (i in 0 until x[3].toInt()) {
        if (x[0].toInt() > i) q += 'A' + i % 2
        if (x[1].toInt() > i) q += 'a' + i % 2
        if (x[2].toInt() > i) q += '0' + i % 2
        if (x[0].toInt() + x[1].toInt() + x[2].toInt() <= i) q += '+' + i % 2
    }
    print(q)
}