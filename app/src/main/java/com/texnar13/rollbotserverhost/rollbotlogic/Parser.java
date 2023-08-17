package com.texnar13.rollbotserverhost.rollbotlogic;

import java.util.Random;

public class Parser {

    Random randomizer;
    int errorPoz;

    String stringToParse;
    int parsedSymbolsCount;


    public Parser(long seed) {
        randomizer = new Random(seed);
    }

    /*
     * Пример работы:
     *
     * r 4r6d10
     *
     * Rolled by test: 4r=( [6d10]=(<9> + <9> + <6> + <6> + <4> + <4>) =38 +
     * [6d10]=(<4> + <4> + <9> + <3> + <5> + <3>) =28 +  [6d10]=(<1> + <6> + <7> + <2> + <10> + <10>) =36 +
     * [6d10]=(<8> + <5> + <2> + <3> + <5> + <7>) =30)  =  132
     *
     *
     * */



    //todo проверка: не слишком ли большое число
    public RollAnswer parseRollString(String rollString) {

        // задаем исходную строку,
        this.stringToParse = rollString
                .replaceAll(" ", "") // удаляем пробелы
                .replaceAll("%", "100-1") // для d%, которое выдает числа от 0 до 99 (mothership)
                .toLowerCase();
        parsedSymbolsCount = 0;
        // задаем начальную ошибку
        errorPoz = -1;

        // объект ответа
        RollAnswer returnAnswer = new RollAnswer();
        returnAnswer.trimmedString = stringToParse;

        try {
            // заполняем его
            ReturnObject returnObject = sumCheck();

            returnAnswer.number = returnObject.numberValue;
            returnAnswer.outputString = returnObject.stringValue;
            returnAnswer.errorPoz = errorPoz;

            // парсинг мог просто не дойти до конца - значит в веденной строке ошибка
            if (parsedSymbolsCount != stringToParse.length()) {
                returnAnswer.errorPoz = parsedSymbolsCount;
            }

        } catch (ArithmeticException e) {
            if (e.getMessage().equals("/ by zero")) {
                returnAnswer.errorPoz = -2;
            } else {
                returnAnswer.errorPoz = 0;
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            returnAnswer.errorPoz = -3;// на случай не учтенных ошибок
        }
        // возвращаем
        return returnAnswer;
    }


    // =========================================== методы парсинга выражений ===========================================
    // операции по приоритетам
    //   +   *   r   d  ( )  20
    //  (5) (4) (3) (2) (1) (0)

    /* ищем слагаемые в строке
        один из тройки, приоритет 5
        вызывается первым

                ++++++
                ++++++
                ++++++
          ++++++++++++++++++   ------------------
          ++++++++++++++++++   ------------------
          ++++++++++++++++++   ------------------
                ++++++
                ++++++
                ++++++
    */
    private ReturnObject sumCheck() {
        ReturnObject returnAnswer = new ReturnObject();

        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return returnAnswer;
        }

        // считываем минус в начале числа, если он есть
        boolean minus = stringToParse.charAt(parsedSymbolsCount) == '-';
        if (minus) {
            parsedSymbolsCount++;
            returnAnswer.stringValue.append(" - ");
        }

        // todo по идее можно пропускать следующую проверку если minus, и переносить ее в цикл ниже

        // сразу отдаем на проверку
        ReturnObject multiplyResult = multiplyCheck();
        // добавляем результаты проверки к текущим
        returnAnswer.numberValue = ((minus) ? (-multiplyResult.numberValue) : (multiplyResult.numberValue));
        returnAnswer.stringValue.append(multiplyResult.stringValue);


        // если возможно идем дальше, проверяя на знаки + -
        while (parsedSymbolsCount != stringToParse.length() && errorPoz == -1) {

            // если после предыдущей проверки строка заканчивается на символ отличный от искомого,
            //   значит последовательность операторов закончилась и дальше идет оператор находящийся выше по иерархии
            if (stringToParse.charAt(parsedSymbolsCount) != '+' &&
                    stringToParse.charAt(parsedSymbolsCount) != '-')
                break;

            boolean isPlus = stringToParse.charAt(parsedSymbolsCount) == '+';
            // нашли знак, отмечаем, переходим дальше
            returnAnswer.stringValue.append((isPlus) ? (" + ") : (" - "));
            parsedSymbolsCount++;

            // раз есть знак, идем дальше
            ReturnObject result = multiplyCheck();
            returnAnswer.stringValue.append(result.stringValue);
            if (isPlus)
                returnAnswer.numberValue += result.numberValue;
            else
                returnAnswer.numberValue -= result.numberValue;
        }
        return returnAnswer;
    }

    /* ищем множители в строке
        один из тройки, приоритет 4

                                        //////
                                       //////
                ******                //////
              **********             //////
              **********            //////
              **********           //////
                ******            //////
                                 //////
                                //////
    */
    private ReturnObject multiplyCheck() {
        ReturnObject returnAnswer = new ReturnObject();

        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return returnAnswer;
        }

        {// сразу отдаем на проверку
            ReturnObject result = multiRollCheck();
            // добавляем результаты проверки к текущим
            returnAnswer.numberValue = result.numberValue;
            returnAnswer.stringValue.append(result.stringValue);
        }

        // если возможно идем дальше, проверяя на знаки * /
        while (parsedSymbolsCount != stringToParse.length() && errorPoz == -1) {

            // если после предыдущей проверки строка заканчивается на символ отличный от искомого,
            //   значит последовательность операторов закончилась и дальше идет оператор находящийся выше по иерархии
            if (stringToParse.charAt(parsedSymbolsCount) != '*' &&
                    stringToParse.charAt(parsedSymbolsCount) != '/')
                break;

            boolean isMultiply = stringToParse.charAt(parsedSymbolsCount) == '*';
            // нашли знак, отмечаем, переходим дальше
            returnAnswer.stringValue.append((isMultiply) ? (" * ") : (" / "));
            parsedSymbolsCount++;

            // раз есть знак, идем дальше
            ReturnObject result = multiRollCheck();
            returnAnswer.stringValue.append(result.stringValue);
            if (isMultiply)
                returnAnswer.numberValue *= result.numberValue;
            else
                returnAnswer.numberValue /= result.numberValue;
        }
        return returnAnswer;
    }


    /* ищем "мультироллы" в строке (бросает выражение несколько раз с новыми костями)
               (рольнуть одно и то же выражение заданное число раз, сложить и выводить промежуточный результат)
        , приоритет 3

          RRRRRRRRRRRRRR
          RRRRRRRRRRRRRRRRRR
          RRRRRR      RRRRRR
          RRRRRR      RRRRRR
          RRRRRRRRRRRRRRRRRR
          RRRRRRRRRRRRRRRRRR
          RRRRRR       RRRRRR
          RRRRRR        RRRRRR
          RRRRRR         RRRRRR
    */
    private ReturnObject multiRollCheck() {

        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return new ReturnObject();
        }

        // если перед r нет оператора количества
        ReturnObject rollsCount;
        if (stringToParse.charAt(parsedSymbolsCount) != 'r') {
            // сразу отдаем на проверку
            rollsCount = diceCheck();
        } else {
            // количество роллов ставим 0, тк строка пустая
            rollsCount = new ReturnObject();
        }

        // если возможно идем дальше, проверяя на знак r
        if (parsedSymbolsCount != stringToParse.length() && errorPoz == -1)
            if (stringToParse.charAt(parsedSymbolsCount) == 'r') {

                // обрабатываем найденный символ
                parsedSymbolsCount++;


                // переменная в которую будем суммировать рероллы
                ReturnObject multiRollSumma = new ReturnObject();


                // проверяем что количество рероллов != 0
                if (rollsCount.numberValue == 0 && rollsCount.stringValue.length() != 0) {
                    // если при расчетах количества рероллов вышел 0 (не пустое место, а именно 0)
                    multiRollSumma.numberValue = 0;
                    multiRollSumma.stringValue.append('0');

                } else {

                    // если перед r пустое место, то кидаем 1 раз
                    if(rollsCount.numberValue == 0){
                        rollsCount.numberValue = 1;
                    }

                    // проверяем что количество рероллов > 0
                    boolean isNegative = rollsCount.numberValue < 0;
                    int rollsCountModule = (isNegative) ? (-rollsCount.numberValue) : (rollsCount.numberValue);

                    // заголовок реролла
                    if (isNegative) multiRollSumma.stringValue.append('-');
                    multiRollSumma.stringValue
                            .append(rollsCount.stringValue)
                            .append("r=(");

                    // замораживаем количество обработанных символов
                    int freezeParsedSymbolsCount = parsedSymbolsCount;

                    // в цикле повторяем обработку заданное в rollsCount количество раз
                    for (int multirolI = 0; multirolI < rollsCountModule; multirolI++) {

                        // сбрасываем количество обработанных символов
                        parsedSymbolsCount = freezeParsedSymbolsCount;

                        // делаем итерацию просчетов
                        ReturnObject multiRollIteration = multiRollCheck();

                        // добавляем к общей сумме
                        multiRollSumma.numberValue += multiRollIteration.numberValue;
                        // записываем все в красивую строку
                        if (multirolI != 0) multiRollSumma.stringValue.append(" + ");
                        multiRollSumma.stringValue
                                .append(multiRollIteration.stringValue)
                                .append("=")
                                .append(multiRollIteration.numberValue);

                    }
                    multiRollSumma.stringValue.append(")");

                    // переворачиваем значение реролла тк количество рероллов отрицательное
                    if (isNegative) {
                        multiRollSumma.numberValue = -multiRollSumma.numberValue;
                    }
                }

                return multiRollSumma;
            }

        return rollsCount;
    }

    /* ищем кости в строке
        один из новой двойки, приоритет 2

          DDDDDDDDDDDD
          DDDDDDDDDDDDDDDDDD
          DDDDDD      DDDDDD
          DDDDDD      DDDDDD
          DDDDDD      DDDDDD
          DDDDDD      DDDDDD
          DDDDDD      DDDDDD
          DDDDDDDDDDDDDDDDDD
          DDDDDDDDDDDD
    */
    // ищем кости в строке
    //  один из новой двойки, приоритет 2
    private ReturnObject diceCheck() {

        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return new ReturnObject();
        }

        // если перед костью нет оператора количества
        ReturnObject diceCount;
        if (stringToParse.charAt(parsedSymbolsCount) != 'd') {
            // сразу отдаем на проверку
            diceCount = bracketsOrNumberCheck();
        } else {
            // количество костей ставим 0, тк строка пустая
            diceCount = new ReturnObject();
        }

        // если возможно идем дальше, проверяя на знак d
        if (parsedSymbolsCount != stringToParse.length() && errorPoz == -1)
            if (stringToParse.charAt(parsedSymbolsCount) == 'd') {

                // обрабатываем найденный символ
                parsedSymbolsCount++;

                // обрабатываем число после d
                ReturnObject diceValue = diceCheck();

                // переменная в которую будем суммировать дайсы
                ReturnObject diceSumma = new ReturnObject();

                // если отрицательное число в количестве костей или в номинале кости
                boolean isMinus = false;
                if (diceCount.numberValue < 0) {
                    isMinus = true;
                    diceCount.numberValue = -diceCount.numberValue;
                }
                if (diceValue.numberValue < 0) {
                    isMinus = !isMinus;
                    diceValue.numberValue = -diceValue.numberValue;
                }

                // работаем со знаками значений кости
                if (isMinus) {
                    diceSumma.stringValue.append(" -");
                }

                // смотрим число перед костью
                if (diceCount.numberValue == 0 && diceCount.stringValue.length() != 0 || diceValue.numberValue == 0) {
                    // если при расчетах количества костей вышел 0 (не пустое место, а именно 0)
                    //  или сама кость нулевая
                    diceSumma.numberValue = 0;
                    diceSumma.stringValue.append('0');

                } else if (diceCount.numberValue == 0 || diceCount.numberValue == 1) {
                    // кидаем одну кость
                    diceSumma.numberValue = randomizer.nextInt(diceValue.numberValue) + 1;
                    diceSumma.stringValue.append(" [")
                            .append(diceCount.stringValue)
                            .append('d')
                            .append(diceValue.stringValue)
                            .append("]=<")
                            .append(diceSumma.numberValue)
                            .append("> ");

                } else {
                    // бросаем кость несколько раз
                    diceSumma.stringValue.append(" [")
                            .append(diceCount.stringValue)
                            .append('d')
                            .append(diceValue.stringValue)
                            .append("]=(");
                    for (int rollI = 0; rollI < diceCount.numberValue; rollI++) {

                        // кидаем одну из костей
                        int currentRoll = randomizer.nextInt(diceValue.numberValue) + 1;
                        // добавляем к общей сумме
                        diceSumma.numberValue += currentRoll;
                        // записываем все в красивую строку
                        if (rollI != 0) diceSumma.stringValue.append(" + ");
                        diceSumma.stringValue.append('<').append(currentRoll).append('>');

                    }
                    diceSumma.stringValue.append(") ");
                }

                // работаем со знаками значений кости
                if (isMinus) {
                    diceSumma.numberValue = -diceSumma.numberValue;
                }

                return diceSumma;
            }

        return diceCount;
    }


    /* ищем выражение в скобках или число в строке
        один из тройки, приоритет 1

             ((((((      ))))))
            ((((((        ))))))
           ((((((          ))))))
          ((((((            ))))))
          ((((((            ))))))
          ((((((            ))))))
           ((((((          ))))))
            ((((((        ))))))
             ((((((      ))))))
    */
    private ReturnObject bracketsOrNumberCheck() {
        ReturnObject returnAnswer = new ReturnObject();

        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return returnAnswer;
        }

        // конструкция в скобках
        if (stringToParse.charAt(parsedSymbolsCount) == '(') {
            // нашли скобку, обрабатываем
            parsedSymbolsCount++;
            returnAnswer.stringValue.append('(');

            // считаем выражение внутри скобок
            ReturnObject sum = sumCheck();
            returnAnswer.numberValue = sum.numberValue;
            returnAnswer.stringValue.append(sum.stringValue);

            // если ошибок нет
            if (errorPoz == -1) {

                // не пустая ли строка, тк еще должна быть закрывающая скобка
                if (parsedSymbolsCount == stringToParse.length()) {
                    errorPoz = parsedSymbolsCount;
                    return returnAnswer;
                }

                // предыдущий метод должен был дойти до закрывающей скобки
                if (stringToParse.charAt(parsedSymbolsCount) == ')') {
                    // нашли скобку, обрабатываем
                    parsedSymbolsCount++;
                    returnAnswer.stringValue.append(')');

                } else {
                    // если закрывающей скобки нет, это ошибка
                    errorPoz = parsedSymbolsCount;

                }
            }

            // возвращаем выражение со скобками
            return returnAnswer;

        } else {
            // просто множитель
            return parseSimpleUnsignedNumber();
        }
    }


    /* считываем беззнаковое число из строки
        приоритет 0

           222222      000000
           222222      000000
           222222      000000
           222222      000000
           222222      000000
           222222      000000
           222222      000000
           222222      000000
           222222      000000
    */
    private ReturnObject parseSimpleUnsignedNumber() {

        ReturnObject returnAnswer = new ReturnObject();
        // нулевая строка сразу нет
        if (parsedSymbolsCount == stringToParse.length()) {
            errorPoz = parsedSymbolsCount;
            return returnAnswer;
        }

        // считываем число
        int parsedNumber = 0;

        // пока встречаем цифры
        boolean digitFlag = true;
        // пока строка не пустая
        while (parsedSymbolsCount != stringToParse.length() && digitFlag) {

            // получаем символ
            char numberChar = stringToParse.charAt(parsedSymbolsCount);
            // проверяем что это цифра
            digitFlag = numberChar >= '0' && numberChar <= '9';
            if (digitFlag) {
                // добавляем цифру к числу
                parsedNumber = parsedNumber * 10 + (numberChar - '0');// [code 5] - [code 0] = 5
                // символ обработан
                parsedSymbolsCount++;
            }

        }

        // возвращаем считанное число
        returnAnswer.numberValue = parsedNumber;
        returnAnswer.stringValue.append(parsedNumber);
        return returnAnswer;
    }




    /*
     * todo ошибки
     *
     *  /r--1
     *  Texnar13:-0-1  =  -1
     *
     *  /r1**2
     *  Rolled by test: 1 * 0 * 2  =  0
     *
     * это можно решить в
     * parseSimpleUnsignedNumber
     * кидая ошибку, когда количество знаков в числе 0
     * */


    // возвращаемый из парсера ответ
    public static class RollAnswer {// результат кидания костей
        // численный ответ
        int number;
        // исходная текстовая запись выражения
        String trimmedString;

        // текстовая запись выражения которая сформируется в итоге
        StringBuilder outputString;

        // позиция ошибки
        int errorPoz;


        private RollAnswer() {
            number = 0;
            outputString = new StringBuilder();
            errorPoz = -1;
        }
    }


    // объект todo описание
    private static class ReturnObject {

        // Полученная сумма
        int numberValue;
        // Распознанная интерпретация выражения
        StringBuilder stringValue;

        public ReturnObject() {
            this.numberValue = 0;
            this.stringValue = new StringBuilder();
        }
    }

}
