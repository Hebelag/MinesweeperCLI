package com.example.myapplication.chess

import kotlin.math.abs


enum class ChessColor(val colorChar: Char, val colorName: String) {
    WHITE('W', "White"), BLACK('B', "Black");

    companion object {
        fun getChessColorName(x: ChessColor): String {
            return x.name.lowercase()
        }
        fun additionByColor(first: Int, second: Int, color: ChessColor): Int {
            return if (color == WHITE) {
                first - second
            } else {
                first + second
            }
        }
    }
}

enum class GameState {
    RUNNING, STALEMATE, WINNER, ABORTED;
}

enum class Action {
    MOVED, CAPTURED, CASTLE_SHORT, CASTLE_LONG, PROMOTION, CHECK, CHECKMATE
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
        fun getRankByIndex(index: Int): ChessRank? {
            if (index !in values().indices) {
                return null
            }
            return values()[index]
        }
        fun getRankByRankInt(rank: Int): ChessRank? = values().firstOrNull {
            it.rankInt == rank }
    }
}

enum class ChessFile {
    A, B, C, D, E, F, G, H;

    companion object {
        fun isIndexInFile(index: Int): Boolean = index in values().indices
        fun getFileByIndex(index: Int): ChessFile?{
            return if (isIndexInFile(index)){
                values()[index]
            } else {
                null
            }

        }
    }

}


class IllegalMoveException: Exception("Move not possible")
class InvalidChessPositionException: Exception("No Chess Position here!")


open class ChessPiece(var chessRank: ChessRank, var chessFile: ChessFile, val color: ChessColor) {
    fun getColor(): String {
        return this.color.colorChar.toString()
    }
    fun getPosition(): String {
        return "${this.chessFile.toString().lowercase()}${this.chessRank.rankInt}"
    }
    open fun move(to: String) {
        chessFile = ChessFile.valueOf(to[0].toString().uppercase())
        chessRank = ChessRank.getRankByRankInt(to[1].digitToInt())!!
    }

    open fun getLegalMoves(board: ChessBoard): List<Pair<ChessFile, ChessRank>> {
        // DO NOT USE THIS FUNCTION
        throw Exception("Do not use super function 'getLegalMoves', inherit and override!")
    }

    open fun getLegalAttacks(chessBoard: ChessBoard): List<ChessPiece> {
        throw Exception("Do not use super function 'getLegalAttacks', inherit and override!")
    }

    companion object {
        fun convertRankFileToPosition(file: ChessFile?, rank: ChessRank?): String {
            if (file == null || rank == null) {
                return "empty"
            }
            return "${file.name.lowercase()}${rank.rankInt}"
        }

        fun convertPositionToRankFile(position: String): Pair<ChessFile, ChessRank> {
            val file = ChessFile.valueOf(position[0].toString().uppercase())
            val rank = ChessRank.getRankByRankInt(position[1].digitToInt())!!
            return Pair(file,rank)
        }

    }
}

class Pawn(chessRank: ChessRank, chessFile: ChessFile, color: ChessColor): ChessPiece(chessRank, chessFile, color) {
    var moved = false
    var didDoubleMoveInLastMove = false
    var enPassantValid = false
    val shortNotation = ""

    override fun move(to: String) {
        val newChessFile = ChessFile.valueOf(to[0].toString().uppercase())
        val newChessRank = ChessRank.getRankByRankInt(to[1].digitToInt())!!
        if (abs(newChessRank.ordinal - this.chessRank.ordinal) == 2) {
            didDoubleMoveInLastMove = true
        }
        moved = true
        chessFile = newChessFile
        chessRank = newChessRank
    }

    override fun getLegalMoves(board: ChessBoard): List<Pair<ChessFile, ChessRank>> {
        val legalMoves = mutableListOf<Pair<ChessFile,ChessRank>>()
        val file = this.chessFile
        val rank = this.chessRank
        /*
        * Die Hierarchie und Effizienz dieser Kondition muss nochmal überdacht werden.
        * Für den Fall des Bauern klappt das noch, da es nur max 2 mögliche Züge sind.
         */

        val singleMove = Pair(file, ChessRank.getRankByIndex(ChessColor.additionByColor(rank.ordinal,1, this.color)))
        if (board.checkIfValidLegalMove(singleMove)) {
            if (!moved) {
                val doubleMove = Pair(file, ChessRank.getRankByIndex(ChessColor.additionByColor(rank.ordinal, 2, this.color)))
                if (board.checkIfValidLegalMove(doubleMove)) {
                    legalMoves.add(doubleMove as Pair<ChessFile, ChessRank>)
                }
            }
            legalMoves.add(singleMove as Pair<ChessFile, ChessRank>)
        }
        return legalMoves
    }

    override fun getLegalAttacks(chessBoard: ChessBoard): List<ChessPiece> {
        // Pawn can attack front-left or front-right OR EP-left EP-right
        val legalTakes = mutableListOf<ChessPiece>()
        val pieceFrontLeft = chessBoard.getPieceOrNull(convertRankFileToPosition(
            ChessFile.getFileByIndex(this.chessFile.ordinal - 1), ChessRank.getRankByIndex(ChessColor.additionByColor(this.chessRank.ordinal, 1, this.color))))

        if (pieceFrontLeft != null && pieceFrontLeft.color != this.color) {
            // TAKE FRONT LEFT
            legalTakes.add(pieceFrontLeft)
        }


        val pieceFrontRight = chessBoard.getPieceOrNull(convertRankFileToPosition(
            ChessFile.getFileByIndex(this.chessFile.ordinal + 1), ChessRank.getRankByIndex(ChessColor.additionByColor(this.chessRank.ordinal,1, this.color))))
        if (pieceFrontRight != null && pieceFrontRight.color != this.color) {
            // TAKE FRONT LEFT
            legalTakes.add(pieceFrontRight)
        }
        return legalTakes
    }

    fun getLegalPassants(chessBoard: ChessBoard): List<ChessPiece> {
        val legalPassants = mutableListOf<ChessPiece>()
        if (this.color == ChessColor.WHITE && this.chessRank == ChessRank.FIFTH ||
            this.color == ChessColor.BLACK && this.chessRank == ChessRank.FOURTH) {

            val pieceLeft = chessBoard.getPieceOrNull(
                convertRankFileToPosition(
                    ChessFile.getFileByIndex(this.chessFile.ordinal - 1), this.chessRank
                )
            )

            if (pieceLeft != null && pieceLeft is Pawn && pieceLeft.enPassantValid && pieceLeft.color != this.color) {
                // EN PASSANT LEFT
                legalPassants.add(pieceLeft)
            }

            val pieceRight = chessBoard.getPieceOrNull(
                convertRankFileToPosition(
                    ChessFile.getFileByIndex(this.chessFile.ordinal + 1), this.chessRank
                )
            )
            if (pieceRight != null && pieceRight is Pawn && pieceRight.enPassantValid && pieceRight.color != this.color) {
                // EN PASSANT RIGHT
                legalPassants.add(pieceRight)
            }
        }
        return legalPassants
    }
}

class Chess(val player_names: List<String>) {
    val board = ChessBoard()
    var gameState: GameState = GameState.RUNNING
    val chessRegexPattern = Regex("[a-h][1-8][a-h][1-8]")
    val moveHistory: MutableList<String> = mutableListOf()
    var moveNumber = 0
    val enPassantValid = mutableMapOf<Pawn, Int>()
    var winner: ChessColor? = null

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
        /*
        * Nun ist die Frage, mit welcher Hierarchie ein Schachprogramm nach gültigen Zügen schaut.
        * 1. Prüfe, welche Schachfigur bewegt werden soll.
        * 2. Erhalte zunächst alle Züge, die diese Schachfigur erreichen kann (vom Brett her).
        * 3. (zunächst Optional) Ist diese Schachfigur gepinnt? (durch Turm, Läufer, Dame etc.)
        * 4. Filtere alle Züge aus 2., bei denen eine Figur geschlagen werden kann.
        * 4,5. Spezialfälle -> En Passant, Castle (Mehr Sachen zu berücksichtigen)
        * 5. Filtere alle Züge aus 2., auf die die Schachfigur gehen kann.
        * 6. Anhand der übergebenen Position: Ist es ein Move oder ein Take?
        * */
        val playerColor: List<ChessColor> = listOf(ChessColor.WHITE, ChessColor.BLACK)

        gameloop@while (gameState == GameState.RUNNING) {
            moveNumber++
            for (i in player_names.indices) {
                enPassantExpired()
                while (true) {
                    // Ask for player input
                    println("${player_names[i]}'s turn:")
                    val chessInput = readLine()!!
                    if (chessInput.lowercase() == "exit") {
                        gameState = GameState.ABORTED
                        break@gameloop
                    }

                    // Check if input makes sense and has valid format
                    if (chessRegexPattern.matches(chessInput.lowercase())) {
                        val (from, to) = divideMoveString(chessInput)

                        // Which chess piece was taken "from"
                        val piece = board.getPieceOrNull(from)
                        if (piece == null) {
                            println("No ${listOf("white","black")[i]} Pawn at $from")
                            continue
                        }
                        if (piece.color != playerColor[i]) {
                            println("No ${listOf("white","black")[i]} Pawn at $from")
                            continue
                        }

                        val performedAction: Action
                        try {
                            performedAction = board.performMove(piece, to)
                        } catch (e: IllegalMoveException) {
                            printInvalidMove()
                            continue
                        }
                        when (performedAction) {
                            Action.CAPTURED -> moveHistory.add("${from}x$to")
                            else -> moveHistory.add("$from$to")
                        }
                        board.printBoard()
                        checkDoubleMove()
                        val promoted = isLastRankReached(playerColor[i])
                        val allCaptured = isAllCaptured()
                        val stalemated = isStalemate()
                        if (promoted || allCaptured) {
                            gameState = GameState.WINNER
                            winner = playerColor[i]
                            break@gameloop
                        } else if (stalemated) {
                            gameState = GameState.STALEMATE
                            break@gameloop
                        } else {
                            break
                        }

                    } else {
                        printInvalidMove()
                        continue
                    }
                }
            }

        }
        if (gameState == GameState.WINNER) {
            println("${winner?.colorName} Wins!")
        } else if (gameState == GameState.STALEMATE) {
            println("Stalemate!")
        }
    }



    private fun isAllCaptured(): Boolean {
        val allPawns = board.board.flatMap { it.toList() }.filterNotNull().filterIsInstance<Pawn>()
        val (whitePawns, blackPawns) = allPawns.partition { it.color == ChessColor.WHITE }
        return when {
            whitePawns.isEmpty() -> true
            blackPawns.isEmpty() -> true
            else -> false
        }
    }

    private fun isStalemate(): Boolean {
        val allPawns = board.board.flatMap { it.toList() }.filterNotNull().filterIsInstance<Pawn>()
        val (whitePawns, blackPawns) = allPawns.partition{ it.color == ChessColor.WHITE }
        val whiteMoves = whitePawns.flatMap { it.getLegalMoves(board) }.isEmpty()
        val whiteAttacks = whitePawns.flatMap { it.getLegalAttacks(board) }.isEmpty()
        val whitePassants = whitePawns.flatMap { it.getLegalPassants(board) }.isEmpty()
        val blackMoves = blackPawns.flatMap { it.getLegalMoves(board) }.isEmpty()
        val blackAttacks = blackPawns.flatMap { it.getLegalAttacks(board) }.isEmpty()
        val blackPassants = blackPawns.flatMap { it.getLegalPassants(board) }.isEmpty()


        return when {
            whiteMoves && whiteAttacks && whitePassants -> true
            blackMoves && blackAttacks && blackPassants -> true
            else -> false
        }
    }

    private fun isLastRankReached(currentTurnColor: ChessColor): Boolean {
        val allPawns = board.board.flatMap { it.toList() }.filterNotNull().filterIsInstance<Pawn>()
        val ownPawns = allPawns.filter { it.color == currentTurnColor }
        return when (currentTurnColor) {
            ChessColor.WHITE -> {
                ownPawns.any { it.chessRank == ChessRank.EIGHTH }
            }
            ChessColor.BLACK -> {
                ownPawns.any {it.chessRank == ChessRank.FIRST}
            }
        }
    }

    private fun enPassantExpired() {
        enPassantValid.forEach { (piece, move) ->
            if (move == moveNumber) {
                piece.enPassantValid = false
            }
        }
    }

    private fun checkDoubleMove() {
        val doubleList = board.board.flatMap { it.toList() }.filterNotNull().filterIsInstance<Pawn>().filter{ it.didDoubleMoveInLastMove }
        doubleList.forEach {
            enPassantValid[it] = moveNumber + 2
            it.enPassantValid = true
            it.didDoubleMoveInLastMove = false
        }
    }

    fun printInvalidMove() {
        println("Invalid Input")
    }

    companion object {
        fun divideMoveString(move: String): Pair<String, String> {
            val from = move.substring(0, 2)
            val to = move.substring(2)
            return Pair(from, to)
        }
    }
}



class ChessBoard(val pawnOnly: Boolean = true) {
    val separator = "  +---+---+---+---+---+---+---+---+"
    val board = List(8){MutableList<ChessPiece?>(8){null}}
    val killedPieces = mutableListOf<ChessPiece>()
    fun resetBoard() {
        if (pawnOnly) {
            repeat(8) {
                board[ChessRank.EIGHTH.ordinal][it] = null
                board[ChessRank.SEVENTH.ordinal][it] = Pawn(ChessRank.SEVENTH, ChessFile.getFileByIndex(it)!!, ChessColor.BLACK)
                board[ChessRank.SIXTH.ordinal][it] = null
                board[ChessRank.FIFTH.ordinal][it] = null
                board[ChessRank.FOURTH.ordinal][it] = null
                board[ChessRank.THIRD.ordinal][it] = null
                board[ChessRank.SECOND.ordinal][it] = Pawn(ChessRank.SECOND, ChessFile.getFileByIndex(it)!!, ChessColor.WHITE)
                board[ChessRank.FIRST.ordinal][it] = null
            }
        } else {
            TODO("Reset whole board with all pieces like in traditional chess.")
        }
    }

    fun printBoard() {
        println(separator)
        board.forEachIndexed { index, mutableList ->
            print(ChessRank.getRankByIndex(index)!!.rankInt.toString() + " | ")
            print(mutableList.joinToString(" | ") { it?.getColor() ?: " " } )
            println(" |")
            println(separator)
        }
        print("    ")
        print(ChessFile.values().joinToString("   ") { it.toString().lowercase() })
        println("  ")
    }

    fun performMove(piece: ChessPiece, to: String): Action {
        // If there is no piece at the final position, means it is a simple "move"
        if (piece is Pawn && piece.getLegalPassants(this).isNotEmpty()) {
            val legalPassants = piece.getLegalPassants(this)
            val legalPassantsPositions = legalPassants.map{
                val behindPieceRank = ChessRank.getRankByIndex(ChessColor.additionByColor(it.chessRank.ordinal, -1, it.color))
                ChessPiece.convertRankFileToPosition(it.chessFile, behindPieceRank)}
            if (to !in legalPassantsPositions) {
                throw IllegalMoveException()
            }

            val legalPassantIndex = legalPassantsPositions.indexOfFirst { it == to }
            val theLegalPassant = legalPassants[legalPassantIndex]
            board[piece.chessRank.ordinal][piece.chessFile.ordinal] = null
            piece.move(to)
            val (toFile, toRank) = ChessPiece.convertPositionToRankFile(to)
            val attackedPiece = board[theLegalPassant.chessRank.ordinal][theLegalPassant.chessFile.ordinal]!!
            killedPieces.add(attackedPiece)
            board[theLegalPassant.chessRank.ordinal][theLegalPassant.chessFile.ordinal] = null
            board[toRank.ordinal][toFile.ordinal] = piece
            return Action.CAPTURED
        }
        if (getPieceOrNull(to) == null) {
            // Get all possible moves
            val legalMoves = piece.getLegalMoves(this)
            // Convert then to the positional string
            val mappedFilteredLegalMoves = legalMoves.map{ChessPiece.convertRankFileToPosition(it.first,it.second)}.filter{it != "empty"}

            // Check if the provided move is even legal
            if (to !in mappedFilteredLegalMoves) {
                throw IllegalMoveException()
            }
            // All checks are made now, now the move-process can begin
            // Remove piece from old position
            board[piece.chessRank.ordinal][piece.chessFile.ordinal] = null

            // Change piece internal info about position
            when (piece) {
                is Pawn -> {
                    piece.move(to)
                }
            }
            val (file, rank) = ChessPiece.convertPositionToRankFile(to)
            board[rank.ordinal][file.ordinal] = piece
            return Action.MOVED
        } else {
            // if there is an enemy piece, it is an attack
            // can the player even attack it?
            val legalAttacks = piece.getLegalAttacks(this)
            val mappedFilteredLegalAttacks = legalAttacks.map{ChessPiece.convertRankFileToPosition(it.chessFile, it.chessRank)}
            if (to !in mappedFilteredLegalAttacks) {
                throw  IllegalMoveException()
            }

            board[piece.chessRank.ordinal][piece.chessFile.ordinal] = null
            when (piece) {
                is Pawn -> {
                    piece.move(to)
                }
            }
            val (attackedPieceFile, attackedPieceRank) = ChessPiece.convertPositionToRankFile(to)
            val attackedPiece = board[attackedPieceRank.ordinal][attackedPieceFile.ordinal]!!
            killedPieces.add(attackedPiece)
            board[attackedPieceRank.ordinal][attackedPieceFile.ordinal] = piece
            return Action.CAPTURED

        }
    }

    fun checkIfInBetweenEmpty(): (ChessPiece, Pair<ChessFile, ChessRank>) -> Boolean = {
            chessPiece, pair ->
        val position = ChessPiece.convertRankFileToPosition(pair.first, pair.second)
        val x = ChessFile.valueOf(position[0].uppercase()).ordinal
        val y = ChessRank.getRankByRankInt(position[1].digitToInt())!!.ordinal
        true

    }

    fun checkIfValidLegalMove(coordinates: Pair<ChessFile?, ChessRank?>): Boolean {
        val position = ChessPiece.convertRankFileToPosition(coordinates.first, coordinates.second)
        if (position == "empty") {
            return false
        }
        val (x,y) = ChessPiece.convertPositionToRankFile(position)
        val file = x.ordinal
        val rank = y.ordinal
        if (coordinates.first == null || coordinates.second == null) {
            return false
        }
        return board[rank][file] == null
    }

    fun getPieceOrNull(position: String): ChessPiece? {
        if (position == "empty") {
            return null
        }
        val file = ChessFile.valueOf(position[0].toString().uppercase())
        val rank = ChessRank.getRankByRankInt(position[1].digitToInt()) ?: return null
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
    chess.board.printBoard()
    chess.gameLoop()
    println("Bye!")
}