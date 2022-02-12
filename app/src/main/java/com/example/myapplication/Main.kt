package com.example.myapplication

import kotlin.random.Random

enum class GameState {
    INITIALIZING, RUNNING, FINISHED
}
class Main() {
    val deck = Deck()
    val table = Table()
    val player = Player()
    val computerPlayer = ComputerPlayer()
    var gameState = GameState.INITIALIZING
    val players = mutableListOf<Participant>()

    fun gameLoop() {
        var turnOrderIndex = 0
        while (this.gameState != GameState.FINISHED) {
            val turn = turnOrderIndex % this.players.size
            if (this.players[turn].checkIfEmptyHand()) {
                try {
                    this.players[turn].fillHand(this.deck.drawFromDeck(6))
                } catch (e: Exception) {
                    this.gameState = GameState.FINISHED
                    break
                }
            }
            this.table.addToCardPile(players[turn].playCard())
            turnOrderIndex++

            table.printTableInfo()
            if (this.deck.deck.isEmpty()) {
                this.gameState = GameState.FINISHED
            }
        }

    }

    fun setupGame() {
        println("Indigo Card Game")
        this.deck.resetDeck()
        this.deck.shuffleDeck()
        this.isPlayerStarting()
        this.dealCards()



    }

    fun isPlayerStarting() {
        var input: String
        while (true) {
            println("Play first?")
            input = readLine()!!.lowercase()
            if (input == "yes" || input == "no") {
                when (input) {
                    "yes" -> this.players.addAll(mutableListOf(this.player, this.computerPlayer))
                    "no" -> this.players.addAll(mutableListOf(this.computerPlayer, this.player))
                }
            }
        }
    }

    fun dealCards() {
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
    fun showCards() {
        print("Cards in hand: ")
        for (i in 0 until this.hand.size) {
            print("$i)${this.hand[i]} ")
        }
    }

    override fun playCard(): String {
        var cardIndex: String
        this.showCards()
        while (true) {
            println("Choose a card to play (1-${this.hand.size})")
            try {
                // TODO: EXIT MUSS IRGENDWIE IMPLEMENTIERT WERDEN.
                cardIndex = readLine()!!
                if (cardIndex == "exit") {
                    return cardIndex
                }
            } catch (e: NumberFormatException) {
                continue
            }
            if (cardIndex in 1..this.hand.size) {
                cardIndex - 1
                break
            }

        }
        return this.hand.removeAt(cardIndex)
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
    val cardPile = mutableListOf<String>()

    fun printTableInfo() {
        println("${cardPile.size} cards on the table, and the top card is ${cardPile[cardPile.lastIndex]}")
    }

    fun addToCardPile(playCard: String) {
        this.cardPile.add(playCard)
    }
}



class Deck() {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf("♦", "♥", "♠", "♣")
    val deck = mutableListOf<String>()

    fun generateNewDeck() {
        this.deck.clear()
        for (suit in suits) {
            for (rank in ranks) {
                this.deck.add("$rank$suit")
            }
        }
    }

    fun resetDeck() {
        generateNewDeck()
        println("Card deck is reset.")
    }

    fun shuffleDeck() {
        for (i in 0..100) {
            val index = Random.nextInt(0, this.deck.lastIndex)
            val index2 = Random.nextInt(0, this.deck.lastIndex)
            val card = this.deck.removeAt(index2)
            this.deck.add(index, card)
        }
        println("Card deck is shuffled.")

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
        }


    }

    fun exit() {
        println("Bye")
    }


}


fun main() {
    val indigo = Main()
    indigo.startGame()

}