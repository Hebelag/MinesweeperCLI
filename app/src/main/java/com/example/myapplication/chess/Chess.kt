package com.example.myapplication.chess

enum class ChessColor(val colorChar: Char) {
    WHITE('W'), BLACK('B')
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

class ChessBoard(val pawnOnly: Boolean = true) {
    val separator = "  +---+---+---+---+---+---+---+---+"
    val board = List(8){MutableList<ChessPiece?>(8){null}}
    init {

    }

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
    val chess = ChessBoard()
    chess.resetBoard()
    chess.printBoard()
}