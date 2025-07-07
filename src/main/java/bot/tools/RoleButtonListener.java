package bot.tools;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":");
        if (parts.length != 3 || !parts[0].equals("role") || !parts[1].equals("toggle")) {
            return; // 잘못된 형식이면 무시
        }

        String roleId = parts[2];
        if (roleId == null || roleId.equals("null")) {
            event.reply("❌ 역할 ID를 찾을 수 없어요. 관리자에게 문의하세요.").setEphemeral(true).queue();
            return;
        }
        Role role = event.getGuild().getRoleById(roleId);
        Member member = event.getMember();

        if (role == null || member == null) {
            event.reply("⚠️ 역할을 찾을 수 없거나 멤버 정보를 불러올 수 없다냥!").setEphemeral(true).queue();
            return;
        }

        if (member.getRoles().contains(role)) {
            // 이미 역할 있음 → 제거
            event.getGuild().removeRoleFromMember(member, role).queue();
            event.reply("❌ 역할 `" + role.getName() + "` 을(를) 제거했다냥!").setEphemeral(true).queue();
        } else {
            // 역할 없음 → 추가
            event.getGuild().addRoleToMember(member, role).queue();
            event.reply("✅ 역할 `" + role.getName() + "` 을(를) 부여했다냥!").setEphemeral(true).queue();
        }
    }
}