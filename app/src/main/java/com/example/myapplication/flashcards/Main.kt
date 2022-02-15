package com.example.myapplication.flashcards

class Main {

}

fun main() {
    val cards: MutableList<String> = mutableListOf()
    val definitions: MutableList<String> = mutableListOf()
    val flashcards: MutableMap<String, String> = mutableMapOf()

    println("Input the number of cards:")
    val numOfCards: Int = readLine()!!.toInt()
    repeat(numOfCards) {
        println("Card #${it + 1}:")
        var card: String
        while (true){
            card = readLine()!!
            if (flashcards.containsKey(card)) {
                println("The term \"${card}\" already exists. Try again:")
            } else {
                break
            }
        }

        println("The definition for card #${it + 1}:")
        var definition: String
        while (true) {
            definition = readLine()!!
            if (flashcards.containsValue(definition)) {
                println("The definition \"${definition}\" already exists. Try again:")
            } else {
                break
            }
        }
        flashcards[card] = definition


    }
    for( (card, definition) in flashcards) {
        println("Print the definition of \"${card}\":")
        val userDefinition: String = readLine()!!
        when {
            userDefinition == definition -> {
                println("Correct!")
            }
            flashcards.containsValue(userDefinition) -> {
                val correctDefinition = flashcards.filterValues { it == userDefinition }
                println("Wrong. The right answer is \"${definition}\", but your definition is correct for \"${correctDefinition.keys.joinToString(", ")}\"")
            }
            else -> {
                println("Wrong. The right answer is \"${definition}\".")
            }
        }
    }

}