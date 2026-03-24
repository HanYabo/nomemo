package com.han.nomemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CategoryCatalog {
    public static final String GROUP_LIFE = "LIFE";
    public static final String GROUP_WORK = "WORK";

    public static final String CODE_LIFE_PICKUP = "LIFE_PICKUP";
    public static final String CODE_LIFE_DELIVERY = "LIFE_DELIVERY";
    public static final String CODE_LIFE_CARD = "LIFE_CARD";
    public static final String CODE_LIFE_TICKET = "LIFE_TICKET";
    public static final String CODE_WORK_TODO = "WORK_TODO";
    public static final String CODE_WORK_SCHEDULE = "WORK_SCHEDULE";

    public static final class CategoryOption {
        public final String groupCode;
        public final String categoryCode;
        public final String categoryName;

        public CategoryOption(String groupCode, String categoryCode, String categoryName) {
            this.groupCode = groupCode;
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
        }
    }

    private static final List<CategoryOption> LIFE_CATEGORIES;
    private static final List<CategoryOption> WORK_CATEGORIES;
    private static final List<CategoryOption> ALL_CATEGORIES;

    static {
        List<CategoryOption> life = new ArrayList<>();
        life.add(new CategoryOption(GROUP_LIFE, CODE_LIFE_PICKUP, "取餐"));
        life.add(new CategoryOption(GROUP_LIFE, CODE_LIFE_DELIVERY, "快递"));
        life.add(new CategoryOption(GROUP_LIFE, CODE_LIFE_CARD, "卡证"));
        life.add(new CategoryOption(GROUP_LIFE, CODE_LIFE_TICKET, "票券"));
        LIFE_CATEGORIES = Collections.unmodifiableList(life);

        List<CategoryOption> work = new ArrayList<>();
        work.add(new CategoryOption(GROUP_WORK, CODE_WORK_TODO, "待办"));
        work.add(new CategoryOption(GROUP_WORK, CODE_WORK_SCHEDULE, "日程"));
        WORK_CATEGORIES = Collections.unmodifiableList(work);

        List<CategoryOption> all = new ArrayList<>();
        all.addAll(LIFE_CATEGORIES);
        all.addAll(WORK_CATEGORIES);
        ALL_CATEGORIES = Collections.unmodifiableList(all);
    }

    private CategoryCatalog() {
    }

    public static List<CategoryOption> getLifeCategories() {
        return LIFE_CATEGORIES;
    }

    public static List<CategoryOption> getWorkCategories() {
        return WORK_CATEGORIES;
    }

    public static List<CategoryOption> getAllCategories() {
        return ALL_CATEGORIES;
    }

    public static List<CategoryOption> getCategoriesByGroup(String groupCode) {
        if (GROUP_WORK.equals(groupCode)) {
            return WORK_CATEGORIES;
        }
        return LIFE_CATEGORIES;
    }

    public static String getGroupName(String groupCode) {
        return GROUP_WORK.equals(groupCode) ? "工作" : "生活";
    }

    public static String getCategoryName(String categoryCode) {
        for (CategoryOption option : ALL_CATEGORIES) {
            if (option.categoryCode.equals(categoryCode)) {
                return option.categoryName;
            }
        }
        return "快递";
    }

    public static String getGroupByCategoryCode(String categoryCode) {
        for (CategoryOption option : ALL_CATEGORIES) {
            if (option.categoryCode.equals(categoryCode)) {
                return option.groupCode;
            }
        }
        return GROUP_LIFE;
    }

    public static boolean isWorkCategoryCode(String categoryCode) {
        return CODE_WORK_TODO.equals(categoryCode) || CODE_WORK_SCHEDULE.equals(categoryCode);
    }

    public static boolean isReminderCategory(String categoryCode) {
        return CODE_WORK_TODO.equals(categoryCode) || CODE_WORK_SCHEDULE.equals(categoryCode);
    }
}
