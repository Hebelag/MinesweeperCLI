package com.example.myapplication.chess

enum class ChessColor(val colorChar: Char, val colorName: String) {
    WHITE('W', "white"), BLACK('B', "black");

    companion object {
        fun getChessColorName(x: ChessColor): String {
            return x.name.lowercase()
        }
    }
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
        fun getRankByRankInt(rank: Int): ChessRank = values().first {
            it.rankInt == rank }
    }
}

enum class ChessFile {
    A, B, C, D, E, F, G, H;

    companion object {
        fun getFileByIndex(index: Int): ChessFile = values()[index]
    }

}

interface Moveable {
    fun move()
}

open class ChessPiece(var chessRank: ChessRank, var chessFile: ChessFile, val color: ChessColor) {
    fun getColor(): String {
        return this.color.colorChar.toString()
    }
    fun getPosition(): String {
        return "${this.chessFile.toString().lowercase()}${this.chessRank.rankInt}"
    }
    open fun move(to: String) {
        this.chessFile = ChessFile.valueOf(to[0].toString().uppercase())
        this.chessRank = ChessRank.getRankByRankInt(to[1].digitToInt())
    }

    open fun getLegalMoves(): List<Pair<ChessFile, ChessRank>> {
        // DO NOT USE THIS FUNCTION
        throw Exception("Do not use super function 'getLegalMoves', inherit and override!")
    }

    companion object {
        fun convertRankFileToPositionString(file: ChessFile, rank: ChessRank): String {
            return "${file.name.lowercase()}${rank.rankInt}"
        }
    }
}

class Pawn(chessRank: ChessRank, chessFile: ChessFile, color: ChessColor): ChessPiece(chessRank, chessFile, color) {
    var notMoved = true

    override fun getLegalMoves(): List<Pair<ChessFile, ChessRank>> {
        val legalMoves = mutableListOf<Pair<ChessFile,ChessRank>>()
        val file = this.chessFile
        val rank = this.chessRank
        if (this.color == ChessColor.BLACK) {
            if (notMoved) {
                legalMoves.add(Pair(file, ChessRank.getRankByRankInt(rank.rankInt - 2)))
            }
            legalMoves.add(Pair(file, ChessRank.getRankByRankInt(rank.rankInt - 1)))
        } else {
            if (notMoved) {
                legalMoves.add(Pair(file, ChessRank.getRankByRankInt(rank.rankInt + 2)))
            }
            legalMoves.add(Pair(file, ChessRank.getRankByRankInt(rank.rankInt + 1)))
        }
        return legalMoves

    }
}

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
        val playerColor: List<ChessColor> = listOf(ChessColor.WHITE, ChessColor.BLACK)
        gameloop@while (gameState == GameState.RUNNING) {
            for (i in player_names.indices) {
                board.printBoard()
                while (true) {
                    println("${player_names[i]}'s turn:")
                    val chessInput = readLine()!!
                    if (chessInput.lowercase() == "exit") {
                        gameState = GameState.ABORTED
                        println("Bye!")
                        break@gameloop
                    }
                    if (chessRegexPattern.matches(chessInput.lowercase())) {
                        // Do stuff
                        val from = chessInput.substring(0, 2)
                        val to = chessInput.substring(2)
                        val piece = board.getPieceOrNull(from)
                        if (piece == null) {
                            println("No ${listOf("white","black")[i]} Pawn at $from")
                            continue
                        }
                        if (piece.color != playerColor[i]) {
                            println("No ${listOf("white","black")[i]} Pawn at $from")
                            continue
                        }
                        try {
                            board.movePiece(piece, to)
                        } catch (e: IllegalMoveException) {
                            printInvalidMove()
                            continue
                        }

                        break
                    } else {
                        printInvalidMove()
                        continue
                    }
                }
            }

        }
    }

    fun printInvalidMove() {
        println("Invalid Input")
    }
}

class IllegalMoveException: Exception("Move not possible")


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

    fun movePiece(piece: ChessPiece, to: String) {
        // to ist alles von a1 bis h8
        if (getPieceOrNull(to) == null) {
            val legalMoves = piece.getLegalMoves()
            val filteredLegalMoves = legalMoves.filter(checkIfValidLegalMove())
            val mappedFilteredLegalMoves = filteredLegalMoves.map{ChessPiece.convertRankFileToPositionString(it.first,it.second)}
            if (to !in mappedFilteredLegalMoves) {
                throw IllegalMoveException()
            }
            board[piece.chessRank.ordinal][piece.chessFile.ordinal] = null
            piece.move(to)
            if (piece is Pawn) {
                piece.notMoved = false
            }
            val file = ChessFile.valueOf(to[0].toString().uppercase())
            val rank = ChessRank.getRankByRankInt(to[1].digitToInt())
            board[rank.ordinal][file.ordinal] = piece
        } else {
            throw IllegalMoveException()
        }
    }

    fun checkIfValidLegalMove(): (Pair<ChessFile, ChessRank>) -> Boolean = {
        val position = ChessPiece.convertRankFileToPositionString(it.first, it.second)
        val x = ChessFile.valueOf(position[0].uppercase()).ordinal
        val y = ChessRank.getRankByRankInt(position[1].digitToInt()).ordinal
        print(board[y][x]?.chessFile.toString() + " " + board[y][x]?.chessRank.toString())
        board[y][x] == null
    }

    fun getPieceOrNull(position: String): ChessPiece? {
        val file = ChessFile.valueOf(position[0].toString().uppercase())
        val rank = ChessRank.getRankByRankInt(position[1].digitToInt())
        return board[rank.ordinal][file.ordinal]
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