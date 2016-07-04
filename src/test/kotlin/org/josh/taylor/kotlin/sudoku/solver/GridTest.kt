package org.josh.taylor.kotlin.sudoku.solver

import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GridTest : Spek({

    describe("Test") {
        var grid = Grid()

        beforeEach {
            grid = Grid()
        }

        it("should not be complete when empty grid") {
            val result = grid.isComplete()

            assertFalse(result)
        }

        it("should not be complete when missing single entry") {
            givenSolvedGrid(grid)
            grid.clear(5, 5)

            val result = grid.isComplete()

            assertFalse(result)
        }

        it("should be complete when grid is fully filled") {
            givenGridWithDupesInSubgrid(grid)

            assertTrue(grid.isComplete())
        }

        it("should not allow marking integer with value of 0") {
            assertFailsWith(IllegalArgumentException::class) {
                grid.mark(0, 0, 0)
            }
        }

        it("should not allow marking integer with value of 10") {
            assertFailsWith(IllegalArgumentException::class) {
                grid.mark(0, 0, 10)
            }
        }

        it("should be able to print complete grid") {
            givenSolvedGrid(grid);

            assertEquals(grid.toString().replace("\n", "").replace("\r", ""),
                    "123456789456789123789123456234567891567891234891234567345678912678912345912345678")
        }

        it("should be able to get coord from grid") {
            grid.mark(4, 7, 9)

            val result = grid.get(4, 7);

            assertEquals(result, 9)
        }

        it("should be invalid if two identical numbers in same row") {
            givenSolvedGrid(grid)

            grid.mark(5, 3, 5)
            grid.mark(5, 8, 7)

            assertFalse(grid.isValid())
        }

        it("should be invalid if two identical numbers in same column") {
            givenSolvedGrid(grid)

            grid.mark(3, 5, 7)
            grid.mark(8, 5, 2)

            assertFalse(grid.isValid())
        }

        it("should be invalid if two identical numbers in same 3x3 subgrid") {
            givenGridWithDupesInSubgrid(grid)

            assertFalse(grid.isValid())
        }

        it("should be able to solve grid with a missing blank cell") {
            givenSolvedGrid(grid)
            grid.clear(4, 4)
0
            BruteForceSolver().solve(grid)

            assertTrue(grid.isComplete() && grid.isValid())
        }

        it("should be able to solve grid with three missing blank cells") {
            givenSolvedGrid(grid)
            grid.clear(Random().nextInt(8) + 1, Random().nextInt(8) + 1)
            grid.clear(Random().nextInt(8) + 1, Random().nextInt(8) + 1)
            grid.clear(Random().nextInt(8) + 1, Random().nextInt(8) + 1)

            BruteForceSolver().solve(grid)

            assertTrue(grid.isComplete() && grid.isValid())
        }

        it("should be able to find a solution to an empty grid") {
            BruteForceSolver().solve(grid)

            assertTrue(grid.isComplete() && grid.isValid())
        }

        it("should be able to solve the hardest sudoku puzzle") {
            grid.mark(0, 0, 8)
            grid.mark(2, 1, 3)
            grid.mark(3, 1, 6)
            grid.mark(1, 2, 7)
            grid.mark(4, 2, 9)
            grid.mark(6, 2, 2)
            grid.mark(1, 3, 5)
            grid.mark(5, 3, 7)
            grid.mark(4, 4, 4)
            grid.mark(5, 4, 5)
            grid.mark(6, 4, 7)
            grid.mark(3, 5, 1)
            grid.mark(7, 5, 3)
            grid.mark(2, 6, 1)
            grid.mark(7, 6, 6)
            grid.mark(8, 6, 8)
            grid.mark(2, 7, 8)
            grid.mark(3, 7, 5)
            grid.mark(7, 7, 1)
            grid.mark(1, 8, 9)
            grid.mark(6, 8, 4)

            BruteForceSolver().solve(grid)

            assertTrue(grid.isComplete() && grid.isValid())
        }
    }

})

private fun givenGridWithDupesInSubgrid(grid: Grid) {
    var rowFirstNum = 9;
    for (y in 0..8) {
        var currentNum = rowFirstNum;
        for (x in 0..8) {
            grid.mark(x, y, currentNum);
            currentNum = incrementNumber(currentNum)
        }
        rowFirstNum -= 1;
    }
}

private fun givenSolvedGrid(grid: Grid) {
    var numberMark = 1;
    for (x in 0..8) {
        grid.mark(x, 0, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 4;
    for (x in 0..8) {
        grid.mark(x, 1, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 7;
    for (x in 0..8) {
        grid.mark(x, 2, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 2;
    for (x in 0..8) {
        grid.mark(x, 3, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 5;
    for (x in 0..8) {
        grid.mark(x, 4, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 8;
    for (x in 0..8) {
        grid.mark(x, 5, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 3;
    for (x in 0..8) {
        grid.mark(x, 6, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 6;
    for (x in 0..8) {
        grid.mark(x, 7, numberMark)
        numberMark = incrementNumber(numberMark)
    }
    numberMark = 9;
    for (x in 0..8) {
        grid.mark(x, 8, numberMark)
        numberMark = incrementNumber(numberMark)
    }
}

private fun incrementNumber(numberMark: Int): Int {
    if (numberMark == 9) {
        return 1;
    }
    return numberMark + 1
}

class Grid {

    private val grid = Array(9, { IntArray(9) })

    fun isValid(): Boolean {
        for (y in 0..8) {
            val encounteredNumbers = hashSetOf<Int>()
            for (x in 0..8) {
                val gridNum = grid[x][y]
                if (gridNum > 0 && encounteredNumbers.contains(gridNum)) {
                    return false
                }
                encounteredNumbers.add(gridNum)
            }
        }

        for (x in 0..8) {
            val encounteredNumbers = hashSetOf<Int>()
            for (y in 0..8) {
                val gridNum = grid[x][y]
                if (gridNum > 0 && encounteredNumbers.contains(gridNum)) {
                    return false
                }
                encounteredNumbers.add(gridNum)
            }
        }

        for (x in 1..3) {
            for (y in 1..3) {
                if (checkSubGridFromBottomRightCorner((x * 3) - 1, ((y * 3) - 1)) == false) {
                    return false
                }
            }
        }

        return true;
    }

    private fun checkSubGridFromBottomRightCorner(bttmRightX: Int, bttmRightY: Int): Boolean {
        val encounteredNumbers = hashSetOf<Int>()
        for (x in bttmRightX - 2..bttmRightX) {
            for (y in bttmRightY - 2..bttmRightY) {
                val gridNum = grid[x][y]
                if (gridNum > 0 && encounteredNumbers.contains(gridNum)) {
                    return false
                }
                encounteredNumbers.add(gridNum)
            }
        }
        return true;
    }

    fun mark(x: Int, y: Int, number: Int) {
        if (number < 1 || number > 9) {
            throw IllegalArgumentException()
        }
        grid[x][y] = number;
    }

    fun clear(x: Int, y: Int) {
        grid[x][y] = 0
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0..8) {
            for (x in 0..8) {
                sb.append(grid[x][y])
            }
            sb.appendln()
        }
        return sb.toString();
    }

    fun isComplete(): Boolean {
        for (x in 0..8) {
            for (y in 0..8) {
                if (grid[x][y] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    fun get(x: Int, y: Int): Int {
        return grid[x][y]
    }

}

interface Solver {
    fun solve(grid: Grid): Grid
}

class BruteForceSolver : Solver {

    override fun solve(grid: Grid): Grid {
        var blankCells = mutableListOf<Pair<Int, Int>>()
        for (x in 0..8) {
            for (y in 0..8) {
                if (grid.get(x, y) == 0) {
                    blankCells.add(Pair(x, y))
                }
            }
        }

        val grid = solve(blankCells, grid)
        println("SOLUTION")
        println(grid)
        return grid
    }

    private fun solve(blankCells: List<Pair<Int, Int>>, grid: Grid): Grid {
        if (blankCells.isEmpty() || !grid.isValid()) {
            return grid
        }
        val cell = blankCells[0]
        for (numberAttempt in 1..9) {
            grid.mark(cell.first, cell.second, numberAttempt)
            var tailedList = blankCells.toMutableList().subList(1, blankCells.size)
            solve(tailedList, grid)
            if (grid.isComplete() && grid.isValid()) {
                return grid
            }
        }
        grid.clear(cell.first, cell.second)
        return grid
    }

}