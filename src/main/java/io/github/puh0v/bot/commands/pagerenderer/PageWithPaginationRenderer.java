package io.github.puh0v.bot.commands.pagerenderer;


import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.config.youtubeproperties.YouTubeProperties;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageWithPaginationRenderer {
    private static final int PAGE_SIZE = 10;
    private final YouTubeProperties youTubeProperties;
    private final ListOfButtons listOfButtons;

    public PageWithPaginationRenderer(YouTubeProperties youTubeProperties, ListOfButtons listOfButtons) {
        this.youTubeProperties = youTubeProperties;
        this.listOfButtons = listOfButtons;
    }


    public TextAndPaginationButtonsContext getReadyPageContext(List<SubscriptionsEntity> listOfChannels, int initialPage, CommandNames fromCommand) {
        int totalPages = (listOfChannels.size() + PAGE_SIZE - 1)/PAGE_SIZE;
        String readyTextWithChannels = getPageWithChannels(listOfChannels, initialPage, totalPages, fromCommand);
        List<InlineKeyboardButton> inlineKeyboardButtons = getPaginationButtons(initialPage, totalPages);

        if (inlineKeyboardButtons.isEmpty()) {
            return new TextAndPaginationButtonsContext(readyTextWithChannels);
        } else {
            return new TextAndPaginationButtonsContext(readyTextWithChannels, inlineKeyboardButtons);
        }
    }


    private String getPageWithChannels(List<SubscriptionsEntity> listOfChannels, int numberOfPage, int totalPages, CommandNames fromCommand) {
        if (numberOfPage <= 0 || numberOfPage > totalPages) {
            return "\uD83D\uDE42 Такой страницы не существует";
        }
        List<List<SubscriptionsEntity>> allPages = getListOfPages(listOfChannels);
        return getListOfChannels(allPages, numberOfPage, fromCommand).toString();
    }


    private List<List<SubscriptionsEntity>> getListOfPages(List<SubscriptionsEntity> listOfChannels) {
        List<List<SubscriptionsEntity>> allPages = new ArrayList<>();
        List<SubscriptionsEntity> tempList = new ArrayList<>();

        for (int i = 0; i < listOfChannels.size(); i++) {
            tempList.add(listOfChannels.get(i));

            if ((tempList.size() == PAGE_SIZE) || (i == listOfChannels.size() - 1)) {
                allPages.add(new ArrayList<>(tempList));
                tempList.clear();
            }
        }
        return allPages;
    }


    private StringBuilder getListOfChannels(List<List<SubscriptionsEntity>> allPages, int numberOfPage, CommandNames fromCommand) {
        StringBuilder sb = new StringBuilder();

        if (numberOfPage == 1) {
            if (fromCommand == CommandNames.MY_SUBSCRIPTIONS) {
                sb.append("\uD83D\uDCCC Стримеры, которых вы отслеживаете \uD83D\uDCCC\n\n");
            } else if (fromCommand == CommandNames.DELETE_SUBSCRIPTION) {
                sb.append("\uD83D\uDC47\uD83C\uDFFB Отправьте номер канала из списка, который хотите удалить\n\n");
            }
        }

        List<SubscriptionsEntity> page = allPages.get(numberOfPage - 1);
        int num = (numberOfPage - 1) * PAGE_SIZE + 1;

        for(SubscriptionsEntity channel : page) {
            String channelTitle = channel.getChannel().getTitle();
            String channelHandle = channel.getChannel().getChannelHandle();
            String urlToChannel = "<a href=\"" + youTubeProperties.mainPageUrl() + "/" + channelHandle + "\">" + channelTitle + "</a>";

            if (fromCommand == CommandNames.MY_SUBSCRIPTIONS) {
                sb.append(urlToChannel).append("\n");
            } else if (fromCommand == CommandNames.DELETE_SUBSCRIPTION) {
                sb.append(num++).append(") ").append(urlToChannel).append("\n");
            }
        }
        return sb;
    }


    private List<InlineKeyboardButton> getPaginationButtons(int initialPage, int totalPages) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        if (totalPages == 1) {
            return buttons;

        } else if (initialPage == 1 && totalPages > 1) {
            InlineKeyboardButton nextPageButton = listOfButtons.getNextPageButton(initialPage);
            buttons.add(nextPageButton);

        } else if (initialPage > 1 && initialPage < totalPages) {
            InlineKeyboardButton previousPageButton = listOfButtons.getPreviousPageButton(initialPage);
            InlineKeyboardButton nextPageButton = listOfButtons.getNextPageButton(initialPage);
            buttons.add(previousPageButton);
            buttons.add(nextPageButton);

        } else if (initialPage == totalPages) {
            InlineKeyboardButton previousPageButton = listOfButtons.getPreviousPageButton(initialPage);
            buttons.add(previousPageButton);
        }
        return buttons;
    }
}
