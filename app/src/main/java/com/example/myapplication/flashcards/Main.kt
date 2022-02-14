package com.example.myapplication.flashcards

class Main {

}

fun main() {
    val cards: MutableList<String> = mutableListOf()
    val definitions: MutableList<String> = mutableListOf()

    println("Input the number of cards:")
    val numOfCards: Int = readLine()!!.toInt()
    repeat(numOfCards) {
        println("Card #${it + 1}:")
        cards.add(readLine()!!)
        println("The definition for card #${it + 1}:")
        definitions.add(readLine()!!)

    }
    for( i in 0 until cards.size) {
        println("Print the definition of \"${cards[i]}\":")
        val userDefinition: String = readLine()!!
        if (userDefinition == definitions[i]) {
            println("Correct!")
        } else {
            println("Wrong. The right answer is \"${definitions[i]}\".")
        }
    }
}