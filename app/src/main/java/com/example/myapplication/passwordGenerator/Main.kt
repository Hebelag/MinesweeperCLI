package com.example.myapplication.passwordGenerator
import kotlin.random.Random

fun main() {
    val settings = readLine()!!.split(" ")
    val uppercase = settings[0].toInt()
    val lowercase = settings[1].toInt()
    val digits = settings[2].toInt()
    val symbolCount = settings[3].toInt()

    val password = mutableListOf<Char>()
    fun shuffleCharacters(index: Int) {
        val character = password.removeAt(index)
        password.add(Random.nextInt(0, password.size - 1), character)
    }

    fun shuffleCharactersRandom() {
        for (i in 0..10) {
            password.shuffle()
        }
    }

    fun exchangeCharInTwoPw() {
        var tempChar = password.removeAt(0)
        tempChar = when (tempChar) {
            in 'A'..'Z' -> tempChar.lowercaseChar()
            in 'a'..'Z' -> tempChar.uppercaseChar()
            in '0'..'9' -> Random.nextInt(48, 57).toChar()
            else -> Random.nextInt(48, 57).toChar()
        }
        password.add(0, tempChar)
    }

    fun checkForDuplicates(): Boolean {
        if (password.size > 2) {
            for (i in 1 until password.size - 1) {
                if (password[i] == password[i - 1]) {
                    shuffleCharacters(i)
                    return true
                } else if (password[i] == password[i + 1]) {
                    shuffleCharacters(i)
                    return true
                }
            }
            return false
        } else if (password.size == 2 && password[0] == password[1]) {
            exchangeCharInTwoPw()
            return true

        } else {
            return false
        }
    }
    repeat(uppercase) {
        password.add(Random.nextInt(65, 90).toChar())
    }
    repeat(lowercase) {
        password.add(Random.nextInt(97, 122).toChar())
    }
    repeat(digits) {
        password.add(Random.nextInt(48, 57).toChar())
    }
    repeat(symbolCount - uppercase - lowercase - digits) {
        var choice = Random.nextInt(1, 3)
        when (choice) {
            1 -> password.add(Random.nextInt(97, 122).toChar())
            2 -> password.add(Random.nextInt(65, 90).toChar())
            3 -> password.add(Random.nextInt(48, 57).toChar())
        }
    }

    repeat(5) {
        if (password.size > 1) {
            shuffleCharactersRandom()
        }
    }

    while(true) {
        if (!checkForDuplicates()) {
            break
        }
    }

    println(password.joinToString(""))

}

