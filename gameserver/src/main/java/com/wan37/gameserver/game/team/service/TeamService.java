package com.wan37.gameserver.game.team.service;

import com.wan37.gameserver.event.EventBus;
import com.wan37.gameserver.event.model.TeamEvent;
import com.wan37.gameserver.game.gameInstance.model.GameInstance;
import com.wan37.gameserver.game.gameInstance.service.InstanceService;
import com.wan37.gameserver.game.player.model.Player;
import com.wan37.gameserver.game.player.service.PlayerDataService;
import com.wan37.gameserver.game.scene.servcie.GameSceneService;
import com.wan37.gameserver.game.team.manager.TeamManager;
import com.wan37.gameserver.game.team.model.Team;
import com.wan37.gameserver.manager.notification.NotificationManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/12/17 12:20
 * @version 1.00
 * Description: 队伍服务
 */

@Service
public class TeamService {


    @Resource
    private PlayerDataService playerDataService;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private InstanceService instanceService;


    @Resource
    private GameSceneService gameSceneService;


    /**
     *  邀请玩家加入自己的队伍
     * @param invitee 被邀请者
     * @return 是否邀请成功
     */
    public boolean inviteTeam(Player invitor,Player invitee) {
        // 如果被要求者已经有队伍了，不能组队
        if (!invitee.getTeamId().isEmpty()) {
            return false;
        }

        Long invitorIdNow = TeamManager.getTeamRequest(invitee.getId());
        // 如果当前有人正在邀请玩家，则不能组队
        if (null != invitorIdNow) {
            return false;
        }
        //保存组队请求
        TeamManager.putTeamRequest(invitee.getId(),invitor.getId());

        return true;


    }

    /**
     *    退出队伍
     * @param ctx 通道上下文
     */
    public void leaveTeam(ChannelHandlerContext ctx) {
        Player leaver = playerDataService.getPlayerByCtx(ctx);
        Team team = TeamManager.getTeam(leaver.getTeamId());
        if (leaver.getTeamId().isEmpty() || Objects.isNull(team)) {
            notificationManager.notifyPlayer(leaver,"你并不在任何队伍里");
        } else {
            team.getTeamPlayer().remove(leaver.getId());
            leaver.setTeamId("");
            // 如果退出者是队长，随机指定一个队员作为队长
            if (leaver.getId().equals(team.getCaptainId())) {
                team.getTeamPlayer().values().stream().findAny().ifPresent(
                        p -> team.setCaptainId(p.getId())
                );
            }
            team.getTeamPlayer().values().forEach(
                    p -> notificationManager.notifyPlayer(p, MessageFormat.format("玩家{0}已经离开队伍",leaver.getName()))
            );
            notificationManager.notifyPlayer(leaver,"你已经离开队伍");
        }
    }


    /**
     *  如果有人邀请加入，根据当前的组队请求加入队伍
     * @param ctx 通道上下文
     */
    public void joinTeam(ChannelHandlerContext ctx) {
        Player invitee = playerDataService.getPlayerByCtx(ctx);
        Long invitorId = TeamManager.getTeamRequest(invitee.getId());
        Player invitor = playerDataService.getOnlinePlayerById(invitorId);

        String invitorTeamId = invitor.getTeamId();
         // 检测发起组队的玩家是否已经有队伍
        if (invitorTeamId.isEmpty()) {
            // 新建队伍
            Map<Long,Player> playerMap = new ConcurrentHashMap<>(5);
            playerMap.putIfAbsent(invitor.getId(), invitor);
            playerMap.putIfAbsent(invitee.getId(),invitee) ;
            String teamId = invitor.getId().toString();
            Team team = new Team(teamId,playerMap);
            TeamManager.putTeam(teamId,team);
            invitee.setTeamId(teamId);
            invitor.setTeamId(teamId);
            team.setCaptainId(invitor.getId());

            notificationManager.notifyPlayer(invitor,"新建队伍成功");
            showTeam(ctx);
            showTeam(invitor.getCtx());
        } else {
            // 从发起组队的人获取队伍实体
            Team team = TeamManager.getTeam(invitorTeamId);
            team.getTeamPlayer().putIfAbsent(invitee.getId(),invitee);
            invitee.setTeamId(team.getId());
            showTeam(ctx);
            showTeam(invitor.getCtx());
        }

        // 组队事件
        EventBus.publish(new TeamEvent(Arrays.asList(invitee,invitor)));
    }


    /**
     *  查看当前队伍
     * @param ctx 通道上下文
     */
    public void showTeam(ChannelHandlerContext ctx) {
        Player player = playerDataService.getPlayerByCtx(ctx);
        String teamId = player.getTeamId();
        if (teamId.isEmpty()) {
            notificationManager.notifyPlayer(player,"你并没有加入队伍 \n");
        } else {
            Team team = TeamManager.getTeam(teamId);
            notificationManager.notifyPlayer(player,"当前队伍：\n");
            Optional.ofNullable(team).ifPresent(
                    t -> t.getTeamPlayer().values().forEach(
                            p -> {
                                if (p.getId().equals(team.getCaptainId())) {
                                    notificationManager.notifyPlayer(player,MessageFormat.format("队长： {0} hp:{1} mp:{2} \n",
                                            p.getName(),p.getHp(),p.getMp()));
                                } else {
                                    notificationManager.notifyPlayer(player,MessageFormat.format("队员： {0} hp:{1} mp:{2} \n",
                                            p.getName(),p.getHp(),p.getMp()));
                                }
                            }
                    )
            );
        }
    }

    /**
     *      传送进入团队副本
     * @param team 队伍
     * @param instanceId 副本id
     */
    public void teamInstance(Team team, Integer instanceId) {

        instanceService.enterTeamInstance(team.getTeamPlayer().values(),instanceId);

        team.getTeamPlayer().values()
                .forEach(player -> notificationManager.notifyPlayer(player,MessageFormat.format("{0}进入副本",
                        player.getName())));
    }



}
