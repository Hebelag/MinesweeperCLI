package com.example.myapplication.flashcards

import java.io.File
import java.io.FileNotFoundException
import kotlin.Exception

enum class GameState {
    STOPPED, STARTED
}

class FlashCard(var name: String, var definition: String, var mistakes: Int) {
}
class Logger{
    val loggingList: MutableList<String> = mutableListOf()

    fun logAndPrint(string: String) {
        loggingList.add(string)
        println(string)
    }

    fun logAndSend(string: String): String {
        loggingList.add("> $string")
        return string
    }
}
class Main(args: Array<String>) {
    val flashcards: MutableList<FlashCard> = mutableListOf()
    var gameState: GameState = GameState.STARTED
    val logger: Logger = Logger()
    var exitSavePath: String? = null
    init {
        if ("-import" in args) {
            val fileName = args[args.indexOf("-import") + 1]
            importCards(fileName)
        }
        if ("-export" in args) {
            val fileName = args[args.indexOf("-export") + 1]
            exitSavePath = fileName
        }
    }
    fun performAction(action: String) {
        when (action) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> importCardsPrompt()
            "export" -> exportCardsPrompt()
            "ask" -> askCards()
            "exit" -> exitGame()
            "log" -> log()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
        }
    }

    fun addCard() {
        logger.logAndPrint("The card:")
        val card: String = logger.logAndSend(readLine()!!)
        if (flashcards.any { it.name == card }) {
            logger.logAndPrint("The card \"${card}\" already exists.")
            return
        }
        logger.logAndPrint("The definition of the card:")

        val definition: String = logger.logAndSend(readLine()!!)
        if (flashcards.any {definition == it.definition}) {
            logger.logAndPrint("The definition \"${definition}\" already exists.")
            return
        }
        flashcards.add(FlashCard(card, definition, 0))
        logger.logAndPrint("The pair (\"${card}\":\"${definition}\") has been added.")

    }

    fun addCardFromImport(flashCard: List<String>) {
        if (flashcards.any {it.name == flashCard[0]}) {
            flashcards.removeAll(flashcards.filter { it.name == flashCard[0] })
        }
        flashcards.add(FlashCard(flashCard[0], flashCard[1], flashCard[2].toInt()))
    }

    fun removeCard() {
        logger.logAndPrint("Which card?")
        val cardToRemove = logger.logAndSend(readLine()!!)
        if (!flashcards.any {cardToRemove == it.name}) {
            logger.logAndPrint("Can't remove \"${cardToRemove}\": there is no such card.")
            return
        }
        flashcards.removeAll(flashcards.filter { it.name == cardToRemove })
        logger.logAndPrint("The card has been removed.")
    }

    fun importCards(fileName: String) {
        try {
            val fileDest = File(fileName)
            fileDest.readLines().forEach { line -> addCardFromImport(line.split(",,,")) }
            val flashCardCount = fileDest.readLines().size
            logger.logAndPrint("$flashCardCount cards have been loaded.")
        } catch (e: FileNotFoundException) {
            logger.logAndPrint("File not found.")
        }
    }

    fun importCardsPrompt() {
        logger.logAndPrint("File name:")
        val fileName = logger.logAndSend(readLine()!!)
        importCards(fileName)
    }

    fun exportCards(fileName: String) {
        try {
            val fileDest = File(fileName)
            fileDest.writeText("")
            flashcards.forEach {fileDest.appendText("${it.name},,,${it.definition},,,${it.mistakes}\n")}
            logger.logAndPrint("${flashcards.size} cards have been saved.")
        } catch (e: Exception) {
            logger.logAndPrint(e.message.toString())

        }
    }

    fun exportCardsPrompt() {
        logger.logAndPrint("File name:")
        val fileName = logger.logAndSend(readLine()!!)
        exportCards(fileName)
    }

    fun askCards() {
        logger.logAndPrint("How many times to ask?")
        val askCount = logger.logAndSend(readLine()!!).toInt()
        repeat(askCount) {
            val flashCard = flashcards.shuffled().first()
            logger.logAndPrint("Print the definition of \"${flashCard.name}\"")
            val userDefinition = logger.logAndSend(readLine()!!)
            when {
                userDefinition == flashCard.definition -> {
                    logger.logAndPrint("Correct!")
                }
                flashcards.any {userDefinition == it.definition} -> {
                    val correctDefinition = flashcards.filter { it.definition == userDefinition }.joinToString(", ") {it.name}
                    logger.logAndPrint("Wrong. The right answer is \"${flashCard.definition}\", but your definition is correct for \"${correctDefinition}\".")
                    flashcards.first { it.name == flashCard.name }.mistakes += 1
                }
                else -> {
                    logger.logAndPrint("Wrong. The right answer is \"${flashCard.definition}\".")
                    flashcards.first { it.name == flashCard.name }.mistakes += 1
                }
            }
        }
    }

    fun exitGame() {
        if (exitSavePath != null) {
            exportCards(exitSavePath!!)
        }
        logger.logAndPrint("Bye bye!")
        gameState = GameState.STOPPED
    }

    fun log() {
        logger.logAndPrint("File name:")
        val fileName = logger.logAndSend(readLine()!!)
        try {
            val fileDest = File(fileName)
            fileDest.writeText("")
            logger.loggingList.forEach {fileDest.appendText("$it\n")}
            logger.logAndPrint("The log has been saved.")
        } catch (e: Exception) {
            logger.logAndPrint(e.message.toString())
        }
    }

    fun hardestCard() {
        val hardestCards = flashcards.filter{ x -> x.mistakes == flashcards.maxByOrNull { it.mistakes }?.mistakes}.filter{it.mistakes != 0}
        when (hardestCards.size) {
            0 -> logger.logAndPrint("There are no cards with errors")
            1 -> {
                logger.logAndPrint("The hardest card is \"${hardestCards.first().name}\". You have ${hardestCards.first().mistakes} errors answering it.")
            }
            else -> {
                val hardestCardsString = hardestCards.joinToString(", ") { "\"${it.name}\"" }
                logger.logAndPrint("The hardest cards are $hardestCardsString. You have ${hardestCards.first().mistakes} errors answering them.")
            }
        }
    }

    fun resetStats() {
        flashcards.forEach { it.mistakes = 0 }
        logger.logAndPrint("Card statistics have been reset.")
    }

    fun gameLoop() {
        while (gameState != GameState.STOPPED) {
            logger.logAndPrint("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            val inputAction = logger.logAndSend(readLine()!!)
            performAction(inputAction)
        }
    }
}

fun main(args: Array<String>) {
    val game = Main(args)
    game.gameLoop()
}