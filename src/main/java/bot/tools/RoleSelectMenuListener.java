package bot.tools;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class RoleSelectMenuListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();
        List<String> selectedRoleIds = event.getValues(); // 선택된 역할 ID들
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (guild == null || member == null) {
            event.reply("⚠️ 정보를 불러올 수 없다냥!").setEphemeral(true).queue();
            return;
        }

        // 전체 드롭다운에 표시된 역할 목록
        List<String> allRoleIds = event.getComponent().getOptions().stream()
                .map(o -> o.getValue())
                .collect(Collectors.toList());

        // 제거할 역할: 선택되지 않은 것들
        for (String roleId : allRoleIds) {
            Role role = guild.getRoleById(roleId);
            if (role == null) continue;
            if (!selectedRoleIds.contains(roleId) && member.getRoles().contains(role)) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }

        // 부여할 역할: 선택된 것들
        for (String roleId : selectedRoleIds) {
            Role role = guild.getRoleById(roleId);
            if (role == null) continue;
            if (!member.getRoles().contains(role)) {
                guild.addRoleToMember(member, role).queue();
            }
        }

        // 메뉴 하나만 선택하기
        if (componentId.startsWith("role:exclusive:")) {
            for (String roleId: allRoleIds) {
                if (selectedRoleIds.contains(roleId)) continue;
                Role role = guild.getRoleById(roleId);
                if (role != null && member.getRoles().contains(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                }
            }
        }

        event.reply("✅ 역할이 업데이트 되었다냥!").setEphemeral(true).queue();
    }
}
