package com.wan37.gameServer.server.dispatcher;

import com.wan37.gameServer.common.IController;
import com.wan37.common.entity.Message;
import com.wan37.common.entity.MsgId;
import com.wan37.gameServer.controller.ErrorController;
import com.wan37.gameServer.manager.ControllerManager;
import com.wan37.gameServer.service.PlayerQuitService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/9/18 17:02
 * @version 1.00
 * Description: mmorpg
 */

@Slf4j
@ChannelHandler.Sharable
@Component
public class RequestDispatcher  extends SimpleChannelInboundHandler<Message> {

    @Resource
    private ControllerManager controllerManager;

    @Resource
    private ErrorController errorController;

    @Resource
    private PlayerQuitService playerQuitService;


    //  当客户端连上服务器的时候触发此函数
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端: " + ctx.channel().id() + " 加入连接",CharsetUtil.UTF_8);

    }

    // 当客户端断开连接的时候触发函数
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 将角色信息保存到数据库
        playerQuitService.savePlayer(ctx);

        // 清除缓存
        playerQuitService.cleanPlayerCache(ctx);
        log.info("客户端: " + ctx.channel().id() + " 已经离线");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        log.info("收到的信息： "+msg.toString());
        IController controller = controllerManager.get(msg.getMsgId());
        if (controller == null) {
            errorController.handle( ctx ,msg);
        } else {
            controller.handle(ctx,msg);
        }
     }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush("服务器内部发生错误");
        log.error("服务器内部发生错误");
        throw new RuntimeException(cause.getCause());
    }
}
