package com.example.myapplication.flashcards

import java.io.File
import java.io.FileNotFoundException
import kotlin.Exception

enum class GameState {
    STOPPED, STARTED
}

class Main {
    val flashcards: MutableMap<String, String> = mutableMapOf()
    var gameState: GameState = GameState.STARTED

    fun performAction(action: String) {
        when (action) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> importCards()
            "export" -> exportCards()
            "ask" -> askCards()
            "exit" -> exitGame()
        }
    }

    fun addCard() {
        println("The card:")
        val card: String = readLine()!!
        if (flashcards.containsKey(card)) {
            println("The card \"${card}\" already exists.")
            return
        }
        println("The definition of the card:")

        val definition: String = readLine()!!
        if (flashcards.containsValue(definition)) {
            println("The definition \"${definition}\" already exists.")
            return
        }
        flashcards[card] = definition
        println("The pair (\"${card}\":\"${definition}\") has been added.")

    }

    fun addCardFromImport(flashCard: List<String>) {
        flashcards[flashCard.first()] = flashCard.last()
    }

    fun removeCard() {
        println("Which card?")
        val cardToRemove = readLine()!!
        if (!flashcards.containsKey(cardToRemove)) {
            println("Can't remove \"${cardToRemove}\": there is no such card.")
            return
        }
        flashcards.remove(cardToRemove)
        println("The card has been removed.")
    }

    fun importCards() {
        println("File name:")
        val fileName = readLine()!!
        try {
            val fileDest = File(fileName)
            val fileContent = fileDest.readLines().forEach { line -> addCardFromImport(line.split(" ")) }
            val flashCardCount = fileDest.readLines().size
            println("$flashCardCount cards have been loaded.")
        } catch (e: FileNotFoundException) {
            println("File not found.")
        }

    }

    fun exportCards() {
        println("File name:")
        val fileName = readLine()!!
        try {
            val fileDest = File(fileName)
            fileDest.writeText("")
            flashcards.forEach {fileDest.appendText("${it.key} ${it.value}\n")}
            println("${flashcards.size} cards have been saved.")
        } catch (e: Exception) {
            println(e.message)

        }
    }

    fun askCards() {
        println("How many times to ask?")
        val askCount = readLine()!!.toInt()
        repeat(askCount) {
            val flashCard = flashcards.entries.shuffled().first()
            println("Print the definition of \"${flashCard.key}\"")
            val userDefinition = readLine()!!
            when {
                userDefinition == flashCard.value -> {
                    println("Correct!")
                }
                flashcards.containsValue(userDefinition) -> {
                    val correctDefinition = flashcards.filterValues { it == userDefinition }
                    println("Wrong. The right answer is \"${flashCard.value}\", but your definition is correct for \"${correctDefinition.keys.joinToString(", ")}\".")
                }
                else -> {
                    println("Wrong. The right answer is \"${flashCard.value}\".")
                }
            }
        }
    }

    fun exitGame() {
        println("Bye bye!")
        gameState = GameState.STOPPED
    }
}

fun main() {
    val game = Main()
    while (game.gameState != GameState.STOPPED) {
        println("Input the action (add, remove, import, export, ask, exit):")
        val inputAction = readLine()!!
        game.performAction(inputAction)

    }
}