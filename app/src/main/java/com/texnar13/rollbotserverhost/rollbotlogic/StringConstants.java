package com.texnar13.rollbotserverhost.rollbotlogic;

public class StringConstants {


    // тег бота
    public static final String TAG = "RollBot";

    // текстовые цифры для вывода
    public static final String[][] NUMBERS_CODES = {
            {//0
                    "░███░",
                    "█░░░█",
                    "█░░░█",
                    "█░░░█",
                    "░███░"
            },
            {//1
                    "░░█░░",
                    "░██░░",
                    "░░█░░",
                    "░░█░░",
                    "░███░"
            },
            {//2
                    "░███░",
                    "█░░░█",
                    "░░░█░",
                    "░█░░░",
                    "█████"
            },
            {//3
                    "░███░",
                    "░░░░█",
                    "░░██░",
                    "░░░░█",
                    "░███░"
            },
            {//4
                    "█░░░█",
                    "█░░░█",
                    "█████",
                    "░░░░█",
                    "░░░░█"
            },
            {//5
                    "█████",
                    "█░░░░",
                    "████░",
                    "░░░░█",
                    "████░"
            },
            {//6
                    "░███░",
                    "█░░░░",
                    "████░",
                    "█░░░█",
                    "░███░"
            },
            {//7
                    "█████",
                    "░░░█░",
                    "░░█░░",
                    "░█░░░",
                    "░█░░░"
            },
            {//8
                    "░███░",
                    "█░░░█",
                    "░███░",
                    "█░░░█",
                    "░███░"
            },
            {//9
                    "░███░",
                    "█░░░█",
                    "░████",
                    "░░░░█",
                    "░███░"
            },
            {//-
                    "░░░░░",
                    "░░░░░",
                    "░███░",
                    "░░░░░",
                    "░░░░░"
            }
    };

    static final String helpUTF8Message = "Привет, я " + TAG +
            "\n Вот список доступных команд:" +
            "\n\t\t/dN (d10, d3, d100..) - кинуть кость и вывести получившееся значение (от 1 до N);" +

            // todo  r - начало сложного выражения (любое выражение начинается с r)
            //  d - кинуть кость


            "\n\t\t/r (2d2 * 2) + d2 - 5  - кинуть кости, посчитать выражение и вывести ответ" +
            "\n\t\t\t(Поддерживаются + * / - () dN KdN );" +
            "\n\t\t/help - показ этого сообщения;" +
            "\n\t\t/exit - завершение работы бота;";

    static final String serverHelpUTF8Message = "Привет, я " + TAG +
            "\n Вот список доступных команд:" +
            "\n\t\t/dN (d10, d3, d100..) - кинуть кость и вывести получившееся значение (от 1 до N);" +
            "\n\t\t/r 2d2 * 2 + d2 - 5  - кинуть кости, посчитать выражение и вывести ответ" +
            "\n\t\t\t(Поддерживаются + * / - () dN KdN );" +
            "\n\t\t/stat - количество двадцаток и единиц за текущую игру;" +
            "\n\t\t/help - показ этого сообщения;" +
            "\n\t\t/bind - назначить канал для костей, писать в нужном канале;" +
            "\n\t\t/md 24 - назначить главную кость, по которой учитывается статистика;" +
            "\n\t\t/exit - завершение работы бота;";

    static final String inputErrorMessage = "Вы ввели некорректное выражение для броска! Читайте /help";




    public static StringBuilder getBigTextInt(int n) {
        // создаем строку вывода
        StringBuilder answer = new StringBuilder();
        // выводим цифры
        if (n != 0) {
            // ставим отрицательный знак если он есть
            boolean isNegative = false;
            if (n < 0) {
                isNegative = true;
                n = -n;
            }
            // разбиваем число на цифры
            int[] numbers = new int[String.valueOf(n).length()];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = n % 10;
                n /= 10;
            }
            // выводим пять строк
            for (int linesIterator = 0; linesIterator < 5; linesIterator++) {
                // выводим отрицательный знак, если он есть
                if (isNegative) {
                    answer.append(StringConstants.NUMBERS_CODES[10][linesIterator]).append("  ");
                }
                // выводим число наоборот
                for (int numbersIterator = numbers.length - 1; numbersIterator >= 0; numbersIterator--) {
                    answer.append(StringConstants.NUMBERS_CODES[numbers[numbersIterator]][linesIterator]).append("  ");
                }
                // завершаем строку
                answer.append("\n");
            }
        } else {
            // выводим пять строк нуля
            for (int linesIterator = 0; linesIterator < 5; linesIterator++) {
                answer.append(StringConstants.NUMBERS_CODES[0][linesIterator]).append("  ").append("\n");
            }
        }

        // возвращаем результат
        return answer;
    }

}
