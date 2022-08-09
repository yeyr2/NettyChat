package client.SimpleChannelHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.RequestMessage;

import static client.Start.semaphore;

public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        if(msg.isGroupORSingle()){
            if(!msg.isConfirm() && msg.isFriend()){
                System.err.println("你已经进入该群了");
            }else{

            }
            return;
        }
        if(msg.isFriend()){
            System.err.println("你与目标已经是好友了！");
        }else{
            System.out.println(msg.getNotice());
            semaphore.release();
        }
    }
}
