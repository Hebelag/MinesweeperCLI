package com.example.myapplication

import kotlin.random.Random

enum class GameState {
    INITIALIZING, RUNNING, FINISHED
}

class Constants {
    companion object {
        val COUNTED_CARDS = listOf<String>("10", "J", "Q", "K", "A")
    }
}

class Main {
    private val deck = Deck()
    private val table = Table()
    private val player = Player()
    private val computerPlayer = ComputerPlayer()
    private val players = mutableListOf<Participant>()
    private var gameState = GameState.INITIALIZING
    private var lastWinner: Participant? = null
    private var starter: Participant? = null

    private fun printIfWon(participant: Participant) {
        when (participant) {
            is Player -> println("Player wins cards")
            is ComputerPlayer -> println("Computer wins cards")
            else -> throw Exception("Current participant is neither Player nor Computer?")
        }
    }

    private fun printStats() {
        println("Score: ${this.player.name} ${this.player.points} - ${this.computerPlayer.name} ${this.computerPlayer.points}")
        println("Cards: ${this.player.name} ${this.player.cardsWon.size} - ${this.computerPlayer.name} ${this.computerPlayer.cardsWon.size}")
    }
    private fun checkMaxCards() {
        var maxCardPlayer: Participant? = null
        var maxCards: Int = -1
        for (player in players) {
            if (player.cardsWon.size > maxCards) {
                maxCardPlayer = player
                maxCards = player.cardsWon.size
            } else if (player.cardsWon.size == maxCards){
                maxCardPlayer = this.starter
                break
            }
        }
        maxCardPlayer?.wonMaxCards()
    }

    private fun remainingCardsToLastWinner(cardPile: MutableList<String>) = this.lastWinner?.addCardsWon(cardPile)


    private fun gameLoop() {

        var turnOrderIndex = 0
        while (this.gameState != GameState.FINISHED) {
            table.printTableInfo()

            if (this.deck.deck.isEmpty() && this.player.checkIfEmptyHand() && this.computerPlayer.checkIfEmptyHand()) {
                this.remainingCardsToLastWinner(this.table.cardPile)
                this.checkMaxCards()
                this.players.forEach { it.countPoints() }
                this.printStats()
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
            val playedCard = when (players[turn]) {
                is Player -> this.player.playCard()
                is ComputerPlayer -> {
                    this.computerPlayer.showCards()
                    this.computerPlayer.playCard(this.table.cardPile.lastOrNull())
                }
                else -> "exit"
            }
            if (playedCard == "exit"){
                println("Game Over")
                this.gameState = GameState.FINISHED
                continue
            }


            if (this.table.cardPile.isNotEmpty()) {
                if (playedCard.substring(0, playedCard.length - 1) == this.table.cardPile.last().substring(0, playedCard.length - 1) || playedCard.last() == this.table.cardPile.last().last()) {
                    this.table.addToCardPile(playedCard)
                    this.lastWinner = players[turn]
                    players[turn].addCardsWon(this.table.cardPile)
                    this.table.cardPile.clear()
                    this.printIfWon(players[turn])
                    this.players.forEach { it.countPoints() }
                    this.printStats()

                } else {
                        this.table.addToCardPile(playedCard)
                }
            } else {
                this.table.addToCardPile(playedCard)
            }
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
        this.players.forEach { it.countPoints() }

    }

    private fun isPlayerStarting() {
        var input: String
        while (true) {
            println("Play first?")
            input = readLine()!!.lowercase()
            if (input == "yes" || input == "no") {
                when (input) {
                    "yes" -> {
                        this.starter = this.player
                        this.players.addAll(mutableListOf(this.player, this.computerPlayer))
                    }
                    "no" -> {
                        this.starter = this.computerPlayer
                        this.players.addAll(mutableListOf(this.computerPlayer, this.player))
                    }
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

open class Participant(val name: String) {
    val hand: MutableList<String> = mutableListOf<String>()
    val cardsWon: MutableList<String> = mutableListOf<String>()
    var points: Int = 0
    var maxCards: Boolean = false

    fun countPoints() {
        var tempPoints = 0
        for (card in cardsWon) {
            var rank = card.substring(0, card.length - 1)
            if (rank in Constants.COUNTED_CARDS) {
                tempPoints++
            }
        }
        tempPoints += if (this.maxCards) 3 else 0
        this.points = tempPoints
    }

    fun wonMaxCards() {
        this.maxCards = true
    }

    fun addCardsWon(cards: MutableList<String>) = cards.forEach { cardsWon.add(it) }

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

class Player() : Participant("Player") {
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

class ComputerPlayer() : Participant("Computer") {
    var candidateCards: MutableList<String> = mutableListOf()
    fun isCandidateCard(topCard: String, playCard: String): Boolean {
        val topCardRank = topCard.substring(0, topCard.length - 1)
        val topCardSuit = topCard.last()
        return playCard.substring(0, topCard.length - 1) == topCardRank || playCard.last() == topCardSuit
    }

    fun showCards() {
        println(this.hand.joinToString(" "))
    }

    fun isLastCard(): Boolean = this.hand.size == 1

    fun countCandidateCards(): Int = this.candidateCards.size

    fun setCandidateCards(topCard: String) {
        this.candidateCards = this.hand.filter { isCandidateCard(topCard, it) }.toMutableList()
    }

    fun getCandidateCardIndex(): List<Int> {
        return this.candidateCards.map { this.hand.indexOf(it) }
    }

    fun throwCuzEmptyPile(): String {
        return when {
            this.getSameSuits(this.hand).isNotEmpty() -> this.getSameSuits(this.hand).random()
            this.getSameRanks(this.hand).isNotEmpty() -> this.getSameRanks(this.hand).random()
            else -> this.hand.random()
        }
    }

    fun getSameSuits(cardCollection: MutableList<String>): MutableSet<String> {
        val sameSuit = mutableSetOf<String>()
        for (card in cardCollection) {
            for (card2 in cardCollection) {
                if (card != card2 && card.last() == card2.last()) {
                    sameSuit.add(card)
                }
            }
        }
        return sameSuit
    }

    fun getSameRanks(cardCollection: MutableList<String>): MutableSet<String> {
        val sameRank = mutableSetOf<String>()
        for (card in cardCollection) {
            for (card2 in cardCollection) {
                if (card != card2 && card.substring(0, card.length - 1) == card2.substring(0, card.length - 1)) {
                    sameRank.add(card)
                }
            }
        }
        return sameRank
    }
    fun throwMultipleCandidates(): String {
        return when {
            this.getSameSuits(this.candidateCards).isNotEmpty() -> this.getSameSuits(this.candidateCards).random()
            this.getSameRanks(this.candidateCards).isNotEmpty() -> this.getSameRanks(this.candidateCards).random()
            else -> this.candidateCards.random()
        }
    }
    fun playCard(topCard: String?): String {
        if (topCard != null) {
            this.setCandidateCards(topCard)
        }
        val playedCard = when {
            isLastCard() -> this.hand.removeAt(this.hand.lastIndex)
            countCandidateCards() == 1 -> this.hand.removeAt(this.getCandidateCardIndex().first())
            topCard == null -> this.hand.removeAt(this.hand.indexOf(this.throwCuzEmptyPile()))
            countCandidateCards() == 0 -> this.hand.removeAt(this.hand.indexOf(this.throwCuzEmptyPile()))
            countCandidateCards() >= 2 -> this.hand.removeAt(this.hand.indexOf(this.throwMultipleCandidates()))
            else -> "exit"
        }
        println("Computer plays $playedCard")
        return playedCard
    }
}

class Table() {
    val cardPile = mutableListOf<String>()

    fun printTableInfo() {
        if (cardPile.isEmpty()) {
            println("No cards on the table")
        } else {
            println("${cardPile.size} cards on the table, and the top card is ${cardPile[cardPile.lastIndex]}")
        }
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