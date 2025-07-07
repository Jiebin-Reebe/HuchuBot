package bot.commands;

import bot.managers.RoleIdManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoleGiverCommand extends ListenerAdapter {
    private final RoleIdManager roleIdManager = new RoleIdManager();

    String normalID = roleIdManager.getNormalID();

    String lolID = roleIdManager.getLolID();
    String tftID = roleIdManager.getTftID();
    String valID = roleIdManager.getValID();
    String mcID = roleIdManager.getMcID();
    String chessID = roleIdManager.getChessID();
    String pubgID = roleIdManager.getPubgID();
    String stdID = roleIdManager.getStdID();
    String owID  = roleIdManager.getOwID();
    String etcID =  roleIdManager.getEtcID();

    String maleID = roleIdManager.getMaleID();
    String femaleID = roleIdManager.getFemaleID();

    String ID90 = roleIdManager.getID90();
    String ID91 = roleIdManager.getID91();
    String ID92 = roleIdManager.getID92();
    String ID93 = roleIdManager.getID93();
    String ID94 = roleIdManager.getID94();
    String ID95 = roleIdManager.getID95();
    String ID96 = roleIdManager.getID96();
    String ID97 = roleIdManager.getID97();
    String ID98 = roleIdManager.getID98();
    String ID99 = roleIdManager.getID99();
    String ID00 = roleIdManager.getID00();
    String ID01 = roleIdManager.getID01();
    String ID02 = roleIdManager.getID02();
    String ID03 = roleIdManager.getID03();
    String ID04 = roleIdManager.getID04();
    String ID05 = roleIdManager.getID05();
    String ID06 = roleIdManager.getID06();
    String ID07 = roleIdManager.getID07();
    String ID08 = roleIdManager.getID08();
    String ID09 = roleIdManager.getID09();
    String ID10 = roleIdManager.getID10();
    String ID11 = roleIdManager.getID11();
    String IDL11 = roleIdManager.getIDL11();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String msg = event.getMessage().getContentRaw();
        String[] parts = msg.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "!기본권한":
                giveRoleIfReadRule(event);
                break;

            case "!게임메뉴":
                giveRoleGameNotification(event);
                break;

            case "!성별선택":
                giveRoleGender(event);
                break;

            case "!나이선택":
                giveRoleAge(event);
                break;
        }
    }

    private void giveRoleIfReadRule(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("리베, 서버장")
                .setTitle("규칙을 읽으셨나요?")
                .setDescription("규칙을 다 읽으셨다면 해당 버튼을 눌러주세요!")
                .setColor(Color.CYAN);

        Button roleButton = Button.success("role:toggle:" + normalID, "서버 기본 권한 받기");

        event.getChannel().sendMessageEmbeds(embed.build())
                .setActionRow(roleButton)
                .queue();
    }

    private void giveRoleGameNotification(MessageReceivedEvent event) {
        Map<String, String> roles = getGameRoles();

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("리베, 서버장")
                .setTitle("어떤 게임을 하십니까?")
                .setDescription("하시는 게임을 전부 눌러주세요!")
                .setColor(Color.CYAN);

        menuBuilder(event, roles, embed);
    }

    @NotNull
    private Map<String, String> getGameRoles() {
        Map<String, String> roles = new LinkedHashMap<>();

        if (lolID != null) roles.put(lolID, "롤");
        if (tftID != null) roles.put(tftID, "롤체");
        if (valID != null) roles.put(valID, "발로란트");
        if (mcID != null) roles.put(mcID, "마크");
        if (chessID != null) roles.put(chessID, "체스");
        if (pubgID != null) roles.put(pubgID, "배그");
        if (stdID != null) roles.put(stdID, "스타듀벨리");
        if (owID != null) roles.put(owID, "오버워치");
        if (etcID != null) roles.put(etcID, "기타등등");
        return roles;
    }

    private void giveRoleGender(MessageReceivedEvent event) {
        Map<String, String> roles = new LinkedHashMap<>();
        if (maleID != null) roles.put(maleID, "남자");
        if (femaleID != null) roles.put(femaleID, "여자");

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("리베, 서버장")
                .setTitle("당신의 성별은?")
                .setDescription("당신의 성별을 알려주세요!")
                .setColor(Color.CYAN);

        menuBuilder(event, roles, embed, "role:exclusive:gender");
    }

    private void giveRoleAge(MessageReceivedEvent event) {
        Map<String, String> roles = getAgeRoles();

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("리베, 서버장")
                .setTitle("당신의 나이는?")
                .setDescription("당신의 나이를 선택해주세요!")
                .setColor(Color.CYAN);

        menuBuilder(event, roles, embed, "role:exclusive:age");
    }

    @NotNull
    private Map<String, String> getAgeRoles() {
        Map<String, String> roles = new LinkedHashMap<>();
        if (ID90 != null) roles.put(ID90, "90년생");
        if (ID91 != null) roles.put(ID91, "91년생");
        if (ID92 != null) roles.put(ID92, "92년생");
        if (ID93 != null) roles.put(ID93, "93년생");
        if (ID94 != null) roles.put(ID94, "94년생");
        if (ID95 != null) roles.put(ID95, "95년생");
        if (ID96 != null) roles.put(ID96, "96년생");
        if (ID97 != null) roles.put(ID97, "97년생");
        if (ID98 != null) roles.put(ID98, "98년생");
        if (ID99 != null) roles.put(ID99, "99년생");
        if (ID00 != null) roles.put(ID00, "00년생");
        if (ID01 != null) roles.put(ID01, "01년생");
        if (ID02 != null) roles.put(ID02, "02년생");
        if (ID03 != null) roles.put(ID03, "03년생");
        if (ID04 != null) roles.put(ID04, "04년생");
        if (ID05 != null) roles.put(ID05, "05년생");
        if (ID06 != null) roles.put(ID06, "06년생");
        if (ID07 != null) roles.put(ID07, "07년생");
        if (ID08 != null) roles.put(ID08, "08년생");
        if (ID09 != null) roles.put(ID09, "09년생");
        if (ID10 != null) roles.put(ID10, "10년생");
        if (ID11 != null) roles.put(ID11, "11년생");
        if (IDL11 != null) roles.put(IDL11, "<11년생");
        return roles;
    }

    private void menuBuilder(MessageReceivedEvent event, Map<String, String> roles, EmbedBuilder embed) {
        StringSelectMenu.Builder menu = StringSelectMenu.create("role:select")
                .setPlaceholder("원하는 역할을 선택하세요!")
                .setMinValues(0)
                .setMaxValues(roles.size());

        for (Map.Entry<String, String> entry : roles.entrySet()) {
            menu.addOption(entry.getValue(), entry.getKey());
        }

        event.getChannel().sendMessageEmbeds(embed.build())
                .setActionRow(menu.build())
                .queue();
    }

    private void menuBuilder(MessageReceivedEvent event, Map<String, String> roles, EmbedBuilder embed, String componentId) {
        StringSelectMenu.Builder menu = StringSelectMenu.create(componentId)
                .setPlaceholder("원하는 역할을 선택하세요!")
                .setMinValues(0)
                .setMaxValues(componentId.startsWith("role:exclusive") ? 1 : roles.size());

        for (Map.Entry<String, String> entry : roles.entrySet()) {
            menu.addOption(entry.getValue(), entry.getKey());
        }

        event.getChannel().sendMessageEmbeds(embed.build())
                .setActionRow(menu.build())
                .queue();
    }
}
