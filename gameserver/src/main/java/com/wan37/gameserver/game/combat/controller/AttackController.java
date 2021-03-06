package com.wan37.gameserver.game.combat.controller;

import com.wan37.common.entity.Cmd;
import com.wan37.common.entity.Message;
import com.wan37.gameserver.game.combat.service.CombatService;
import com.wan37.gameserver.game.player.model.Player;
import com.wan37.gameserver.game.player.service.PlayerDataService;

import com.wan37.gameserver.game.scene.model.GameScene;
import com.wan37.gameserver.game.scene.servcie.GameSceneService;
import com.wan37.gameserver.manager.controller.ControllerManager;
import com.wan37.gameserver.manager.notification.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/11/8 14:29
 * @version 1.00
 * Description: 玩家攻击怪物
 */


@Controller
public class AttackController {

    {
        ControllerManager.add(Cmd.COMMON_ATTACK,this::commonAttack);
        ControllerManager.add(Cmd.PVE_SKILL,this::useSkillAttackMonster);
        ControllerManager.add(Cmd.PVP,this::pvpAttack);
        ControllerManager.add(Cmd.SKILL_PVP,this::pvpBySkill);
    }


    @Resource
    private CombatService combatService;

    @Resource
    private PlayerDataService playerDataService;

    @Resource
    private GameSceneService gameSceneService;

    @Resource
    private NotificationManager notificationManager;



    private void commonAttack(ChannelHandlerContext ctx, Message message) {
        String[] command = new String(message.getContent()).split("\\s+");
        Long gameObjectId = Long.valueOf(command[1]);

        Player player = playerDataService.getPlayer(ctx);
       combatService.commonAttack(player,gameObjectId);
    }




    private void useSkillAttackMonster(ChannelHandlerContext ctx, Message message) {
        String[] command = new String(message.getContent()).split("\\s+");
        Integer skillId = Integer.valueOf(command[1]);
        Long targetId = Long.valueOf(command[2]);
        List<Long> targetIdList = Arrays.stream(Arrays.copyOfRange(command,2,command.length))
                .map(Long::valueOf).collect(Collectors.toList());

        combatService.useSkillAttackMonster(ctx,skillId,targetIdList);
    }



    private void pvpAttack(ChannelHandlerContext ctx, Message message) {
        String[] command = new String(message.getContent()).split("\\s+");
        Long targetId = Long.valueOf(command[1]);

        Player player = playerDataService.getPlayerByCtx(ctx);
        GameScene gameScene = gameSceneService.getSceneByPlayer(player);

        Player targetPlayer = gameScene.getPlayers().get(targetId);

        if (player.getId().equals(targetId)) {
            notificationManager.notifyPlayer(player,"自己不能攻击自己");
            return;
        }
        if (Objects.isNull(targetPlayer)) {
            notificationManager.notifyPlayer(player,"目标不在当前场景");
            return;
        }
        combatService.commonAttackByPVP(player,targetPlayer);
    }


    private void pvpBySkill(ChannelHandlerContext ctx, Message message) {
        String[] command = new String(message.getContent()).split("\\s+");
        if (command.length < 3 ) {
            NotificationManager.notifyByCtx(ctx,"参数不足，模板是：pvp_skill  技能id  目标id..");
            return;
        }
        Integer skillId = Integer.valueOf(command[1]);
        String[] targetListString = Arrays.copyOfRange(command,2,command.length);
        Player player = playerDataService.getPlayerByCtx(ctx);
        List<Long>  targetIdList =  Arrays.stream(targetListString).map(Long::valueOf).collect(Collectors.toList());
        combatService.useSkillPVP(player,skillId,targetIdList);
    }


}
