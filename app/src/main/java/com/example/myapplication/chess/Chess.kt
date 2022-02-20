package com.example.myapplication.chess

enum class ChessColor(val colorChar: Char) {
    WHITE('W'), BLACK('B')
}

enum class GameState {
    RUNNING, STALEMATE, WINNER, ABORTED;
}

enum class ChessRank(val rankInt: Int) {
    EIGHTH(8),
    SEVENTH(7),
    SIXTH(6),
    FIFTH(5),
    FOURTH(4),
    THIRD(3),
    SECOND(2),
    FIRST(1);

    companion object {
        fun getRankByIndex(index: Int): ChessRank = values()[index]
    }
}

enum class ChessFile {
    A, B, C, D, E, F, G, H;

    companion object {
        fun getFileByIndex(index: Int): ChessFile = values()[index]
    }

}

open class ChessPiece(var chessRank: ChessRank, var chessFile: ChessFile, val color: ChessColor) {
    fun getColor(): String {
        return this.color.colorChar.toString()
    }
}

class Pawn(chessRank: ChessRank, chessFile: ChessFile, color: ChessColor): ChessPiece(chessRank, chessFile, color)

class Chess(val player_names: List<String>) {
    val board = ChessBoard()
    var gameState: GameState = GameState.RUNNING
    val chessRegexPattern = Regex("[a-h][1-8][a-h][1-8]")

    fun setupGame() {
        // Ask who is starting
        // println("Who is starting? Type \'0\' for ${player_names[0]} and \'1\' for ${player_names[1]}")
        // if (readLine()!!.toInt() == 1) {
        //      val temp = player_names[0]
        //      player_names[0] = player_names[1]
        //      player_names[1] = temp
        // }

        board.resetBoard()

    }

    fun gameLoop() {
        gameloop@while (gameState == GameState.RUNNING) {
            for (player in player_names)
                while (true) {
                    println("$player's turn:")
                    val chessInput = readLine()!!
                    if (chessInput.lowercase() == "exit") {
                        gameState = GameState.ABORTED
                        println("Bye!")
                        break@gameloop
                    }
                    if (chessRegexPattern.matches(chessInput.lowercase())) {
                        // Do stuff
                        break
                    } else {
                        println("Invalid Input")
                    }
                }

        }
    }
}


class ChessBoard(val pawnOnly: Boolean = true) {
    val separator = "  +---+---+---+---+---+---+---+---+"
    val board = List(8){MutableList<ChessPiece?>(8){null}}

    fun resetBoard() {
        if (pawnOnly) {
            repeat(8) {
                board[ChessRank.EIGHTH.ordinal][it] = null
                board[ChessRank.SEVENTH.ordinal][it] = Pawn(ChessRank.SEVENTH, ChessFile.getFileByIndex(it), ChessColor.BLACK)
                board[ChessRank.SIXTH.ordinal][it] = null
                board[ChessRank.FIFTH.ordinal][it] = null
                board[ChessRank.FOURTH.ordinal][it] = null
                board[ChessRank.THIRD.ordinal][it] = null
                board[ChessRank.SECOND.ordinal][it] = Pawn(ChessRank.SECOND, ChessFile.getFileByIndex(it), ChessColor.WHITE)
                board[ChessRank.FIRST.ordinal][it] = null
            }
        } else {
            TODO("Reset whole board with all pieces like in traditional chess.")
        }
    }

    fun printBoard() {
        println(separator)
        board.forEachIndexed { index, mutableList ->
            print(ChessRank.getRankByIndex(index).rankInt.toString() + " | ")
            print(mutableList.joinToString(" | ") { it?.getColor() ?: " " } )
            println(" |")
            println(separator)
        }
        print("    ")
        print(ChessFile.values().joinToString("   ") { it.toString().lowercase() })
        println("  ")
    }
}

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val player_one = readLine()!!
    println("Second Player's name:")
    val player_two = readLine()!!
    val player_names = listOf(player_one, player_two)
    val chess = Chess(player_names)
    chess.setupGame()
    chess.gameLoop()
}