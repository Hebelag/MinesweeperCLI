import kotlin.NumberFormatException
import kotlin.random.Random

enum class FieldState {
    UNEXPLORED, NUMBERED, MARKED, FREE
}

enum class GameState {
    RUNNING, FINISHED
}

enum class ActionState {
    DONE, RETRY
}

enum class Action {
    MINE, FREE;
    companion object {
        fun isInAction(name: String): Boolean {
            for (enum in Action.values()) {
                if (name.uppercase() == enum.name) return true
            }
            return false
        }
    }
}

class Main(var mineCount: Int, var height: Int = 9, var width: Int = 9) {
    var steppedOnMine: Boolean = false

    private val mineFieldArray = mutableListOf<MutableList<Char>>()
    private val onlyMines = mutableListOf<MutableList<Char>>()
    private val activeGameBoard = mutableListOf<MutableList<Char>>()
    private val mineList = mutableListOf<Pair<Int,Int>>()
    init {
        // Creating empty Fields
        for (y in 0 until this.height){
            val mineRow = mutableListOf<Char>()
            for (x in 0 until this.width){
                mineRow.add('.')
            }
            this.mineFieldArray.add(mineRow)
            this.onlyMines.add(mineRow.toMutableList())
            this.activeGameBoard.add(mineRow.toMutableList())
        }

        // Filling field with mines
        for (i in 0 until this.mineCount){
            var y: Int
            var x: Int
            do{
                y = Random.nextInt(0,this.mineFieldArray.lastIndex+1)
                x = Random.nextInt(0,this.mineFieldArray[y].lastIndex+1)
                mineList.add(Pair(x,y))
                val selectedMineStatus = this.mineFieldArray[y][x]
            } while (selectedMineStatus == 'X')

            this.mineFieldArray[y][x] = 'X'
        }

        // Calculating adjacent mines of each field
        for (y in 0 until this.height) {
            for (x in 0 until this.width) {
                val tempCount = checkIfMines(x, y, Action.MINE)
                this.changeFieldPos(tempCount, y, x)

            }
        }
    }



    private fun checkIfMine(y: Int, x: Int): Boolean {
        return this.mineFieldArray[y][x] == 'X'
    }

    private fun checkIfMineOrFree(y: Int, x: Int): Boolean {
        return (this.mineFieldArray[y][x] == 'X' || this.activeGameBoard[y][x] == '/')
    }

    private fun incrementIfMine(y: Int, x: Int, action: Action): Int {
        return if (y < 0 || y > this.height-1 || x < 0 || x > this.width-1) {
            0
        } else{
            when(action){
                Action.MINE -> if (checkIfMine(y, x)) 1 else 0
                Action.FREE -> if (checkIfMine(y, x)) 1 else 0
            }
        }

    }

    private fun checkIfMines(x: Int, y: Int, action: Action): Int {
        var tempCount = 0
        tempCount += incrementIfMine(y+1, x, action)
        tempCount += incrementIfMine(y-1, x, action)
        tempCount += incrementIfMine(y, x+1, action)
        tempCount += incrementIfMine(y, x-1, action)
        tempCount += incrementIfMine(y+1, x+1, action)
        tempCount += incrementIfMine(y-1, x+1, action)
        tempCount += incrementIfMine(y+1, x-1, action)
        tempCount += incrementIfMine(y-1, x-1, action)

        return tempCount
    }

    fun printField() {
        /*
         │123456789│
        —│—————————│
        1│*........│
        2│.........│
        3│.........│
        4│....111..│
        5│....1*1..│
        6│....111..│
        7│.........│
        8│.........│
        9│.........│
        —│—————————│
        */
        print(" |")
        for (y in 0 until this.width) {
            print(y+1)
        }
        println("|")
        print("-|")
        for (y in 0 until this.width) {
            print("-")
        }
        println("|")
        for (y in 0 until this.height) {
            println("${y+1}|${this.activeGameBoard[y].joinToString("")}|")
        }
        print("-|")
        for (y in 0 until this.width) {
            print("-")
        }
        println("|")
    }

    fun printWithMines() {
        print(" |")
        for (y in 0 until this.width) {
            print(y+1)
        }
        println("|")
        print("-|")
        for (y in 0 until this.width) {
            print("-")
        }
        println("|")
        for (mines in this.mineList) {
            this.activeGameBoard[mines.second][mines.first] = 'X'
        }
        for(column in 0 until this.height) {
            print("${column+1}|")
            print(this.activeGameBoard[column].joinToString(""))
            println("|")
        }
        print("-|")
        for (y in 0 until this.width) {
            print("-")
        }
        println("|")
    }

    private fun getFieldState(column: Int, row: Int): FieldState {
        /*
        . = Unexplored minefield
        / = explored free cells without mines around it
        * = unexplored marked cells
        * */
        return when(this.activeGameBoard[column][row]) {
            '.' -> FieldState.UNEXPLORED
            '*' -> FieldState.MARKED
            '/' -> FieldState.FREE
            else -> FieldState.NUMBERED
        }
    }

    fun performFieldAction(coords: List<Int>, action: Action): ActionState {

        val row: Int = coords[0] - 1
        val column: Int = coords[1] - 1

        fun errorInput(action: Action): ActionState {
            println("Can't use ${action.name} on coordinates $row, $column: Cell is ${this.getFieldState(column, row).name}")
            return ActionState.RETRY
        }

        fun flagField(): ActionState {
            return when {
                this.activeGameBoard[column][row] == '.' -> {
                    this.activeGameBoard[column][row] = '*'
                    ActionState.DONE
                }
                this.activeGameBoard[column][row] == '*' -> {
                    println("Can't flag a flagged field")
                    ActionState.RETRY
                }
                else -> {
                    ActionState.RETRY
                }
            }
        }

        fun deflagField(): ActionState {
            return when {
                this.activeGameBoard[column][row] == '*' -> {
                    this.activeGameBoard[column][row] = '.'
                    ActionState.DONE
                }
                this.activeGameBoard[column][row] == '.' -> {
                    println("Can't deflag an unflagged field")
                    ActionState.RETRY
                }
                else -> {
                    ActionState.RETRY
                }
            }
        }

        fun freeCells(row: Int, column: Int) {
            // Check cells around if also free
            if (row < 0 || row > this.width-1 || column < 0 || column > this.height-1) {
                println("Illegal freeCell operation")
                return
            }
            if (Pair(row,column) in this.mineList) {
                return
            }
            if (this.activeGameBoard[column][row] != '.') {
                return
            }
            this.activeGameBoard[column][row] = '/'
            val tempCount = checkIfMines(row, column, Action.FREE)
            if (tempCount == 0){
                /*
                * DER FEHLER STECKT HIER IRGENDWO IN DER ABFRAGE, OB OBEN
                * IN DEN EINZELNEN IFS ODER SONST WO, ABER HIER IST DER LETZTE
                * SCHRITT ZUM ERFOLG, DRAN BLEIBEN!
                * */
                freeCells(row, column-1, ) // Top
                freeCells(row+1,column-1) //TopRight
                freeCells(row+1, column) // Right
                freeCells(row+1, column+1)  //BotRight
                freeCells(row, column+1) // Bot
                freeCells(row-1, column+1) // BotLeft
                freeCells(row-1, column) // Left
                freeCells(row-1, column-1) // TopLeft
            } else {
                if (this.mineFieldArray[column][row] in listOf('1','2','3','4','5','6','7','8')) {
                    this.activeGameBoard[column][row] = this.mineFieldArray[column][row]
                }
            }
        }
        fun exploreCell(row: Int, column: Int): ActionState {
            if (this.activeGameBoard[column][row] != '.') {
                println("Can't explore an explored cell, try again!")
                return ActionState.RETRY
            }
            return when (this.mineFieldArray[column][row]) {
                'X' -> {
                    this.steppedOnMine = true
                    ActionState.DONE
                }
                in listOf('1','2','3','4','5','6','7','8') -> {
                    this.activeGameBoard[column][row] = this.mineFieldArray[column][row]
                    ActionState.DONE
                }
                '.' -> {
                    freeCells(row, column)
                    ActionState.DONE
                }
                else -> {
                    errorInput(Action.FREE)
                }
            }

        }


        return when(action) {
            Action.MINE -> {
                when (this.getFieldState(column, row)){
                    FieldState.UNEXPLORED -> flagField()
                    FieldState.MARKED -> deflagField()
                    else -> errorInput(action)
                }
            }// Flag an unexplored Cell, Unflag a Flagged Cell
            Action.FREE -> {
                when (this.getFieldState(column, row)) {
                    FieldState.UNEXPLORED -> exploreCell(row, column)
                    else -> errorInput(action)
                }
            }
        }
    }

    private fun changeFieldPos(mineCount: Int, y: Int, x: Int) {
        if (this.mineFieldArray[y][x] != 'X') {
            if (mineCount > 0) {
                this.mineFieldArray[y][x] = (mineCount + 48).toChar()

            } else {
                this.mineFieldArray[y][x] = '.'
            }
        }
    }

    private fun allMinesFound(): Boolean {
        for (mine in this.mineList) {
            if (this.activeGameBoard[mine.second][mine.first] != '*') {
                return false
            }
        }
        return true
    }

    private fun allNonMinesExplored(): Boolean {
        for (column in 0 until this.height) {
            for (row in 0 until this.width) {
                if (this.activeGameBoard[column][row] == '.') {
                    if (Pair(row,column) !in this.mineList) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun isGameFinished(): Boolean {
        /*
        2 Konditionen:
        alle Minen geflagged
        alle nicht-minen explored
        */
        return (allMinesFound() || allNonMinesExplored())

    }
}

fun main() {
    var mineCount: Int = -1
    while (true) {

        println("How many mines do you want on the field?")
        try {
            mineCount = readLine()!!.toInt()
        } catch (e: NumberFormatException) {
            println("Input was not a valid number!")
            continue
        }
        if (mineCount <= 0 || mineCount > 81) {
            println("Mine count cannot be bigger than the number of fields, or can't be negative")
            continue
        }
        break
    }

    val minesweeper = Main(mineCount = mineCount, height = 9, width = 9)
    /*
    Infinite Loop until the game finishes
    1. Ask user about mines in the 10*10 field
    2. Build minefield, locate mines in vicinity with numbers
    3. Start game loop
    4. Ask user about coordinates
    5. evaluate coordinates
    6. Depending on field state, do a task
    7. repeat 4 until user wins.
    */
    var gameState: GameState = GameState.RUNNING
    while (gameState == GameState.RUNNING) {
        do {
            minesweeper.printField()
            val coordinates = mutableListOf<Int>()
            var coordinateInput: List<String>
            val action: Action
            while (true) {
                println("Set/delete mines marks (x and y coordinates): ")
                coordinateInput = readLine()!!.split(" ")
                var row: Int
                var column: Int
                try {
                    row = coordinateInput[0].toInt()
                } catch (e: NumberFormatException) {
                    println("First input was not a number!")
                    continue
                }
                if (row !in 0..minesweeper.width) {
                    println("x coordinate $row is out of bounds")
                    continue
                } else {
                    coordinates.add(row)
                }
                try {
                    column = coordinateInput[1].toInt()
                } catch (e: NumberFormatException) {
                    println("Second input was not a number!")
                    continue
                }
                if (column !in 0..minesweeper.height) {
                    println("y coordinate $column is out of bounds")
                    continue
                } else {
                    coordinates.add(column)
                }
                if (!Action.isInAction(coordinateInput[2].uppercase())) {
                    println("Third input was not a valid action! Either 'mine' or 'free'.")
                    continue
                } else {
                    action = Action.valueOf(coordinateInput[2].uppercase())
                }
                break
            }
            val result = minesweeper.performFieldAction(coordinates, action)
        } while (result == ActionState.RETRY)

        gameState = if (minesweeper.isGameFinished() || minesweeper.steppedOnMine) GameState.FINISHED else GameState.RUNNING

    }
    if (minesweeper.steppedOnMine) {
        println("You stepped on a mine and failed!")
        minesweeper.printWithMines()
    } else {
        minesweeper.printField()
        println("Congratulations! You found all the mines!")
    }
}

