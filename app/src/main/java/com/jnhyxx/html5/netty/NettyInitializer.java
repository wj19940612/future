package com.jnhyxx.html5.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    private NettyClientHandler.Callback mCallback;

    public NettyInitializer(NettyClientHandler.Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new SocketDecoder());
        pipeline.addLast(new SocketEncoder());

        pipeline.addLast(new NettyClientHandler(mCallback));
    }
}
