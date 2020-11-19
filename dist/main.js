if (typeof kotlin === 'undefined') {
  throw new Error("Error loading module 'main'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'main'.");
}var main = function (_, Kotlin) {
  'use strict';
  var replace = Kotlin.kotlin.text.replace_680rmw$;
  var equals = Kotlin.equals;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var contains = Kotlin.kotlin.text.contains_li3zpu$;
  var throwCCE = Kotlin.throwCCE;
  var split = Kotlin.kotlin.text.split_ip8yn$;
  var removeClass = Kotlin.kotlin.dom.removeClass_hhb33f$;
  var dropLast = Kotlin.kotlin.text.dropLast_6ic1pp$;
  var addClass = Kotlin.kotlin.dom.addClass_hhb33f$;
  var Exception = Kotlin.kotlin.Exception;
  var toString = Kotlin.toString;
  var toInt = Kotlin.kotlin.text.toInt_pdl1vz$;
  var toString_0 = Kotlin.kotlin.text.toString_dqglrj$;
  var joinToString = Kotlin.kotlin.collections.joinToString_fmv235$;
  var toDouble = Kotlin.kotlin.text.toDouble_pdl1vz$;
  var Regex_init = Kotlin.kotlin.text.Regex_init_61zpoe$;
  var Math_0 = Math;
  function ExprTree(value, left, right) {
    this.value = value;
    this.left = left;
    this.right = right;
  }
  ExprTree.prototype.resolver = function () {
    var tmp$;
    if (this.left == null) {
      tmp$ = toDouble(replace(this.value, '@', '-'));
    } else if (this.right == null) {
      tmp$ = toDouble(replace(this.value, '@', '-'));
    } else if (equals(this.value, '*'))
      tmp$ = this.left.times_ur3wz1$(this.right);
    else if (equals(this.value, '/'))
      tmp$ = this.left.div_ur3wz1$(this.right);
    else if (equals(this.value, '%'))
      tmp$ = this.left.rem_ur3wz1$(this.right);
    else if (equals(this.value, '+'))
      tmp$ = this.left.plus_ur3wz1$(this.right);
    else if (equals(this.value, '-'))
      tmp$ = this.left.minus_ur3wz1$(this.right);
    else {
      tmp$ = toDouble(replace(this.value, '@', '-'));
    }
    return tmp$;
  };
  ExprTree.prototype.plus_ur3wz1$ = function (b) {
    return this.resolver() + b.resolver();
  };
  ExprTree.prototype.minus_ur3wz1$ = function (b) {
    return this.resolver() - b.resolver();
  };
  ExprTree.prototype.times_ur3wz1$ = function (b) {
    return this.resolver() * b.resolver();
  };
  ExprTree.prototype.div_ur3wz1$ = function (b) {
    return this.resolver() / b.resolver();
  };
  ExprTree.prototype.rem_ur3wz1$ = function (b) {
    return this.resolver() % b.resolver();
  };
  ExprTree.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ExprTree',
    interfaces: []
  };
  ExprTree.prototype.component1 = function () {
    return this.value;
  };
  ExprTree.prototype.component2 = function () {
    return this.left;
  };
  ExprTree.prototype.component3 = function () {
    return this.right;
  };
  ExprTree.prototype.copy_mi988y$ = function (value, left, right) {
    return new ExprTree(value === void 0 ? this.value : value, left === void 0 ? this.left : left, right === void 0 ? this.right : right);
  };
  ExprTree.prototype.toString = function () {
    return 'ExprTree(value=' + Kotlin.toString(this.value) + (', left=' + Kotlin.toString(this.left)) + (', right=' + Kotlin.toString(this.right)) + ')';
  };
  ExprTree.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.value) | 0;
    result = result * 31 + Kotlin.hashCode(this.left) | 0;
    result = result * 31 + Kotlin.hashCode(this.right) | 0;
    return result;
  };
  ExprTree.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.value, other.value) && Kotlin.equals(this.left, other.left) && Kotlin.equals(this.right, other.right)))));
  };
  function isOperator(value, except) {
    if (except === void 0)
      except = '';
    return contains('/*+-.%', value) && !equals(value, except);
  }
  function isCalculable(expression) {
    return Regex_init('([/*-+%])').containsMatchIn_6bul2c$(expression);
  }
  function isHex(value) {
    return Regex_init('(\\#[\\d|a-f])').containsMatchIn_6bul2c$(value);
  }
  function isBin(value) {
    return Regex_init('(\\!b[0-1])').containsMatchIn_6bul2c$(value);
  }
  function getInputElement() {
    var tmp$;
    return Kotlin.isType(tmp$ = document.getElementById('expression-field'), HTMLInputElement) ? tmp$ : throwCCE();
  }
  function getCurrentValue() {
    return getInputElement().value;
  }
  function expression2Tree$choseOp(closure$operator, closure$expression) {
    return function () {
      var tmp$, tmp$_0;
      tmp$ = closure$operator;
      if (contains(closure$expression, tmp$))
        tmp$_0 = closure$operator;
      else
        switch (tmp$) {
          case '-':
            tmp$_0 = '+';
            break;
          case '+':
            tmp$_0 = '%';
            break;
          case '%':
            tmp$_0 = '/';
            break;
          case '/':
            tmp$_0 = '*';
            break;
          case '*':
            tmp$_0 = '';
            break;
          default:tmp$_0 = '';
            break;
        }
      return tmp$_0;
    };
  }
  function expression2Tree(expression, operator) {
    if (operator === void 0)
      operator = '-';
    var tmp$;
    var choseOp = expression2Tree$choseOp(operator, expression);
    var replacedExpression = Regex_init('^-').replace_x2uqeu$(expression, '@');
    var splicedExpression = split(replacedExpression, [operator], void 0, 2);
    var nextOp = choseOp();
    if (splicedExpression.size === 1)
      tmp$ = isCalculable(replacedExpression) ? expression2Tree(replacedExpression, nextOp) : new ExprTree(replacedExpression, null, null);
    else if (splicedExpression.size > 1)
      tmp$ = new ExprTree(operator, expression2Tree(splicedExpression.get_za3lpa$(0), nextOp), expression2Tree(splicedExpression.get_za3lpa$(1), nextOp));
    else
      tmp$ = null;
    return tmp$;
  }
  function clean() {
    var input = getInputElement();
    removeClass(input, ['error']);
    input.value = '';
  }
  function backspace() {
    var inputField = getInputElement();
    var currentValue = inputField.value;
    inputField.value = dropLast(currentValue, 1);
  }
  function key(e) {
    if (e.keyCode === 13)
      calculate();
    else if (e.keyCode === 8)
      backspace();
    else if (e.keyCode === 27)
      clean();
    else
      typeValue(e.key);
  }
  function type(e) {
    var tmp$;
    var button = Kotlin.isType(tmp$ = e.target, HTMLButtonElement) ? tmp$ : throwCCE();
    var buttonText = button.innerText;
    typeValue(buttonText);
  }
  function typeValue$isValidDigit(closure$inputField, closure$value) {
    return function () {
      var tmp$;
      var inputValue = closure$inputField.value;
      var startIndex = inputValue.length - 1 | 0;
      var lastIndex = inputValue.substring(startIndex);
      if (lastIndex.length === 0 && equals(closure$value, '-'))
        tmp$ = true;
      else if (isOperator(closure$value)) {
        tmp$ = (lastIndex.length > 0 && !isOperator(lastIndex));
      } else {
        tmp$ = !Regex_init('(\\D)').containsMatchIn_6bul2c$(closure$value);
      }
      return tmp$;
    };
  }
  function typeValue(value) {
    var inputField = getInputElement();
    var isValidDigit = typeValue$isValidDigit(inputField, value);
    if (isValidDigit())
      inputField.value = inputField.value + value;
  }
  function pow() {
    var inputField = getInputElement();
    try {
      var expressionTree = expression2Tree(inputField.value);
      var result = expressionTree != null ? expressionTree.resolver() : null;
      if (result != null)
        inputField.value = (result * result).toString();
    } catch (err) {
      if (Kotlin.isType(err, Exception)) {
        addClass(inputField, ['error']);
      } else
        throw err;
    }
  }
  function squareRoot() {
    var inputField = getInputElement();
    try {
      var expressionTree = expression2Tree(inputField.value);
      var result = expressionTree != null ? expressionTree.resolver() : null;
      if (result != null) {
        inputField.value = Math_0.sqrt(result).toString();
      }} catch (err) {
      if (Kotlin.isType(err, Exception)) {
        addClass(inputField, ['error']);
      } else
        throw err;
    }
  }
  function toBin$lambda(it) {
    return toString_0(toInt(it), 2);
  }
  function toBin() {
    var inputField = getInputElement();
    if (isBin(inputField.value)) {
      addClass(inputField, ['error']);
      window.alert('Esse numero j\xE1 \xE9 um valor binario!');
    } else {
      try {
        var expressionTree = expression2Tree(inputField.value);
        var floatResult = expressionTree != null ? expressionTree.resolver() : null;
        var result = replace(joinToString(split(toString(floatResult), ['.']), void 0, void 0, void 0, void 0, void 0, toBin$lambda), ', ', '.');
        removeClass(inputField, ['error']);
        inputField.value = '!b' + result;
      } catch (err) {
        if (Kotlin.isType(err, Exception)) {
          addClass(inputField, ['error']);
        } else
          throw err;
      }
    }
  }
  function toHex$lambda(it) {
    return toString_0(toInt(it), 16);
  }
  function toHex() {
    var inputField = getInputElement();
    if (isHex(inputField.value)) {
      addClass(inputField, ['error']);
      window.alert('Esse numero j\xE1 \xE9 um valor hexadecimal!');
    } else {
      try {
        var expressionTree = expression2Tree(inputField.value);
        var floatResult = expressionTree != null ? expressionTree.resolver() : null;
        var result = replace(joinToString(split(toString(floatResult), ['.']), void 0, void 0, void 0, void 0, void 0, toHex$lambda), ', ', '.');
        removeClass(inputField, ['error']);
        inputField.value = '#' + result;
      } catch (err) {
        if (Kotlin.isType(err, Exception)) {
          addClass(inputField, ['error']);
        } else
          throw err;
      }
    }
  }
  function calculate() {
    var inputField = getInputElement();
    try {
      var expressionTree = expression2Tree(inputField.value);
      var result = expressionTree != null ? expressionTree.resolver() : null;
      removeClass(inputField, ['error']);
      inputField.value = toString(result);
    } catch (err) {
      if (Kotlin.isType(err, Exception)) {
        addClass(inputField, ['error']);
      } else
        throw err;
    }
  }
  _.ExprTree = ExprTree;
  _.isOperator_puj7f4$ = isOperator;
  _.isCalculable_61zpoe$ = isCalculable;
  _.isHex_61zpoe$ = isHex;
  _.isBin_61zpoe$ = isBin;
  _.getInputElement = getInputElement;
  _.getCurrentValue = getCurrentValue;
  _.expression2Tree_puj7f4$ = expression2Tree;
  _.clean = clean;
  _.backspace = backspace;
  _.digitar = key;
  _.type = type;
  _.typeValue = typeValue;
  _.pow = pow;
  _.squareRoot = squareRoot;
  _.toBin = toBin;
  _.toHex = toHex;
  _.calculate = calculate;
  Kotlin.defineModule('main', _);
  return _;
}(typeof main === 'undefined' ? {} : main, kotlin);
