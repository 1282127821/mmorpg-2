package com.wan37.gameserver.game.gameInstance.controller;

import com.wan37.common.entity.Message;
import com.wan37.common.entity.Cmd;
import com.wan37.gameserver.game.gameInstance.model.GameInstance;
import com.wan37.gameserver.game.gameInstance.service.InstanceService;
import com.wan37.gameserver.game.player.model.Player;
import com.wan37.gameserver.game.player.service.PlayerDataService;
import com.wan37.gameserver.game.scene.model.GameScene;
import com.wan37.gameserver.manager.controller.ControllerManager;
import com.wan37.gameserver.manager.notification.NotificationManager;
import com.wan37.gameserver.server.GameServer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/11/26 16:49
 * @version 1.00
 * Description: mmorpg
 */

@Controller
public class InstanceController {

    {
        ControllerManager.add(Cmd.ENTER_INSTANCE,this::instanceEnter);
        ControllerManager.add(Cmd.EXIT_INSTANCE,this::instanceExit);
        ControllerManager.add(Cmd.EXIT_INSTANCE,this::showInstance);

    }


    @Resource
    private PlayerDataService playerDataService;

    @Resource
    private InstanceService instanceService;



    /**
     *  进入副本
     */
    public void instanceEnter(ChannelHandlerContext ctx, Message message) {
        String[] cmd  = new String(message.getContent()).split("\\s+");

        Integer instanceId = Integer.valueOf(cmd[1]);


        Player player = playerDataService.getPlayer(ctx);
        GameInstance gameInstance = instanceService.enterInstance(player,instanceId);
        if (gameInstance != null) {

            NotificationManager.notifyByCtx(ctx,MessageFormat.format("进入副本 {}",gameInstance.getName()));
        } else {
            NotificationManager.notifyByCtx(ctx,MessageFormat.format("进入id为{}的失败", instanceId));
        }
        ctx.writeAndFlush(message);
    }


    /**
     *  退出副本
     */
    public void instanceExit(ChannelHandlerContext ctx, Message message) {
        Player player = playerDataService.getPlayer(ctx);

        if (instanceService.isInInstance(player)) {
            instanceService.exitInstance(player,(GameInstance)player.getCurrentScene());
            NotificationManager.notifyByCtx(ctx,"退出副本成功");
        } else {
            NotificationManager.notifyByCtx(ctx,"你不在副本中");
        }
        ctx.writeAndFlush(message);
    }


    /**
     *  显示副本
     *
     */
    private void showInstance(ChannelHandlerContext ctx, Message message) {

        List<GameScene> sceneList = instanceService.findInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("副本列表： \n");
        sceneList.stream()
                .forEach(scene -> sb.append(MessageFormat.format("{副本id ：{}  副本名称：{} ",
                        scene.getId(),scene.getName())));
        NotificationManager.notifyByCtx(ctx,sb);


    }

}
