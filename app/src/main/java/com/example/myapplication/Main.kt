package com.example.myapplication

import kotlin.random.Random

enum class GameState {
    INITIALIZING, RUNNING, FINISHED
}
class Main() {
    private val deck = Deck()
    private val table = Table()
    private val player = Player()
    private val computerPlayer = ComputerPlayer()
    private val players = mutableListOf<Participant>()
    private var gameState = GameState.INITIALIZING

    private fun gameLoop() {
        var turnOrderIndex = 0
        while (this.gameState != GameState.FINISHED) {

            table.printTableInfo()
            if (this.deck.deck.isEmpty() && this.player.checkIfEmptyHand() && this.computerPlayer.checkIfEmptyHand()) {
                println("Game Over")
                this.gameState = GameState.FINISHED
                continue
            }
            val turn = turnOrderIndex % this.players.size
            if (this.players[turn].checkIfEmptyHand()) {
                try {
                    this.players[turn].fillHand(this.deck.drawFromDeck(6))
                } catch (e: Exception) {
                    this.gameState = GameState.FINISHED
                    continue
                }
            }
            val playedCard = players[turn].playCard()
            if (playedCard == "exit"){
                println("Game Over")
                this.gameState = GameState.FINISHED
                continue
            }

            this.table.addToCardPile(playedCard)
            turnOrderIndex++

            println("")


        }

    }

    private fun setupGame() {
        println("Indigo Card Game")
        this.deck.resetDeck()
        this.deck.shuffleDeck()
        this.isPlayerStarting()
        this.dealCards()
        this.table.addInitialCards(this.deck.drawFromDeck(4))
        this.gameState = GameState.RUNNING
        this.gameLoop()



    }

    private fun isPlayerStarting() {
        var input: String
        while (true) {
            println("Play first?")
            input = readLine()!!.lowercase()
            if (input == "yes" || input == "no") {
                when (input) {
                    "yes" -> this.players.addAll(mutableListOf(this.player, this.computerPlayer))
                    "no" -> this.players.addAll(mutableListOf(this.computerPlayer, this.player))
                }
                break
            }
        }
    }

    private fun dealCards() {
        for (player in players) {
            player.fillHand(this.deck.drawFromDeck(6))
        }
    }

    fun startGame() {
        this.setupGame()

    }
}

open class Participant() {
    val hand: MutableList<String> = mutableListOf<String>()

    open fun playCard(): String {
        return this.hand.removeAt(this.hand.lastIndex)
    }

    fun fillHand(cards: MutableList<String>) {
        for (card in cards) {
            hand.add(card)
        }
    }

    fun checkIfEmptyHand(): Boolean {
        return this.hand.isEmpty()
    }

}

class Player() : Participant() {
    private fun showCards() {
        print("Cards in hand: ")
        for (i in 0 until this.hand.size) {
            print("${i + 1})${this.hand[i]} ")
        }
        println()
    }

    override fun playCard(): String {
        var card: String
        var cardIndex: Int
        this.showCards()
        while (true) {
            println("Choose a card to play (1-${this.hand.size}):")
            try {
                card = readLine()!!
                if (card == "exit") {
                    return card
                }
                cardIndex = card.toInt()
            } catch (e: NumberFormatException) {
                continue
            }
            if (cardIndex in 1..this.hand.size) {
                return this.hand.removeAt(cardIndex - 1 )
            }

        }

    }

}

class ComputerPlayer() : Participant() {
    override fun playCard(): String {
        val playedCard = this.hand.removeAt(this.hand.lastIndex)
        println("Computer plays $playedCard")
        return playedCard
    }
}

class Table() {
    private val cardPile = mutableListOf<String>()

    fun printTableInfo() {
        println("${cardPile.size} cards on the table, and the top card is ${cardPile[cardPile.lastIndex]}")
    }

    fun addToCardPile(playCard: String) {
        this.cardPile.add(playCard)
    }

    fun addInitialCards(fourCards: MutableList<String>) {
        println("Initial cards on the table: ${fourCards.joinToString(" ")}")
        println("")
        this.cardPile.addAll(fourCards)
    }
}



class Deck() {
    private val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    private val suits = listOf("♦", "♥", "♠", "♣")
    val deck = mutableListOf<String>()

    private fun generateNewDeck() {
        this.deck.clear()
        for (suit in suits) {
            for (rank in ranks) {
                this.deck.add("$rank$suit")
            }
        }
    }

    fun resetDeck() {
        generateNewDeck()
    }

    fun shuffleDeck() {
        for (i in 0..100) {
            val index = Random.nextInt(0, this.deck.lastIndex)
            val index2 = Random.nextInt(0, this.deck.lastIndex)
            val card = this.deck.removeAt(index2)
            this.deck.add(index, card)
        }

    }

    fun drawFromDeck(quantity: Int): MutableList<String> {
        try {
            if (quantity == 0 || quantity > 52) {
                println("Invalid number of cards.")
            }
        }
        catch (e: NumberFormatException) {
            println("Invalid number of cards.")
        }

        if (quantity > this.deck.size) {
            println("The remaining cards are insufficient to meet the request.")
        }
        val drawnCards = mutableListOf<String>()
        for (i in 0 until quantity) {
            drawnCards.add(this.deck.removeAt(this.deck.lastIndex))
        }
        if (drawnCards.size > 0) {
            return drawnCards
        } else {
            throw Exception("No cards drawn despite checking for value > 0")
        }


    }


}


fun main() {
    val indigo = Main()
    indigo.startGame()
}