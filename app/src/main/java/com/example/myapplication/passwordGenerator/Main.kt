package com.example.myapplication.passwordGenerator
import kotlin.random.Random

class Main {
}

    fun main() {
        val settings = readLine()!!.split(" ")
        val uppercase = settings[0].toInt()
        val lowercase = settings[1].toInt()
        val digits = settings[2].toInt()
        val symbolCount = settings[3].toInt()

        val password = mutableListOf<Char>()
        fun shuffleCharacters(second: Int) {
            val character = password.removeAt(second)

            password.add(Random.nextInt(0, password.size - 1), character)
        }
        fun shuffleCharactersRandom() {
            for (i in 0..10) {
                password.shuffle()
            }
        }

        if (uppercase > 0){
            for (i in 0 until uppercase) {
                var character: Char
                character = Random.nextInt(65, 90).toChar()

                password.add(character)
            }
        }
        println(password.joinToString(""))

        if (lowercase > 0){
            for (i in 0 until lowercase) {
                val character = Random.nextInt(65, 90).toChar().lowercaseChar()
                password.add(character)
            }
        }
        println(password.joinToString(""))
        if (digits > 0){
            for (i in 0 until digits) {
                val character = Random.nextInt(48, 57).toChar()
                password.add(character)
            }
        }

        println(password.joinToString(""))
        if (password.size < symbolCount) {
            for (i in 0 until symbolCount - password.size) {
                password.add(Random.nextInt(33, 126).toChar())
            }
        }
        println(password.joinToString(""))
        for (i in 1..5){
            println("Random Shuffle")
            shuffleCharactersRandom()
            println(password.joinToString(""))
        }


        while (true){
            if (password.size > 1){
                if (password[0] == password[1]) {
                    println(password.joinToString(""))
                    shuffleCharacters(0)
                    continue
                }
                if (password[password.lastIndex] == password[password.lastIndex - 1]) {
                    println(password.joinToString(""))
                    shuffleCharacters(password.lastIndex)
                    continue
                }

                for (i in 1 until password.size - 1) {
                    if (password[i] == password[i-1]){
                        println(password.joinToString(""))
                        shuffleCharacters(i)
                        continue
                    } else if (password[i] == password[i+1]) {
                        println(password.joinToString(""))
                        shuffleCharacters(i)
                        continue
                    }
                }
                break
            } else {
                break
            }

        }

        println(password.joinToString(""))



    }
