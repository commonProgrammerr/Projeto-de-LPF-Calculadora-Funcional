import kotlin.browser.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.dom.addClass
import kotlin.dom.removeClass
import kotlin.math.sqrt as sqrt

// Implementação de uma arvore binaria de expresões
data class ExprTree(
        val value: String,
        val left: ExprTree?,
        val right: ExprTree?
) {

    // Metodo para resover a arvore/expressão (Calcular o valor da expressão)
    fun resolver(): Float {

        return when {
            // o @ representa que o numero é negativo (afim de evitar conflito com os operadores da expressão)
            this.left == null -> this.value.replace("@", "-").toFloat() 
            this.right == null -> this.value.replace("@", "-").toFloat()
            this.value == "*" -> this.left * this.right
            this.value == "/" -> this.left / this.right
            this.value == "%" -> this.left % this.right
            this.value == "+" -> this.left + this.right
            this.value == "-" -> this.left - this.right
            else -> this.value.replace("@", "-").toFloat()
        }
    }

    operator fun plus(b: ExprTree) = this.resolver() + b.resolver()
    operator fun minus(b: ExprTree) = this.resolver() - b.resolver()
    operator fun times(b: ExprTree) = this.resolver() * b.resolver()
    operator fun div(b: ExprTree) = this.resolver() / b.resolver()
    operator fun rem(b: ExprTree) = this.resolver() % b.resolver()

}


fun isOperator(value: String, except: String = ""): Boolean = value in "/*+-.%" && value != except

fun isCalculable(expression: String): Boolean = "([/*-+%])".toRegex().containsMatchIn(expression)

fun isHex(value: String): Boolean = """(\#[\d|a-f])""".toRegex().containsMatchIn(value)

fun isBin(value: String): Boolean = """(\!b[0-1])""".toRegex().containsMatchIn(value)

fun getInputElement() = document.getElementById("expression-field") as HTMLInputElement

fun getCurrentValue() = getInputElement().value


fun expression2Tree(expression: String, operator: String = "-"): ExprTree? {

    fun choseOp(): String {
        return when (operator) {
            in expression -> operator
            "-" -> "+"
            "+" -> "%"
            "%" -> "/"
            "/" -> "*"
            "*" -> ""
            else -> ""
        }
    }

    val replacedExpression = expression.replace("^-".toRegex(), "@")
    val splicedExpression = replacedExpression.split(operator, limit = 2)

    val nextOp = choseOp()

    return when {
        splicedExpression.size == 1 ->
            if (isCalculable(replacedExpression)) expression2Tree(replacedExpression, nextOp)
            else ExprTree(replacedExpression, null, null)

        splicedExpression.size > 1 -> ExprTree(operator,
                expression2Tree(splicedExpression[0], nextOp),
                expression2Tree(splicedExpression[1], nextOp)
        )

        else -> null
    }
}


@JsName("clean")
fun clean() {
    val input = getInputElement()
    input.removeClass("error")
    input.value = ""
}

@JsName("backspace")
fun backspace() {
    val inputField = getInputElement()
    val currentValue = inputField.value
    inputField.value = currentValue.dropLast(1)
}

@JsName("digitar")
fun key(e: KeyboardEvent) {
    when {
        e.keyCode == 13 -> calculate()
        else -> typeValue(e.key)
    }
}

@JsName("type")
fun type(e: Event) {
    val button = e.target as HTMLButtonElement
    val buttonText = button.innerText
    typeValue(buttonText)
}

@JsName("typeValue")
fun typeValue(value: String) {
    
    val inputField = getInputElement()
    
    fun isValidDigit(): Boolean {
        val inputValue = inputField.value
        val lastIndex = inputValue.substring(inputValue.length - 1)
        return when {
            lastIndex.isEmpty() && value == "-" -> true
            isOperator(value) -> lastIndex.isNotEmpty() && !isOperator(lastIndex)
            else -> !"""(\D)""".toRegex().containsMatchIn(value)
        }
    }
    
    if (isValidDigit())
        inputField.value += value

}

@JsName("pow")
fun pow() {
    val inputField = getInputElement()
    
    try {
        val expressionTree = expression2Tree(inputField.value)
        val result = expressionTree?.resolver()
    
        if (result != null)
            inputField.value = (result * result).toString()
    } catch (err: Exception) {    
        inputField.addClass("error")
    }    
}    

@JsName("squareRoot")
fun squareRoot() {
    val inputField = getInputElement()
    try {
        val expressionTree = expression2Tree(inputField.value)
        val result = expressionTree?.resolver()

        if (result != null)
            inputField.value = sqrt(result).toString()
    } catch (err: Exception) {    
        inputField.addClass("error")
    }    
}    

@JsName("toBin")
fun toBin() {
    val inputField = getInputElement()

    if (isBin(inputField.value)) {
        inputField.addClass("error")
        window.alert("Esse numero já é um valor binario!")
    } else {

        try {
            val expressionTree = expression2Tree(inputField.value)
            val floatResult = expressionTree?.resolver()
            val result = floatResult.toString()
                    .split(".")
                    .joinToString { it.toInt().toString(2) }
                    .replace(", ", ".")
            
            inputField.removeClass("error")
            inputField.value = "!b" + result

        } catch (err: Exception) {
            inputField.addClass("error")
        }
    }
}

@JsName("toHex")
fun toHex() {
    val inputField = getInputElement()

    if (isHex(inputField.value)) {
        inputField.addClass("error")
        window.alert("Esse numero já é um valor hexadecimal!")
    } else {

        try {
            val expressionTree = expression2Tree(inputField.value)
            val floatResult = expressionTree?.resolver()
            val result = floatResult.toString()
                    .split(".")
                    .joinToString { it.toInt().toString(16) }
                    .replace(", ", ".")
            
            inputField.removeClass("error")
            inputField.value = "#" + result

        } catch (err: Exception) {
            inputField.addClass("error")
        }
    }
}

@JsName("calculate")
fun calculate() {

    val inputField = getInputElement()
    
    try {
        val expressionTree = expression2Tree(inputField.value)
        val result = expressionTree?.resolver()
        inputField.removeClass("error")
        inputField.value = result.toString()
    } catch (err: Exception) {
        inputField.addClass("error")
    }
}
