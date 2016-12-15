package com.jnhyxx.html5.netty;

import android.text.TextUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    interface Callback {

        void onChannelActive(ChannelHandlerContext ctx);

        void onChannelInActive(ChannelHandlerContext ctx);

        void onReceiveData(String data);

        void onError(ChannelHandlerContext ctx, Throwable cause);
    }

    private Callback mCallback;

    public NettyClientHandler(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (TextUtils.isEmpty(s) || s.equalsIgnoreCase("null")) return;

        if (mCallback != null) {
            mCallback.onReceiveData(s);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (mCallback != null) {
            mCallback.onChannelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (mCallback != null) {
            mCallback.onChannelInActive(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (mCallback != null) {
            mCallback.onError(ctx, cause);
        }
        cause.printStackTrace();
        ctx.close();
    }
}
