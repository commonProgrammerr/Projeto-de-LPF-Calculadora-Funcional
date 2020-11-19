import kotlin.browser.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.dom.addClass
import kotlin.dom.removeClass
import kotlin.math.sqrt as sqrt

/*
*
*   Aluno: ANDRÉ GUSTAVO V. ESCOREL RIBEIRO
*   2º Exercicio Escolar - 2020.3 - LPF
*
*/

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
    // Operadores
    operator fun plus(b: ExprTree) = this.resolver() + b.resolver()
    operator fun minus(b: ExprTree) = this.resolver() - b.resolver()
    operator fun times(b: ExprTree) = this.resolver() * b.resolver()
    operator fun div(b: ExprTree) = this.resolver() / b.resolver()
    operator fun rem(b: ExprTree) = this.resolver() % b.resolver()

}
/*
*
*   Funções auxiliares ou "de chamada Nativa"
*
*/

fun isOperator(value: String, except: String = ""): Boolean = value in "/*+-.%" && value != except

fun isCalculable(expression: String): Boolean = "([/*-+%])".toRegex().containsMatchIn(expression)

fun isHex(value: String): Boolean = """(\#[\d|a-f])""".toRegex().containsMatchIn(value)

fun isBin(value: String): Boolean = """(\!b[0-1])""".toRegex().containsMatchIn(value)

fun getInputElement() = document.getElementById("expression-field") as HTMLInputElement

fun getCurrentValue() = getInputElement().value // Retorna o valor atual do 

// Gera uma arvore binaria a partir de uma expressão
fun expression2Tree(expression: String, operator: String = "-"): ExprTree? {

    fun getNextOperator(): String {
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

    val replacedExpression = expression.replace("^-".toRegex(), "@")//Subistitue o sinal engativo de numros, para evitar conflitos de analise
    
    val splicedExpression = replacedExpression.split(operator, limit = 2)// Divide a expressão para serem repssados para os nós subsequentes

    val nextOp = getNextOperator()// Salva o valor do operador da proxima chamda

    return when {
        splicedExpression.size == 1 -> // Caso ainda tenha operadores, continua a chamada recursiva, caso contrario só adiciona um nó
            if (isCalculable(replacedExpression)) expression2Tree(replacedExpression, nextOp)
            else ExprTree(replacedExpression, null, null)

        splicedExpression.size > 1 -> ExprTree(operator, // Adiciona os dois nós filhos, com uma chamda recursiva, e salvando o valor da operação montando a arovores
                expression2Tree(splicedExpression[0], nextOp),
                expression2Tree(splicedExpression[1], nextOp)
        )

        else -> null
    }
}


/*
*
*   Funções principais ou "de chamada do Client"
*
*/

//Limpa a expressão
@JsName("clean")
fun clean() {
    val input = getInputElement()
    input.removeClass("error")
    input.value = ""
}

//Apaga 0 ultimo elemento
@JsName("backspace")
fun backspace() {
    val inputField = getInputElement()
    val currentValue = inputField.value
    inputField.value = currentValue.dropLast(1)
}

//Permite digitar pelo teclado
@JsName("digitar")
fun key(e: KeyboardEvent) {
    when {
        e.keyCode == 13 -> calculate()
        else -> typeValue(e.key)
    }
}

//Permite digitar pelos buttons da aplicação
@JsName("type")
fun type(e: Event) {
    val button = e.target as HTMLButtonElement
    val buttonText = button.innerText
    typeValue(buttonText)
}

//Digita o valor inserido, caso o mesmo seja aprovado
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

// Obtem o valor da expressão ao quadrado, resolvendo-a antes de à calcular
@JsName("pow")
fun pow() {
    val inputField = getInputElement() //Obtem o elemento HTML com a expressão
    
    try {
        val expressionTree = expression2Tree(inputField.value) //Obtem a arvore de expressão
        val result = expressionTree?.resolver() //Resolve a expressão e armazena o valor em "result"
    
        if (result != null)
            inputField.value = (result * result).toString() //Calcula o resultado ao quadrado e transforma em string
    } catch (err: Exception) {    
        inputField.addClass("error") //Caso ocorra algum erro, muda a classe do elemento para indicar o erro ao usuario
    }    
}    

// Obtem a rais quadrada da expressão, resolvendo-a antes de à calcular
@JsName("squareRoot")
fun squareRoot() {
    val inputField = getInputElement()//Obtem o elemento HTML com a expressão

    try {
        val expressionTree = expression2Tree(inputField.value)//Obtem a arvore de expressão
        val result = expressionTree?.resolver()//Resolve a expressão e armazena o valor em "result"

        if (result != null)
            inputField.value = sqrt(result).toString()//Calcula a raiz do resultadoe e transforma em string
    } catch (err: Exception) {    
        inputField.addClass("error")//Caso ocorra algum erro, muda a classe do elemento para indicar o erro ao usuario
    }    
}    

@JsName("toBin")
fun toBin() {
    val inputField = getInputElement()//Obtem o elemento HTML com a expressão

    if (isBin(inputField.value)) {
        inputField.addClass("error") // Indica o erro trocando a cor do elemento
        window.alert("Esse numero já é um valor binario!") // Indica o erro especifico!
    } else {

        try {
            val expressionTree = expression2Tree(inputField.value)//Obtem a arvore de expressão
            val floatResult = expressionTree?.resolver()//Resolve a expressão e armazena o valor em "result"
            val result = floatResult.toString()// Passa o resultado para string
                    .split(".")// Divide a string do resultado, afim de tratar as casa decimais em separado
                    .joinToString { it.toInt().toString(2) } // Junta ambas partes em uma string unica, apois alterar o valor para binario
                    .replace(", ", ".")// Modifica o separador de "," para "."
            
            inputField.removeClass("error")// Remove o indicador de erro, caso o mesmo exista
            inputField.value = "!b" + result// Imprime o resultado com um marcador

        } catch (err: Exception) {
            inputField.addClass("error")//Caso ocorra algum erro, muda a classe do elemento para indicar o erro ao usuario
        }
    }
}

@JsName("toHex")
fun toHex() {
    val inputField = getInputElement()//Obtem o elemento HTML com a expressão

    if (isHex(inputField.value)) {
        inputField.addClass("error")// Indica o erro trocando a cor do elemento
        window.alert("Esse numero já é um valor hexadecimal!")// Indica o erro especifico!
    } else {

        try {
            val expressionTree = expression2Tree(inputField.value)//Obtem a arvore de expressão
            val floatResult = expressionTree?.resolver()//Resolve a expressão e armazena o valor em "result"
            val result = floatResult.toString()// Passa o resultado para string
                    .split(".")// Divide a string do resultado, afim de tratar as casa decimais em separado
                    .joinToString { it.toInt().toString(16) }// Junta ambas partes em uma string unica, apois alterar o valor para binario
                    .replace(", ", ".")// Modifica o separador de "," para "."
            
            inputField.removeClass("error")// Remove o indicador de erro, caso o mesmo exista
            inputField.value = "#" + result// Imprime o resultado com um marcador

        } catch (err: Exception) {
            inputField.addClass("error")//Caso ocorra algum erro, muda a classe do elemento para indicar o erro ao usuario
        }
    }
}

@JsName("calculate")
fun calculate() {

    val inputField = getInputElement()//Obtem o elemento HTML com a expressão
    
    try {
        val expressionTree = expression2Tree(inputField.value)//Obtem a arvore de expressão
        val result = expressionTree?.resolver()//Resolve a expressão e armazena o valor em "result"
        inputField.removeClass("error")// Remove o indicador de erro, caso o mesmo exista
        inputField.value = result.toString()//Transforma o resultado em string e o mostra em tela
    } catch (err: Exception) {
        inputField.addClass("error")//Caso ocorra algum erro, muda a classe do elemento para indicar o erro ao usuario
    }
}
