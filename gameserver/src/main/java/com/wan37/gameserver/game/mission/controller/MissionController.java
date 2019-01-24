package com.wan37.gameserver.game.mission.controller;

import com.wan37.common.entity.Message;
import com.wan37.common.entity.Cmd;
import com.wan37.gameserver.game.mission.model.MissionProgress;
import com.wan37.gameserver.game.mission.service.MissionService;
import com.wan37.gameserver.game.player.model.Player;
import com.wan37.gameserver.game.player.service.PlayerDataService;
import com.wan37.gameserver.manager.controller.ControllerManager;
import com.wan37.gameserver.manager.notification.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/12/26 19:44
 * @version 1.00
 * Description: mmorpg
 */


@Controller
public class MissionController {

    @Resource
    private MissionService missionService;


    @Resource
    private PlayerDataService playerDataService;


    {
        ControllerManager.add(Cmd.MISSION_SHOW,this::showPlaerMission);
        ControllerManager.add(Cmd.ALL_MISSION,this::allMission);
        ControllerManager.add(Cmd.MISSION_ACEEPT,this::acceptMission);
    }


    /**
     *  接受任务
     */
    private void acceptMission(ChannelHandlerContext cxt, Message message) {
    }



    /**
     *  显示任务成就
     * @param cxt 上下文
     * @param message 信息
     */
    private void missionShow(ChannelHandlerContext cxt, Message message) {

        missionService.missionShow(cxt);
    }


    /**
     *  显示所有任务和成就
     */

    private void allMission(ChannelHandlerContext cxt, Message message) {
        missionService.allMissionShow(cxt);
    }



    private void showPlaerMission(ChannelHandlerContext cxt, Message message) {
        Player player = playerDataService.getPlayerByCtx(cxt);
        Map<Integer, MissionProgress> missionProgressMap = missionService.getPlayerMissionProgress(player);
        StringBuilder sb = new StringBuilder();
        sb.append("玩家当前进行的任务： \n");
        missionProgressMap.values().forEach(
                missionProgress -> {
                    sb.append(MessageFormat.format("{0} {1} 等级：{2} 描述：{3}  进度: ",
                            missionProgress.getMission().getId(),
                            missionProgress.getMission().getName(),
                            missionProgress.getMission().getLevel(),
                            missionProgress.getMission().getDescribe()
                            ));
                    missionProgress.getMission().getConditionsMap().forEach(
                            (k,v) -> sb.append(MessageFormat.format("{0}: {1}/{2} \n",k,
                                    missionProgress.getProgressMap().get(k).getNow(),
                                    missionProgress.getProgressMap().get(k).getGoal()))
                    );
                }
        );
        NotificationManager.notifyByCtx(cxt,sb);
    }



}
