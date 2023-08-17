package com.texnar13.rollbotserverhost.rollbotlogic;

import java.util.Scanner;

public class TestClass {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//
//        // todo надо отделить логику бота от основного кода чтобы и этот код смотрелся красиво
//        String msg;
//        do {
//            System.out.println("Enter command:");
//
//            msg = scanner.nextLine();
//            if (msg.charAt(0) == 'd') {
//                // кинуть кость
//                rollCommand(msg);
//            } else if (msg.charAt(0) == 'r') {
//                // кинуть выражение
//                rollCommand(msg.substring(1));
//            }
//
//        } while (!msg.equals("exit"));
//    }
//
//    static void rollCommand(String command) {
//
//        // отдаем строку на обработку/расшифровку
//        Parser parser = new Parser(System.currentTimeMillis());
//        Parser.RollAnswer answer = parser.parseRollString(command);
//
//        // выводим итоги
//        switch (answer.errorPoz) {
//            case -1 ->// без ошибок
//                    System.out.println(
//                            "Rolled by test: " + answer.outputString + "  =  " +
//                                    answer.number + "\n" + StringConstants.getBigTextInt(answer.number)
//                    );
//            case -2 ->// в расчетах был деление на 0
//                    System.out.println(
//                            "В общем в расчетах получилось деление на 0, а я так делить не умею. \nНо ты можешь попробовать еще раз :)"
//                    );
//            case -3 ->// арифметическая ошибка
//                    System.out.println("какая-то арифметическая ошибка, Ваня срочно смотри логи..");
//            default ->// ошибки
//                    System.out.println("/r " +
//                            answer.trimmedString.substring(0, answer.errorPoz) +
//                            "<= Здесь ошибка"
//                    );
//        }
//
//    }
}
