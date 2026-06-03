package com.han.nomemo;

final class NotificationIconResolver {
    private NotificationIconResolver() {
    }

    static int forCategory(String categoryCode) {
        if (CategoryCatalog.CODE_LIFE_PICKUP.equals(categoryCode)) {
            return R.drawable.ic_nm_food_notification;
        }
        if (CategoryCatalog.CODE_LIFE_DELIVERY.equals(categoryCode)) {
            return R.drawable.ic_nm_package_notification;
        }
        if (CategoryCatalog.CODE_LIFE_CARD.equals(categoryCode)) {
            return R.drawable.ic_nm_card_notification;
        }
        if (CategoryCatalog.CODE_LIFE_TICKET.equals(categoryCode)) {
            return R.drawable.ic_nm_ticket_notification;
        }
        if (CategoryCatalog.CODE_WORK_TODO.equals(categoryCode)) {
            return R.drawable.ic_nm_todo_notification;
        }
        if (CategoryCatalog.CODE_WORK_SCHEDULE.equals(categoryCode)) {
            return R.drawable.ic_nm_schedule_notification;
        }
        return R.drawable.ic_nm_note_notification;
    }
}
