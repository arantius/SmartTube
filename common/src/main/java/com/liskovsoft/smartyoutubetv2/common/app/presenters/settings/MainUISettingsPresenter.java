package com.liskovsoft.smartyoutubetv2.common.app.presenters.settings;

import android.content.Context;
import com.liskovsoft.sharedutils.helpers.MessageHelpers;
import com.liskovsoft.smartyoutubetv2.common.R;
import com.liskovsoft.smartyoutubetv2.common.app.models.playback.ui.OptionItem;
import com.liskovsoft.smartyoutubetv2.common.app.models.playback.ui.UiOptionItem;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.AppSettingsPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.BrowsePresenter;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.base.BasePresenter;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData.ColorScheme;

import java.util.ArrayList;
import java.util.List;

public class MainUISettingsPresenter extends BasePresenter<Void> {
    private final MainUIData mMainUIData;
    private boolean mRestartApp;

    public MainUISettingsPresenter(Context context) {
        super(context);
        mMainUIData = MainUIData.instance(context);
    }

    public static MainUISettingsPresenter instance(Context context) {
        return new MainUISettingsPresenter(context);
    }

    public void show() {
        AppSettingsPresenter settingsPresenter = AppSettingsPresenter.instance(getContext());
        settingsPresenter.clear();

        appendColorScheme(settingsPresenter);
        appendCardsStyle(settingsPresenter);
        //appendCardTitleLines(settingsPresenter);
        appendChannelSortingCategory(settingsPresenter);
        appendPlaylistsStyle(settingsPresenter);

        settingsPresenter.showDialog(getContext().getString(R.string.dialog_main_ui), () -> {
            if (mRestartApp) {
                mRestartApp = false;
                MessageHelpers.showLongMessage(getContext(), R.string.msg_restart_app);
            }
        });
    }

    private void appendCardsStyle(AppSettingsPresenter settingsPresenter) {
        List<OptionItem> options = new ArrayList<>();

        OptionItem animatedPreviewsOption = UiOptionItem.from(getContext().getString(R.string.card_animated_previews),
                option -> mMainUIData.enableCardAnimatedPreviews(option.isSelected()), mMainUIData.isCardAnimatedPreviewsEnabled());

        OptionItem multilineTitle = UiOptionItem.from(getContext().getString(R.string.card_multiline_title),
                option -> mMainUIData.enableCardMultilineTitle(option.isSelected()), mMainUIData.isCardMultilineTitleEnabled());

        OptionItem autoScrolledTitle = UiOptionItem.from(getContext().getString(R.string.card_auto_scrolled_title),
                option -> mMainUIData.enableCardTextAutoScroll(option.isSelected()), mMainUIData.isCardTextAutoScrollEnabled());

        options.add(animatedPreviewsOption);
        options.add(multilineTitle);
        options.add(autoScrolledTitle);

        settingsPresenter.appendCheckedCategory(getContext().getString(R.string.cards_style), options);
    }

    private void appendCardTitleLines(AppSettingsPresenter settingsPresenter) {
        List<OptionItem> options = new ArrayList<>();

        for (int linesNum : new int[] {1, 2, 3, 4}) {
            options.add(UiOptionItem.from(String.format("%s", linesNum),
                    optionItem -> mMainUIData.setCartTitleLinesNum(linesNum),
                    linesNum == mMainUIData.getCardTitleLinesNum()));
        }

        settingsPresenter.appendRadioCategory(getContext().getString(R.string.card_title_lines_num), options);
    }

    private void appendChannelSortingCategory(AppSettingsPresenter settingsPresenter) {
        List<OptionItem> options = new ArrayList<>();

        for (int[] pair : new int[][] {
                {R.string.sorting_by_new_content, MainUIData.CHANNEL_SORTING_UPDATE},
                {R.string.sorting_alphabetically, MainUIData.CHANNEL_SORTING_AZ},
                {R.string.sorting_last_viewed, MainUIData.CHANNEL_SORTING_LAST_VIEWED}}) {
            options.add(UiOptionItem.from(getContext().getString(pair[0]), optionItem -> {
                mMainUIData.setChannelCategorySorting(pair[1]);
                BrowsePresenter.instance(getContext()).updateChannelCategorySorting();
            }, mMainUIData.getChannelCategorySorting() == pair[1]));
        }

        settingsPresenter.appendRadioCategory(getContext().getString(R.string.channel_category_sorting), options);
    }

    private void appendPlaylistsStyle(AppSettingsPresenter settingsPresenter) {
        List<OptionItem> options = new ArrayList<>();

        for (int[] pair : new int[][] {
                {R.string.playlists_style_grid, MainUIData.PLAYLISTS_STYLE_GRID},
                {R.string.playlists_style_rows, MainUIData.PLAYLISTS_STYLE_ROWS}}) {
            options.add(UiOptionItem.from(getContext().getString(pair[0]), optionItem -> {
                mMainUIData.setPlaylistsStyle(pair[1]);
                BrowsePresenter.instance(getContext()).updatePlaylistsStyle();
            }, mMainUIData.getPlaylistsStyle() == pair[1]));
        }

        settingsPresenter.appendRadioCategory(getContext().getString(R.string.playlists_style), options);
    }

    private void appendColorScheme(AppSettingsPresenter settingsPresenter) {
        List<ColorScheme> colorSchemes = mMainUIData.getColorSchemes();

        settingsPresenter.appendRadioCategory(getContext().getString(R.string.color_scheme), fromColorSchemes(colorSchemes));
    }

    private List<OptionItem> fromColorSchemes(List<ColorScheme> colorSchemes) {
        List<OptionItem> styleOptions = new ArrayList<>();

        for (ColorScheme colorScheme : colorSchemes) {
            styleOptions.add(UiOptionItem.from(
                    getContext().getString(colorScheme.nameResId),
                    option -> {
                        mMainUIData.setColorScheme(colorScheme);
                        mRestartApp = true;
                    },
                    colorScheme.equals(mMainUIData.getColorScheme())));
        }

        return styleOptions;
    }
}
