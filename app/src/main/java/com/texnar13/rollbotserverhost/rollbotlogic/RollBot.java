package com.texnar13.rollbotserverhost.rollbotlogic;

import androidx.annotation.NonNull;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RollBot extends ListenerAdapter{
    // todo сделать случайные приветствия!
    // todo стоит ограничение в 2000 символов, надо проверять и разбивать вывод на сообщения \о/

    // а прикинь потом сделать это на андроиде, на котлине с потоками в asyncTask,
    //   и с уведомлением в шторке, мол бот запущен и работой в фоне

    // todo все строки в StringConstants

    // TODO проверять канал для кидания костей по названию, "Кидальня костей"
    //  возможно чтобы название задавалось в настройках приложения админом, нет

    // рандомайзер
    Random randomizer = new Random(System.currentTimeMillis());
    // время запуска бота
    static long startTimeMillis;


    // метод запуска бота RollBot#0469
    public static void main(String[] args) {
        // builder аккаунта бота
        JDABuilder builder = JDABuilder
                // идентификатор бота
                .createDefault("test token not valid")
                // выставляем статус бота онлайн
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("DnD"))
                // выставляем разрешения боту
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                // добавляем экземпляр этого класса в качестве обработчика сообщений
                .addEventListeners(new RollBot());
        // логинимся в дискорде в аккаунт бота
        builder.build();

        System.out.println(StringConstants.TAG + ":Log in success!");

        // считываем время начала работы бота
        startTimeMillis = System.currentTimeMillis();

    }

    // отработает 1 раз когда бот проснулся и подключился
    @Override
    public void onReady(@NonNull ReadyEvent event) {
        super.onReady(event);
        System.out.println(StringConstants.TAG + ":Connected!");

    }

    // метод отрабатывающий при получении сообщения
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // получаем текст сообщения
        String msg = event.getMessage().getContentDisplay();

        // выводим логи
        System.out.println("-----" + msg);// красивый разделитель
        if (event.isFromType(ChannelType.PRIVATE)) {
            // выводим сообщение в лог
            System.out.printf(StringConstants.TAG + ":[Private] %s: %s\n",
                    event.getAuthor().getName(),
                    msg
            );
        } else {
            System.out.printf(
                    StringConstants.TAG + ":[Server][%s][%s] %s(%s): %s\n",
                    event.getGuild().getName(), // гильдия (сервер)
                    event.getChannel().asTextChannel().getName(), // канал
                    Objects.requireNonNull(event.getMember()).getEffectiveName(),// никнейм специфичный для данной гильдии написавшего
                    event.getAuthor().getName(), // имя написавшего
                    msg // сообщение
            );
        }

        // отфильтровываем сообщения от самого бота и пустые сообщения
        if (event.getAuthor().isBot()) return;
        if (msg.length() <= 2) return;
        if (msg.charAt(0) != '/' && msg.charAt(0) != '.') return;

        // todo сделать получение гильдии только в одном месте? getGuildFromListById
        if (event.isFromType(ChannelType.PRIVATE)) {// личное сообщение
            privateChatCommand(event, msg.substring(1));
        } else {// сообщение с сервера
            serverChatCommand(event, msg.substring(1));
        }
    }

    // команды в личном чате
    private void privateChatCommand(MessageReceivedEvent event, String msg) {
        if (msg.equals("help")) {
            // помощь
            event.getChannel().sendMessage(StringConstants.helpUTF8Message).queue();
            return;
        }

        if (msg.equals("exit")) {
            // команда выхода
            exitCommand(event);
        }

        msg = clearRollFromFuckingRussianLetters(msg);
        if (msg.charAt(0) == 'd') {
            // кинуть кость
            rollCommand(event, msg);
        } else if (msg.charAt(0) == 'r') {
            // кинуть выражение
            rollCommand(event, msg.substring(1));
        }
    }

    // команды на сервере
    private void serverChatCommand(MessageReceivedEvent event, String msg) {

        // отфильтровываем сообщения только в нужном канале (если такая настройка стоит)
        if (event.getChannel().getName().equals("кидальня-костей")) {

            switch (msg) {
                case "help" ->// помощь
                        event.getChannel().sendMessage(StringConstants.serverHelpUTF8Message).queue();
                case "time" -> {// выводим время работы бота
                    long millis = (System.currentTimeMillis() - startTimeMillis) % 1000;
                    long second = ((System.currentTimeMillis() - startTimeMillis) / 1000) % 60;
                    long minute = ((System.currentTimeMillis() - startTimeMillis) / (1000 * 60)) % 60;
                    long hour = ((System.currentTimeMillis() - startTimeMillis) / (1000 * 60 * 60)) % 24;
                    sendMessageInEventChannel(event,
                            "Время работы: "
                                    + String.format("%02dh %02dm %02d.%ds", hour, minute, second, millis)
                    );
                }
                case "debug" ->// отладочный
                        debugCommand(event);
                case "exit" -> // команда выхода
                        exitCommand(event);

                // назначение главной кости
                default -> {
                    msg = clearRollFromFuckingRussianLetters(msg);
                    if (msg.charAt(0) == 'd') {
                        // кинуть кость
                        rollCommand(event, msg);
                    } else if (msg.charAt(0) == 'r') {
                        // кинуть выражение
                        rollCommand(event, msg.substring(1));
                    }
                }

            }
        }
    }


    private String clearRollFromFuckingRussianLetters(String rollString) {
        // получаем волшебные символы для проверок, из кодировки windows-1251
        // todo все исправил, просто убери этот код


        // переводим все в нижний регистр
        StringBuilder builder = new StringBuilder(rollString.toLowerCase());

        // меняем ошибки раскладки в выражении
        if (rollString.charAt(0) == 'к' || rollString.charAt(0) == 'р')
            builder.setCharAt(0, 'r');

        // все варианты обозначения кости
        for (int symbolI = 0; symbolI < builder.length(); symbolI++)
            if (builder.charAt(symbolI) == 'к' || builder.charAt(symbolI) == 'в' || builder.charAt(symbolI) == 'д')
                builder.setCharAt(symbolI, 'd');

        return builder.toString();
    }

    // ========================================= команды =========================================


    void debugCommand(MessageReceivedEvent event) {
        StringBuilder answer = new StringBuilder("Сервер: ")
                .append(event.getGuild().getName())
                .append(" (")
                .append(event.getGuild().getIdLong())
                .append(")\nУчастники онлайн:");

        List<Member> members = event.getGuild().getMembers();
        for (Member member : members) {
            answer.append("\n\t ")
                    .append(member.getEffectiveName())
                    .append(" (")
                    .append(member.getIdLong())
                    .append(')');
        }
        sendMessageInEventChannel(event, answer.toString());
    }

    void rollCommand(MessageReceivedEvent event, String command) {

        // отдаем строку на обработку/расшифровку
        Parser parser = new Parser(System.currentTimeMillis());
        Parser.RollAnswer answer = parser.parseRollString(command);

        // выводим итоги
        switch (answer.errorPoz) {
            case -1: {// без ошибок
                sendMessageInEventChannel(event,
                        "Rolled by " + getAuthorName(event) + ": " + answer.outputString + "  =  " +
                                answer.number + "\n" + StringConstants.getBigTextInt(answer.number)
                );
                break;
            }
            case -2: {// в расчетах был деление на 0
                sendMessageInEventChannel(event,
                        "В общем в расчетах получилось деление на 0, а я так делить не умею. \nНо ты можешь попробовать еще раз :)"
                );
                break;
            }
            case -3: {// арифметическая ошибка
                sendMessageInEventChannel(event, "какая-то арифметическая ошибка, Ваня срочно смотри логи..");
                break;
            }
            default:// ошибки
                sendMessageInEventChannel(event, "/r " +
                        command.replaceAll(" ", "").substring(0, answer.errorPoz) +
                        "<= Здесь ошибка"
                );
        }

        // реакция бота и счетчик
        // addReactionAndCounter(event, answer.numberOfOnes, answer.numberOfTwenties);
    }

    void exitCommand(MessageReceivedEvent event) {
        // выводим время работы бота
        long millis = (System.currentTimeMillis() - startTimeMillis) % 1000;
        long second = ((System.currentTimeMillis() - startTimeMillis) / 1000) % 60;
        long minute = ((System.currentTimeMillis() - startTimeMillis) / (1000 * 60)) % 60;
        long hour = ((System.currentTimeMillis() - startTimeMillis) / (1000 * 60 * 60)) % 24;

        // прощаемся
        sendMessageInEventChannel(event,
                "Bye, bye " + getAuthorName(event) + ".. Working time: "
                        + String.format("%02dh %02dm %02d.%ds", hour, minute, second, millis)
        );

        // задаем оффлайн статус
        event.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);

        // спим 2 секунды (чтобы все успело отправиться)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Эта команда мягко говорит закрыться всем внутренним процессам дискорда,
        //  после закрытия сама программа будет работать еще секунд 10
        //  есть еще более агрессивная shutdownNow(), которая работает чуть быстрее
        //  и не рекомендую использовать System.exit(0);
        event.getJDA().shutdown();
    }

    // =================================== вспомогательные методы ===================================


    String getAuthorName(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {// личное сообщение
            return event.getAuthor().getName();
        } else {// сообщение с сервера (обращаемся по нику, а не по имени)
            return Objects.requireNonNull(event.getMember()).getEffectiveName();
        }
    }


    // ================= новое от 20.06.2021 ===========================================================================

    void sendMessageInEventChannel(MessageReceivedEvent event, String message) {
        event.getChannel().sendMessage(message).queue();
    }

}


// ======================= helpful code: ============================
// event.getAuthor().getName() + event.getAuthor().getDiscriminator()
//event.getMessage().addReaction("\uD83C\uDFB2").queue();//":game_die:"

