import java.math.BigDecimal
import java.util.Stack

class Calculator {
    var mapValue = mutableMapOf<String, String>()

    init {
        inputNumber()
    }

    fun infixToPostfix(input: String) {
        if (!checkError(input)) {
            val queue = mutableListOf<String>()
            var stack = Stack<String>()
//            val formatInput = input.replace(Regex("(-?[A-Za-z0-9]+)"), " $1 ")
            val formatInput = input.replace(Regex("(\\+|\\*|\\(|\\)|/)"), " $1 ")
                .replace(Regex(" {2,}"), " ").trim().split(" ")
            for (el in formatInput) {
                if (el.matches(Regex("-?[a-zA-Z0-9]+"))) queue.add(el)
                else if (stack.isEmpty() || stack.peek() == "(" || el == "("
                    || (el.matches(Regex("""[/*]""")) && stack.peek().matches(Regex("[+-]")))
                ) stack.push(el)
                else if (el == ")") {
                    if (!stack.contains("(")) {
                        println("Invalid expression")
                        return
                    }
                    for (index in stack.lastIndex downTo 0) {
                        if (stack[index] == "(") {
                            stack.pop()
                            break
                        } else {
                            queue.add(stack[index])
                            stack.pop()
                        }
                    }
                } else {
                    for (index in stack.lastIndex downTo 0) {
                        if (stack[index] == "(") {
                            stack.push(el)
                            break
                        } else
                            queue.add(stack[index])
                        stack.pop()
                    }
                    if (stack.isEmpty()) stack.push(el)
                }
            }
            queue.addAll(stack.reversed())
            calc(queue)
        }
    }

    fun getValue(element: String): BigDecimal? {
        return if (element.matches(Regex("[0-9]+"))) element.toBigDecimal()
        else mapValue.getValue(element).toBigDecimal()
    }

    fun operationWithTwoElement(operator: String, stack: Stack<BigDecimal>): Stack<BigDecimal> {
        val second = stack.pop()
        val first = stack.pop()
        val result = when (operator) {
            "+" -> first + second
            "-" -> first - second
            "/" -> first / second
            else -> first * second
        }
        stack.push(result)
        return stack
    }

    fun calc(queue: MutableList<String>) {
        val stack = Stack<BigDecimal>()
        for (i in queue.indices) {
            if (queue[i].matches(Regex("[0-9A-Za-z]+"))) stack.push(getValue(queue[i]))
            else operationWithTwoElement(queue[i], stack)
        }
        println(stack.peek())
    }

    fun checkIfOneElement(input: String) {
        if (input.split(" ")[0].matches(Regex("-?[0-9]+"))) println(input.split(" ")[0].toInt())
        else infixToPostfix(input)
    }

    fun inputNumber() {
        while (true) {
            val input = readln().replace(Regex(""" {2,}"""), " ").replace("--", "+").replace("+-", "-")
                .replace(Regex("""[+]{2,}"""), "+")
            when {
                input == "/exit" -> {
                    println("Bye!")
                    break
                }

                input == "/help" -> println("The program calculates the sum of numbers")
                input.isEmpty() -> continue
                input.split(" ").size == 1 -> checkIfOneElement(input)
                else -> {
                    try {
                        infixToPostfix(input)
                    } catch (e: Exception) {
                        println("Invalid expression")
                    }
                }
            }
        }
    }

    fun initializationValue(input: String, mapValue: MutableMap<String, String>): MutableMap<String, String> {
        val pair = input.replace(" ", "").split("=")
        if (!pair[0].matches(Regex("""[A-Za-z]+"""))) println("Invalid identifier")
        else if (pair.size == 1) {
            if (mapValue.keys.contains(pair[0])) println(mapValue.getValue(pair[0]))
            else println("Unknown variable")
        } else if (pair.size == 2) {
            if (pair.last().matches(Regex("""-?[0-9]+"""))) mapValue[pair.first()] = pair.last()
            else if (mapValue.keys.contains(pair.last())) mapValue[pair.first()] = mapValue.getValue(pair.last())
            else println("Invalid assignment")
        } else println("Invalid assignment")

        return mapValue
    }

    fun checkError(input: String): Boolean {
        val list = input.split(" ").toMutableList()
        if (input.first() == '/') {
            println("Unknown command")
            return true
        } else {
            if (list.size == 1 || input.contains("=")) {
                initializationValue(input, mapValue)
                return true
            }
            if (list.contains("="))
                for (i in list.indices) {
                    if (!i.isOdd() && list[i].toIntOrNull() == null && !mapValue.keys.contains(list[i])) list[i] =
                        "error"
                    else if (i.isOdd() && !list[i].matches(Regex("""[+-]+"""))) list[i] = "error"
                }
        }
        if (list.contains("error")) {
            println("Invalid expression")
            return true
        }

        return false
    }
}

fun Int.isOdd() : Boolean = this%2==1

fun main() {
    val calculator = Calculator()
}