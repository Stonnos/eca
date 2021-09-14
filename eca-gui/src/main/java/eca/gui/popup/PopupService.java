package eca.gui.popup;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import static eca.gui.service.TemplateService.getInfoMessageAsHtml;

/**
 * Application popups service.
 */
@Slf4j
public class PopupService {

    private static final int MAX_POPUPS_AT_TIME = 3;

    private static final long POPUP_VISIBILITY_TIME_MILLIS = 4000L;

    private static final int POPUP_MARGIN_LEFT = 275;
    private static final int POPUP_MARGIN_TOP = 70;

    private final PopupFactory popupFactory = new PopupFactory();

    private final ConcurrentLinkedDeque<PopupDescriptor> popups = new ConcurrentLinkedDeque<>();

    /**
     * Popup descriptor.
     */
    @Data
    @AllArgsConstructor
    public static class PopupDescriptor {

        /**
         * Popup component
         */
        private Popup popup;
        /**
         * Popup x value
         */
        private int x;
        /**
         * Popup y value
         */
        private int y;
    }

    /**
     * Shows popup with specified message.
     *
     * @param infoMessage - info message
     * @param component   - parent component
     */
    public void showInfoPopup(String infoMessage, Component component) {
        createAndAddPopupDescriptor(infoMessage, component).ifPresent(popupDescriptor -> new Thread(() -> {
            popupDescriptor.getPopup().show();
            try {
                Thread.sleep(POPUP_VISIBILITY_TIME_MILLIS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                popupDescriptor.getPopup().hide();
                popups.remove(popupDescriptor);
            }
        }).start());
    }

    private synchronized Optional<PopupDescriptor> createAndAddPopupDescriptor(String infoMessage,
                                                                               Component component) {
        if (popups.size() == MAX_POPUPS_AT_TIME) {
            return Optional.empty();
        }
        PopupDescriptor popupDescriptor = createInfoMessagePopup(infoMessage, component);
        popups.addLast(popupDescriptor);
        return Optional.of(popupDescriptor);
    }

    private int calculatePopupY(Component component) {
        if (popups.isEmpty()) {
            return component.getY() + POPUP_MARGIN_TOP;
        } else {
            PopupDescriptor last = popups.getLast();
            return last.getY() + 2 * POPUP_MARGIN_TOP;
        }
    }

    private PopupDescriptor createInfoMessagePopup(String message, Component component) {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(PanelBorderUtils.createEtchedBorder());
        JLabel messageLabel = new JLabel(getInfoMessageAsHtml(message));
        JButton closeButton = ButtonUtils.createCloseButton();
        infoPanel.add(messageLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));
        infoPanel.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        int x = component.getX() + component.getWidth() - POPUP_MARGIN_LEFT;
        int y = calculatePopupY(component);
        Popup popup = popupFactory.getPopup(component, infoPanel, x, y);
        PopupDescriptor popupDescriptor = new PopupDescriptor(popup, x, y);
        closeButton.addActionListener(evt -> {
            popup.hide();
            popups.remove(popupDescriptor);
        });
        return popupDescriptor;
    }
}
